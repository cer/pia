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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.dao.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.util.*;

import org.jmock.cglib.*;

public class PlaceOrderTransactionScriptsImplTests
        extends MockObjectTestCase {

    private Mock mockPendingOrderDAO;

    private Mock mockRestaurantDAO;

    private PendingOrderDAO pendingOrderDAO;

    private RestaurantDAO restaurantDAO;

    private PlaceOrderTransactionScriptsImpl service;

    private Mock mockPendingOrder;

    private PendingOrderDTO pendingOrder;

    public void setUp() throws Exception {
        super.setUp();
        mockPendingOrderDAO = new Mock(
                PendingOrderDAO.class);
        mockRestaurantDAO = new Mock(
                RestaurantDAO.class);
        pendingOrderDAO = (PendingOrderDAO) mockPendingOrderDAO
                .proxy();
        restaurantDAO = (RestaurantDAO) mockRestaurantDAO
                .proxy();
        mockPendingOrder = new Mock(
                PendingOrderDTO.class);
        pendingOrder = (PendingOrderDTO) mockPendingOrder
                .proxy();
        service = new PlaceOrderTransactionScriptsImpl(
                pendingOrderDAO, restaurantDAO);
    }

    public void testUpdateDeliveryInfo_good()
            throws Exception {

        Address deliveryAddress = new Address();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 2);
        Date deliveryTime = c.getTime();
        List availableRestaurants = Collections
                .singletonList(new RestaurantDTO());

        mockPendingOrderDAO.expects(once()).method(
                "createPendingOrder").will(
                returnValue(pendingOrder));

        mockRestaurantDAO
                .expects(once())
                .method("findAvailableRestaurants")
                .with(eq(deliveryAddress),
                        eq(deliveryTime))
                .will(
                        returnValue(availableRestaurants));

        mockPendingOrder.expects(once()).method(
                "setDeliveryAddress").with(
                eq(deliveryAddress));
        mockPendingOrder.expects(once()).method(
                "setDeliveryTime").with(
                eq(deliveryTime));
        mockPendingOrder
                .expects(once())
                .method("setState")
                .with(
                        eq(PendingOrder.DELIVERY_INFO_SPECIFIED));

       mockPendingOrderDAO.expects(once()).method(
                "savePendingOrder").with(
                eq(pendingOrder));

        UpdateDeliveryInfoResult result = service
                .updateDeliveryInfo_simple(null,
                        deliveryAddress, deliveryTime);

        assertEquals(
                UpdateDeliveryInfoResult.SELECT_RESTAURANT,
                result.getStatusCode());
        assertSame(pendingOrder, result
                .getPendingOrder());
        assertSame(availableRestaurants, result
                .getAvailableResturants());
    }
}