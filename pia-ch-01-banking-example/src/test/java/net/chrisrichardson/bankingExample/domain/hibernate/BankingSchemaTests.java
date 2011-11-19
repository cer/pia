package net.chrisrichardson.bankingExample.domain.hibernate;

import net.chrisrichardson.ormunit.hibernate.*;

public class BankingSchemaTests extends HibernateSchemaTests {

	@Override
	protected String[] getConfigLocations() {
		return BankingDomainHibernateConstants.BANK_DOMAIN_TEST_CONTEXT;
	}

    public void testBankingSchema() throws Exception{
		assertDatabaseSchema();
	}
}
