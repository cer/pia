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

import java.util.Date;

import net.chrisrichardson.foodToGo.domain.MenuItem;
import net.chrisrichardson.foodToGo.domain.PendingOrder;
import net.chrisrichardson.foodToGo.domain.PendingOrderLineItem;
import net.chrisrichardson.foodToGo.domain.PlaceOrderStatusCodes;
import net.chrisrichardson.foodToGo.domain.Restaurant;
import net.chrisrichardson.foodToGo.domain.RestaurantMother;
import net.chrisrichardson.foodToGo.domain.RestaurantTestData;
import net.chrisrichardson.foodToGo.domain.hibernate.HibernateRestaurantRepositoryImpl;
import net.chrisrichardson.foodToGo.domain.hibernate.HibernateTestConstants;
import net.chrisrichardson.foodToGo.pojoFacade.PlaceOrderFacadeResult;
import net.chrisrichardson.foodToGo.pojoFacade.PlaceOrderFacadeResultFactory;
import net.chrisrichardson.foodToGo.util.Address;
import net.chrisrichardson.foodToGo.views.PendingOrderDetail;
import net.chrisrichardson.ormunit.hibernate.HibernatePersistenceTests;
import net.chrisrichardson.util.TxnCallback;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernatePlaceOrderFacadeResultFactoryTests extends
        HibernatePersistenceTests {

    @Override
    protected String[] getConfigLocations() {
        return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

     }
    private PendingOrder pendingOrder;

    private Restaurant restaurant;

    private PlaceOrderFacadeResultFactory resultFactory;

    private String pendingOrderId;

    private Address address = RestaurantTestData.getADDRESS1();

    private Date time = RestaurantTestData.makeGoodDeliveryTime();


    protected PlaceOrderFacadeResult result;

    private String restaurantId;

    private HibernateRestaurantRepositoryImpl restaurantRepository;

    protected void onSetUp() throws Exception {
        super.onSetUp();

        doWithTransaction(new TxnCallback() {

            public void execute() throws Throwable {
                pendingOrder = new PendingOrder();
                save(pendingOrder);

                pendingOrderId = pendingOrder.getId();

                restaurant = RestaurantMother.makeRestaurant();
                save(restaurant);

                restaurantId = restaurant.getRestaurantId();

            }
        });

        resultFactory = new HibernatePlaceOrderFacadeResultFactory(getHibernateTemplate());
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        restaurantRepository = new HibernateRestaurantRepositoryImpl(
                        hibernateTemplate);
    }

    protected PendingOrder loadPendingOrder() throws Exception {
        return (PendingOrder) load(PendingOrder.class,
                pendingOrderId);
    }

    protected Restaurant loadRestaurant() throws Exception {
        return (Restaurant) load(Restaurant.class, restaurantId);
    }

    public void testBlankPendingOrder() throws Exception {

        doWithTransaction(new TxnCallback() {

            public void execute() throws Exception {
                pendingOrder = loadPendingOrder();
                result = resultFactory
                        .make(
                                PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                                pendingOrder, null);
            }
        });

        assertEquals(PlaceOrderStatusCodes.INVALID_DELIVERY_INFO,
                result.getStatusCode());
        assertNotNull(result.getPendingOrder());
        assertNull(result.getRestaurants());
        assertNull(result.getPendingOrder().getDeliveryAddress());
        assertNull(result.getPendingOrder().getDeliveryTime());
    }

    public void testWithDeliveryInfo() throws Exception {

        doWithTransaction(new TxnCallback() {

            public void execute() throws Exception {

                updateDeliveryInfo();
                result = resultFactory.make(
                        PlaceOrderStatusCodes.OK, pendingOrder,
                        null);

            }
        });

        assertEquals(PlaceOrderStatusCodes.OK, result
                .getStatusCode());

        PendingOrderDetail pendingOrderDetail = result
                .getPendingOrder();
        assertNotNull(pendingOrderDetail);
        assertEquals(time, pendingOrderDetail.getDeliveryTime());
        assertEquals(address, pendingOrderDetail
                .getDeliveryAddress());

        assertNull(result.getRestaurants());

    }

    private void updateDeliveryInfo() throws Exception {

        pendingOrder = loadPendingOrder();
        int r = pendingOrder.updateDeliveryInfo(restaurantRepository,
                address, time, true);
        assertEquals(PlaceOrderStatusCodes.OK, r);
    }

    private void updateRestaurant() throws Exception {
        restaurant = loadRestaurant();
        pendingOrder.updateRestaurant(restaurant);
    }

    public void testAfterUpdateRestaurant() throws Exception {
        doWithTransaction(new TxnCallback() {

            public void execute() throws Exception {
                updateDeliveryInfo();

                updateRestaurant();

                result = resultFactory.make(
                        PlaceOrderStatusCodes.OK, pendingOrder,
                        null);

            }

        });

        PendingOrderDetail pendingOrder = result.getPendingOrder();
        assertNotNull(pendingOrder.getRestaurantDetail().getName());

    }

    public void testWithQuantities() throws Exception {
        doWithTransaction(new TxnCallback() {

            public void execute() throws Exception {
                updateDeliveryInfo();
                updateRestaurant();
                pendingOrder.updateQuantities(new int[] { 1, 2 });

                result = resultFactory.make(
                        PlaceOrderStatusCodes.OK, pendingOrder,
                        null);

            }

        });

        PendingOrderDetail pendingOrder = result.getPendingOrder();
        assertEquals(1, ((PendingOrderLineItem) (pendingOrder
                .getLineItems().get(0))).getQuantity());

        assertNotNull(((MenuItem) (pendingOrder
                .getRestaurantDetail().getMenuItems().iterator()
                .next())).getName());

    }

}