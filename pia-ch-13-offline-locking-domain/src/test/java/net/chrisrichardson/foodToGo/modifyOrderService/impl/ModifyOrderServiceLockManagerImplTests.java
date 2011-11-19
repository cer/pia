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
 
package net.chrisrichardson.foodToGo.modifyOrderService.impl;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.modifyOrderService.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.util.locking.*;

import org.jmock.cglib.*;

public class ModifyOrderServiceLockManagerImplTests extends MockObjectTestCase {

	private ModifyOrderService service;

	private Mock mockLockManager;

	private Mock mockRestaurantRepository;

	private Mock mockPendingOrderRepository;

	private Mock mockOrderRepository;

	private String caller = "caller";

	String orderId = "99";

	private Mock mockOrderFromRepository;

	private Order orderFromRepository;

	private PendingOrder pendingOrder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mockLockManager = new Mock(LockManager.class);

		LockManager lockManager = (LockManager) mockLockManager.proxy();
		mockRestaurantRepository = new Mock(RestaurantRepository.class);

		RestaurantRepository restaurantRepository = (RestaurantRepository) mockRestaurantRepository
				.proxy();
		mockPendingOrderRepository = new Mock(PendingOrderRepository.class);

		PendingOrderRepository pendingOrderRepository = (PendingOrderRepository) mockPendingOrderRepository
				.proxy();
		mockOrderRepository = new Mock(OrderRepository.class);

		OrderRepository orderRepository = (OrderRepository) mockOrderRepository
				.proxy();

		service = new ModifyOrderServiceLockManagerImpl(orderRepository,
				pendingOrderRepository, restaurantRepository, lockManager);

		mockOrderFromRepository = new Mock(Order.class);

		orderFromRepository = (Order) mockOrderFromRepository.proxy();

		pendingOrder = new PendingOrder();
	}

	public void testGetOrderToMotify() {

		mockLockManager.expects(once()).method("acquireLock").with(
				eq(Order.class.getName()), eq(orderId), eq(caller)).will(
				returnValue(true));
		mockOrderRepository.expects(once()).method("findOrder").with(
				eq(orderId)).will(returnValue(orderFromRepository));

		Address deliveryAddress = RestaurantTestData.getADDRESS1();
		Date deliveryTime = RestaurantTestData.makeGoodDeliveryTime();
		Restaurant restaurant = new Restaurant();
		List<OrderLineItem> lineItems = Collections
				.singletonList(new OrderLineItem());

		mockOrderFromRepository.expects(once()).method("getDeliveryAddress")
				.will(returnValue(deliveryAddress));
		mockOrderFromRepository.expects(once()).method("getDeliveryTime").will(
				returnValue(deliveryTime));
		mockOrderFromRepository.expects(once()).method("getRestaurant").will(
				returnValue(restaurant));
		mockOrderFromRepository.expects(once()).method("getLineItems").will(
				returnValue(lineItems));

		mockPendingOrderRepository.expects(once()).method("createPendingOrder")
				.with(eq(deliveryAddress), eq(deliveryTime), eq(restaurant),
						eq(lineItems)).will(returnValue(pendingOrder));

		ModifyOrderServiceResult result = service.getOrderToModify(caller,
				orderId);

		assertEquals(ModifyOrderServiceResult.OK, result.getStatusCode());
		assertSame(pendingOrder, result.getPendingOrder());

	}

	// TODO - write some more tests

}
