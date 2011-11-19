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

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.ormunit.ibatis.*;

import com.ibatis.sqlmap.engine.impl.*;
import com.ibatis.sqlmap.engine.mapping.parameter.*;
import com.ibatis.sqlmap.engine.mapping.result.*;
import com.ibatis.sqlmap.engine.mapping.statement.*;

public class IBatisMappingTests extends IBatisTests {

    private ExtendedSqlMapClient sqlMapClient;

    private MappedStatement mappedStatement;

    private ResultMapping[] resultMappings;

 
	protected String[] getConfigLocations() {
		return IBatisDAOTestConstants.IBATIS_DAO_CONTEXT;
	}

    public void setSqlMapClient(ExtendedSqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}

	public void test() throws Exception {
        assertMappedStatement("findPendingOrder",
                String.class);
        assertResultMap(PendingOrderDTO.class);

        ParameterMapping[] pms = mappedStatement
                .getParameterMap()
                .getParameterMappings();
        ParameterMapping pm = pms[0];

        assertColumnMapping("pendingOrderId",
                "PENDING_ORDER_ID");
        assertSelectMapping("lineItems",
                "PENDING_ORDER_ID", "findLineItems");
    }

    public void testInline() throws Exception {
        mappedStatement = sqlMapClient
                .getMappedStatement("findPendingOrder");
        assertEquals(String.class, mappedStatement
                .getParameterClass());
        ResultMap resultMap = mappedStatement
                .getResultMap();
        assertEquals(PendingOrderDTO.class, resultMap
                .getResultClass());
        resultMappings = resultMap.getResultMappings();

        BasicResultMapping idMapping = findBasicResultMapping("pendingOrderId");
        assertEquals("PENDING_ORDER_ID", idMapping
                .getColumnName());
        BasicResultMapping lineItemsMapping = findBasicResultMapping("lineItems");
        assertEquals("PENDING_ORDER_ID",
                lineItemsMapping.getColumnName());
        assertEquals("findLineItems", lineItemsMapping
                .getStatementName());
    }

    private BasicResultMapping findBasicResultMapping(
            String propertyName) {
        for (int i = 0; i < resultMappings.length; i++) {
            ResultMapping mapping = resultMappings[i];
            if (mapping.getPropertyName().equals(
                    propertyName)) {
                return (BasicResultMapping) mapping;
            }
        }
        fail("no mapping for property: "
                + propertyName);
        return null;
    }

    private void assertResultMap(Class resultClass) {
        ResultMap resultMap = mappedStatement
                .getResultMap();
        assertEquals(resultClass, resultMap
                .getResultClass());
        resultMappings = resultMap.getResultMappings();
    }

    private void assertMappedStatement(
            String statementName, Class parameterType) {
        mappedStatement = sqlMapClient
                .getMappedStatement(statementName);
        assertEquals(parameterType, mappedStatement
                .getParameterClass());
    }

    private void assertColumnMapping(
            String propertyName, String columnName) {
        ResultMapping mapping = findMapping(propertyName);
        assertEquals(columnName,
                ((BasicResultMapping) mapping)
                        .getColumnName());
    }

    private ResultMapping findMapping(
            String propertyName) {
        for (int i = 0; i < resultMappings.length; i++) {
            ResultMapping mapping = resultMappings[i];
            if (mapping.getPropertyName().equals(
                    propertyName)) {
                return mapping;
            }
        }
        fail("no mapping for property: "
                + propertyName);
        return null;
    }

    private void assertSelectMapping(
            String propertyName, String columnName,
            String selectName) {
        ResultMapping mapping = findMapping(propertyName);
        BasicResultMapping basicResultMapping = ((BasicResultMapping) mapping);
        assertEquals(columnName, basicResultMapping
                .getColumnName());
        assertEquals(selectName, basicResultMapping
                .getStatementName());
    }

}