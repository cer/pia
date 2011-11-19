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

/**
 * Implementation of the POJO facade for the Place Order use case
 * @author cer
 *
 */
public class PlaceOrderFacadeImpl implements PlaceOrderFacade {
    private RestaurantRepository restaurantRepository;

    private PlaceOrderFacadeResultFactory resultFactory;

    private PlaceOrderService service;

    public PlaceOrderFacadeImpl(PlaceOrderService service,
            RestaurantRepository restaurantRepository,
            PlaceOrderFacadeResultFactory resultFactory) {
        this.service = service;
        this.restaurantRepository = restaurantRepository;
        this.resultFactory = resultFactory;
    }

    public PlaceOrderFacadeResult updateDeliveryInfo(
            String pendingOrderId, Address deliveryAddress,
            Date deliveryTime) {
        PlaceOrderServiceResult result = service
                .updateDeliveryInfo(pendingOrderId,
                        deliveryAddress, deliveryTime);
        PendingOrder pendingOrder = result.getPendingOrder();
        if (result.getStatusCode() == PlaceOrderStatusCodes.OK) {
            List restaurants = (List) restaurantRepository
                    .findAvailableRestaurants(deliveryAddress,
                            deliveryTime);
            //...
            return resultFactory.make(PlaceOrderStatusCodes.OK,
                    pendingOrder, restaurants);
        } else {
            return resultFactory.make(
                    PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                    pendingOrder, null);
        }
    }

    public PlaceOrderFacadeResult updateRestaurant(
            String pendingOrderId, String restaurantId) {
        PlaceOrderServiceResult result = service.updateRestaurant(
                pendingOrderId, restaurantId);
        return resultFactory.make(result.getStatusCode(), result
                .getPendingOrder());

    }

    public PlaceOrderFacadeResult updateQuantities(
            String pendingOrderId, int[] quantities) {
        PlaceOrderServiceResult result = service.updateQuantities(
                pendingOrderId, quantities);
        return resultFactory.make(PlaceOrderStatusCodes.OK, result
                .getPendingOrder(), null);
    }

    public PlaceOrderFacadeResult checkout(String pendingOrderId) {
        PlaceOrderServiceResult result = service
                .checkout(pendingOrderId);
        return resultFactory.make(PlaceOrderStatusCodes.OK, result
                .getPendingOrder(), null);
    }

    public PlaceOrderFacadeResult updatePaymentInformation(
            String pendingOrderId,
            PaymentInformation paymentInformation, String code) {
        PlaceOrderServiceResult result = service
                .updatePaymentInformation(pendingOrderId,
                        paymentInformation, code);
        return resultFactory.make(PlaceOrderStatusCodes.OK, result
                .getPendingOrder(), null);
    }

    public PlaceOrderFacadeResult placeOrder(String pendingOrderId) {
        PlaceOrderServiceResult result = service
                .placeOrder(pendingOrderId);
        return resultFactory.make(PlaceOrderStatusCodes.OK, result
                .getOrder());
    }

    public PlaceOrderFacadeResult confirmDeliveryInfoChange(
            String pendingOrderId, Address deliveryAddress,
            Date deliveryTime) {
        throw new NotYetImplementedException(); // FIXME
    }

    public PlaceOrderFacadeResult getPendingOrder(
            String pendingOrderId) {
        throw new NotYetImplementedException(); // FIXME
    }

}