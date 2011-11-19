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

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.hibernate.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.ormunit.hibernate.*;

/**
 * @author cer
 * 
 */
public class SpringHibernateDomainRestaurantNotificationServiceTests extends
		HibernatePersistenceTests {

	private RestaurantNotificationService service;

	private Order order1;

	private Order order2;

	@Override
	protected String[] getConfigLocations() {
		String[] facadeFiles = {
				"send-orders-local-transaction.xml",
				"send-orders-domain-services.xml",
				};

		return ArrayUtils.concatenate(facadeFiles,
				HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT);
	}

	public void setService(RestaurantNotificationService service) {
		this.service = service;
	}

	protected void onSetUp() throws Exception {
		order1 = OrderMother.makeOrderWithDeliveryTimeInPast();
		order2 = OrderMother.makeOrder(order1.getRestaurant());
		hibernateTemplate.save(order1.getRestaurant());
		hibernateTemplate.save(order1);
		hibernateTemplate.save(order2);
	}

	private void assertOrderState(String id, String expectedState) {
		Order o = (Order) load(Order.class, id);
		assertEquals(expectedState, o.getState());
	}

	public void testSendOrders() throws Exception {
		assertTrue(service.sendOrders());
		assertOrderState(order1.getId(), Order.SENT);
		assertOrderState(order2.getId(), Order.PLACED);
	}

}