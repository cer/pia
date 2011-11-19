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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details;

import java.util.*;

import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.views.*;

public class OrderDTO implements OrderDetail {

	private int bizVersion;

	private List lineItems = new ArrayList();

	private RestaurantDTO restaurant;

	private Date deliveryTime;

	private Address deliveryAddress;

	private String orderId;

	public static final String SENT = "SENT";

	public static final String REJECTED = "REJECTED";

	public static final String ACCEPTED = "ACCEPTED";

	public OrderDTO() {
	}

	public OrderDTO(String orderId, int version) {
		this.orderId = orderId;
		this.bizVersion = version;
	}

	public int getBizVersion() {
		return bizVersion;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public List getLineItems() {
		return lineItems;
	}

	public void setDeliveryAddress(Address address) {
		this.deliveryAddress = address;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public void setRestaurant(RestaurantDTO restaurant) {
		this.restaurant = restaurant;
	}

	public void add(OrderLineItemDTO lineItem) {
		lineItems.add(lineItem);
	}

	public RestaurantDTO getRestaurant() {
		return restaurant;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setBizVersion(int bizVersion) {
		this.bizVersion = bizVersion;
	}

	public void setLineItems(List lineItems) {
		this.lineItems = lineItems;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRestaurantName() {
		return getRestaurant().getName();
	}
}