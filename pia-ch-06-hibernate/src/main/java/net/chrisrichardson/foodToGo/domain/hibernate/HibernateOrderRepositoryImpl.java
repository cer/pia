/*
 * Copyright (c) 2005 Chris Richardson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package net.chrisrichardson.foodToGo.domain.hibernate;

import java.sql.*;
import java.util.*;

import junit.framework.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.Order;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.util.hibernate.*;

import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;

public class HibernateOrderRepositoryImpl extends HibernateDaoSupport implements
		OrderRepository {

	public static final String OPTIMISTIC_LOCKING = "OPTIMISTIC_LOCKING";

	public static final String PESSIMISTIC_LOCKING = "PESSIMISTIC_LOCKING";

	public static final String NO_LOCKING = "NO_LOCKING";

	private int timeWindowInMinutes = 60;

	private String lockingMode = NO_LOCKING;

	private HibernateWrapper queryExecutor;

	public HibernateOrderRepositoryImpl(HibernateTemplate template,
			HibernateWrapper queryExecutor) {
		this.queryExecutor = queryExecutor;
		setHibernateTemplate(template);
	}

	public void setTimeWindowInMinutes(int i) {
		timeWindowInMinutes = i;
	}

	public void setLockingMode(String string) {
		lockingMode = string;
	}

	public Order createOrder(PendingOrder pendingOrder) {
		Order order = new Order("THIS SHOULD BE GEN ID", pendingOrder // FIXME
				.getDeliveryAddress(), pendingOrder.getDeliveryTime(),
				pendingOrder.getRestaurant(), pendingOrder
						.getPaymentInformation());

		for (Iterator it = pendingOrder.getLineItems().iterator(); it.hasNext();) {
			PendingOrderLineItem lineItem = (PendingOrderLineItem) it.next();
			order.add(lineItem.getMenuItem(), lineItem.getQuantity());
		}

		getHibernateTemplate().save(order);
		return order;
	}

	public List findOrdersToSend() {
		HibernateQueryParameters qp = new HibernateQueryParameters(
				"findOrdersToSend");
		Calendar cutOffTime = Calendar.getInstance();
		cutOffTime.add(Calendar.MINUTE, -timeWindowInMinutes);
		if (lockingMode.equals(PESSIMISTIC_LOCKING))
			qp.setLockMode("waitingOrder", LockMode.UPGRADE);

		qp.setDate("cutOffTime", cutOffTime.getTime());

		return queryExecutor.executeNamedQuery(qp);

	}

	public List findOrdersToSend_Inline() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.getNamedQuery("findOrdersToSend");
				query.setLockMode("waitingOrder", LockMode.UPGRADE);
				Calendar cutOffTime = Calendar.getInstance();
				cutOffTime.add(Calendar.MINUTE, -timeWindowInMinutes);
				query.setParameter("cutOffTime", cutOffTime.getTime());
				return query.list();
			}
		});
	}

	public Order findOrder(String orderId) {
		// Eager load the line items and restaurants for acknowledgeOrderService
		List result = getHibernateTemplate().findByNamedQuery(
				"Order.findOrderWithLineItemsAndRestaurant",
				new Integer(orderId));
		return (Order) result.get(0);
	}

	public PagedQueryResult findOrders(int startingIndex, int pageSize,
			OrderSearchCriteria criteria) {
		return queryExecutor.executeCriteriaQuery(Order.class, startingIndex,
				pageSize, new FindOrdersCriteriaBuilder(criteria));
	}

	class FindOrdersCriteriaBuilder implements CriteriaBuilder {

		OrderSearchCriteria searchCriteria;

		public FindOrdersCriteriaBuilder(OrderSearchCriteria searchCriteria) {
			this.searchCriteria = searchCriteria;
		}

		public void addCriteria(Criteria criteria) throws HibernateException {
			if (searchCriteria.isDeliveryTimeSpecified()) {
				criteria.add(Restrictions.ge("deliveryTime", searchCriteria
						.getDeliveryTime()));
			}
			if (searchCriteria.isRestaurantSpecified()) {
				criteria.createCriteria("restaurant").add(
						Restrictions.like("name", searchCriteria
								.getRestaurantName()));
			} else {
				criteria.setFetchMode("restaurant", FetchMode.JOIN);
			}
			if (searchCriteria.isDeliveryCitySpecified()) {
				criteria.add(Restrictions.eq("deliveryAddress.city",
						searchCriteria.getDeliveryCity()));
			}

			switch (searchCriteria.getSortBy()) {
			case OrderSearchCriteria.SORT_BY_ORDER_ID:
				criteria
						.addOrder(searchCriteria.isSortAscending() ? org.hibernate.criterion.Order
								.asc("externalOrderId")
								: org.hibernate.criterion.Order
										.desc("externalOrderId"));
				break;
			default:
				throw new NotYetImplementedException();
			}
		}
	}

	public PagedQueryResult findOrdersInline(int startingIndex, int pageSize,
			OrderSearchCriteria searchCriteria) {
		return (PagedQueryResult) getHibernateTemplate().execute(
				new FindOrdersHibernateCallback(startingIndex, pageSize,
						searchCriteria));
	}

	private final class FindOrdersHibernateCallback implements
			HibernateCallback {
		private final int startingIndex;

		private final int pageSize;

		private final OrderSearchCriteria searchCriteria;

		private FindOrdersHibernateCallback(int startingIndex, int pageSize,
				OrderSearchCriteria searchCriteria) {
			super();
			this.startingIndex = startingIndex;
			this.pageSize = pageSize;
			this.searchCriteria = searchCriteria;
		}

		public Object doInHibernate(Session session) throws HibernateException,
				SQLException {
			Criteria criteria = session.createCriteria(Order.class);
			addCriteria(criteria, searchCriteria);
			addSortBy(criteria, searchCriteria);
			addRange(criteria);
			List result = criteria.list();
			return makePagedQueryResult(result);
		}

		private PagedQueryResult makePagedQueryResult(List result) {
			boolean more = result.size() > pageSize;
			if (more) {
				result.remove(pageSize);
			}
			return new PagedQueryResult(result, more);
		}

		private void addRange(Criteria criteria) {
			criteria.setFirstResult(startingIndex);
			criteria.setMaxResults(pageSize + 1);
		}

		public void addCriteria(Criteria criteria,
				OrderSearchCriteria searchCriteria) throws HibernateException {
			if (searchCriteria.isDeliveryTimeSpecified()) {
				criteria.add(Restrictions.ge("deliveryTime", searchCriteria
						.getDeliveryTime()));
			}
			if (searchCriteria.isRestaurantSpecified()) {
				criteria.createCriteria("restaurant").add(
						Restrictions.like("name", searchCriteria
								.getRestaurantName()));
			} else {
				criteria.setFetchMode("restaurant", FetchMode.JOIN);
			}
			if (searchCriteria.isDeliveryCitySpecified()) {
				criteria.add(Restrictions.eq("deliveryAddress.city",
						searchCriteria.getDeliveryCity()));
			}

		}

		private void addSortBy(Criteria criteria,
				OrderSearchCriteria searchCriteria) {
			switch (searchCriteria.getSortBy()) {
			case OrderSearchCriteria.SORT_BY_ORDER_ID:
				criteria
						.addOrder(searchCriteria.isSortAscending() ? org.hibernate.criterion.Order
								.asc("externalOrderId")
								: org.hibernate.criterion.Order
										.desc("externalOrderId"));
				break;
			default:
				throw new NotYetImplementedException();
			}
		}
	}

	List findOrdersUsingScroll() {
		List orders = (List) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria x = session.createCriteria(Order.class);
						x.setProjection(Projections.projectionList().add(
								Property.forName("id")).add(
								Property.forName("deliveryAddress.street1")));
						List result = x.list();
						Assert.assertFalse(result.isEmpty());
						Object[] first = (Object[]) result.get(0);
						Assert.assertEquals(2, first.length);
						return result;
					}
				});
		return orders;
	}

	List findOrdersUsingSQLAndList() {
		List orders = (List) getHibernateTemplate().execute(
				new SqlUsingListCallback());
		return orders;
	}

	private final class SqlUsingListCallback implements HibernateCallback {
		public Object doInHibernate(Session session) throws HibernateException,
				SQLException {
			String sqlQuery = "SELECT  {o.*},{r.*} "
					+ " FROM FTGO_PLACED_ORDER o, FTGO_RESTAURANT r "
					+ " WHERE r.restaurant_id = o.restaurant_id "
					+ "AND o.delivery_city = :name "
					+ " ORDER BY DELIVERY_TIME desc";
			SQLQuery query = session.createSQLQuery(sqlQuery);
			query.addEntity("o", Order.class);
			query.addJoin("r", "o.restaurant");
			query.setParameter("name", "Oakland");
			List results = query.list();
			return results;
		}
	}

	List findOrdersUsingSQLAndScroll() {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				SQLQuery query = makeSqlQuery(session);
				ScrollableResults results = query.scroll();
				List orders = getOrdersFromScrollableResults(results);
				Order o = (Order) orders.get(0);
				return orders;
			}

		});
	}

	private SQLQuery makeSqlQuery(Session session) {
		SQLQuery query = session.createSQLQuery("SELECT  {o.*},{r.*} "
				+ " FROM FTGO_PLACED_ORDER o, FTGO_RESTAURANT r "
				+ " WHERE r.restaurant_id = o.restaurant_id "
				+ "AND o.delivery_city = :name "
				+ " ORDER BY DELIVERY_TIME desc");
		query.addEntity("o", Order.class);
		query.addJoin("r", "o.restaurant");
		query.setParameter("name", "Oakland");
		return query;
	}

	private List getOrdersFromScrollableResults(ScrollableResults results1) {
		List orders = new ArrayList();
		int pageSize = 10;
		if (results1.first() && results1.scroll(0)) {
			for (int i = 0; i < pageSize; i++) {
				orders.add(results1.get(0));
				if (!results1.next())
					break;
			}
		}

		List results = orders;
		return results;
	}
}