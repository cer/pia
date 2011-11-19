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

import java.io.*;
import java.util.*;

public class OrderSearchCriteria implements Serializable {

    public static final int SORT_BY_ORDER_ID = 0;
    public static final int SORT_BY_ORDER_DATE = 1;

    private int sortBy = SORT_BY_ORDER_ID;
    private boolean sortAscending = false;
    
    private String orderId;
    private String deliveryCity;
    private String state;
    private String restaurantName;

	private Date deliveryTime;

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public boolean isDeliveryCitySpecified() {
        return isSpecified(deliveryCity);
    }

    public String getState() {
        return state;
    }
    public void setState(String status) {
        this.state = status;
    }

    public boolean isStatusSpecified() {
        return isSpecified(state);
    }

    boolean isSpecified(String s) {
        return s != null && !s.equals("");
    }
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public boolean isRestaurantSpecified() {
        return isSpecified(restaurantName);
    }
    public boolean isSortAscending() {
        return sortAscending;
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

	public void setDeliveryTime(Date date) {
        this.deliveryTime = date;
	}

	public boolean isDeliveryTimeSpecified() {
		return deliveryTime != null;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

}