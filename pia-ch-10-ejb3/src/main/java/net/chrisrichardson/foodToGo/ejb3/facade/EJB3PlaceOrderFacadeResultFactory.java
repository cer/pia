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
 
package net.chrisrichardson.foodToGo.ejb3.facade;

import java.util.*;

import javax.ejb.*;

import net.chrisrichardson.foodToGo.ejb3.domain.*;

@Stateless
public class EJB3PlaceOrderFacadeResultFactory
        implements PlaceOrderFacadeResultFactory {

    public EJB3PlaceOrderFacadeResultFactory() {

    }

    public PlaceOrderFacadeResult make(int statusCode,
            PendingOrder pendingOrder, List restaurants) {
        initializePendingOrder(pendingOrder);
        return new PlaceOrderFacadeResult(statusCode,
                pendingOrder, restaurants);
    }

    public PlaceOrderFacadeResult make(int statusCode,
            PendingOrder pendingOrder) {
        initializePendingOrder(pendingOrder);
        return new PlaceOrderFacadeResult(statusCode,
                pendingOrder);
    }

    private void initializePendingOrder(
            PendingOrder pendingOrder) {
        Restaurant restaurant = pendingOrder
                .getRestaurant();
        if (restaurant != null) {
            MenuItem menuItem = restaurant
                    .getMenuItems().get(0);
            menuItem.getName();
        }
        List<PendingOrderLineItem> lineItems = pendingOrder
                .getLineItems();
        if (lineItems != null && !lineItems.isEmpty()) {
            lineItems.get(0);
        }
    }

}
