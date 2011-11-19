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
import org.jmock.cglib.*;

public class HibernateNamedQueryExecutorCallbackTests extends
        MockObjectTestCase {

    public void test() throws HibernateException {
        Mock mockSession = new Mock(Session.class);
        List expectedResult = new ArrayList();

        Session session = (Session) mockSession.proxy();

        Mock mockQuery = new Mock(Query.class);
        Query query = (Query) mockQuery.proxy();
        
        HibernateQueryParameters queryParams = new HibernateQueryParameters("MyQuery");
        queryParams.setString("x", "y");

        mockSession.expects(once()).method("getNamedQuery").with(
                eq("MyQuery")).will(returnValue(query));

        mockQuery.expects(once()).method("setCacheable").with(eq(true));
        mockQuery.expects(once()).method("setParameter").with(eq("x"), eq("y"));
        mockQuery.expects(once()).method("list").withNoArguments().will(returnValue(expectedResult));
        
        HibernateNamedQueryExecutorCallback cb = new HibernateNamedQueryExecutorCallback(
                queryParams);
        Object result = cb.doInHibernate(session);
        assertSame(expectedResult, result);
    }
}