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

import org.jmock.*;

import com.ibatis.sqlmap.client.*;

public class InsertLineItemsIBatisCallbackMockTests extends
        MockObjectTestCase {

    private InsertLineItemsIBatisCallback callback;

    private Mock mockSqlMapExecutor;

    private PendingOrderLineItemDTO lineItem;

    private SqlMapExecutor executor;

    private PendingOrderDTO pendingOrder;

    protected void setUp() throws Exception {
        super.setUp();
        pendingOrder = new PendingOrderDTO();
        lineItem = new PendingOrderLineItemDTO();
        pendingOrder.setLineItems(Collections
                .singletonList(lineItem));

        mockSqlMapExecutor = new Mock(SqlMapExecutor.class);
        executor = (SqlMapExecutor) mockSqlMapExecutor.proxy();
        callback = new InsertLineItemsIBatisCallback(pendingOrder);
    }

    public void test() throws SQLException {
        mockSqlMapExecutor.expects(once()).method("startBatch");
        Map lineItemMap = callback.makeMapForLineItem(pendingOrder, lineItem);
        mockSqlMapExecutor.expects(once()).method("insert").with(eq("saveLineItem"), eq(lineItemMap));
        mockSqlMapExecutor.expects(once()).method("executeBatch").withNoArguments().will(returnValue(1));
        callback.doInSqlMapClient(executor);

    }
}