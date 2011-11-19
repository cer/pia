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

import net.chrisrichardson.foodToGo.util.*;

import org.jmock.cglib.*;
import org.jmock.core.matcher.*;
import org.jmock.core.stub.*;

public class PendingOrderMother {

    public static PendingOrder makePendingOrderWithRestaurant()
            throws InvalidPendingOrderStateException {
        Restaurant restaurant = RestaurantMother.makeRestaurant();
        Address deliveryAddress = RestaurantTestData.getADDRESS1();

        Date deliveryTime = RestaurantTestData
                .makeGoodDeliveryTime();

        PendingOrder pendingOrder = new PendingOrder();

        Mock mockRestaurantRepository = new Mock(
                RestaurantRepository.class);
        RestaurantRepository restaurantRepository = (RestaurantRepository) mockRestaurantRepository
                .proxy();
        mockRestaurantRepository.expects(new InvokeOnceMatcher())
                .method("isRestaurantAvailable").will(
                        new ReturnStub(new Boolean(true)));
        pendingOrder.updateDeliveryInfo(restaurantRepository,
                deliveryAddress, deliveryTime, true);
        pendingOrder.updateRestaurant(restaurant);
        return pendingOrder;
    }

}