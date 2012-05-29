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

import net.chrisrichardson.util.TxnCallback;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Base class for writing unit tests that create, find, update and delete
 * persistent objects using Hibernate
 * 
 * @author cer
 * 
 */

public abstract class HibernatePersistenceTests extends HibernateORMTestCase {

	protected HibernateTemplate hibernateTemplate;

	protected HibernatePersistenceTestsStrategy strategy;

	protected DatabaseResetStrategy resetStrategy;

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public void setStrategy(HibernatePersistenceTestsStrategy strategy) {
		this.strategy = strategy;
	}

	protected void onSetUp() throws Exception {
		super.onSetUp();
		strategy.onSetUp();
	}

    public void setResetStrategy(
            DatabaseResetStrategy resetStrategy) {
        this.resetStrategy = resetStrategy;
    }

	@Override
	protected void onTearDown() throws Exception {
		super.onTearDown();
		strategy.onTearDown();
	}

	public void save(Object r) {
		hibernateTemplate.save(r);
	}

	public Object load(Class type, String id) {
		return hibernateTemplate.get(type, new Integer(id));
	}

	public void doWithTransaction(TxnCallback cb) {
		strategy.doWithTransaction(cb);
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	protected void delete(Class type) {
		HibernateTemplate t = getHibernateTemplate();
		t.deleteAll(t.find("from " + type.getName()));
	}

	protected void delete(Object object) {
		getHibernateTemplate().delete(object);
	}

}