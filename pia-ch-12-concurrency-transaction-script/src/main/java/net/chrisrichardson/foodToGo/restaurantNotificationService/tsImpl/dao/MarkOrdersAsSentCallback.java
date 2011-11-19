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
 
package net.chrisrichardson.foodToGo.restaurantNotificationService.tsImpl.dao;

import java.sql.*;
import java.util.*;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.restaurantNotificationService.commonImpl.*;

import org.springframework.orm.ibatis.*;

import com.ibatis.sqlmap.client.*;

public class MarkOrdersAsSentCallback implements
        SqlMapClientCallback {

    private String lockingMode;
    private List orders;

    private List notifications;

    public MarkOrdersAsSentCallback(List orders,
            List notifications, String lockingMode) {
        this.orders = orders;
        this.notifications = notifications;
        this.lockingMode = lockingMode;
    }

    public boolean equals(Object x) {
        if (x == null)
            return false;
        if (!(x instanceof MarkOrdersAsSentCallback))
            return false;

        MarkOrdersAsSentCallback other = (MarkOrdersAsSentCallback) x;

        return orders.equals(other.orders)
                && notifications.equals(other.notifications)
                & lockingMode.equals(other.lockingMode);
    }

    public int hashCode() {
        return orders.hashCode() ^ notifications.hashCode() ^ lockingMode.hashCode();
    }

    public Object doInSqlMapClient(SqlMapExecutor executor)
            throws SQLException {
        executor.startBatch();
        Iterator ordersIt = orders.iterator();
        Iterator notificationsIt = notifications.iterator();
        while (ordersIt.hasNext()) {
            OrderDTO order = (OrderDTO) ordersIt.next();
            NotificationDetails notification = (NotificationDetails) notificationsIt
                    .next();
            Map map = makeMapForOrder(order, notification);
            executor.insert("markOrderAsSent_" + lockingMode, map);
        }
        executor.executeBatch();
        return null;
    }

    public Map makeMapForOrder(OrderDTO order,
            NotificationDetails notification) {
        Map map = new HashMap();
        map.put("orderId", order.getOrderId());
        map.put("version", new Integer(order.getBizVersion()));
        map.put("messageId", notification.getMessageId());
        map.put("timestamp", new Timestamp(notification.getTimestamp().getTime()));
        return map;
    }

}