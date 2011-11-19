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

import java.util.*;

import net.chrisrichardson.bankingExample.domain.*;

import org.springframework.orm.hibernate3.*;

public class HibernateBankingTransactionRepository implements
        BankingTransactionRepository {

    private HibernateTemplate hibernateTemplate;

    public HibernateBankingTransactionRepository(
            HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public BankingTransaction createTransferTransaction(Account fromAccount,
            Account toAccount, double amount) {
        TransferTransaction txn = new TransferTransaction(fromAccount,
                toAccount, amount, new Date());
        hibernateTemplate.save(txn);
        return txn;
    }

}
