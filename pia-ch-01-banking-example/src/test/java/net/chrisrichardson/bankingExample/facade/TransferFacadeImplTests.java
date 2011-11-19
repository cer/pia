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
 


package net.chrisrichardson.bankingExample.facade;

import net.chrisrichardson.bankingExample.domain.*;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

public class TransferFacadeImplTests extends MockObjectTestCase {

    private Mock mockAccountRepository;

    private Mock mockMoneyTranferService;

    private TransferFacadeImpl facade;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockAccountRepository = new Mock(AccountRepository.class);
        mockMoneyTranferService = new Mock(MoneyTransferService.class);
        AccountRepository accountRepository = (AccountRepository) mockAccountRepository
                .proxy();
        MoneyTransferService moneyTranferService = (MoneyTransferService) mockMoneyTranferService
                .proxy();
        facade = new TransferFacadeImpl(accountRepository,
                moneyTranferService);
    }

    public void testTransfer() throws Exception {

        String accountId1 = "A." + System.currentTimeMillis();
        double initialBalance1 = 10;
        String accountId2 = "B." + System.currentTimeMillis();
        double initialBalance2 = 20;

        mockMoneyTranferService.expects(once()).method("transfer").with(
                eq(accountId1), eq(accountId2), eq(5.0));

        BankingTransaction transaction = facade.transfer(accountId1,
                accountId2, 5);

    }
}
