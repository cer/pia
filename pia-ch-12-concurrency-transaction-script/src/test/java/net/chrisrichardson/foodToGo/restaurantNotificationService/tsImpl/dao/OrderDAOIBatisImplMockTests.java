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

import java.sql.*;
import java.util.*;
import java.util.Date;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.commonImpl.*;

import org.jmock.cglib.*;
import org.jmock.core.*;
import org.springframework.orm.ibatis.*;

public class OrderDAOIBatisImplMockTests extends MockObjectTestCase {

	private final class Approximately implements Constraint {
		private final Timestamp time;

		private Approximately(Timestamp time) {
			super();
			this.time = time;
		}

		public boolean eval(Object arg) {
			if (!(arg instanceof Timestamp)) return false;
			long diff = Math.abs(((Date)arg).getTime() - time.getTime());
			return diff < 1000;
		}

		public StringBuffer describeTo(StringBuffer arg0) {
			return arg0.append("close to: " + time);
		}
	}

	private Mock mockSqlMapClientTemplate;

	private OrderDAOIBatisImpl dao;

	private OrderDTO order;

	protected void setUp() throws Exception {
		super.setUp();
		mockSqlMapClientTemplate = new Mock(SqlMapClientTemplate.class);
		SqlMapClientTemplate sqlMapClientTemplate = (SqlMapClientTemplate) mockSqlMapClientTemplate
				.proxy();
		dao = new OrderDAOIBatisImpl(sqlMapClientTemplate);
		order = new OrderDTO("1", 2);
	}

	public void testFindOrdersToSend_NO_LOCKING() throws Exception {
		final Timestamp cutoffTime = dao.calculateCutOffTime();
		List orders = new ArrayList();
		mockSqlMapClientTemplate.expects(once()).method("queryForList").with(
				eq("findOrdersToSend_ISOLATED_TRANSACTION"), new Approximately(cutoffTime)).will(
				returnValue(orders));
		List result = dao.findOrdersToSend();
		assertSame(orders, result);

	}

	public void testFindOrdersToSend_OPTIMISTIC_LOCKING() throws Exception {
		Timestamp cutoffTime = dao.calculateCutOffTime();
		List orders = new ArrayList();
		mockSqlMapClientTemplate.expects(once()).method("queryForList").with(
				eq("findOrdersToSend_OPTIMISTIC_LOCKING"), new Approximately(cutoffTime))
				.will(returnValue(orders));
		dao.setLockingMode(OrderDAOIBatisImpl.OPTIMISTIC_LOCKING);
		List result = dao.findOrdersToSend();
		assertSame(orders, result);

	}

	public void testFindOrdersToSend_PESSIMISTIC_LOCKING() throws Exception {
		Timestamp cutoffTime = dao.calculateCutOffTime();
		List orders = new ArrayList();
		mockSqlMapClientTemplate.expects(once()).method("queryForList").with(
				eq("findOrdersToSend_PESSIMISTIC_LOCKING"), new Approximately(cutoffTime))
				.will(returnValue(orders));
		dao.setLockingMode(OrderDAOIBatisImpl.PESSIMISTIC_LOCKING);
		List result = dao.findOrdersToSend();
		assertSame(orders, result);

	}

	public void testMarkOrdersAsSent() throws Exception {
		NotificationDetails notification = new NotificationDetails("msgId",
				new Date());

		List orders = Collections.singletonList(order);
		List notifications = Collections.singletonList(notification);

		MarkOrdersAsSentCallback callback = new MarkOrdersAsSentCallback(
				orders, notifications, OrderDAOIBatisImpl.ISOLATED_TRANSACTION);

		mockSqlMapClientTemplate.expects(once()).method("execute").with(
				eq(callback));

		dao.markAsSent(orders, notifications);
	}

}