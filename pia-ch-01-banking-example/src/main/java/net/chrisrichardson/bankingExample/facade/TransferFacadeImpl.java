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

public class TransferFacadeImpl implements TransferFacade {

    private MoneyTransferService transferService;
    private AccountRepository accountRepository;
    
    public TransferFacadeImpl(AccountRepository repository, MoneyTransferService service) {
        this.accountRepository = repository;
        this.transferService = service;
    }

    public Account createAccount(String accountId, double initialBalance) {
        return accountRepository.createAccount(accountId, initialBalance);
    }

    public double getBalance(String accountId) {
        Account account = accountRepository.findAccount(accountId);
        return account.getBalance();
    }

    public BankingTransaction transfer(String fromAccountId,
            String toAccountId, double amount) throws MoneyTransferException {
        return transferService.transfer(fromAccountId, toAccountId, amount);
    }


}
