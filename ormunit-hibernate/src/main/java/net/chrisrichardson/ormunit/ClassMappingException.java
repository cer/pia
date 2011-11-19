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
 
package net.chrisrichardson.ormunit;

/** Thrown when a class is mapped to the wrong table */

public class ClassMappingException extends ORMUnitMappingException {

	private final Class type;

	private final String expectedTableName;

	private final String actualTableName;

	public ClassMappingException(Class type, String expectedTableName,
			String actualTableName) {
		super("Type: " + type.getName() + " should be mapped to table " + expectedTableName + " but is mapped to " + actualTableName);
		this.type = type;
		this.expectedTableName = expectedTableName;
		this.actualTableName = actualTableName;
	}

	public String getActualTableName() {
		return actualTableName;
	}

	public String getExpectedTableName() {
		return expectedTableName;
	}

	public Class getType() {
		return type;
	}
	
	
	

}
