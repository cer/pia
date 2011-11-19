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

/**
 * Handy class for tests that creates Orders.
 * author cer
 *
 */
public class OrderMother {

	public static Order makeOrder() {
		Date deliveryTime = new Date();
		return makeOrder(deliveryTime);
	}

	public static Order makeOrder(Date deliveryTime) {
		Address deliveryAddress = new Address("1 High Street", null, "Oakland", "CA", "94619");
		Restaurant restaurant = RestaurantMother.makeRestaurant();

		PaymentInformation p = new PaymentInformation("VISA", "Foo bar", "444",
				10, 2007, RestaurantTestData.getADDRESS1(), "foo@bar.com",
				"5105551212");
		Order order = new Order(Long.toString(System.currentTimeMillis()),
				deliveryAddress, deliveryTime, restaurant, p);
		for (Iterator it = restaurant.getMenuItems().iterator(); it.hasNext();) {
			MenuItem menuItem = (MenuItem) it.next();
			order.add(menuItem, 1);
		}
		return order;
	}

	public static Order makeOrder(Restaurant r) {
		Date deliveryTime = RestaurantMother.makeDeliveryTime();
		Address deliveryAddress = new Address("go", null, "city", "CA", "94619");
		PaymentInformation p = new PaymentInformation("VISA", "Foo bar", "444",
				10, 2007, RestaurantTestData.getADDRESS1(), "foo@bar.com",
				"5105551212");
		Order order = new Order(Long.toString(System.currentTimeMillis()),
				deliveryAddress, deliveryTime, r, p);
		for (Iterator it = r.getMenuItems().iterator(); it.hasNext();) {
			MenuItem menuItem = (MenuItem) it.next();
			order.add(menuItem, 4);
		}
		return order;
	}

	public static Order makeOrderWithTime(Date order1Time, Restaurant r) {
		PaymentInformation p = new PaymentInformation("VISA", "Foo bar", "444",
				10, 2007, RestaurantTestData.getADDRESS1(), "foo@bar.com",
				"5105551212");
		final Order o1 = new Order(Long.toString(System.currentTimeMillis()),
				RestaurantTestData.getADDRESS1(), order1Time, r, p);
		return o1;
	}

	public static Order makeOrderWithDeliveryTimeInPast() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -5);
		Date date = c.getTime();
		return makeOrder(date);
	}

}