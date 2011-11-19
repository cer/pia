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
 
package net.chrisrichardson.foodToGo.pojoFacade;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.views.*;

/**
 * DTO returned by the PlaceOrderFacade
 * @author cer
 *
 */
public class PlaceOrderFacadeResult {

    private int statusCode;
    private PendingOrderDetail pendingOrder;
    private List restaurants;
    private Order order;

    public PlaceOrderFacadeResult(
        int statusCode,
        PendingOrderDetail pendingOrder,
        List restaurants) {
        this.statusCode = statusCode;
        this.pendingOrder = pendingOrder;
        this.restaurants = restaurants;
    }

    public PlaceOrderFacadeResult(
        int statusCode,
        PendingOrderDetail pendingOrder) {
        this(statusCode, pendingOrder, null);
    }

    public PlaceOrderFacadeResult(int statusCode, Order order) {
        this.statusCode = statusCode;
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
    public int getStatusCode() {
        return statusCode;
    }

    public PendingOrderDetail getPendingOrder() {
        return pendingOrder;
    }

    public List getRestaurants() {
        return restaurants;
    }

}
