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
 
package net.chrisrichardson.ormunit.hibernate;

import net.chrisrichardson.util.*;

import org.springframework.orm.hibernate3.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

/*
 * Starts a transaction at the start of the test
 * Rollbacks the transaction at the end of a test
 * doWithTransaction() flushes/clears the session at the start/end 
 */

public class RollbackTransactionHibernatePersistenceTestsStrategy
        implements HibernatePersistenceTestsStrategy {

    private PlatformTransactionManager transactionManager;

    private TransactionStatus status;

    private HibernateTemplate hibernateTemplate;

    public RollbackTransactionHibernatePersistenceTestsStrategy(
            HibernateTemplate template,
            PlatformTransactionManager manager) {
        super();
        hibernateTemplate = template;
        transactionManager = manager;
    }

    public void onSetUp() {
        status = transactionManager
                .getTransaction(new DefaultTransactionDefinition());
    }

    public void onTearDown() {
        transactionManager.rollback(status);
    }

    public void doWithTransaction(TxnCallback cb) {
        try {
            resetSession();
            cb.execute();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            resetSession();
        }
    }

    private void resetSession() {
        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }

}
