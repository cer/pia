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

/**
 * Handy utility class for executing iBATIS queries that return a page of results
 * @author cer
 *
 */
public class IBatisPagedQueryExecutor extends
        SqlMapClientDaoSupport {

    protected IBatisPagedQueryExecutor() {
        
    }
    
    public IBatisPagedQueryExecutor(SqlMapClientTemplate template) {
        setSqlMapClientTemplate(template);
    }

    public PagedQueryResult execute(String queryName,
            int startingIndex, int pageSize,
            OrderSearchCriteria criteria, boolean useRowNum) {
        List result = executeQuery(queryName, startingIndex,
                pageSize, criteria, useRowNum);
        boolean more = result.size() > pageSize;
        if (more) {
            result.remove(pageSize);
        }
        return new PagedQueryResult(result, more);
    }

    private List executeQuery(String queryName, int startingIndex,
            int pageSize, OrderSearchCriteria criteria,
            boolean useRowNum) {
        if (useRowNum) {
            return executeQueryUsingRowNum(queryName,
                    startingIndex, pageSize, criteria);
        } else
            return executeQueryWithoutRownum(queryName,
                    startingIndex, pageSize, criteria);
    }

    private List executeQueryWithoutRownum(String queryName,
            int startingIndex, int pageSize,
            OrderSearchCriteria criteria) {
        return getSqlMapClientTemplate().queryForList(queryName,
                criteria, startingIndex, pageSize + 1);
    }

    public List executeQueryUsingRowNum(String queryName,
            int startingIndex, int pageSize,
            OrderSearchCriteria criteria) {
        Map map = new HashMap();
        map.put("start", new Integer(startingIndex));
        map.put("maxRows",
                new Integer(pageSize + startingIndex + 2));
        map.put("criteria", criteria);
        return getSqlMapClientTemplate().queryForList(queryName,
                map);
    }

}