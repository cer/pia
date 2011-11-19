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
 
package net.chrisrichardson.foodToGo.viewOrders.hibernateImpl;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.hibernate.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.viewOrders.*;
import net.chrisrichardson.ormunit.hibernate.*;

public class SpringHibernateViewOrderServiceTests extends HibernatePersistenceTests {

    private ViewOrdersService service;
    
    
    public void setService(ViewOrdersService service) {
        this.service = service;
    }


    @Override
    protected String[] getConfigLocations() {
        String[] facadeFiles = {
                "view-orders-local-transaction.xml",
                "view-orders-hibernate-detacher.xml",
                "view-orders-domain-service.xml"};

        return ArrayUtils.concatenate(
                facadeFiles,
                HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT);
    }

 
    public void test() throws Exception {
        PagedQueryResult results = service.findOrders(0, 10,
                new OrderSearchCriteria());
        assertNotNull(results);
    }

}