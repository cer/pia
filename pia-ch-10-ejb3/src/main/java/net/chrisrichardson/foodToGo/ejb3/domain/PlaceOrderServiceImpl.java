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

import javax.annotation.*;
import javax.ejb.*;

import net.chrisrichardson.foodToGo.util.*;

@Stateless
public class PlaceOrderServiceImpl implements
        PlaceOrderService {

    @EJB
    private PendingOrderRepository pendingOrderRepository;

    @EJB
    private RestaurantRepository restaurantRepository;

    private PlaceOrderServiceImpl() {
    }

    public PlaceOrderServiceImpl(
            PendingOrderRepository pendingOrderRepository,
            RestaurantRepository restaurantRepository) {
        this.pendingOrderRepository = pendingOrderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    //
    PendingOrder findOrCreatePendingOrder(
            String pendingOrderId) {
        if (pendingOrderId == null)
            return pendingOrderRepository
                    .createPendingOrder();
        else
            return pendingOrderRepository
                    .findPendingOrder(pendingOrderId);
    }

    int count = 0;

    public PlaceOrderServiceResult updateDeliveryInfo(
            String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {
        PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
        int result = pendingOrder.updateDeliveryInfo(
                restaurantRepository, deliveryAddress,
                deliveryTime, false);
        switch (result) {
        case PlaceOrderStatusCodes.OK:
            return new PlaceOrderServiceResult(
                    PlaceOrderStatusCodes.OK,
                    pendingOrder);
        case PlaceOrderStatusCodes.INVALID_DELIVERY_INFO:
            return new PlaceOrderServiceResult(
                    PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                    pendingOrder);
        case PlaceOrderStatusCodes.CONFIRM_CHANGE:
            return new PlaceOrderServiceResult(
                    PlaceOrderStatusCodes.CONFIRM_CHANGE,
                    pendingOrder);
        default:
            throw new NotYetImplementedException(
                    "Result=" + result);
        }
    }

    public PlaceOrderServiceResult updateRestaurant(
            String pendingOrderId, String restaurantId) {
        PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
        Restaurant restaurant = restaurantRepository
                .findRestaurant(restaurantId);
        boolean success = pendingOrder
		        .updateRestaurant(restaurant);
        pendingOrder
                .setCoupon(new PercentageDiscountCoupon(
                        "xx", 10, 50));
        return new PlaceOrderServiceResult(
                success ? PlaceOrderStatusCodes.OK
                        : PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                pendingOrder);
    }

    public PlaceOrderServiceResult updateQuantities(
            String pendingOrderId, int[] quantities) {
        PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
        pendingOrder.updateQuantities(quantities);
        return new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.OK, pendingOrder);
    }

    // public PlaceOrderServiceResult placeOrder(String pendingOrderId) {
    // PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
    // if (!pendingOrder.isReadyToPlace())
    // throw new NotYetImplementedException();
    //
    // try {
    // AuthorizationTransaction txn = creditCardProcessor
    // .authorize(pendingOrder.getPaymentInformation());
    // pendingOrder.noteAuthorized(txn);
    // Order order = orderRepository.createOrder(pendingOrder);
    // pendingOrder.notePlaced();
    // orderCreationListener.noteOrderCreated(order);
    // return new PlaceOrderServiceResult(
    // PlaceOrderStatusCodes.OK, order);
    // } catch (CreditCardProcessingException e) {
    // // What should we do
    // throw new NotYetImplementedException();
    // }
    // }

    public PlaceOrderServiceResult checkout(
            String pendingOrderId) {
        PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
        return new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.OK, pendingOrder);
    }

    public PlaceOrderServiceResult updatePaymentInformation(
            String pendingOrderId,
            PaymentInformation paymentInformation,
            String couponCode) {
        PendingOrder pendingOrder = findOrCreatePendingOrder(pendingOrderId);
        try {
            pendingOrder.updatePaymentInformation(
                    paymentInformation, null); // FIXME
        } catch (InvalidPendingOrderStateException e) {
            throw new NotYetImplementedException(e);
        }
        return new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.OK, pendingOrder);
    }

}