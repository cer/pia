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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Base class for writing unit tests that use Hibernate. Makes the Hibernate
 * Configuration available to subclasses
 */

public abstract class HibernateORMTestCase extends
		AbstractDependencyInjectionSpringContextTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected SessionFactory sessionFactory;

	protected Configuration configuration;

	private LocalSessionFactoryBean localSessionFactoryBean;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Configuration getConfiguration() {
		if (configuration == null) {
			localSessionFactoryBean = LocalSessionFactoryBeanUtil
					.getLocalSessionFactoryBean(applicationContext);
			configuration = localSessionFactoryBean.getConfiguration();
		}
		return configuration;
	}

}
