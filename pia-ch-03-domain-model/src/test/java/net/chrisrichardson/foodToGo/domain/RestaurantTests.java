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

import junit.framework.*;
import net.chrisrichardson.foodToGo.util.*;

public class RestaurantTests extends TestCase {

    private Restaurant restaurant;

    private Date goodDeliveryTime;

    private Date badDeliveryTime;

    String GOOD_ZIP_CODE = "94619";

    String BAD_ZIP_CODE = "94618";

    private Address goodDeliveryAddress = new Address(
            "1 good street", null, "SomeCity", "CA", GOOD_ZIP_CODE);

    private Address badDeliveryAddress = new Address("1 good street",
            null, "SomeCity", "CA", BAD_ZIP_CODE);

    public RestaurantTests(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        restaurant = makeRestaurant();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 18);
        goodDeliveryTime = c.getTime();
        c.set(Calendar.HOUR_OF_DAY, 11);
        badDeliveryTime = c.getTime();
    }

    private Restaurant makeRestaurant() {
        TimeRange tr = new TimeRange(Calendar.MONDAY, 17, 0, 23, 0);
        Set serviceArea = new HashSet();
        serviceArea.add(GOOD_ZIP_CODE);
        return new Restaurant("Ajanta", "Indian", serviceArea, Collections
				        .singleton(tr), new ArrayList());
    }

    public void testIsInServiceArea_good() {

        assertTrue(restaurant.isInServiceArea(goodDeliveryAddress,
                goodDeliveryTime));
    }

    public void testIsInServiceArea_badDeliveryAddress() {
        assertFalse(restaurant.isInServiceArea(badDeliveryAddress,
                goodDeliveryTime));
    }

    public void testIsInServiceArea_badDeliveryTime() {
        assertFalse(restaurant.isInServiceArea(goodDeliveryAddress,
                badDeliveryTime));
    }

    public void testIsInServiceArea_bothBad() {
        assertFalse(restaurant.isInServiceArea(badDeliveryAddress,
                badDeliveryTime));

    }

}