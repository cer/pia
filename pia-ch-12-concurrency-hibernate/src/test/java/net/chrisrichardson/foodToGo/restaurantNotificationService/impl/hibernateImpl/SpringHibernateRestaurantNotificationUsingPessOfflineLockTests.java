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
 
package net.chrisrichardson.foodToGo.restaurantNotificationService.impl.hibernateImpl;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.hibernate.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.util.locking.*;
import net.chrisrichardson.ormunit.hibernate.*;

import org.springframework.jdbc.core.*;

public class SpringHibernateRestaurantNotificationUsingPessOfflineLockTests
		extends HibernatePersistenceTestsWithResetDatabase {

	private RestaurantNotificationServiceUsingPessimisticOfflineLocking service;

	private JdbcTemplate jdbcTemplate;

	private LockManager lockManager;
	
	private Order order;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	protected String[] getConfigLocations() {
		String[] facadeFiles = {
				"send-orders-local-transaction.xml",
				"send-orders-domain-services.xml", 
				"lock-manager.xml",
				"lock-manager-ibatis-config.xml" };

		return ArrayUtils.concatenate(facadeFiles,
				HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT);
	}


	public void setService(
			RestaurantNotificationServiceUsingPessimisticOfflineLocking service) {
		this.service = service;
	}

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

	protected void onSetUp() throws Exception {
		super.onSetUp();
		jdbcTemplate.execute("DROP TABLE FTGO_LOCK IF EXISTS");
		jdbcTemplate
				.execute("CREATE TABLE FTGO_LOCK(CLASS_ID VARCHAR, PK VARCHAR, OWNER VARCHAR, PRIMARY KEY (CLASS_ID, PK))");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -5);
		Date date = c.getTime();
		order = OrderMother.makeOrder(date);
		save(order.getRestaurant());
		save(order);
	}

	public void test() throws Exception {
		assertFalse(lockManager.verifyLock(Order.class.getName(), order
				.getId(), "FooBar"));
		assertTrue(service.sendOrders("FooBar"));
		assertFalse(lockManager.verifyLock(Order.class.getName(), order
				.getId(), "FooBar"));
		assertFalse(service.sendOrders("FooBar"));
	}

	public void testWithLockedOrder() throws Exception {
		assertTrue(lockManager.acquireLock(Order.class.getName(), order
				.getId(), "Baz"));
		assertTrue(service.sendOrders("FooBar"));
		assertTrue(lockManager.verifyLock(Order.class.getName(), order
				.getId(), "Baz"));
	}
}