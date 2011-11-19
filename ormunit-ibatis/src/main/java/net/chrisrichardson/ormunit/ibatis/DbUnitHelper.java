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

import java.io.*;
import java.net.*;
import java.sql.*;

import javax.sql.*;

import junit.framework.*;

import org.dbunit.*;
import org.dbunit.database.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;

/**
 * DbUnit helper class used by IBatisTests
 * 
 * @author cer
 * 
 */
public class DbUnitHelper {

	private Class type;

	private DatabaseConnection connection;

	public DbUnitHelper(Class type, DataSource ds, String schema)
			throws SQLException {
		this.type = type;
		connection = new DatabaseConnection(ds.getConnection(), schema);
	}

	public FlatXmlDataSet cleanInsert(String initialDataSet)
			throws DataSetException, IOException, DatabaseUnitException,
			SQLException {
		FlatXmlDataSet dataSet = getDataSet(initialDataSet);
		DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
		return dataSet;
	}

	private FlatXmlDataSet getDataSet(String name) throws DataSetException,
			IOException {
		// TODO - do something better than this
		if (name.startsWith("file:"))
			return new FlatXmlDataSet(new URL(name).openStream());
		else {
			InputStream stream = type.getResourceAsStream(name);
			Assert.assertNotNull("File: " + name, stream);
			return new FlatXmlDataSet(stream);
		}
	}

	public ITable assertExpected(String expectedDataSetName, String tableName)
			throws SQLException, DataSetException, IOException,
			DatabaseUnitException {
		return assertExpected(expectedDataSetName, tableName == null ? null
				: new String[] { tableName })[0];
	}

	public ITable[] assertExpected(String expectedDataSetName,
			String[] tableNames) throws SQLException, DataSetException,
			IOException, DatabaseUnitException {

		IDataSet actualDataSet = connection.createDataSet();

		IDataSet expectedDataSet = getDataSet(expectedDataSetName);

		if (tableNames == null)
			tableNames = expectedDataSet.getTableNames();

		ITable[] result = new ITable[tableNames.length];
		for (int i = 0; i < tableNames.length; i++) {
			String tableName = tableNames[i];
			ITable actualTable = actualDataSet.getTable(tableName);
			ITable expectedTable = expectedDataSet.getTable(tableName);
			Column[] expectedColumns = expectedTable.getTableMetaData()
					.getColumns();
			ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedColumns);

			SortedTable sortedExpectedTable = new SortedTable(expectedTable);
			SortedTable sortedActualTable = new SortedTable(filteredTable,
					expectedTable.getTableMetaData());
			if (sortedExpectedTable.getRowCount() == 0)
				Assert.assertEquals(0, sortedActualTable.getRowCount());
			else
				Assertion.assertEquals(sortedExpectedTable, sortedActualTable);
			result[i] = expectedTable;
		}
		return result;
	}

	public void close() throws SQLException {
		connection.getConnection().close();
	}

	public ITable assertExpected(String expectedDataSetName)
			throws DataSetException, SQLException, IOException,
			DatabaseUnitException {
		return assertExpected(expectedDataSetName, (String) null);
	}

}