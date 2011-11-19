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
import net.chrisrichardson.foodToGo.views.*;

public class PendingOrder implements
        PendingOrderDetail {

    public static final int NEW = 0;

    public static final int DELIVERY_INFO_SPECIFIED = 1;

    public static final int RESTAURANT_SELECTED = 2;

    public static final int READY_FOR_CHECKOUT = 3;

    public static final int NEEDS_PAYMENT = 4;

    public static final int READY_TO_PLACE = 5;

    public static final int PLACED = 6;

    private int state = PendingOrder.NEW;

    private int id = 0; // Hibernate

    private int version;

    private Date deliveryTime;

    private Restaurant restaurant;

    private List lineItems = new ArrayList();

    private Coupon coupon;

    private PaymentInformation paymentInformation;

    private Address deliveryAddress;

    public PendingOrder() {

    }

    public PendingOrder(int id) {
        this.id = id;
    }

    public PendingOrder(Address deliveryAddress,
            Date deliveryTime, Restaurant restaurant,
            List quantitiesAndMenuItems) {
        this.deliveryAddress = deliveryAddress;
        this.deliveryTime = deliveryTime;
        this.restaurant = (Restaurant) restaurant;
        this.state = PendingOrder.RESTAURANT_SELECTED; // ??
        initializeLineItems(restaurant,
                quantitiesAndMenuItems);
    }

    public int getState() {
        return state;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List getLineItems() {
        return lineItems;
    }

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    protected PendingOrderLineItem makeLineItem(
            int index, MenuItem menuItem, int quantity) {
        return new PendingOrderLineItem(quantity,
                menuItem);
    }

    public Coupon getCoupon() {
        return coupon;
    }

    protected PendingOrder getPendingOrder() {
        return this;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }


	public String getId() {
		return Integer.toString(id);
	}

    /**
     * Returns true if the delivery address and time can be served by the
     * selected restaurant
     * 
     * @param deliveryAddress
     * @param deliveryTime
     * @return
     */
    protected boolean isCompatibleWithSelectedRestaurant(
            Address deliveryAddress, Date deliveryTime) {
        return restaurant == null
                || restaurant.isInServiceArea(
                        deliveryAddress, deliveryTime);
    }

    /**
     * Change the delivery info
     * 
     * @param deliveryAddress
     * @param deliveryTime
     * @param force
     * @return
     */
    public int updateDeliveryInfo(
            RestaurantRepository rr,
            Address deliveryAddress,
            Date deliveryTime, boolean force) {

        Calendar earliestDeliveryTime = Calendar
                .getInstance();
        earliestDeliveryTime.add(Calendar.HOUR, 1);
        if (deliveryTime.before(earliestDeliveryTime
                .getTime()))
            return PlaceOrderStatusCodes.INVALID_DELIVERY_INFO;

        if (!rr.isRestaurantAvailable(deliveryAddress,
                deliveryTime))
            return PlaceOrderStatusCodes.INVALID_DELIVERY_INFO;

        if (restaurant != null) {
            if (restaurant.isInServiceArea(
                    deliveryAddress, deliveryTime)) {
                this.deliveryAddress = deliveryAddress;
                this.deliveryTime = deliveryTime;
                // getState() unchanged
                return PlaceOrderStatusCodes.OK;
            } else if (!force) {
                return PlaceOrderStatusCodes.CONFIRM_CHANGE;
            }
        }

        this.deliveryAddress = deliveryAddress;
        this.deliveryTime = deliveryTime;
        this.restaurant = null;
        this.state = PendingOrder.DELIVERY_INFO_SPECIFIED;

        return PlaceOrderStatusCodes.OK;
    }

    /**
     * Change the restaurant
     * 
     * @param restaurant
     * @return
     * @exception InvalidPendingOrderStateException
     */
    public boolean updateRestaurant(
            Restaurant restaurant)
            throws InvalidPendingOrderStateException {

        if (state == PendingOrder.NEW
                || state == PendingOrder.PLACED)
            throw new InvalidPendingOrderStateException(
                    "State = " + getState());

        if (restaurant.isInServiceArea(
                deliveryAddress,
                deliveryTime)) {
            this.restaurant = restaurant;
            this.lineItems.clear();
            this.state = PendingOrder.RESTAURANT_SELECTED;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Change the quantities
     * 
     * @param quantities
     * @exception InvalidPendingOrderStateException
     */
    public void updateQuantities(int[] quantities)
            throws InvalidPendingOrderStateException {
        if (getState() != PendingOrder.RESTAURANT_SELECTED
                && getState() != PendingOrder.READY_FOR_CHECKOUT
                && getState() != PendingOrder.READY_TO_PLACE)
            throw new InvalidPendingOrderStateException();

        List menuItems = restaurant.getMenuItems();

        if (menuItems.size() != quantities.length)
            throw new InvalidPendingOrderStateException(
                    "Not the same size. Wanted: "
                            + menuItems.size()
                            + ", but got "
                            + quantities.length);

        List<PendingOrderLineItem> newLineItems = new ArrayList();
        Iterator it = menuItems.iterator();
        int index = 0;
        for (int i = 0; i < quantities.length; i++) {
            int quantity = quantities[i];
            MenuItem menuItem = (MenuItem) it.next();
            if (quantity > 0) {
                newLineItems.add(makeLineItem(index++,
                        menuItem, quantity));
            }

        }

        lineItems = newLineItems;

        this.state = meetsMinimumOrder() ? PendingOrder.READY_FOR_CHECKOUT
                : PendingOrder.RESTAURANT_SELECTED;

    }

    public boolean checkout()
            throws InvalidPendingOrderStateException {
        if (getState() != PendingOrder.READY_FOR_CHECKOUT
                && getState() != PendingOrder.RESTAURANT_SELECTED)
            throw new InvalidPendingOrderStateException();

        return getState() == PendingOrder.READY_FOR_CHECKOUT;
    }

    protected boolean meetsMinimumOrder() {
        return getSubtotal() > 0;
    }

    public double getSubtotal() {
        if (getLineItems() == null)
            return 0;

        double subTotal = 0;
        Iterator it = getLineItems().iterator();
        while (it.hasNext()) {
            PendingOrderLineItem li = (PendingOrderLineItem) it
                    .next();
            subTotal += li.getExtendedPrice();
        }
        return subTotal;
    }

    public double getDeliveryCharges() {
        return 5.00;
    }

    public double getSalesTax() {
        return getDiscountedSubtotal() * 0.0825; // TODO Make me real one day
    }

    public double getTotal() {
        return getDiscountedSubtotal()
                + getDiscountedDeliveryCharges()
                + getSalesTax();
    }

    private double getDiscountedDeliveryCharges() {
        return getDeliveryCharges()
                - getDeliveryChargeDiscount();
    }

    private double getDeliveryChargeDiscount() {
        if (getCoupon() == null)
            return 0;
        else
            return getCoupon()
                    .getDeliveryChargeDiscount(
                            getPendingOrder());
    }

    public double getDiscountedSubtotal() {
        return getSubtotal() - getSubtotalDiscount();
    }

    private double getSubtotalDiscount() {
        if (getCoupon() == null)
            return 0;
        else
            return getCoupon().getSubtotalDiscount(
                    getPendingOrder());
    }

    /**
     * Change the payment information
     * 
     * @param paymentInformation
     * @exception InvalidPendingOrderStateException
     */
    public void updatePaymentInformation(
            PaymentInformation paymentInformation,
            Coupon coupon)
            throws InvalidPendingOrderStateException {
        if (state != PendingOrder.READY_FOR_CHECKOUT)
            throw new InvalidPendingOrderStateException();
        this.coupon = coupon;
        this.paymentInformation = paymentInformation;
        this.state = PendingOrder.READY_TO_PLACE;
    }

    public boolean isReadyToPlace() {
        return state == PendingOrder.READY_TO_PLACE;
    }

    protected void initializeLineItems(
            Restaurant restaurant,
            List quantitiesAndMenuItems) {
        this.lineItems.clear();
        updateQuantities(quantitiesAndMenuItems);
    }

    private void updateQuantities(
            List quantitiesAndMenuItems) {
        int index = 0;
        for (Iterator it = quantitiesAndMenuItems
                .iterator(); it.hasNext();) {
            QuantityAndMenuItem qm = (QuantityAndMenuItem) it
                    .next();
            MenuItem menuItem = qm.getMenuItem();
            List newLineItems = new ArrayList();

            int quantity = qm.getQuantity();
            if (quantity > 0) {
                newLineItems.add(makeLineItem(index++,
                        menuItem, quantity));

            }

            this.lineItems = newLineItems;
        }
    }

    public void noteAuthorized(
            AuthorizationTransaction txn) {
    	// TODO - make me real one day
    }

    public void notePlaced() {
        this.state = PendingOrder.PLACED;
    }

    public RestaurantDetail getRestaurantDetail() {
        return restaurant;
    }

}