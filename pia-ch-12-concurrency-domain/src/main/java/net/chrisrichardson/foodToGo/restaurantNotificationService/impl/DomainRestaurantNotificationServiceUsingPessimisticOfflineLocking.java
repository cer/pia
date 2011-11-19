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
import net.chrisrichardson.foodToGo.util.locking.*;

public class DomainRestaurantNotificationServiceUsingPessimisticOfflineLocking
		implements RestaurantNotificationServiceUsingPessimisticOfflineLocking {

	private RestaurantNotificationGateway notificationGateway;

	private OrderRepository orderRepository;

	private LockManager lockManager;

	public DomainRestaurantNotificationServiceUsingPessimisticOfflineLocking(
			RestaurantNotificationGateway notificationGateway,
			OrderRepository orderRepository, LockManager lockManager) {
		super();
		this.notificationGateway = notificationGateway;
		this.orderRepository = orderRepository;
		this.lockManager = lockManager;
	}

	public boolean sendOrders(String caller) {
		Collection orders = orderRepository.findOrdersToSend();
		Collection lockedOrders = new ArrayList();
		for (Iterator it = orders.iterator(); it.hasNext();) {
			Order order = (Order) it.next();
			if (!lockOrder(caller, order))
				continue;
			lockedOrders.add(order);
			Restaurant restaurant = order.getRestaurant();
			NotificationDetails notificationDetails = notificationGateway
					.sendOrder(order);
			Date timestamp = notificationDetails.getTimestamp();
			String messageId = notificationDetails.getMessageId();
			order.noteSent(messageId, timestamp);
		}
		unlockOrders(caller, lockedOrders);
		return !orders.isEmpty();
	}

	private boolean lockOrder(String caller, Order order) {
		return lockManager.acquireLock(Order.class.getName(), order.getId(),
				caller);
	}

	private void unlockOrders(String caller, Collection orders) {
		for (Iterator it = orders.iterator(); it.hasNext();) {
			Order order = (Order) it.next();
			lockManager.releaseLock(Order.class.getName(), order.getId(),
					caller);
		}

	}
}
