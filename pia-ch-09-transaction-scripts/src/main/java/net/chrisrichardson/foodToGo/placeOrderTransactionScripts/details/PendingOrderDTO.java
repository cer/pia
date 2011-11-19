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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details;

import java.util.*;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;

public class PendingOrderDTO {

    private String pendingOrderId;

    private int state;

    private Address deliveryAddress;

    private Date deliveryTime;

    private RestaurantDTO restaurant;

    private List lineItems = new ArrayList();

    private PendingOrderTotals totals;

    public PendingOrderDTO() {
    }

    public PendingOrderDTO(String pendingOrderId, int state) {
        this.pendingOrderId = pendingOrderId;
        this.state = state;
    }

    public PendingOrderDTO(String pendingOrderId, int state,
            Address deliveryAddress, Date deliveryTime,
            RestaurantDTO restaurant) {
        this(pendingOrderId, state);
        this.deliveryAddress = deliveryAddress;
        this.deliveryTime = deliveryTime;
        this.restaurant = restaurant;
    }

    public boolean equals(Object x) {
        if (!(x instanceof PendingOrderDTO))
            return false;
        PendingOrderDTO other = (PendingOrderDTO) x;
        return safeEquals(pendingOrderId, other.pendingOrderId)
                && safeEquals(deliveryAddress, other.deliveryAddress)
                && safeEquals(deliveryTime, other.deliveryTime)
                && safeEquals(restaurant, other.restaurant)
                && safeEquals(lineItems, other.lineItems);
    }

    private boolean safeEquals(Object x, Object y) {
        return x == null || y == null ? x == y : x.equals(y);
    }

    public int hashCode() {
        // TODO
        return super.hashCode();
    }

    public String getPendingOrderId() {
        return pendingOrderId;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public int getState() {
        return state;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public PaymentInformation getPaymentInformation() {
        return null;
    }

    public List getLineItems() {
        return lineItems;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }

    public void add(PendingOrderLineItemDTO lineItem) {
        lineItems.add(lineItem);
    }

    public void setLineItems(List newLineItems) {
        this.lineItems = newLineItems;
    }

   
    public void setPendingOrderId(String pendingOrderId) {
        this.pendingOrderId = pendingOrderId;
    }

    public String getRestaurantId() {
        return getRestaurant() != null ? getRestaurant().getRestaurantId()
                : null;
    }

    public void setTotals(PendingOrderTotals totals) {
        this.totals = totals;
    }

}