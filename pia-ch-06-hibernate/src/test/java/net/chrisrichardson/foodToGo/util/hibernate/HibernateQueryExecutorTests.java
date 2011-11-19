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

import org.jmock.cglib.*;
import org.springframework.orm.hibernate3.*;

public class HibernateQueryExecutorTests extends
        MockObjectTestCase {

    private HibernateWrapper qe;

    private ArrayList expectedResults;

    private Mock mockHibernateTemplate;

    private HibernateTemplate hibernateTemplate;

    public void setUp() {

        mockHibernateTemplate = new Mock(HibernateTemplate.class);
        hibernateTemplate = (HibernateTemplate) mockHibernateTemplate
                .proxy();
        
        qe = new HibernateQueryExecutor(hibernateTemplate);
        expectedResults = new ArrayList();
    }

    public void testExecute() {
        HibernateQueryParameters queryParams = new HibernateQueryParameters("MyQuery");
        queryParams.setString("x", "y");
        HibernateNamedQueryExecutorCallback cb = new HibernateNamedQueryExecutorCallback(
                queryParams);

        mockHibernateTemplate.expects(once()).method("executeFind")
                .with(eq(cb)).will(returnValue(expectedResults));
        
        List actualResults = qe.executeNamedQuery(queryParams);
        assertSame(expectedResults, actualResults);
    }

}