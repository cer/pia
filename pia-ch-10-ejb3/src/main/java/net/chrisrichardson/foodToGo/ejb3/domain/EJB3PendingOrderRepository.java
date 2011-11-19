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
 
package net.chrisrichardson.foodToGo.ejb3.domain;

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;

@Stateless
public class EJB3PendingOrderRepository implements
        PendingOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public EJB3PendingOrderRepository() {
    }

    public EJB3PendingOrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PendingOrder createPendingOrder() {
        PendingOrder po = new PendingOrder();
        entityManager.persist(po);
        return po;
    }

    public PendingOrder findPendingOrder(String pendingOrderId) {
        return (PendingOrder) entityManager.find(
                PendingOrder.class, new Integer(pendingOrderId));
    }

    public PendingOrder findOrCreatePendingOrder(
            String pendingOrderId) {
        if (pendingOrderId == null)
            return createPendingOrder();
        return findPendingOrder(pendingOrderId);
    }

    public String getObjectId(PendingOrder pendingOrder) {
        return null;
    }

    public PendingOrder createPendingOrder(Address deliveryAddress,
            Date deliveryTime, Restaurant restaurant,
            List quantitiesAndMenuItems) {
        return null;
    }

}