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
 
package net.chrisrichardson.foodToGo.domain.hibernate;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.ormunit.hibernate.*;

public class HibernateRestaurantRepositoryQueryTests
        extends HibernatePersistenceTests {
    
    @Override
    protected String[] getConfigLocations() {
        return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

    }

    protected void onSetUp() throws Exception {
        super.onSetUp();
        delete(PendingOrderLineItem.class);
        delete(PendingOrder.class);
        delete(OrderLineItem.class);
        delete(Order.class);
        delete(Restaurant.class);
        Restaurant r = RestaurantMother
                .makeRestaurant(RestaurantTestData.GOOD_ZIP_CODE);
        save(r);
    }

    private void findAvailableRestaurants(
            int dayOfWeek, int hour, int minute,
            String zipCode, boolean expectRestaurants)
            throws Exception {
        String[] paramNames = { "zipCode",
                "dayOfWeek", "hour", "minute" };
        Object[] paramValues = new Object[] { zipCode,
                new Integer(dayOfWeek),
                new Integer(hour), new Integer(minute) };
        List availableRestaurants = getHibernateTemplate()
                .findByNamedQueryAndNamedParam(
                        "findAvailableRestaurants",
                        paramNames, paramValues);
        if (expectRestaurants)
            assertFalse(availableRestaurants.isEmpty());
        else
            assertTrue(availableRestaurants.isEmpty());
    }

    public void testFindAvailableRestaurants_good()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.GOOD_HOUR, 0,
                RestaurantTestData.GOOD_ZIP_CODE, true);
    }

    public void testFindAvailableRestaurants_badZipCode()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.GOOD_HOUR, 0,
                RestaurantTestData.BAD_ZIP_CODE, false);
    }

    public void testFindAvailableRestaurants_closedTime()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.BAD_HOUR, 0,
                RestaurantTestData.BAD_ZIP_CODE, false);
    }

    public void testFindAvailableRestaurants_closedDay()
            throws Exception {
        findAvailableRestaurants(Calendar.MONDAY,
                RestaurantMother.GOOD_HOUR, 0,
                RestaurantTestData.GOOD_ZIP_CODE,
                false);
    }

    public void testFindAvailableRestaurants_beforeOpening()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.OPENING_HOUR,
                RestaurantMother.OPENING_MINUTE - 1,
                RestaurantTestData.GOOD_ZIP_CODE,
                false);
    }

    public void testFindAvailableRestaurants_atOpening()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.OPENING_HOUR,
                RestaurantMother.OPENING_MINUTE,
                RestaurantTestData.GOOD_ZIP_CODE, true);
    }

    public void testFindAvailableRestaurants_atClosing()
            throws Exception {
        findAvailableRestaurants(Calendar.TUESDAY,
                RestaurantMother.CLOSING_HOUR,
                RestaurantMother.CLOSING_MINUTE,
                RestaurantTestData.GOOD_ZIP_CODE,
                false);
    }

}