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
 


package net.chrisrichardson.bankingExample.facade.spring;

import net.chrisrichardson.bankingExample.domain.Account;
import net.chrisrichardson.bankingExample.domain.hibernate.BankingDomainHibernateConstants;
import net.chrisrichardson.bankingExample.facade.TransferFacade;
import net.chrisrichardson.foodToGo.util.ArrayUtils;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class SpringHibernateTransferFacadeImplTests extends
AbstractDependencyInjectionSpringContextTests {

	private TransferFacade facade;

	public void setFacade(TransferFacade facade) {
		this.facade = facade;
	}

	@Override
	protected String[] getConfigLocations() {
		return ArrayUtils.concatenate(new String[] { "banking-facade.xml", },
				BankingDomainHibernateConstants.BANK_DOMAIN_TEST_CONTEXT);

	}

	public void test() throws Exception {

		String accountId = "A." + System.currentTimeMillis();
		double initialBalance = 1.5;
		Account account = facade.createAccount(accountId, initialBalance);
		assertEquals(accountId, account.getAccountId());

		double balance = facade.getBalance(accountId);
		assertEquals(initialBalance, balance);
	}

	public void testTransfer() throws Exception {
		String accountId1 = "A." + System.currentTimeMillis();
		double initialBalance1 = 10;
		String accountId2 = "B." + System.currentTimeMillis();
		double initialBalance2 = 20;
		Account account1 = facade.createAccount(accountId1, initialBalance1);
		Account account2 = facade.createAccount(accountId2, initialBalance2);
		facade.transfer(accountId1, accountId2, 5);

		double balance1 = facade.getBalance(accountId1);
		double balance2 = facade.getBalance(accountId2);

		assertEquals(initialBalance1 - 5, balance1);
		assertEquals(initialBalance2 + 5, balance2);

	}

}
