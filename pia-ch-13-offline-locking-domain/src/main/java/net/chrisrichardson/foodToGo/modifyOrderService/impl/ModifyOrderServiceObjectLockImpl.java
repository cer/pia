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

/**
 * Implementation of the ModifyOrderService that uses an object lock
 * @author cer
 *
 */public class ModifyOrderServiceObjectLockImpl implements
        ModifyOrderService {

    private PendingOrderRepository pendingOrderRepository;

    private OrderRepository orderRepository;

    private RestaurantRepository restaurantRepository;

    public ModifyOrderServiceObjectLockImpl(
            OrderRepository orderRepository,
            PendingOrderRepository pendingOrderRepository,
            RestaurantRepository restaurantRepository) {
        this.pendingOrderRepository = pendingOrderRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public ModifyOrderServiceResult getOrderToModify(String caller, String orderId) {
        Order order = orderRepository.findOrder(orderId);
        if (order.acquireLock(caller)) {
            PendingOrder pendingOrder = createPendingOrder(order);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, pendingOrder);
        } else
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.ALREADY_LOCKED);
    }

    protected PendingOrder createPendingOrder(Order order) {
        Address deliveryAddress = order.getDeliveryAddress();
        Date deliveryTime = order.getDeliveryTime();
        Restaurant restaurant = order.getRestaurant();
        List orderLineItems = new ArrayList();
        orderLineItems.addAll(order.getLineItems());
        PendingOrder pendingOrder = pendingOrderRepository
                .createPendingOrder(deliveryAddress, deliveryTime,
                        restaurant, orderLineItems);
        return pendingOrder;
    }

    public ModifyOrderServiceResult updateDeliveryInfo(
            String caller, String orderId,
            String pendingOrderId, Address deliveryAddress, Date deliveryTime) {

        Order order = orderRepository.findOrder(orderId);
        if (order.verifyLock(caller)) {
            PendingOrder pendingOrder = pendingOrderRepository
                    .findPendingOrder(pendingOrderId);
            int result = pendingOrder.updateDeliveryInfo(
                    restaurantRepository, deliveryAddress, deliveryTime,
                    false);
            // FIXME deal with result
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, pendingOrder);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

    public ModifyOrderServiceResult updateQuantities(
            String caller, String orderId, String pendingOrderId, int[] quantities)
            throws InvalidPendingOrderStateException {
        Order order = orderRepository.findOrder(orderId);
        if (order.verifyLock(caller)) {
            PendingOrder pendingOrder = pendingOrderRepository
                    .findPendingOrder(pendingOrderId);
            pendingOrder.updateQuantities(quantities);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, pendingOrder);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

    public ModifyOrderServiceResult saveChangesToOrder(
            String caller, String orderId, String pendingOrderId) {
        Order order = orderRepository.findOrder(orderId);
        if (order.verifyLock(caller)) {
            PendingOrder pendingOrder = pendingOrderRepository
                    .findPendingOrder(pendingOrderId);

            order.modify(pendingOrder.getDeliveryAddress(),
                    pendingOrder.getDeliveryTime(), pendingOrder
                            .getRestaurant(), pendingOrder
                            .getPaymentInformation(),
                    (List) pendingOrder.getLineItems());

            order.releaseLock(caller);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, order);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

    public ModifyOrderServiceResult cancelModifyOrder(
            String caller, String orderId, String pendingOrderId) {
        Order order = orderRepository.findOrder(orderId);
        if (order.verifyLock(caller)) {
            order.verifyLock(caller);
            order.releaseLock(caller);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, order);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

}