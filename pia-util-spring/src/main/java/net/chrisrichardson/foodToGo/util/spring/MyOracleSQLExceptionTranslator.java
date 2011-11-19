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
 


package net.chrisrichardson.foodToGo.util.spring;

import java.sql.*;

import org.springframework.dao.*;
import org.springframework.jdbc.support.*;

public class MyOracleSQLExceptionTranslator extends
        SQLErrorCodeSQLExceptionTranslator {

    protected DataAccessException customTranslate(String task,
            String sql, SQLException sqlex) {
        switch (sqlex.getErrorCode()) {
        case 8177:
            return new CannotSerializeTransactionException(
                    "Can't serialize", sqlex);
        case 60:
            return new CannotAcquireLockException(
                    "Deadlock", sqlex);
        default:
            return null;
        }
    }
}