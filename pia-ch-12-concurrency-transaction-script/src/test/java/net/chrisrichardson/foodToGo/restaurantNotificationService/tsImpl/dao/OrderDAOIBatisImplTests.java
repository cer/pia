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
 
package net.chrisrichardson.foodToGo.restaurantNotificationService.tsImpl.dao;

import java.util.*;

import net.chrisrichardson.foodToGo.restaurantNotificationService.commonImpl.*;
import net.chrisrichardson.ormunit.ibatis.*;

import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

public class OrderDAOIBatisImplTests extends IBatisTests {

	private OrderDAOIBatisImpl dao;

	protected String[] getConfigLocations() {
		return new String[] {
				"define-hsqldb-datasource.xml",
				"domain-common-session-factory.xml",
				"domain-hsqldb-session-factory.xml", 
				"send-orders-ibatis-dao.xml", "send-orders-ibatis-config.xml" };

	}

	public void setDao(OrderDAOIBatisImpl dao) {
		this.dao = dao;
	}

	protected String getInitialDataSet() {
		return "initialize-orders.xml";
	}

	public void testFindOrdersToSend_noLocking() throws Exception {
		List orders = dao.findOrdersToSend();
		assertFalse(orders.isEmpty());
		assertEquals(1, orders.size());
	}

	public void testFindOrdersToSend_pessimistic() throws Exception {
		DataSourceTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate tt = new TransactionTemplate(tm);

		// This has to be done in a transaction

		tt.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				dao.setLockingMode(OrderDAOIBatisImpl.PESSIMISTIC_LOCKING);
				List orders = dao.findOrdersToSend();
				assertFalse(orders.isEmpty());
				return null;
			}
		});
	}

	public void testFindOrdersToSend_optimistic() throws Exception {
		dao.setLockingMode(OrderDAOIBatisImpl.OPTIMISTIC_LOCKING);
		List orders = dao.findOrdersToSend();
		assertFalse(orders.isEmpty());
	}

	public void testMarkOrdersAsSent_noLocking() throws Exception {
		List orders = dao.findOrdersToSend();
		List notifications = makeNotifications(orders);
		dao.setLockingMode(OrderDAOIBatisImpl.ISOLATED_TRANSACTION);
		dao.markAsSent(orders, notifications);
	}

	public void testMarkOrdersAsSent_optimistic() throws Exception {
		List orders = dao.findOrdersToSend();
		List notifications = makeNotifications(orders);

		dao.setLockingMode(OrderDAOIBatisImpl.OPTIMISTIC_LOCKING);
		dao.markAsSent(orders, notifications);
	}

	public void testMarkOrdersAsSent_pessimistic() throws Exception {
		List orders = dao.findOrdersToSend();
		List notifications = makeNotifications(orders);
		dao.setLockingMode(OrderDAOIBatisImpl.PESSIMISTIC_LOCKING);
		dao.markAsSent(orders, notifications);
	}

	private List makeNotifications(List orders) {
		List notifications = new ArrayList();
		for (Iterator it = orders.iterator(); it.hasNext();) {
			it.next();
			notifications.add(new NotificationDetails("x", new Date()));
		}
		return notifications;
	}

}