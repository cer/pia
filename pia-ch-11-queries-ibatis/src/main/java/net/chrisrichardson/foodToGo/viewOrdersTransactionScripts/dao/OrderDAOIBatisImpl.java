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

import org.springframework.orm.ibatis.*;
import org.springframework.orm.ibatis.support.*;

public class OrderDAOIBatisImpl extends SqlMapClientDaoSupport implements
		OrderDAO {

	private IBatisPagedQueryExecutor pagedQueryExecutor;

	public OrderDAOIBatisImpl(SqlMapClientTemplate sqlMapClientTemplate,
			IBatisPagedQueryExecutor pagedQueryExecutor) {
		setSqlMapClientTemplate(sqlMapClientTemplate);
		this.pagedQueryExecutor = pagedQueryExecutor;
	}

	/** 
	 * This is the official version of find orders
	 * It uses the IBatisPagedQueryExecutor
	 */
	public PagedQueryResult findOrders(int startingIndex, int pageSize,
			OrderSearchCriteria criteria) {
		return pagedQueryExecutor.execute("findOrders", startingIndex,
				pageSize, criteria, true);
	}

	/** 
	 * This is one of the examples from the book
	 * It uses ROWNUM
	 * 
	 * @param startingIndex
	 * @param pageSize
	 * @param criteria
	 * @return
	 */
	public PagedQueryResult findOrdersInline(int startingIndex, int pageSize,
			OrderSearchCriteria criteria) {
		Map map = new HashMap();
		map.put("start", new Integer(startingIndex));
		map.put("pageSize", new Integer(pageSize + startingIndex + 2));
		map.put("criteria", criteria);
		List result = getSqlMapClientTemplate().queryForList("findOrders", map);
		boolean more = result.size() > pageSize;
		if (more) {
			result.remove(pageSize);
		}
		return new PagedQueryResult(result, more);
	}

	/** 
	 * This is another example from the book
	 * It uses a query that does not use ROWNUM
	 * 
	 * @param startingIndex
	 * @param pageSize
	 * @param criteria
	 * @return
	 */
	public PagedQueryResult findOrdersInlineWithoutRownum(int startingIndex,
			int pageSize, OrderSearchCriteria criteria) {
		Map map = new HashMap();
		map.put("pageSize", new Integer(pageSize + startingIndex));
		map.put("criteria", criteria);
		List result = getSqlMapClientTemplate().queryForList("findOrders", map,
				startingIndex, pageSize);
		boolean more = result.size() > pageSize;
		if (more) {
			result.remove(pageSize);
		}
		return new PagedQueryResult(result, more);
	}

}