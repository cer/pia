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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService.impl;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.*;
import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.locking.*;

public class DetachingAcknowledgeOrderServiceWithLockImpl implements
        DetachingAcknowledgeOrderServiceWithLock {

    OrderRepository orderRepository;

    OrderAttachmentManager attachmentManager;

    LockManager lockManager;

    public DetachingAcknowledgeOrderServiceWithLockImpl(
            OrderRepository orderRepository,
            OrderAttachmentManager attachmentManager,
            LockManager lockManager) {
        this.orderRepository = orderRepository;
        this.attachmentManager = attachmentManager;
        this.lockManager = lockManager;
    }

    public AcknowledgeOrderResult getOrderToAcknowledge(
            String orderId) {
        Order order = orderRepository.findOrder(orderId);
        Order detachedOrder = attachmentManager.detach(order);
        if (order.isAcknowledgable()) {
            if (lockManager.isLocked(Order.class.getName(), order.getId()))
                return new AcknowledgeOrderResult(
                        AcknowledgeOrderResult.LOCKED,
                        detachedOrder);
            else
                return new AcknowledgeOrderResult(
                        AcknowledgeOrderResult.OK, detachedOrder);
        } else
            return new AcknowledgeOrderResult(
                    AcknowledgeOrderResult.ILLEGAL_STATE,
                    detachedOrder);
    }

    public AcknowledgeOrderResult verifyOrderUnchanged(
            Order detachedOrder) {
        Order order = attachmentManager.attach(detachedOrder);
        Order detachedOrder2 = attachmentManager.detach(order);
        return new AcknowledgeOrderResult(
                AcknowledgeOrderResult.OK, detachedOrder2);
    }

    // TODO - do some validation - is the order really placed->acknowledged?
    // TODO - do some more business logic
    // TODO Doing this again gets a disconnected session for the menu items
    // Order detachedOrder2 = attachmentManager.detach(order);

    public AcknowledgeOrderResult acknowledgeOrder(
            String caller, Order detachedOrder) {
        if (!lockManager.acquireLock(Order.class.getName(),
                detachedOrder.getId(), caller)) {
            return new AcknowledgeOrderResult(
                    AcknowledgeOrderResult.LOCKED, detachedOrder);
        }
        try {
            Order order = attachmentManager.attach(detachedOrder);
            Order detachedOrder2 = attachmentManager.detach(order);
            return new AcknowledgeOrderResult(
                    AcknowledgeOrderResult.OK, detachedOrder2);
        } finally {
            lockManager.releaseLock(Order.class.getName(),
                    detachedOrder.getId(), caller);
        }
    }

}