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
 
package net.chrisrichardson.foodToGo.restaurantNotificationService.tsImpl;

import java.util.*;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.commonImpl.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.tsImpl.dao.*;

public class JDBCRestaurantNotificationService implements
        RestaurantNotificationService {

    private RestaurantNotificationGateway notificationGateway;

    private OrderDAO orderDAO;

    JDBCRestaurantNotificationService(OrderDAO orderDAO,
            RestaurantNotificationGateway notificationGateway) {
        this.orderDAO = orderDAO;
        this.notificationGateway = notificationGateway;
    }

    public boolean sendOrders() {
        List orders = orderDAO.findOrdersToSend();
        List notifications = sendOrders(orders);
        orderDAO.markAsSent(orders, notifications);
        return !notifications.isEmpty();
    }

    private List sendOrders(List orders) {
        List notifications = new ArrayList();
        for (Iterator it = orders.iterator(); it.hasNext();) {
            OrderDTO order = (OrderDTO) it.next();
            RestaurantDTO restaurant = order.getRestaurant();
            NotificationDetails notification = notificationGateway
                    .sendOrder(restaurant, order);
            notifications.add(notification);
        }
        return notifications;
    }

}