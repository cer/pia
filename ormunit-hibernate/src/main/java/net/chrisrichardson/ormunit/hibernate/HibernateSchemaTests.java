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

import java.util.*;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.dialect.*;
import org.hibernate.tool.hbm2ddl.*;

/** 
 * Base class for writing tests that verify that the schema defines all tables and columns
 * referenced by the object/relational mapping
 * @author cer
 *
 */
public abstract class HibernateSchemaTests extends
        HibernateORMTestCase {

	/**
	 * Verifies that all tables and columns referenced by the object/relational mapping
	 * exist
	 * @throws Exception
	 */
    public void assertDatabaseSchema()
            throws Exception {
        String[] script = generateScript();
        List differences = getSignificantDifferences(script);
        assertTrue(differences.toString(), differences
                .isEmpty());
    }

    private String[] generateScript()
            throws Exception {
        Configuration cfg = getConfiguration();
        Session session = sessionFactory.openSession();
        try {
            Dialect dialect = getDatabaseDialect();
            DatabaseMetadata dbm = new DatabaseMetadata(
                    session.connection(), dialect);
            String[] script = cfg
                    .generateSchemaUpdateScript(
                            dialect, dbm);
            return script;
        } finally {
            session.close();
        }
    }

    protected Dialect getDatabaseDialect() throws Exception {
        return (Dialect)Class.forName(
                getConfiguration().getProperty(
                        "hibernate.dialect"))
                .newInstance();
    }

    private List getSignificantDifferences(
            String[] script) {
        List differences = new ArrayList();
        for (int i = 0; i < script.length; i++) {
            String line = script[i];
            if (line.indexOf("add constraint") == -1)
                differences.add(line);
        }
        return differences;
    }

}