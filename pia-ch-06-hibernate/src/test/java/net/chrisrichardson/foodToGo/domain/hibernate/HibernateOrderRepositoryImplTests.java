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
import java.util.Date;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.Order;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.util.hibernate.*;
import net.chrisrichardson.ormunit.hibernate.*;
import net.chrisrichardson.util.*;

import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.*;

public class HibernateOrderRepositoryImplTests extends
		HibernatePersistenceTests {

	@Override
	protected String[] getConfigLocations() {
		return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

	}

	private final class ScrollHibernateCallback implements HibernateCallback {
		public Object doInHibernate(Session session) throws HibernateException,
				SQLException {
			ScrollableResults results = session.createCriteria(Order.class)
					.add(Restrictions.eq("deliveryAddress.city", "Oakland"))
					.setFetchMode("restaurant", FetchMode.JOIN).scroll();
			List orders = new ArrayList();
			int pageSize = 100;
			if (results.first() && results.scroll(1)) {
				for (int i = 0; i < pageSize; i++) {
					orders.add(results.get(0));
					if (!results.next())
						break;
				}
			}

			Order order = (Order) orders.get(0);
			Restaurant r = order.getRestaurant();
			return null;
		}
	}

	private HibernateOrderRepositoryImpl repository;

	private OrderSearchCriteria criteria;

	private String orderId1;

	private String orderId2;

	protected void onSetUp() throws Exception {
		super.onSetUp();

		insertTestData();

		HibernateWrapper queryExecutor = new HibernateQueryExecutor(
				getHibernateTemplate());
		repository = new HibernateOrderRepositoryImpl(getHibernateTemplate(),
				queryExecutor);
		criteria = new OrderSearchCriteria();
	}

	private void insertTestData() throws HibernateException {

		doWithTransaction(new TxnCallback() {

			public void execute() throws Throwable {
				delete(OrderLineItem.class);
				delete(Order.class);
				Calendar c = Calendar.getInstance();
				Date order2Time = c.getTime();
				c.add(Calendar.DAY_OF_YEAR, -30);
				Date order1Time = c.getTime();
				Order o1 = OrderMother.makeOrder(order1Time);
				Order o2 = OrderMother.makeOrder(order2Time);
				save(o1.getRestaurant());
				save(o2.getRestaurant());
				save(o1);
				save(o2);
				orderId1 = o1.getId();
				orderId2 = o2.getId();
			}
		});
	}

	public void testFindOrder() throws Exception {
		Order order = repository.findOrder(orderId1);
		assertNotNull(((OrderLineItem) order.getLineItems().get(0)).getName());
	}

	public void testFindOrders() throws Exception {
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertFalse(results.getResults().isEmpty());
	}

	public void testFindOrders_since() throws Exception {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -20);
		criteria.setDeliveryTime(c.getTime());
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertFalse(results.getResults().isEmpty());
	}

	public void testFindOrders_fromTwo() throws Exception {
		PagedQueryResult results = repository.findOrdersInline(2, 10, criteria);
		assertTrue(results.getResults().isEmpty());
	}

	public void testFindOrdersExecute_validCity() throws Exception {
		criteria.setDeliveryCity("Oakland");
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertFalse(results.getResults().isEmpty());
	}

	public void testFindOrdersExecute_bogusCity() throws Exception {
		criteria.setDeliveryCity("xxxxOaxkxlaxnd");
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertTrue(results.getResults().isEmpty());
	}

	public void testFindOrdersExecute_restaurant() throws Exception {
		criteria.setRestaurantName("Ajanta");
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertFalse(results.getResults().isEmpty());
	}

	public void testFindOrdersExecute_all() throws Exception {
		criteria.setDeliveryCity("Oakland");
		criteria.setState(Order.PLACED);
		criteria.setRestaurantName("Ajanta");
		PagedQueryResult results = repository.findOrdersInline(0, 10, criteria);
		assertFalse(results.getResults().isEmpty());
	}

	public void testFindOrdersSomething() throws Exception {
		getHibernateTemplate()
				.find(
						"select o from Order o     inner join fetch o.lineItems     inner join fetch o.restaurant where o.deliveryAddress.city='Oakland'");
	}

	public void testFindOrdersProjection() throws Exception {
		List orders = repository.findOrdersUsingScroll();
		assertNotNull(orders);
	}

	public void testScroll() throws Exception {
		getHibernateTemplate().execute(new ScrollHibernateCallback());
	}

	public void testSqlUsingList() throws Exception {
		List orders = repository.findOrdersUsingSQLAndList();
		assertNotNull(orders);
		Object[] firstResult = (Object[]) orders.get(0);
		Order o = (Order) firstResult[0];
		Restaurant r = (Restaurant) firstResult[1];
	}

	public void testSqlUsingScroll() throws Exception {
		List orders = repository.findOrdersUsingSQLAndScroll();
		assertNotNull(orders);
	}

}