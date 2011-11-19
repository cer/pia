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
 


package net.chrisrichardson.bankingExample.domain;

import java.util.*;

import org.jmock.cglib.*;

public class MoneyTransferServiceTests extends
        MockObjectTestCase {

    private Mock mockAccountRepository;

    private Mock mockBankingTransactionRepository;

    private MoneyTransferService service;

    private Mock mockFromAccount;

    private Mock mockToAccount;

    private Account toAccount;

    private Account fromAccount;

    private BankingTransaction bankingTransaction;

    protected void setUp() throws Exception {
        super.setUp();
        mockAccountRepository = new Mock(
                AccountRepository.class);
        mockBankingTransactionRepository = new Mock(
                BankingTransactionRepository.class);
        service = new MoneyTransferServiceImpl(
                (AccountRepository) mockAccountRepository
                        .proxy(),
                (BankingTransactionRepository) mockBankingTransactionRepository
                        .proxy());

        mockFromAccount = new Mock(Account.class);
        mockToAccount = new Mock(Account.class);
        fromAccount = (Account) mockFromAccount
                .proxy();
        toAccount = (Account) mockToAccount.proxy();

        bankingTransaction = new TransferTransaction(fromAccount, toAccount,
                1.0, new Date());
    }

    public void testTransfer_normal() throws Exception {

        String fromAccountId = "from";
        String toAccountId = "to";
        double amount = 10.0;

        mockAccountRepository.expects(once()).method(
                "findAccount").with(eq(fromAccountId))
                .will(returnValue(fromAccount));

        mockAccountRepository.expects(once()).method(
                "findAccount").with(eq(toAccountId))
                .will(returnValue(toAccount));

        mockFromAccount.expects(once())
                .method("debit").with(eq(amount));

        mockToAccount.expects(once()).method("credit")
                .with(eq(amount));

        mockBankingTransactionRepository.expects(
                once()).method(
                "createTransferTransaction").with(
                eq(fromAccount), eq(toAccount),
                eq(amount)).will(
                returnValue(bankingTransaction));

        BankingTransaction result = service.transfer(
                fromAccountId, toAccountId, amount);

        assertSame(bankingTransaction, result);

    }

    public void testTransfer_overdrawn()
            throws Exception {

        String fromAccountId = "from";
        String toAccountId = "to";
        double amount = 10.0;

        mockAccountRepository.expects(once()).method(
                "findAccount").with(eq(fromAccountId))
                .will(returnValue(fromAccount));

        mockAccountRepository.expects(once()).method(
                "findAccount").with(eq(toAccountId))
                .will(returnValue(toAccount));

        mockFromAccount
                .expects(once())
                .method("debit")
                .with(eq(amount))
                .will(
                        throwException(new MoneyTransferException()));

        try {
            service.transfer(fromAccountId,
                    toAccountId, amount);
            fail("Exception expected");
        } catch (MoneyTransferException e) {

        }

    }
}
