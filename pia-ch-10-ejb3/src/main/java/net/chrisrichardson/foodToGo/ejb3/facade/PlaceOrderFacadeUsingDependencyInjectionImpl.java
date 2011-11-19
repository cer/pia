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

import javax.annotation.*;
import javax.ejb.*;

import net.chrisrichardson.foodToGo.ejb3.domain.*;
import net.chrisrichardson.foodToGo.ejb3.domain.Address;
import net.chrisrichardson.foodToGo.util.*;

@Stateless
public class PlaceOrderFacadeUsingDependencyInjectionImpl
        implements
        PlaceOrderFacadeUsingDependencyInjection {
    @EJB
    private RestaurantRepository restaurantRepository;

    @EJB
    private PlaceOrderFacadeResultFactory resultFactory;

    @EJB
    private PlaceOrderService service;

    public PlaceOrderFacadeUsingDependencyInjectionImpl() {
    }

    public PlaceOrderFacadeUsingDependencyInjectionImpl(
            RestaurantRepository restaurantRepository,
            PlaceOrderService service,
            PlaceOrderFacadeResultFactory resultFactory) {
        this.restaurantRepository = restaurantRepository;
        this.service = service;
        this.resultFactory = resultFactory;
    }

    public PlaceOrderFacadeResult updateDeliveryInfo(
            String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {
        PlaceOrderServiceResult result = service
                .updateDeliveryInfo(pendingOrderId,
                        deliveryAddress, deliveryTime);
        PendingOrder pendingOrder = result
                .getPendingOrder();
        if (result.getStatusCode() == PlaceOrderStatusCodes.OK) {
            List restaurants = (List) restaurantRepository
                    .findAvailableRestaurants(
                            deliveryAddress,
                            deliveryTime);
            // ...
            return resultFactory.make(
                    PlaceOrderStatusCodes.OK,
                    pendingOrder, restaurants);
        } else {
            return resultFactory
                    .make(
                            PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                            pendingOrder, null);
        }
    }

    public PlaceOrderFacadeResult updateRestaurant(
            String pendingOrderId, String restaurantId) {
        PlaceOrderServiceResult result = service
                .updateRestaurant(pendingOrderId,
                        restaurantId);
        return resultFactory.make(
                PlaceOrderStatusCodes.OK, result
                        .getPendingOrder());

    }

    public PlaceOrderFacadeResult updateQuantities(
            String pendingOrderId, int[] quantities) {
        PlaceOrderServiceResult result = service
                .updateQuantities(pendingOrderId,
                        quantities);
        return resultFactory.make(
                PlaceOrderStatusCodes.OK, result
                        .getPendingOrder(), null);
    }

    public PlaceOrderFacadeResult checkout(
            String pendingOrderId) {
        PlaceOrderServiceResult result = service
                .checkout(pendingOrderId);
        return resultFactory.make(
                PlaceOrderStatusCodes.OK, result
                        .getPendingOrder(), null);
    }

    public PlaceOrderFacadeResult updatePaymentInformation(
            String pendingOrderId,
            PaymentInformation paymentInformation,
            String couponCode) {
        PlaceOrderServiceResult result = service
                .updatePaymentInformation(
                        pendingOrderId,
                        paymentInformation, couponCode);
        return resultFactory.make(
                PlaceOrderStatusCodes.OK, result
                        .getPendingOrder(), null);
    }

    // public PlaceOrderFacadeResult placeOrder(String pendingOrderId) {
    // PlaceOrderServiceResult result = service
    // .placeOrder(pendingOrderId);
    // return resultFactory.make(PlaceOrderStatusCodes.OK, result
    // .getPendingOrder(), null);
    // }

    public PlaceOrderFacadeResult confirmDeliveryInfoChange(
            String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {
        throw new NotYetImplementedException(); // FIXME
    }

    public PlaceOrderFacadeResult getPendingOrder(
            String pendingOrderId) {
        throw new NotYetImplementedException(); // FIXME
    }

}