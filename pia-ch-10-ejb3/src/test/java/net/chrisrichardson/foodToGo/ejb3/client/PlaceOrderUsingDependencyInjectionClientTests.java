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
 
package net.chrisrichardson.foodToGo.ejb3.client;

import java.util.*;

import net.chrisrichardson.foodToGo.ejb3.domain.*;
import net.chrisrichardson.foodToGo.ejb3.facade.*;

/**
 * Tests the PlaceOrderFacade deployed in JBoss
 * 
 * @author cer
 * 
 */
public class PlaceOrderUsingDependencyInjectionClientTests extends EJB3TestCase {

	public void testPlaceOrderUsingDependencyInjectionInServer()
			throws Exception {
		PlaceOrderFacade facade = (PlaceOrderFacade) ctx
				.lookup(PlaceOrderFacadeUsingDependencyInjection.class
						.getName());
		Address address = new Address("1 somewhere", null, "Oakland", "CA",
				"94619");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 20);
		c.set(Calendar.MINUTE, 0);
		c.clear(Calendar.MILLISECOND);
		Date during = c.getTime();
		Date time = during;

		PlaceOrderFacadeResult result = facade.updateDeliveryInfo(null,
				address, time);

		checkResult(result);


		result = facade.updateRestaurant(result.getPendingOrder().getId(), "1");

		checkResult(result);

		result = facade.updateQuantities(result.getPendingOrder().getId(),
				new int[] { 0, 3 });
		checkResult(result);

		result = facade.updateQuantities(result.getPendingOrder().getId(),
				new int[] { 2, 0 });
		checkResult(result);

	}

	private static void checkResult(PlaceOrderFacadeResult result) {
		assertEquals(PlaceOrderStatusCodes.OK, result.getStatusCode());
		PendingOrder pendingOrder = result.getPendingOrder();
		traverseRestaurantAndMenuItems(pendingOrder.getRestaurant());
		traverseLineItems(pendingOrder.getLineItems());
	}

	private static void traverseRestaurantAndMenuItems(Restaurant restaurant) {
		if (restaurant != null) {
			List<MenuItem> menuItems = restaurant.getMenuItems();
			for (MenuItem menuItem : menuItems) {
				menuItem.getName();
			}
		}
	}

	private static void traverseLineItems(List<PendingOrderLineItem> lineItems) {
		if (lineItems != null) {
			for (PendingOrderLineItem lineItem : lineItems) {
				lineItem.getMenuItem().getName();

			}
		}
	}
}