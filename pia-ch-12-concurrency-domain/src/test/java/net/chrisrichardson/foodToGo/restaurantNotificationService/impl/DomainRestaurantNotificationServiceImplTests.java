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
 
package net.chrisrichardson.foodToGo.restaurantNotificationService.impl;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.commonImpl.*;

import org.jmock.cglib.*;

public class DomainRestaurantNotificationServiceImplTests extends
        MockObjectTestCase {

    RestaurantNotificationService service;

    private Mock mockOrderRepository;

    private Mock mockRestaurantNotificationGateway;

	private Mock mockOrder;

	private Mock mockRestaurant;

    protected void setUp() throws Exception {
        super.setUp();
        mockOrderRepository = new Mock(OrderRepository.class);
        OrderRepository orderRepository = (OrderRepository) mockOrderRepository
                .proxy();
        mockRestaurantNotificationGateway = new Mock(
                RestaurantNotificationGateway.class);

        mockOrder = new Mock(Order.class);

        mockRestaurant = new Mock(Restaurant.class);

        RestaurantNotificationGateway restaurantNotificationGateway = (RestaurantNotificationGateway) mockRestaurantNotificationGateway
                .proxy();
        service = new DomainRestaurantNotificationService(
                orderRepository, restaurantNotificationGateway);
    }

    public void test() throws Exception {
		Order order = (Order) mockOrder.proxy();
        List ordersToSend = Collections.singletonList(order);
        mockOrderRepository.expects(once()).method(
                "findOrdersToSend").will(returnValue(ordersToSend));
		Restaurant restaurant = (Restaurant) mockRestaurant.proxy();
        Date messageTime = new Date();
        String messageId = "messageId";
        NotificationDetails notificationDetails = new NotificationDetails(
                messageId, messageTime);
        mockRestaurantNotificationGateway.expects(once()).method(
                "sendOrder").with(eq(order)).will(
                returnValue(notificationDetails));
        mockOrder.expects(once()).method("noteSent").with(
                eq(messageId), eq(messageTime));

        service.sendOrders();
    }
}