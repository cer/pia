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
import net.chrisrichardson.foodToGo.views.*;

public class Restaurant implements RestaurantDetail {

	private int id;

	private String name;

	private Set serviceArea;

	private String notificationEmailAddress;

	private List menuItems;

	private Set timeRanges;

	private String type;

	public Restaurant() {

	}

	/*
	 * Creates a Restaurant
	 */

	public Restaurant(String name, String type, Set serviceArea,
			Set openingHours, List menuItems) {
		this.name = name;
		this.type = type;
		this.serviceArea = serviceArea;
		this.timeRanges = openingHours;
		this.menuItems = menuItems;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getNotificationEmailAddress() {
		return notificationEmailAddress;
	}

	public Set getServiceArea() {
		return serviceArea;
	}

	public List getMenuItems() {
		return menuItems;
	}

	public Set getOpeningHours() {
		return timeRanges;
	}

	public String getRestaurantId() {
		return Integer.toString(id);
	}

	public boolean isInServiceArea(Address address, Date deliveryTime) {
		return isInServiceArea(address) && isOpenAtThisTime(deliveryTime);
	}

	private boolean isOpenAtThisTime(Date deliveryTime) {
		for (Iterator it = timeRanges.iterator(); it.hasNext();) {
			TimeRange range = (TimeRange) it.next();
			if (range.isOpenAtThisTime(deliveryTime))
				return true;
		}

		return false;
	}

	public boolean isInServiceArea(Address address) {
		boolean result = getServiceArea().contains(address.getZip());
		return result;
	}

	public String getId() {
		return Integer.toString(id);
	}

}