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

public class RestaurantMother {

    public static final String RESTAURANT_NAME = "Ajanta";
    public static final int OPENING_HOUR = 18;
    public static final int OPENING_MINUTE = 12;
    public static final int CLOSING_MINUTE = 50;
    public static final int CLOSING_HOUR = 22;
    public static final int GOOD_HOUR = 19;
    public static final int BAD_HOUR = 12;
    public static final String SAMOSAS = "Samosas";

    public static Restaurant makeRestaurant() {
        return makeRestaurant("94619");
    }

    public static Restaurant makeRestaurant(String zipCode) {
//        OpeningHours hours = new OpeningHours();
        Set hours = new HashSet();
        TimeRange tr;
        tr = new TimeRange(Calendar.TUESDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        tr = new TimeRange(Calendar.WEDNESDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        tr = new TimeRange(Calendar.THURSDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        tr = new TimeRange(Calendar.FRIDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        tr = new TimeRange(Calendar.SATURDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        tr = new TimeRange(Calendar.SUNDAY, OPENING_HOUR, OPENING_MINUTE, CLOSING_HOUR, CLOSING_MINUTE);
        hours.add(tr);
        List menuItems = new ArrayList();
        MenuItem mi1 = new MenuItem("Samosas", 5.00);
        MenuItem mi2 = new MenuItem("Chicken Tikka", 6.50);
        menuItems.add(mi1);
        menuItems.add(mi2);

        Set<ZipCode> serviceArea = new HashSet();
        serviceArea.add(new ZipCode(zipCode));

        return new Restaurant(RESTAURANT_NAME, serviceArea, hours,
                menuItems);
    }

}