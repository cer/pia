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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.dao;

import java.sql.*;
import java.util.*;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;

import org.springframework.orm.ibatis.*;

import com.ibatis.sqlmap.client.*;

public class InsertLineItemsIBatisCallback implements
        SqlMapClientCallback {
    private PendingOrderDTO pendingOrder;

    InsertLineItemsIBatisCallback(PendingOrderDTO pendingOrder) {
        this.pendingOrder = pendingOrder;
    }

    public boolean equals(Object x) {
        if (x == null)
            return false;
        if (!(x instanceof InsertLineItemsIBatisCallback))
            return false;
        InsertLineItemsIBatisCallback other = (InsertLineItemsIBatisCallback) x;
        return pendingOrder.equals(other.pendingOrder);
    }

    public int hashCode() {
        return pendingOrder.hashCode();
    }

    public Object doInSqlMapClient(SqlMapExecutor executor)
            throws SQLException {
        executor.startBatch();
        List lineItems = pendingOrder.getLineItems();
        for (Iterator it = lineItems.iterator(); it.hasNext();) {
            PendingOrderLineItemDTO lineItem = (PendingOrderLineItemDTO) it
                    .next();
            Map map = makeMapForLineItem(pendingOrder, lineItem);
            executor.insert("saveLineItem", map);

        }
        executor.executeBatch();
        return null;
    }

    public Map makeMapForLineItem(PendingOrderDTO pendingOrder, PendingOrderLineItemDTO lineItem) {
        Map map = new HashMap();
        map.put("lineItem", lineItem);
        map.put("pendingOrder", pendingOrder);
        return map;
    }
}