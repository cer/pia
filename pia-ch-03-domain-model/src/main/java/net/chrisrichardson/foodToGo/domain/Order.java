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

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.views.*;

public class Order implements OrderDetail, Serializable {

	public static String PLACED = "PLACED";

	public static String ACCEPTED = "ACKNOWLEDGED";

	private static final String REJECTED = "REJECTED";

	public static String SENT = "SENT";

	private int id;

	private Date deliveryTime;

	private Restaurant restaurant;

	private List lineItems = new ArrayList();

	private int version;

	private AbstractCouponImpl coupon;

	private PaymentInformation paymentInformation;

	private Address deliveryAddress;

	private String state = PLACED;

	private String externalOrderId = "X";

	private String notes;

	private String owner;

	private String messageId;

	private Date timestamp;


	public Order() {

	}

	public Order(int id) {
		// this.id = id;
	}

	public Order(Address deliveryAddress, Date deliveryTime,
			Restaurant restaurant) {
		this.deliveryAddress = deliveryAddress;
		this.deliveryTime = deliveryTime;
		this.restaurant = (Restaurant) restaurant;
		// payment information
		this.lineItems = new ArrayList();
	}

	public Order(String externalOrderId, Address address, Date date,
			Restaurant restaurant, PaymentInformation paymentInformation) {
		this.externalOrderId = externalOrderId;
		this.deliveryAddress = address;
		this.deliveryTime = date;
		this.restaurant = restaurant;
		this.paymentInformation = paymentInformation;
		this.lineItems = new ArrayList();
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public PaymentInformation getPaymentInformation() {
		return paymentInformation;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	protected OrderLineItem makeLineItem(int index, MenuItem menuItem,
			int quantity) {
		return new OrderLineItem(quantity, menuItem);
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public String getId() {
		return Integer.toString(id);
	}

	public String getExternalOrderId() {
		return null;
	}

	public int getVersion() {
		return version;
	}

	protected void removeOldLineItems() {
	}

	protected OrderLineItem makeLineItem(QuantityAndMenuItem qm) {
		return new OrderLineItem(qm.getQuantity(), qm.getMenuItem());
	}

	public String getNotes() {
		return notes;
	}

	public String getLockedBy() {
		return owner;
	}

	public List getLineItems() {
		return lineItems;
	}

	public String getState() {
		return state;
	}

	public void add(MenuItem menuItem, int quantity) {
		lineItems.add(new OrderLineItem(quantity, menuItem));
	}

	public void modify(Address address, Date deliveryTime,
			Restaurant restaurant, PaymentInformation paymentInformation,
			List quantitiesAndMenuItems) {

		updateHeader(address, deliveryTime, restaurant, paymentInformation);

		updateLineItems(quantitiesAndMenuItems);

		redoCreditCardAuthorization();

	}

	private void updateHeader(Address address, Date deliveryTime,
			Restaurant restaurant, PaymentInformation paymentInformation) {
		this.deliveryAddress = address;
		this.deliveryTime = deliveryTime;
		this.restaurant = (Restaurant) restaurant;
		this.paymentInformation = paymentInformation;
	}

	private void updateLineItems(List quantitiesAndMenuItems) {
		removeOldLineItems();

		this.lineItems.clear();
		for (Iterator it = quantitiesAndMenuItems.iterator(); it.hasNext();) {
			QuantityAndMenuItem qm = (QuantityAndMenuItem) it.next();
			lineItems.add(makeLineItem(qm));
		}

	}

	private void redoCreditCardAuthorization() {
		// TODO - make this real one day :-)
	}

	public boolean isAcknowledgable() {
		return state.equals(Order.SENT);
	}

	public void accept(String notes) {
		if (!isAcknowledgable())
			throw new NotYetImplementedException("State = " + state);
		this.state = Order.ACCEPTED;
		this.notes = notes;
	}

	public void reject(String notes) {
		if (!isAcknowledgable())
			throw new NotYetImplementedException();
		this.state = Order.REJECTED;
		this.notes = notes;
	}

	public boolean isSameBizVersion(int originalVersion) {
		return getVersion() == originalVersion;
	}

	public void noteSent(String messageId, Date timestamp) {
		if (!state.equals(PLACED))
			throw new NotYetImplementedException("State = " + state);
		this.messageId = messageId;
		this.timestamp = timestamp;
		this.state = Order.SENT;
	}

	public boolean acquireLock(String owner) {
		if (owner != null)
			return false;
		this.owner = owner;
		return true;
	}

	public boolean verifyLock(String owner) {
		return owner.equals(owner);
	}

	public void releaseLock(String owner) {
		this.owner = null;
	}

	public String getOrderId() {
		return null;
	}

	public String getRestaurantName() {
		return getRestaurant().getName();
	}
}