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

import net.chrisrichardson.foodToGo.util.*;

import org.springframework.orm.hibernate3.*;

public class HibernateQueryExecutor implements HibernateWrapper {

    HibernateTemplate hibernateTemplate;

    HibernateQueryExecutor() {
    }

    public HibernateQueryExecutor(
            HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public List executeNamedQuery(
            HibernateQueryParameters queryParams) {
        return hibernateTemplate
                .executeFind(new HibernateNamedQueryExecutorCallback(
                        queryParams));
    }

    public PagedQueryResult executeCriteriaQuery(Class queryClass,
            int startingIndex, int pageSize, CriteriaBuilder builder) {
        return (PagedQueryResult) hibernateTemplate
                .execute(new HibernateCriteriaQueryExecutorCallback(
                        queryClass, builder, startingIndex,
                        pageSize));
    }

}