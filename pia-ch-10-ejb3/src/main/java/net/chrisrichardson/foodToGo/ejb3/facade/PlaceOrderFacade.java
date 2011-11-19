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

import net.chrisrichardson.foodToGo.ejb3.domain.*;

public interface PlaceOrderFacade {
    public PlaceOrderFacadeResult updateDeliveryInfo(
            String pendingOrderId, Address deliveryAddress,
            Date deliveryTime);

    public PlaceOrderFacadeResult updateRestaurant(
            String pendingOrderId, String restaurantId);

    public PlaceOrderFacadeResult updateQuantities(
            String pendingOrderId, int[] quantities);

    public PlaceOrderFacadeResult checkout(String pendingOrderId);

    public PlaceOrderFacadeResult updatePaymentInformation(
            String pendingOrderId,
            PaymentInformation paymentInformation, String couponCode);

//    public PlaceOrderFacadeResult placeOrder(String pendingOrderId);

    public PlaceOrderFacadeResult confirmDeliveryInfoChange(
            String pendingOrderId, Address deliveryAddress,
            Date deliveryTime);

    public PlaceOrderFacadeResult getPendingOrder(String pendingOrderId);
}