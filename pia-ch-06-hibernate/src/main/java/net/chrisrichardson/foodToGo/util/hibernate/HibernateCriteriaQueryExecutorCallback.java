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
 
package net.chrisrichardson.foodToGo.util.hibernate;

import java.sql.*;
import java.util.*;

import net.chrisrichardson.foodToGo.util.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;

final class HibernateCriteriaQueryExecutorCallback implements
        HibernateCallback {
    private final Class queryClass;

    private final CriteriaBuilder builder;

    private final int startingIndex;

    private final int pageSize;

    HibernateCriteriaQueryExecutorCallback(Class queryClass,
            CriteriaBuilder builder, int startingIndex, int pageSize) {
        super();
        this.queryClass = queryClass;
        this.builder = builder;
        this.startingIndex = startingIndex;
        this.pageSize = pageSize;
    }

    public Object doInHibernate(Session session)
            throws HibernateException, SQLException {
        Criteria criteria = session.createCriteria(queryClass);
        builder.addCriteria(criteria);
        criteria.setFirstResult(startingIndex);
        criteria.setMaxResults(pageSize + 1);
        List result = criteria.list();
        boolean more = result.size() > pageSize;
        if (more) {
            result.remove(pageSize);
        }
        return new PagedQueryResult(result, more);
    }
}