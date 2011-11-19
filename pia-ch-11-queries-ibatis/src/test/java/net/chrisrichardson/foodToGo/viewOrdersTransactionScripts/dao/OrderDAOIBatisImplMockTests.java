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
 
package net.chrisrichardson.foodToGo.viewOrdersTransactionScripts.dao;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;

import org.jmock.cglib.*;
import org.jmock.core.*;
import org.springframework.orm.ibatis.*;

public class OrderDAOIBatisImplMockTests extends MockObjectTestCase {

	private Mock mockSqlMapClientTemplate;

	private OrderDAOIBatisImpl dao;

	private Mock mockPagedQueryExecutor;

	protected void setUp() throws Exception {
		super.setUp();
		mockSqlMapClientTemplate = new Mock(SqlMapClientTemplate.class);
		SqlMapClientTemplate sqlMapClientTemplate = (SqlMapClientTemplate) mockSqlMapClientTemplate
				.proxy();
		mockPagedQueryExecutor = new Mock(IBatisPagedQueryExecutor.class);
		dao = new OrderDAOIBatisImpl(sqlMapClientTemplate,
				(IBatisPagedQueryExecutor) mockPagedQueryExecutor.proxy());
	}

	public void testFindOrders() throws Exception {
		OrderSearchCriteria criteria = new OrderSearchCriteria();

		PagedQueryResult executorResult = new PagedQueryResult(
				Collections.EMPTY_LIST, true);

		mockPagedQueryExecutor.expects(once()).method("execute").with(
				new Constraint[] { eq("findOrders"), eq(0), eq(10),
						eq(criteria), eq(true) }).will(
				returnValue(executorResult));

		PagedQueryResult result = dao.findOrders(0, 10, criteria);

		assertSame(result, executorResult);
	}

}