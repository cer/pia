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
import net.chrisrichardson.foodToGo.util.*;

import org.jmock.*;
import org.jmock.core.*;

public class PlaceOrderFacadeMockTests extends MockObjectTestCase {

    private Mock mockRepository;

    private Mock mockPlaceOrderService;

    private Mock mockResultFactory;

    private PendingOrder pendingOrder;

    private Date deliveryTime;

    private Address deliveryAddress;

    private String pendingOrderId;

    private ArrayList availableRestaurants;

    private PlaceOrderService placeOrderService;

    private PlaceOrderFacadeResultFactory resultFactory;

    private PlaceOrderFacade placeOrderFacade;

    private RestaurantRepository restaurantRepository;

    public void setUp() {
        mockRepository = new Mock(RestaurantRepository.class);
        mockPlaceOrderService = new Mock(PlaceOrderService.class);
        mockResultFactory = new Mock(
                PlaceOrderFacadeResultFactory.class);

        placeOrderService = (PlaceOrderService) mockPlaceOrderService
                .proxy();
        resultFactory = (PlaceOrderFacadeResultFactory) mockResultFactory
                .proxy();
        restaurantRepository = (RestaurantRepository) mockRepository
                .proxy();
        placeOrderFacade = new PlaceOrderFacadeImpl(
                placeOrderService, restaurantRepository,
                resultFactory);

        availableRestaurants = new ArrayList();

        pendingOrderId = "pendingOrderId";
        deliveryAddress = RestaurantTestData.getADDRESS1();
        deliveryTime = RestaurantTestData.makeGoodDeliveryTime();

        pendingOrder = new PendingOrder();
    }

    public void testUpdateDeliveryInfoSuccess() throws Exception {
        PlaceOrderServiceResult placeOrderServiceResult = new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.OK, pendingOrder);

        PlaceOrderFacadeResult resultFactoryResult = new PlaceOrderFacadeResult(
                PlaceOrderStatusCodes.OK, pendingOrder,
                availableRestaurants);

        mockPlaceOrderService.expects(once()).method(
                "updateDeliveryInfo").with(eq(pendingOrderId),
                eq(deliveryAddress), eq(deliveryTime)).will(
                (returnValue(placeOrderServiceResult)));

        mockRepository.expects(once()).method(
                "findAvailableRestaurants").will(
                returnValue(availableRestaurants));

        mockResultFactory.expects(once()).method("make")
                .with(
                        new Constraint[] {
                                eq(PlaceOrderStatusCodes.OK),
                                eq(pendingOrder),
                                eq(availableRestaurants) }).will(
                        returnValue(resultFactoryResult));

        PlaceOrderFacadeResult result = placeOrderFacade
                .updateDeliveryInfo(pendingOrderId,
                        deliveryAddress, deliveryTime);

        assertSame(resultFactoryResult, result);
    }

    public void testUpdateDeliveryInfoFail() throws Exception {
        PlaceOrderServiceResult placeOrderServiceResult = new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                pendingOrder);

        PlaceOrderFacadeResult resultFactoryResult = new PlaceOrderFacadeResult(
                PlaceOrderStatusCodes.OK, pendingOrder, null);

        mockPlaceOrderService.expects(once()).method(
                "updateDeliveryInfo").with(
                new Constraint[] { eq(pendingOrderId),
                        eq(deliveryAddress), eq(deliveryTime) })
                .will((returnValue(placeOrderServiceResult)));

        mockResultFactory
                .expects(once())
                .method("make")
                .with(
                        new Constraint[] {
                                eq(PlaceOrderStatusCodes.INVALID_DELIVERY_INFO),
                                eq(pendingOrder), eq(null) }).will(
                        returnValue(resultFactoryResult));

        PlaceOrderFacadeResult result = placeOrderFacade
                .updateDeliveryInfo(pendingOrderId,
                        deliveryAddress, deliveryTime);

        assertEquals(resultFactoryResult, result);
    }

    public void testUpdateRestaurant() throws Exception {
        String restaurantId = "restaurantId";

        PlaceOrderServiceResult placeOrderServiceResult = new PlaceOrderServiceResult(
                PlaceOrderStatusCodes.OK, pendingOrder);

        PlaceOrderFacadeResult resultFactoryResult = new PlaceOrderFacadeResult(
                PlaceOrderStatusCodes.OK, pendingOrder,
                availableRestaurants);

        mockPlaceOrderService.expects(once()).method(
                "updateRestaurant").with(eq(pendingOrderId),
                eq(restaurantId)).will(
                (returnValue(placeOrderServiceResult)));

        mockResultFactory.expects(once()).method("make").with(
                eq(PlaceOrderStatusCodes.OK), eq(pendingOrder))
                .will(returnValue(resultFactoryResult));

        PlaceOrderFacadeResult result = placeOrderFacade
                .updateRestaurant(pendingOrderId, restaurantId);

        assertSame(resultFactoryResult, result);
    }
}