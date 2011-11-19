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

import java.util.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;

final class HibernateNamedQueryExecutorCallback implements
        HibernateCallback {
    private HibernateQueryParameters queryParams;

    public HibernateNamedQueryExecutorCallback(
            HibernateQueryParameters queryParams) {
        this.queryParams = queryParams;
    }

    public boolean equals(Object x) {
        if (!(x instanceof HibernateNamedQueryExecutorCallback))
            return false;
        if (x == null)
            return false;
        HibernateNamedQueryExecutorCallback other = (HibernateNamedQueryExecutorCallback) x;
        return queryParams.equals(other.queryParams);
    }
    public int hashCode() {
        return queryParams.hashCode();
    }
    public Object doInHibernate(Session session)
            throws HibernateException {
        Query query = session.getNamedQuery(queryParams
                .getQueryName());
        query.setCacheable(true);
        for (Iterator it = queryParams.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Object value = entry.getValue();
            query.setParameter(name, value);
        }
        if (queryParams.getLockAlias() != null) {
            query.setLockMode(queryParams.getLockAlias(), queryParams.getLockMode());            
        }
        if (queryParams.isJustOne())
            query.setMaxResults(1);
        return query.list();
    }
}