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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.dao.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.util.*;

/**
 * Implementation of transaction scripts for the Place Order use case
 * @author cer
 *
 */
public class PlaceOrderTransactionScriptsImpl
        implements PlaceOrderTransactionScripts {

    private RestaurantDAO restaurantDAO;

    private PendingOrderDAO pendingOrderDAO;

    public PlaceOrderTransactionScriptsImpl(
            PendingOrderDAO pendingOrderDAO,
            RestaurantDAO restaurantDAO) {
        this.pendingOrderDAO = pendingOrderDAO;
        this.restaurantDAO = restaurantDAO;
    }

    public UpdateDeliveryInfoResult updateDeliveryInfo(
            String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {

        PendingOrderDTO pendingOrder = findOrCreatePendingOrder(pendingOrderId);

        if (pendingOrder.getRestaurant() == null)
            return updateDeliveryInfoWithNoSelectedRestaurant(
                    pendingOrder, deliveryAddress,
                    deliveryTime);
        else
            return updateDeliveryInfoWithRestaurantAlreadySelected(
                    pendingOrder, deliveryAddress,
                    deliveryTime);
    }

    public UpdateDeliveryInfoResult updateDeliveryInfo_simple(
            String pendingOrderId,
            Address deliveryAddress, Date deliveryTime) {

        PendingOrderDTO pendingOrder = findOrCreatePendingOrder(pendingOrderId);

        Calendar earliestDeliveryTime = Calendar
                .getInstance();
        earliestDeliveryTime.add(Calendar.HOUR, 1);

        if (deliveryTime.before(earliestDeliveryTime
                .getTime())) {
            PendingOrderTotals totals = computeTotals(pendingOrder);
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.INVALID_DELIVERY_INFO,
                    pendingOrder, totals, null);
        }

        List availableRestaurants = restaurantDAO
                .findAvailableRestaurants(
                        deliveryAddress, deliveryTime);

        if (availableRestaurants.isEmpty()) {
            PendingOrderTotals totals = computeTotals(pendingOrder);
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.NO_RESTAURANT_AVAILABLE,
                    pendingOrder, totals, null);
        }

        pendingOrder
                .setDeliveryAddress(deliveryAddress);
        pendingOrder.setDeliveryTime(deliveryTime);
        pendingOrder
                .setState(PendingOrder.DELIVERY_INFO_SPECIFIED);

        pendingOrderDAO.savePendingOrder(pendingOrder);

        return new UpdateDeliveryInfoResult(
                UpdateDeliveryInfoResult.SELECT_RESTAURANT,
                pendingOrder, availableRestaurants);
    }

    private PendingOrderDTO findOrCreatePendingOrder(
            String pendingOrderId) {
        if (pendingOrderId == null)
            return pendingOrderDAO
                    .createPendingOrder();
        else
            return pendingOrderDAO
                    .findPendingOrder(pendingOrderId);
    }

    private PendingOrderTotals computeTotals(
            PendingOrderDTO pendingOrder) {
        return new PendingOrderTotals();
    }

    public UpdateDeliveryInfoResult updateDeliveryInfoWithNoSelectedRestaurant(
            PendingOrderDTO pendingOrder,
            Address deliveryAddress, Date deliveryTime) {

        List availableRestaurants = restaurantDAO
                .findAvailableRestaurants(
                        deliveryAddress, deliveryTime);

        if (availableRestaurants.isEmpty()) {
            PendingOrderTotals totals = computeTotals(pendingOrder);
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.NO_RESTAURANT_AVAILABLE,
                    pendingOrder, totals, null);
        }

        pendingOrder
                .setDeliveryAddress(deliveryAddress);
        pendingOrder.setDeliveryTime(deliveryTime);
        pendingOrder
                .setState(PendingOrder.DELIVERY_INFO_SPECIFIED);

        pendingOrderDAO.savePendingOrder(pendingOrder);

        PendingOrderTotals totals = computeTotals(pendingOrder);
        return makeUpdateDeliveryInfoResult(
                UpdateDeliveryInfoResult.SELECT_RESTAURANT,
                pendingOrder, totals,
                availableRestaurants);
    }

    public UpdateDeliveryInfoResult updateDeliveryInfoWithRestaurantAlreadySelected(
            PendingOrderDTO pendingOrder,
            Address deliveryAddress, Date deliveryTime) {
        PendingOrderTotals totals = computeTotals(pendingOrder);
        if (restaurantDAO.isInServiceArea(pendingOrder
                .getRestaurant().getRestaurantId(),
                deliveryAddress, deliveryTime)) {

            pendingOrder
                    .setDeliveryAddress(deliveryAddress);
            pendingOrder.setDeliveryTime(deliveryTime);

            pendingOrder.setState(PendingOrder.DELIVERY_INFO_SPECIFIED);
            
            pendingOrderDAO
                    .savePendingOrder(pendingOrder);
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.KEEP_RESTAURANT,
                    pendingOrder, totals, null);
        } else if (restaurantDAO
                .isRestaurantAvailable(
                        deliveryAddress, deliveryTime)) {
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.CONFIRM_CHANGE,
                    pendingOrder, totals, null);
        } else
            return makeUpdateDeliveryInfoResult(
                    UpdateDeliveryInfoResult.NO_RESTAURANT_AVAILABLE,
                    pendingOrder, totals, null);

    }

    public UpdateDeliveryInfoResult makeUpdateDeliveryInfoResult(
            int statusCode,
            PendingOrderDTO pendingOrder,
            PendingOrderTotals totals,
            List availableRestaurants) {
        pendingOrder.setTotals(totals);
        return new UpdateDeliveryInfoResult(
                statusCode, pendingOrder,
                availableRestaurants);
    }

    public PlaceOrderResult updateRestaurant(
            String pendingOrderId, String restaurantId) {
        PendingOrderDTO pendingOrder = findOrCreatePendingOrder(pendingOrderId);

        RestaurantDTO restaurant = restaurantDAO
                .findRestaurant(restaurantId);

        if (pendingOrder.getState() == PendingOrder.NEW
                || pendingOrder.getState() == PendingOrder.PLACED)
            throw new RuntimeException("State = "
                    + pendingOrder.getState());

        PendingOrderTotals totals = computeTotals(pendingOrder);
        if (restaurantDAO.isInServiceArea(restaurant
                .getRestaurantId(), pendingOrder
                .getDeliveryAddress(), pendingOrder
                .getDeliveryTime())) {
            pendingOrder.setRestaurant(restaurant);
            pendingOrder.setLineItems(new ArrayList());
            pendingOrder
                    .setState(PendingOrder.RESTAURANT_SELECTED);
            pendingOrderDAO
                    .savePendingOrder(pendingOrder);
            return makePlaceOrderResult(
                    PlaceOrderResult.OK, pendingOrder,
                    totals);
        } else {
            return makePlaceOrderResult(
                    PlaceOrderResult.INCOMPATIBLE,
                    pendingOrder, totals);
        }
    }

    private PlaceOrderResult makePlaceOrderResult(
            int statusCode,
            PendingOrderDTO pendingOrder,
            PendingOrderTotals totals) {
        pendingOrder.setTotals(totals);
        return new PlaceOrderResult(statusCode,
                pendingOrder);
    }

    public static void createLineItems(
            PendingOrderDTO pendingOrder,
            Collection menuItems) {
        Iterator it = menuItems.iterator();
        ArrayList newLineItems = new ArrayList();
        int index = 0;
        while (it.hasNext()) {
            MenuItemDTO miv = (MenuItemDTO) it.next();
            newLineItems
                    .add(new PendingOrderLineItemDTO(
                            index++, miv));
        }
        pendingOrder.setLineItems(newLineItems);
    }

    public PlaceOrderResult updateQuantities(
            String pendingOrderId, int[] quantities) {
        PendingOrderDTO pendingOrder = findOrCreatePendingOrder(pendingOrderId);

        if (pendingOrder.getState() != PendingOrder.RESTAURANT_SELECTED
                && pendingOrder.getState() != PendingOrder.READY_FOR_CHECKOUT
                && pendingOrder.getState() != PendingOrder.READY_TO_PLACE)
            throw new RuntimeException();

        List lineItems = pendingOrder.getLineItems();

        if (lineItems.size() != quantities.length)
            throw new RuntimeException(
                    "Not the same size. Wanted: "
                            + lineItems.size()
                            + ", but got "
                            + quantities.length);

        List menuItems = pendingOrder.getRestaurant()
                .getMenuItems();
        List newLineItems = new ArrayList();
        Iterator it = menuItems.iterator();
        int index = 0;
        for (int i = 0; i < quantities.length; i++) {
            MenuItemDTO menuItem = (MenuItemDTO) it
                    .next();
            int quantity = quantities[i];
            if (quantity > 0)
                newLineItems
                        .add(new PendingOrderLineItemDTO(
                                index++, quantity,
                                menuItem));
        }
        pendingOrder.setLineItems(newLineItems);

        pendingOrder
                .setState(meetsMinimumOrder() ? PendingOrder.READY_FOR_CHECKOUT
                        : PendingOrder.RESTAURANT_SELECTED);

        pendingOrderDAO.savePendingOrder(pendingOrder);

        PendingOrderTotals totals = computeTotals(pendingOrder);
        return makePlaceOrderResult(
                PlaceOrderResult.OK, pendingOrder,
                totals);
    }

    private boolean meetsMinimumOrder() {
        return true;
    }

}