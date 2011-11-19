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
 
package net.chrisrichardson.foodToGo.ui.domain.servlets;

import javax.servlet.*;

import net.chrisrichardson.foodToGo.domain.*;

import org.springframework.orm.hibernate3.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

/**
 * Startup servlet that populates teh database with a restaurant
 * @author cer
 *
 */
public class InitializeDatabaseServlet extends BaseSpringServlet {

    public void init(ServletConfig sc) throws ServletException {
        super.init(sc);
        HibernateTransactionManager tm = (HibernateTransactionManager) getBean(
                "myTransactionManager",
                HibernateTransactionManager.class);
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus status) {
                HibernateTemplate hibernateTemplate = (HibernateTemplate) getBean(
                        "HibernateTemplate", HibernateTemplate.class);
                hibernateTemplate.save(RestaurantMother
                        .makeRestaurant());
                return null;
            }
        });
    }

}