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
 
package net.chrisrichardson.foodToGo.domain;

import java.util.*;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;

import org.jmock.cglib.*;

public class PlaceOrderServiceTests extends MockObjectTestCase {

    private Mock mockOrderCreationListener;

    private Mock mockOrderRepository;

    private Mock mockCreditCardProcessor;

    private Restaurant restaurant;

    private Mock mockRestaurant;

    private Mock mockPendingOrder;

    private PendingOrder pendingOrder;

    private String pendingOrderId;

    private Date goodDeliveryTime;

    private Address goodDeliveryAddress;

    private PlaceOrderService service;

    private Mock mockRestaurantRepository;

    private Mock mockPendingOrderRepository;

    private Mock mockTransaction;

    private Mock mockOrder;

    private RestaurantRepository restaurantRepository;

    public void setUp() throws Exception {
        mockPendingOrderRepository = new Mock(
                PendingOrderRepository.class);
        mockRestaurantRepository = new Mock(
                RestaurantRepository.class);

        mockCreditCardProcessor = new Mock(
                CreditCardProcessor.class);
        CreditCardProcessor creditCardProcessor = (CreditCardProcessor) mockCreditCardProcessor.proxy();
        
        mockOrderRepository = new Mock(OrderRepository.class);
        

        mockOrderCreationListener = new Mock(
                OrderCreationListener.class);

        OrderCreationListener orderCreationListener = (OrderCreationListener) mockOrderCreationListener.proxy();
        
        OrderRepository orderRepository = (OrderRepository) mockOrderRepository.proxy();
        PendingOrderRepository pendingOrderRepository = (PendingOrderRepository) mockPendingOrderRepository.proxy();
        restaurantRepository = (RestaurantRepository) mockRestaurantRepository.proxy();
        service = new PlaceOrderServiceImpl(pendingOrderRepository, restaurantRepository, orderRepository, creditCardProcessor, orderCreationListener);

        goodDeliveryAddress = RestaurantTestData.getADDRESS1();
        goodDeliveryTime = RestaurantTestData
                .makeGoodDeliveryTime();

        pendingOrderId = "pendingOrderId";
        mockPendingOrder = new Mock(PendingOrder.class);
        pendingOrder = (PendingOrder) mockPendingOrder.proxy();

        mockRestaurant = new Mock(Restaurant.class);
        restaurant = (Restaurant) mockRestaurant.proxy();

        mockTransaction = new Mock(AuthorizationTransaction.class);
        mockOrder = new Mock(Order.class);
    }

    public void testUpdateDeliveryInfo_Good() throws Exception {

        mockPendingOrderRepository.expects(once()).method(
                "findPendingOrder").with(eq(pendingOrderId)).will(
                (returnValue(pendingOrder)));

        mockPendingOrder.expects(once()).method(
                "updateDeliveryInfo").with(eq(restaurantRepository),
                eq(goodDeliveryAddress), eq(goodDeliveryTime),
                eq(false)).will((returnValue(PlaceOrderStatusCodes.OK)));

        PlaceOrderServiceResult result = service.updateDeliveryInfo(
                pendingOrderId, goodDeliveryAddress,
                goodDeliveryTime);

        assertTrue(result.isSuccess());

        PendingOrder returnedPendingOrder = result
                .getPendingOrder();
        assertSame(pendingOrder, returnedPendingOrder);
    }

    public void test2() throws Exception {
        Address deliveryAddress = RestaurantTestData.getBAD_ADDRESS();
        Date deliveryTime = RestaurantTestData
                .makeGoodDeliveryTime();
        String pendingOrderId = null;

        mockPendingOrderRepository.expects(once()).method(
                "createPendingOrder").will(
                (returnValue(pendingOrder)));

        mockPendingOrder
                .expects(once())
                .method("updateDeliveryInfo")
                .with(eq(restaurantRepository), eq(deliveryAddress),
                        eq(deliveryTime), eq(false))
                .will(
                        (returnValue(PlaceOrderStatusCodes.INVALID_DELIVERY_INFO)));

        PlaceOrderServiceResult result = service.updateDeliveryInfo(
                pendingOrderId, deliveryAddress, deliveryTime);

        assertTrue(!result.isSuccess());
        PendingOrder returnedPendingOrder = result
                .getPendingOrder();
        assertSame(pendingOrder, returnedPendingOrder);

    }

    public void testUpdateRestaurant_good() throws Exception {
        mockPendingOrderRepository.expects(once()).method(
                "findPendingOrder").with(eq(pendingOrderId)).will(
                (returnValue(pendingOrder)));

        String restaurantId = "restaurantId";

        mockRestaurantRepository.expects(once()).method(
                "findRestaurant").with(eq(restaurantId)).will(
                (returnValue(restaurant)));

        mockPendingOrder.expects(once()).method("updateRestaurant")
                .with(eq(restaurant)).will((returnValue(true)));

        PlaceOrderServiceResult result = service.updateRestaurant(
                pendingOrderId, restaurantId);

        assertTrue(result.isSuccess());

        PendingOrder returnedPendingOrder = result
                .getPendingOrder();

        assertSame(pendingOrder, returnedPendingOrder);
    }

    public void testUpdateRestaurant_bad() throws Exception {
        mockPendingOrderRepository.expects(once()).method(
                "findPendingOrder").with(eq(pendingOrderId)).will(
                (returnValue(pendingOrder)));

        String restaurantId = "restaurantId";

        mockRestaurantRepository.expects(once()).method(
                "findRestaurant").with(eq(restaurantId)).will(
                (returnValue(restaurant)));

        mockPendingOrder.expects(once()).method("updateRestaurant")
                .with(eq(restaurant)).will((returnValue(false)));

        PlaceOrderServiceResult result = service.updateRestaurant(
                pendingOrderId, restaurantId);

        assertTrue(!result.isSuccess());

        PendingOrder returnedPendingOrder = result
                .getPendingOrder();

        assertSame(pendingOrder, returnedPendingOrder);
    }

    public void testPlaceOrder() throws Exception {

        AuthorizationTransaction authorizationTransaction = (AuthorizationTransaction) mockTransaction
                .proxy();

        Order order = (Order) mockOrder.proxy();

        PaymentInformation paymentInformation = new PaymentInformation();

        mockPendingOrderRepository.expects(once()).method(
                "findPendingOrder").with(eq(pendingOrderId)).will(
                (returnValue(pendingOrder)));

        mockPendingOrder.expects(once()).method("isReadyToPlace")
                .will((returnValue(true)));

        mockPendingOrder.expects(once()).method(
                "getPaymentInformation").will(
                (returnValue(paymentInformation)));

        mockCreditCardProcessor.expects(once()).method("authorize")
                .with(eq(paymentInformation)).will(
                        (returnValue(authorizationTransaction)));

        mockPendingOrder.expects(once()).method("noteAuthorized")
                .with(eq(authorizationTransaction));

        mockOrderRepository.expects(once()).method("createOrder")
                .will((returnValue(order)));

        mockPendingOrder.expects(once()).method("notePlaced");
        
        mockOrderCreationListener.expects(once()).method(
                "noteOrderCreated").with(eq(order));

        PlaceOrderServiceResult result = service
                .placeOrder(pendingOrderId);

        assertTrue(result.isSuccess());
        assertSame(order, result.getOrder());
    }
}