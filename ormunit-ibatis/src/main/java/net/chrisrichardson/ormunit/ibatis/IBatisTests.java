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
 
package net.chrisrichardson.ormunit.ibatis;

import javax.sql.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.springframework.jdbc.core.*;
import org.springframework.orm.ibatis.*;
import org.springframework.test.*;

/**
 * Base class for writing iBATIS persistence tests Makes a SqlMapClientTemplate,
 * JdbcTemplate and a DataSource available to subclasses Provides some
 * convenience methods for using DbUnit
 * 
 * @author cer
 * 
 */
public abstract class IBatisTests extends
		AbstractDependencyInjectionSpringContextTests {

	protected SqlMapClientTemplate sqlMapClientTemplate;

	protected JdbcTemplate jdbcTemplate;

	protected DataSource ds;

	private String schemaName;
	
	public void setSqlMapClientTemplate(
			SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	protected DbUnitHelper dbUnitHelper;

	/**
	 * Initializes the database
	 * 
	 * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#onSetUp()
	 */
	protected void onSetUp() throws Exception {
		super.onSetUp();
		dbUnitHelper = new DbUnitHelper(getClass(), ds, schemaName);
		String initialDataSet = getInitialDataSet();
		if (initialDataSet != null)
			cleanInsert(initialDataSet);
	}

	
	/**
	 * Subclasses can override to specify a DBUnit dataset to initialize the
	 * database at the start of the test
	 * 
	 * @return
	 */
	protected String getInitialDataSet() {
		return null;
	}

	protected FlatXmlDataSet cleanInsert(String initialDataSetName)
			throws Exception {
		FlatXmlDataSet initialDataSet = dbUnitHelper
				.cleanInsert(initialDataSetName);
		return initialDataSet;
	}

	protected void onTearDown() throws Exception {
		dbUnitHelper.close();
		super.onTearDown();
	}

	protected ITable assertExpected(String datasetName, String tableName)
			throws Exception, DatabaseUnitException {
		return dbUnitHelper.assertExpected(datasetName, tableName);
	}

}
