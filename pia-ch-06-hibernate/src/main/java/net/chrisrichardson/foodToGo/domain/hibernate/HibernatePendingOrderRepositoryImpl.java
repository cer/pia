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

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;

public class HibernatePendingOrderRepositoryImpl extends HibernateDaoSupport
		implements PendingOrderRepository {

	public HibernatePendingOrderRepositoryImpl(HibernateTemplate template) {
		setHibernateTemplate(template);
	}

	public PendingOrder createPendingOrder() {
		PendingOrder po = new PendingOrder();
		getHibernateTemplate().save(po);
		return po;
	}

	public PendingOrder findPendingOrder(String pendingOrderId) {
		return (PendingOrder) getHibernateTemplate().load(PendingOrder.class,
				new Integer(pendingOrderId));
	}

	public PendingOrder findPendingOrderSlowWay(String pendingOrderId) {
		SessionFactory sessionFactory = getSessionFactory();
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		try {
			return (PendingOrder) session.load(PendingOrder.class, new Integer(
					pendingOrderId));
		} catch (HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		} finally {
			SessionFactoryUtils.releaseSession(session, sessionFactory);
		}
	}

	public PendingOrder createPendingOrder(Address deliveryAddress,
			Date deliveryTime, Restaurant restaurant,
			List quantitiesAndMenuItems) {
		PendingOrder po = new PendingOrder(deliveryAddress, deliveryTime,
				restaurant, quantitiesAndMenuItems);
		getHibernateTemplate().save(po);
		return po;
	}

	public PendingOrder findOrCreatePendingOrder(String pendingOrderId) {
		return pendingOrderId == null ? createPendingOrder()
				: findPendingOrder(pendingOrderId);
	}

	public PendingOrder findPendingOrderWithRestaurantLineItemsAndMenuItems(
			String pendingOrderId) {
		return (PendingOrder) getHibernateTemplate()
				.findByNamedQuery(
						"PendingOrder.findPendingOrderWithRestaurantLineItemsAndMenuItems",
						new Integer(pendingOrderId)).get(0);
	}

	public PendingOrder findPendingOrderWithRestaurantAndMenuItems(
			String pendingOrderId) {
		return (PendingOrder) getHibernateTemplate().findByNamedQuery(
				"PendingOrder.findPendingOrderWithRestaurantAndMenuItems",
				new Integer(pendingOrderId)).get(0);
	}

	public PendingOrder findPendingOrderWithLineItems(String pendingOrderId) {
		return (PendingOrder) getHibernateTemplate().findByNamedQuery(
				"PendingOrder.findPendingOrderWithLineItems",
				new Integer(pendingOrderId)).get(0);
	}
}