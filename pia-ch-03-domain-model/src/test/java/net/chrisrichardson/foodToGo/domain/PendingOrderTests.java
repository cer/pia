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
 
package net.chrisrichardson.foodToGo.domain;

import java.util.*;

import net.chrisrichardson.foodToGo.util.*;

import org.jmock.cglib.*;

public class PendingOrderTests extends MockObjectTestCase {

	private Restaurant restaurant;

	private Mock mockRestaurant;

	private Date goodDeliveryTime;

	private Address goodDeliveryAddress;

	private RestaurantRepository restaurantRepository;

	private Mock mockRestaurantRepository;

	private PendingOrder pendingOrder;

	protected void setUp() throws Exception {
		super.setUp();
		pendingOrder = new PendingOrder();
		mockRestaurantRepository = new Mock(RestaurantRepository.class);
		restaurantRepository = (RestaurantRepository) mockRestaurantRepository
				.proxy();

		goodDeliveryAddress = RestaurantTestData.getADDRESS1();
		goodDeliveryTime = RestaurantTestData.makeGoodDeliveryTime();

		mockRestaurant = new Mock(Restaurant.class);
		restaurant = (Restaurant) mockRestaurant.proxy();

	}

	public void testUpdateDeliveryInfo_good() throws Exception {

		mockRestaurantRepository.expects(once())
				.method("isRestaurantAvailable").with(eq(goodDeliveryAddress),
						eq(goodDeliveryTime)).will(returnValue(true));

		assertEquals(PlaceOrderStatusCodes.OK, pendingOrder.updateDeliveryInfo(
				restaurantRepository, goodDeliveryAddress, goodDeliveryTime,
				false));

		assertSame(goodDeliveryAddress, pendingOrder.getDeliveryAddress());
		assertSame(goodDeliveryTime, pendingOrder.getDeliveryTime());

	}

	public void testUpdateDeliveryInfo_notInFuture() {
		Date timeNotInFuture = new Date();
		int result = pendingOrder.updateDeliveryInfo(restaurantRepository,
				goodDeliveryAddress, timeNotInFuture, false);
		assertEquals(PlaceOrderStatusCodes.INVALID_DELIVERY_INFO, result);

		assertNull(pendingOrder.getDeliveryAddress());
		assertNull(pendingOrder.getDeliveryTime());
	}

	public void testUpdateDeliveryInfo_bad() throws Exception {
		mockRestaurantRepository.expects(once())
				.method("isRestaurantAvailable").with(eq(goodDeliveryAddress),
						eq(goodDeliveryTime)).will(returnValue(false));

		assertEquals(PlaceOrderStatusCodes.INVALID_DELIVERY_INFO, pendingOrder
				.updateDeliveryInfo(restaurantRepository, goodDeliveryAddress,
						goodDeliveryTime, false));

		assertNull(pendingOrder.getDeliveryAddress());
		assertNull(pendingOrder.getDeliveryTime());
	}

	public void testUpdateRestaurant_good() throws Exception {
		testUpdateDeliveryInfo_good();

		mockRestaurant.expects(once()).method("isInServiceArea").with(
				eq(goodDeliveryAddress), eq(goodDeliveryTime)).will(
				returnValue(true));

		assertTrue(pendingOrder.updateRestaurant(restaurant));
		assertSame(restaurant, pendingOrder.getRestaurant());
	}

	public void testUpdateRestaurant_bad() throws Exception {
		testUpdateDeliveryInfo_good();

		mockRestaurant.expects(once()).method("isInServiceArea").with(
				eq(goodDeliveryAddress), eq(goodDeliveryTime)).will(
				returnValue(false));

		assertFalse(pendingOrder.updateRestaurant(restaurant));
		assertNull(pendingOrder.getRestaurant());
	}

	public void testUpdateQuantities() throws Exception {
		testUpdateRestaurant_good();

		MenuItem mi1 = new MenuItem("X", 10);
		MenuItem mi2 = new MenuItem("Y", 8);
		MenuItem mi3 = new MenuItem("Z", 15);
		List menuItems = new ArrayList();
		menuItems.add(mi1);
		menuItems.add(mi2);
		menuItems.add(mi3);

		mockRestaurant.expects(once()).method("getMenuItems").withNoArguments()
				.will(returnValue(menuItems));

		pendingOrder.updateQuantities(new int[] { 1, 0, 2 });

		List lineItems = (List) pendingOrder.getLineItems();
		assertEquals(2, lineItems.size());
		PendingOrderLineItem lineItem1 = (PendingOrderLineItem) lineItems
				.get(0);
		PendingOrderLineItem lineItem2 = (PendingOrderLineItem) lineItems
				.get(1);

		assertEquals(1, lineItem1.getQuantity());
		assertEquals(lineItem1.getMenuItem(), mi1);

		assertEquals(2, lineItem2.getQuantity());
		assertEquals(lineItem2.getMenuItem(), mi3);

	}

}