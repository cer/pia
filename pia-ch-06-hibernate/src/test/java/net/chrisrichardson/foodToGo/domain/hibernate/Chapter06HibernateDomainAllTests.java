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

import junit.framework.*;
import net.chrisrichardson.foodToGo.util.hibernate.*;

public class Chapter06HibernateDomainAllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for net.chrisrichardson.foodToGo.domain.hibernate");
        // $JUnit-BEGIN$
        suite.addTestSuite(FoodToGoHibernateMappingTests.class);
        suite.addTestSuite(FoodToGoHibernateSchemaTests.class);
        suite.addTestSuite(HibernatePendingOrderPersistenceTests.class);
        suite.addTestSuite(HibernateOrderPersistenceTests.class);
        suite.addTestSuite(HibernatePendingOrderRepositoryTests.class);
        suite.addTestSuite(HibernateRestaurantRepositoryImplMockTest.class);
        suite.addTestSuite(FindAvailableRestaurantsQueryTests.class);
        suite.addTestSuite(HibernateRestaurantRepositoryQueryTests.class);
        suite.addTestSuite(HibernateNamedQueryExecutorCallbackTests.class);
        suite.addTestSuite(HibernateQueryExecutorTests.class);
        suite.addTestSuite(HibernateOrderRepositoryImplTests.class);
        // $JUnit-END$
        return suite;
    }
}