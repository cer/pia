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

import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

/**
 * Does nothing onSetup()/onTearDown()
 * Only executes a transaction when doWithTransaction() is called
 * Use this when you really want the changes committed to the database
 * @author cer
 *
 */

public class SimpleHibernatePersistenceTestsStrategy
        implements HibernatePersistenceTestsStrategy {

    private TransactionTemplate transactionTemplate;

    public void onSetUp() {
    }

    public void onTearDown() {
    }

    public SimpleHibernatePersistenceTestsStrategy(TransactionTemplate template) {
        super();
        transactionTemplate = template;
    }

    public void doWithTransaction(final TxnCallback cb) {
        transactionTemplate
                .execute(new TransactionCallbackWithoutResult() {

                    protected void doInTransactionWithoutResult(
                            TransactionStatus status) {
                        try {
                            cb.execute();
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Throwable e) {
                            throw new RuntimeException(
                                    e);
                        }
                    }
                });
    }


}
