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

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.views.*;

public class PendingOrderAdapter {

    private PendingOrder pendingOrder;

    public PendingOrderAdapter(PendingOrder pendingOrder) {
        this.pendingOrder = pendingOrder;
    }

    public Coupon getCoupon() {
        return pendingOrder.getCoupon();
    }

    public Address getDeliveryAddress() {
        return pendingOrder.getDeliveryAddress();
    }

    public double getDeliveryCharges() {
        return pendingOrder.getDeliveryCharges();
    }

    public Date getDeliveryDate() {
        return pendingOrder.getDeliveryTime();
    }

    public List getLineItems() {
        return pendingOrder.getLineItems();
    }

    public PaymentInformation getPaymentInformation() {
        return pendingOrder.getPaymentInformation();
    }

    public RestaurantDetail getRestaurant() {
        return (RestaurantDetail)pendingOrder.getRestaurant();
    }

    public double getSalesTax() {
        return pendingOrder.getSalesTax();
    }

    public int getState() {
        return pendingOrder.getState();
    }

    public double getSubtotal() {
        return pendingOrder.getSubtotal();
    }

    public double getTotal() {
        return pendingOrder.getTotal();
    }

}
