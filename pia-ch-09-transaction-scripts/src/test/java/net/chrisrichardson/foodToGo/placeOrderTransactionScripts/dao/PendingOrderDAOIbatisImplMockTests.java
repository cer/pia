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

import java.util.*;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;

import org.jmock.cglib.*;
import org.jmock.core.*;
import org.springframework.orm.ibatis.*;

public class PendingOrderDAOIbatisImplMockTests extends
        MockObjectTestCase {

    private Mock mockSqlMapClientTemplate;

    private PendingOrderDAOIbatisImpl dao;

    private PendingOrderDTO pendingOrder;

    protected void setUp() throws Exception {
        super.setUp();
        mockSqlMapClientTemplate = new Mock(
                SqlMapClientTemplate.class);
        SqlMapClientTemplate sqlMapClientTemplate = (SqlMapClientTemplate) mockSqlMapClientTemplate
                .proxy();
        pendingOrder = new PendingOrderDTO();

        dao = new PendingOrderDAOIbatisImpl(sqlMapClientTemplate);
    }

   public void testFindAndCreatePendingOrder_new() {
        mockSqlMapClientTemplate.expects(once()).method("insert")
                .with(eq("insertPendingOrder"),
                        new MatchNewPendingOrder()).will(
                        returnValue(pendingOrder));
        PendingOrderDTO result = dao.createPendingOrder();
        assertSame(pendingOrder, result);
    }

    public void testFindAndCreatePendingOrder_existing() {
        mockSqlMapClientTemplate.expects(once()).method(
                "queryForObject").with(eq("findPendingOrder"),
                eq("10")).will(returnValue(pendingOrder));
        PendingOrderDTO result = dao.findPendingOrder("10");
        assertSame(pendingOrder, result);
    }

    private final class MatchNewPendingOrder implements Constraint {
        public boolean eval(Object arg0) {
            assertNotNull(arg0);
            pendingOrder = (PendingOrderDTO) arg0;
            return true;
        }

        public StringBuffer describeTo(StringBuffer arg0) {
            return arg0.append("Expecting PendingOrder");
        }
    }


    // TODO test case where restaurant is null

    public void testSavePendingOrder_simple() {
        mockSqlMapClientTemplate.expects(once()).method("update")
                .with(eq("savePendingOrder"),
                        eq(pendingOrder));

        mockSqlMapClientTemplate.expects(once()).method("update")
                .with(eq("deleteLineItems"),
                        eq(pendingOrder));

        dao.savePendingOrder(pendingOrder);
    }

    public void testSavePendingOrder_withLineItems() {
        pendingOrder.setLineItems(Collections
                .singletonList(new PendingOrderLineItemDTO()));

        mockSqlMapClientTemplate.expects(once()).method("update")
                .with(eq("savePendingOrder"),
                        eq(pendingOrder));

        mockSqlMapClientTemplate.expects(once()).method("update")
                .with(eq("deleteLineItems"),
                        eq(pendingOrder));

        InsertLineItemsIBatisCallback expectedCallback = new InsertLineItemsIBatisCallback(
                pendingOrder);

        mockSqlMapClientTemplate.expects(once()).method("execute")
                .with(eq(expectedCallback));

        dao.savePendingOrder(pendingOrder);
    }
}