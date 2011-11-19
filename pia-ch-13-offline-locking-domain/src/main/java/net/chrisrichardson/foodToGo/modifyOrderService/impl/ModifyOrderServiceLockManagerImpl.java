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

/**
 * Implementation of the ModifyOrderService that uses the LockManager
 * @author cer
 *
 */
public class ModifyOrderServiceLockManagerImpl implements
        ModifyOrderService {

    private PendingOrderRepository pendingOrderRepository;

    private OrderRepository orderRepository;

    private LockManager lockManager;

    private RestaurantRepository restaurantRepository;

    public ModifyOrderServiceLockManagerImpl(
            OrderRepository orderRepository,
            PendingOrderRepository pendingOrderRepository,
            RestaurantRepository restaurantRepository,
            LockManager lockManager) {
        this.pendingOrderRepository = pendingOrderRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.lockManager = lockManager;
    }

    public ModifyOrderServiceResult getOrderToModify(String caller,
            String orderId) {
        if (lockManager.acquireLock(Order.class.getName(), orderId,
                caller)) {
            Order order = orderRepository.findOrder(orderId);
            PendingOrder pendingOrder = createPendingOrder(order);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, pendingOrder);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.ALREADY_LOCKED);

        }
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
            String caller, String orderId, String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {

        if (lockManager.verifyLock(Order.class.getName(), orderId,
                caller)) {
            PendingOrder pendingOrder = pendingOrderRepository
                    .findPendingOrder(pendingOrderId);
            int result = pendingOrder.updateDeliveryInfo(
                    restaurantRepository, deliveryAddress, deliveryTime,
                    false);
            // FIXME Deal with result
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, pendingOrder);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

    public ModifyOrderServiceResult updateQuantities(String caller,
            String orderId, String pendingOrderId, int[] quantities)
            throws InvalidPendingOrderStateException {
        if (lockManager.verifyLock(Order.class.getName(), orderId,
                caller)) {
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
        if (lockManager.verifyLock(Order.class.getName(), orderId,
                caller)) {
            Order order = orderRepository.findOrder(orderId);
            PendingOrder pendingOrder = pendingOrderRepository
                    .findPendingOrder(pendingOrderId);

            order.modify(pendingOrder.getDeliveryAddress(),
                    pendingOrder.getDeliveryTime(), pendingOrder
                            .getRestaurant(), pendingOrder
                            .getPaymentInformation(),
                    (List) pendingOrder.getLineItems());

            lockManager.releaseLock(Order.class.getName(), orderId,
                    caller);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, order);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

    public ModifyOrderServiceResult cancelModifyOrder(
            String caller, String orderId, String pendingOrderId) {
        if (lockManager.verifyLock(Order.class.getName(), orderId,
                caller)) {
            Order order = orderRepository.findOrder(orderId);
            lockManager.releaseLock(Order.class.getName(), orderId,
                    caller);
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.OK, order);
        } else {
            return new ModifyOrderServiceResult(
                    ModifyOrderServiceResult.NOT_LOCKED);

        }
    }

}