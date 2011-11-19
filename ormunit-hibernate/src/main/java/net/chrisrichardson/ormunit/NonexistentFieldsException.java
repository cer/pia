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

import java.util.*;

/** Thrown when test makes assertion about a field that does not exist */

public class NonexistentFieldsException extends ORMUnitMappingException {

	private final Class type;
	private final Set fieldNames;

	public NonexistentFieldsException(Class type, Set fieldNames) {
		this.type = type;
		this.fieldNames = fieldNames;
	}

	@Override
	public String getMessage() {
		return "non-existent fields: " + type.getName() + " "
				+ fieldNames;
	}

	public Set getFieldNames() {
		return fieldNames;
	}

	public Class getType() {
		return type;
	}
	
	
}
