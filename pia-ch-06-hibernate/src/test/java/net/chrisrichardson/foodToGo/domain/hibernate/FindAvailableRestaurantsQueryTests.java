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
 
package net.chrisrichardson.foodToGo.domain.hibernate;

import java.util.*;

import net.chrisrichardson.foodToGo.util.hibernate.*;
import net.chrisrichardson.ormunit.hibernate.*;

public class FindAvailableRestaurantsQueryTests extends
        HibernatePersistenceTests {

    @Override
    protected String[] getConfigLocations() {
        return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

     }

    public void testQuery() throws Exception {
        HibernateQueryParameters queryParams = new HibernateQueryParameters("findAvailableRestaurants");
        queryParams.setString("zipCode", "84619");
        queryParams.setInteger("dayOfWeek", 6);
        queryParams.setInteger("hour", 1);
        queryParams.setInteger("minute", 3);

        HibernateWrapper qe = new HibernateQueryExecutor(getHibernateTemplate());
        List result = qe.executeNamedQuery(queryParams);
    }
}