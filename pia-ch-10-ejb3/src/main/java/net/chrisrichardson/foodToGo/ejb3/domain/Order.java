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

import java.io.*;
import java.util.*;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;


public class Order implements Serializable {

	// begin(fields)

	public static String PLACED = "PLACED";

	public static String ACCEPTED = "ACKNOWLEDGED";

	private static final String REJECTED = "REJECTED";

	public static String SENT = "SENT";

	// private int version;

	private int id;

	private Date deliveryTime;

	private Restaurant restaurant;

	private List lineItems = new ArrayList();

	private int version;

	private String lockedBy;

	private Coupon coupon;

	private PaymentInformation paymentInformation;

	private Address deliveryAddress;

	private String state = PLACED;

	private String externalOrderId = "X";

	private String notes;

	// end(fields)

	public Order() {

	}

	public Order(int id) {
		// this.id = id;
	}

	public Order(Address deliveryAddress, Date deliveryTime,
			Restaurant restaurant) {
		setDeliveryAddress(deliveryAddress);
		this.deliveryTime = deliveryTime;
		this.restaurant = (Restaurant) restaurant;
		// payment information
		this.lineItems = new ArrayList();
	}

	public Order(String externalOrderId, Address address, Date date,
			Restaurant restaurant, PaymentInformation information) {
		this.externalOrderId = externalOrderId;
		this.deliveryAddress = address;
		this.deliveryTime = date;
		this.restaurant = restaurant;
		this.paymentInformation = information;
		this.lineItems = new ArrayList();
	}

	// begin(methods)

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryDate) {
		this.deliveryTime = deliveryDate;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = (Restaurant) restaurant;
	}

	public PaymentInformation getPaymentInformation() {
		return paymentInformation;
	}

	protected void setPaymentInformation(PaymentInformation paymentInformation) {
		this.paymentInformation = paymentInformation;
	}

	protected void setDeliveryAddress(Address address) {
		this.deliveryAddress = address;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	protected OrderLineItem makeLineItem(int index, MenuItem menuItem,
			int quantity) {
		return new OrderLineItem(quantity, menuItem);
	}

	protected void setAuthorizationTransaction(AuthorizationTransaction txn) {
		// TODO - fix me
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
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

	protected OrderLineItem makeLineItem(QuantityAndMenuItem qm, int index) {
		return null;
	}

	public String getNotes() {
		return notes;
	}

	public String getLockedBy() {
		return null;
	}

	public void setLockedBy(String owner) {
	}

	public void noteNotificationSent() {
	}

	public List getLineItems() {
		return lineItems;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
		setDeliveryAddress(address);
		setDeliveryTime(deliveryTime);
		setRestaurant(restaurant);
		setPaymentInformation(paymentInformation);
	}

	private void updateLineItems(List quantitiesAndMenuItems) {
		// Do this first because with JDO genie creating a
		// orderline item that references the order adds it to the collection
		// This makes it hard to distinguish old from new

		removeOldLineItems();

		List newLineItems = new ArrayList();
		for (Iterator it = quantitiesAndMenuItems.iterator(); it.hasNext();) {
			QuantityAndMenuItem qm = (QuantityAndMenuItem) it.next();
			newLineItems.add(makeLineItem(qm, newLineItems.size()));
		}

		this.lineItems = newLineItems;
	}

	private void redoCreditCardAuthorization() {
		// This is a problem.
		// This txn could get rolled back - in which case the reauth
		// would not get saved.
		// nested txn???
		// Do it asynchronously once the changes have been committed???

		// What about nested transactions???
		// What about nested EJB calls??
		// This conflicts with the PMRepository - save away in TxnManager
	}

	private String messageId;

	private Date timestamp;

	public boolean isAcknowledgable() {
		return state.equals(Order.SENT);
	}

	public void accept(String notes) {
		if (!isAcknowledgable())
			throw new NotYetImplementedException();
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
		this.messageId = messageId;
		this.timestamp = timestamp;
		setState(Order.SENT);
	}

	public boolean acquireLock(String owner) {
		if (getLockedBy() != null)
			return false;
		setLockedBy(owner);
		return true;
	}

	public boolean verifyLock(String owner) {
		return owner.equals(getLockedBy());
	}

	public void releaseLock(String owner) {
		verifyLock(owner);
		setLockedBy(null);
	}

	public String getOrderId() {
		return null;
	}

	public String getRestaurantName() {
		return getRestaurant().getName();
	}
}