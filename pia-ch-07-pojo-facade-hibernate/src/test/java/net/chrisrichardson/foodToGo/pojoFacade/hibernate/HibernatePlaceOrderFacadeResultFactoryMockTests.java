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
 
package net.chrisrichardson.foodToGo.pojoFacade.hibernate;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.pojoFacade.*;

import org.jmock.cglib.*;
import org.springframework.orm.hibernate3.*;

public class HibernatePlaceOrderFacadeResultFactoryMockTests extends
        MockObjectTestCase {

    private HibernatePlaceOrderFacadeResultFactory resultFactory;

    private Mock mockHibernateTemplate;

    private PendingOrder pendingOrder;

    private List restaurants;

    private Mock mockPendingOrder;

    private List lineItems;

    public void setUp() {

        mockHibernateTemplate = new Mock(HibernateTemplate.class);

        HibernateTemplate hibernateTemplate = (HibernateTemplate) mockHibernateTemplate
                .proxy();

        resultFactory = new HibernatePlaceOrderFacadeResultFactory(
                hibernateTemplate);
        restaurants = new ArrayList();

        mockPendingOrder = new Mock(PendingOrder.class);
        pendingOrder = (PendingOrder) mockPendingOrder.proxy();
        lineItems = new ArrayList();
    }

    public void testDetachPendingOrder() {

        mockPendingOrder.expects(once()).method("getLineItems")
                .will(returnValue(lineItems));

        mockPendingOrder.expects(once()).method("getRestaurant")
                .will(returnValue(null));

        mockHibernateTemplate.expects(once()).method("initialize")
                .with(same(lineItems));

        PlaceOrderFacadeResult result = resultFactory.make(
                PlaceOrderStatusCodes.OK, pendingOrder);

        assertEquals(PlaceOrderStatusCodes.OK, result
                .getStatusCode());
        assertSame(pendingOrder, result.getPendingOrder());
        assertNull(result.getRestaurants());
    }

    public void testDetachPendingOrderAndRestaurants() {
        mockPendingOrder.expects(once()).method("getLineItems")
                .will(returnValue(lineItems));

        mockHibernateTemplate.expects(once()).method("initialize")
                .with(same(lineItems));

        mockPendingOrder.expects(once()).method("getRestaurant")
                .will(returnValue(null));

        PlaceOrderFacadeResult result = resultFactory
                .make(PlaceOrderStatusCodes.OK, pendingOrder,
                        restaurants);

        assertEquals(PlaceOrderStatusCodes.OK, result
                .getStatusCode());
        assertSame(pendingOrder, result.getPendingOrder());
        assertSame(restaurants, result.getRestaurants());
    }

    public void testDetachPendingOrderWithRestaurant()
            throws InvalidPendingOrderStateException {
        mockPendingOrder.expects(once()).method("getLineItems")
                .will(returnValue(lineItems));

        Mock mockRestaurant = new Mock(Restaurant.class);
        Restaurant restaurant = (Restaurant) mockRestaurant.proxy();

        mockPendingOrder.expects(once()).method("getRestaurant")
                .will(returnValue(restaurant));

        List menuItems = new ArrayList();

        mockRestaurant.expects(once()).method("getMenuItems").will(
                returnValue(menuItems));

        mockHibernateTemplate.expects(once()).method("initialize")
                .with(same(lineItems));

        mockHibernateTemplate.expects(once()).method("initialize")
                .with(same(menuItems));

        PlaceOrderFacadeResult result = resultFactory.make(
                PlaceOrderStatusCodes.OK, pendingOrder);

        assertEquals(PlaceOrderStatusCodes.OK, result
                .getStatusCode());

        assertSame(pendingOrder, result.getPendingOrder());
        assertNull(result.getRestaurants());
    }
}