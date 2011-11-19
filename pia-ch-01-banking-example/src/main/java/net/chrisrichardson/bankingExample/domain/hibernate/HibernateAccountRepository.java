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
 


package net.chrisrichardson.bankingExample.domain.hibernate;

import java.sql.*;

import net.chrisrichardson.bankingExample.domain.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;

public class HibernateAccountRepository implements AccountRepository {

    private HibernateTemplate hibernateTemplate;

    public HibernateAccountRepository(HibernateTemplate template) {
        assert template != null;
        hibernateTemplate = template;
    }

    public Account createAccount(String accountId, double initialBalance) {
        Account account = new Account(accountId, initialBalance);
        hibernateTemplate.save(account);
        return account;
    }

    public Account findAccount(final String accountId) {
        return (Account) hibernateTemplate.execute(new HibernateCallback() {

            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Query query = session
                        .getNamedQuery("Account.findAccountByAccountId");
                query.setParameter("accountId", accountId);
                return query.uniqueResult();
            }
        });
    }


}
