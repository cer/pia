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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService.hibernate;

import java.util.*;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.*;
import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.locking.*;
import net.chrisrichardson.ormunit.hibernate.*;

import org.springframework.jdbc.core.*;
import org.springframework.orm.*;

// FIXME - this is not Hibernate-specific
// Introduce a transaction superclass and move doWithTransaction into that.

public class SpringHibernateDetachingAcknowledgeOrderServiceWithLockTests
		extends HibernatePersistenceTests {

	private DetachingAcknowledgeOrderServiceWithLock service;

	private Order order;

	private String orderId;

	private JdbcTemplate jdbcTemplate;

	private LockManager lockManager;

	private static final String CALLER = "FOO_USER";

    @Override
    protected String[] getConfigLocations() {
        return OfflineLockingConstants.OFFLINE_LOCKING_CONTEXT;
    }

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setService(DetachingAcknowledgeOrderServiceWithLock service) {
		this.service = service;
	}

	protected void onSetUp() throws Exception {
		super.onSetUp();
		// FIXME This is duplicated
		jdbcTemplate.execute("DROP TABLE FTGO_LOCK IF EXISTS");
		jdbcTemplate
				.execute("CREATE TABLE FTGO_LOCK(CLASS_ID VARCHAR, PK VARCHAR, OWNER VARCHAR, PRIMARY KEY (CLASS_ID, PK))");
		order = OrderMother.makeOrderWithDeliveryTimeInPast();
		save(order.getRestaurant());
		order.noteSent("SomeMessageId", new Date());
		save(order);
		this.orderId = order.getId();
	}

	public void test() throws Exception {

		logger.debug("---- getting order to acknowledge");
		AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
		assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());
		order = (Order) ar.getOrderValue();
		OrderLineItem lineItem = (OrderLineItem) order.getLineItems().get(0);
		assertEquals(RestaurantMother.SAMOSAS, lineItem.getMenuItem().getName());

		logger.debug("Original version number = " + order.getVersion());

		order.accept("My Notes");

		logger.debug("Original version number = " + order.getVersion());
		logger.debug("---- acknowledging order");

		ar = service.acknowledgeOrder(CALLER, order);
		assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());

		logger.debug("final version number = "
				+ ((Order) ar.getOrderValue()).getVersion());
		order = (Order) load(Order.class, orderId);
		assertEquals(Order.ACCEPTED, order.getState());
		assertEquals("My Notes", order.getNotes());
		logger.debug("db version number = " + order.getVersion());
	}

	public void testFailure() throws Exception {

		AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
		order = (Order) ar.getOrderValue();

		ar = service.getOrderToAcknowledge(orderId);
		Order order2 = (Order) ar.getOrderValue();

		order.accept("My Notes");

		ar = service.acknowledgeOrder(CALLER, order);
		assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());

		try {
			ar = service.acknowledgeOrder(CALLER, order2);
			fail("expected failure");
		} catch (ObjectOptimisticLockingFailureException e) {
			assertEquals(new Integer(order.getId()), e.getIdentifier());
		}
		assertFalse(lockManager.isLocked(Order.class.getName(), orderId));
	}

	public void test_AlreadyLocked() throws Exception {

		logger.debug("---- getting order to acknowledge");
		assertTrue(lockManager.acquireLock(Order.class.getName(), orderId, "Baz"));
		AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
		assertEquals(AcknowledgeOrderResult.LOCKED, ar.getStatus());
		order = (Order) ar.getOrderValue();
		OrderLineItem lineItem = (OrderLineItem) order.getLineItems().get(0);
		assertEquals(RestaurantMother.SAMOSAS, lineItem.getMenuItem().getName());
	}
	
	public void test_becomesLocked() throws Exception {

		logger.debug("---- getting order to acknowledge");
		AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
		assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());
		order = (Order) ar.getOrderValue();
		OrderLineItem lineItem = (OrderLineItem) order.getLineItems().get(0);
		assertEquals(RestaurantMother.SAMOSAS, lineItem.getMenuItem().getName());

		logger.debug("Original version number = " + order.getVersion());

		order.accept("My Notes");

		assertTrue(lockManager.acquireLock(Order.class.getName(), orderId, "Baz"));

		logger.debug("Original version number = " + order.getVersion());
		logger.debug("---- acknowledging order");

		ar = service.acknowledgeOrder(CALLER, order);
		assertEquals(AcknowledgeOrderResult.LOCKED, ar.getStatus());
	}

}