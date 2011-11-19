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

import javax.persistence.*;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;

@SuppressWarnings("serial")
@Entity(access = AccessType.FIELD)
@Table(name = "FTGO_PENDING_ORDER")
public class PendingOrder implements Serializable {

    // begin(fields)

    // private int version;

    @Transient
    public static final int NEW = 0;

    @Transient
    public static final int DELIVERY_INFO_SPECIFIED = 1;

    @Transient
    public static final int RESTAURANT_SELECTED = 2;

    @Transient
    public static final int READY_FOR_CHECKOUT = 3;

    @Transient
    public static final int NEEDS_PAYMENT = 4;

    @Transient
    public static final int READY_TO_PLACE = 5;

    @Transient
    public static final int PLACED = 6;

    @SuppressWarnings("unused")
    private int version;

    @Id(generate = GeneratorType.AUTO)
    private int id; // Hibernate

    private int state = PendingOrder.NEW;

    @Column(name = "DELIVERY_TIME")
    private Date deliveryTime;

    @ManyToOne
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant restaurant;

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = "PENDING_ORDER_ID")
    @org.hibernate.annotations.IndexColumn(name="MENU_ITEM_INDEX")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PendingOrderLineItem> lineItems = new ArrayList<PendingOrderLineItem>();

    @ManyToOne(cascade = CascadeType.PERSIST, targetEntity = AbstractCouponImpl.class)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "street1", column = @Column(name = "DELIVERY_STREET1")),
            @AttributeOverride(name = "street2", column = @Column(name = "DELIVERY_STREET2")),
            @AttributeOverride(name = "city", column = @Column(name = "DELIVERY_CITY")),
            @AttributeOverride(name = "state", column = @Column(name = "DELIVERY_STATE")),
            @AttributeOverride(name = "zip", column = @Column(name = "DELIVERY_ZIP")) })
    private Address deliveryAddress;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "type", column = @Column(name = "PAYMENT_TYPE")),
            @AttributeOverride(name = "name", column = @Column(name = "PAYMENT_NAME")),
            @AttributeOverride(name = "number", column = @Column(name = "PAYMENT_NUMBER")),
            @AttributeOverride(name = "month", column = @Column(name = "PAYMENT_MONTH")),
            @AttributeOverride(name = "year", column = @Column(name = "PAYMENT_YEAR")) })
    private PaymentInformation paymentInformation;

    // end(fields)

    public PendingOrder() {

    }

    public PendingOrder(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

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

    protected void clearLineItems() {
        lineItems = new ArrayList<PendingOrderLineItem>();
    }

    public List<PendingOrderLineItem> getLineItems() {
        return lineItems;
    }

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }

    protected void setPaymentInformation(
            PaymentInformation paymentInformation) {
        this.paymentInformation = paymentInformation;
    }

    protected void setDeliveryAddress(Address address) {
        this.deliveryAddress = address;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    protected void setAuthorizationTransaction(
            AuthorizationTransaction txn) {
        // TODO - fix me
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    protected PendingOrder getPendingOrder() {
        return this;
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
        return getRestaurant() == null
                || getRestaurant().isInServiceArea(
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

        if (getRestaurant() != null) {
            if (getRestaurant().isInServiceArea(
                    deliveryAddress, deliveryTime)) {
                setDeliveryAddress(deliveryAddress);
                setDeliveryTime(deliveryTime);
                // getState() unchanged
                return PlaceOrderStatusCodes.OK;
            } else if (!force) {
                return PlaceOrderStatusCodes.CONFIRM_CHANGE;
            }
        }

        setDeliveryAddress(deliveryAddress);
        setDeliveryTime(deliveryTime);
        setRestaurant(null);
        setState(PendingOrder.DELIVERY_INFO_SPECIFIED);

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

        if (getState() == PendingOrder.NEW
                || getState() == PendingOrder.PLACED)
            throw new InvalidPendingOrderStateException(
                    "State = " + getState());

        if (restaurant.isInServiceArea(
                getDeliveryAddress(),
                getDeliveryTime())) {
            setRestaurant(restaurant);
            clearLineItems();
            setState(PendingOrder.RESTAURANT_SELECTED);
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

        List<MenuItem> menuItems = restaurant
                .getMenuItems();

        if (menuItems.size() != quantities.length)
            throw new InvalidPendingOrderStateException(
                    "Not the same size. Wanted: "
                            + menuItems.size()
                            + ", but got "
                            + quantities.length);

        lineItems.clear();
        // while (lineItems.size() > 0) lineItems.remove(0);

        Iterator it = menuItems.iterator();
        int index = 0;
        for (int i = 0; i < quantities.length; i++) {
            int quantity = quantities[i];
            MenuItem menuItem = (MenuItem) it.next();
            if (quantity > 0) {
                lineItems
                        .add(new PendingOrderLineItem(
                                quantity, menuItem));
            }

        }

        setState(meetsMinimumOrder() ? PendingOrder.READY_FOR_CHECKOUT
                : PendingOrder.RESTAURANT_SELECTED);
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
        Iterator<PendingOrderLineItem> it = getLineItems()
                .iterator();
        while (it.hasNext()) {
            PendingOrderLineItem li = it.next();
            subTotal += li.getExtendedPrice();
        }
        return subTotal;
    }

    public double getDeliveryCharges() {
        return 5.00;
    }

    public double getSalesTax() {
        return getDiscountedSubtotal() * 0.0825;
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
        if (getState() != PendingOrder.READY_FOR_CHECKOUT)
            throw new InvalidPendingOrderStateException();
        // this.coupon = coupon;
        setPaymentInformation(paymentInformation);
        setState(PendingOrder.READY_TO_PLACE);
    }

    public boolean isReadyToPlace() {
        return getState() == PendingOrder.READY_TO_PLACE;
    }

    public void noteAuthorized(
            AuthorizationTransaction txn) {
    }

    public void notePlaced() {
        setState(PendingOrder.PLACED);
    }

}