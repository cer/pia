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
 
package net.chrisrichardson.ormunit.hibernate.tests;

import java.util.*;

import net.chrisrichardson.ormunit.*;
import net.chrisrichardson.ormunit.hibernate.*;

public class ExampleMappingTests extends HibernateMappingTests {

	@Override
	protected String[] getConfigLocations() {

		return new String[] { "classpath:net/chrisrichardson/ormunit/hibernate/tests/context.xml" };
	}

	public void testAllClassesMapped() {
		try {
			assertAllClassesMapped();
			fail();
		} catch (UnmappedFieldsException e) {

		}
	}

	public void testCustomerMapping() throws Exception {
		assertClassMapping(Customer.class, "Customer");
		assertAllFieldsMapped();
	}

	public void testCustomerMappingWrongTable() throws Exception {
		try {
			assertClassMapping(Customer.class, "CustomerX");
			fail();
		} catch (ClassMappingException e) {

		}
	}

	public void testClassWithUnmappedField() throws Exception {
		assertClassMapping(ClassWithUnmappedField.class,
				"ClassWithUnmappedField");
		assertAllFieldsMappedExcept(Collections.singleton("salary"));
	}

	public void testClassWithUnmappedFieldAssumingAllMapped() throws Exception {
		assertClassMapping(ClassWithUnmappedField.class,
				"ClassWithUnmappedField");
		try {
			assertAllFieldsMapped();
			fail();
		} catch (UnmappedFieldsException e) {
		}
	}

	public void testNoSuchNonPersistenceField() throws Exception {
		assertClassMapping(Customer.class, "Customer");
		try {
			assertAllFieldsMappedExcept(Collections.singleton("salary"));
			fail();
		} catch (NonexistentFieldsException e) {
		}
	}

	public void testNoNonPersistenceFieldReallyIs() throws Exception {
		assertClassMapping(Customer.class, "Customer");
		try {
			assertAllFieldsMappedExcept(Collections.singleton("name"));
			fail();
		} catch (NonPersistentFieldException e) {
		}
	}

	public void testClassWithUnmappedSubpropertyAssumeAllMapped() {
		assertClassMapping(CustomerWithComponentAddress.class,
				"CustomerWithComponentAddress");
		try {
			assertAllFieldsMapped();
			fail();
		} catch (UnmappedFieldsException e) {
		}
	}

	public void testClassWithUnmappedSubproperty() {
		assertClassMapping(CustomerWithComponentAddress.class,
				"CustomerWithComponentAddress");
		Set fields = new HashSet();
		fields.add("address.zip");
		fields.add("address.id");
		assertAllFieldsMappedExcept(fields);
	}

	public void testAssertUnmappedSubpropThatReallyIsMapped() {
		assertClassMapping(CustomerWithComponentAddress.class,
				"CustomerWithComponentAddress");
		Set fields = new HashSet();
		fields.add("address.street1");
		fields.add("address.zip");
		fields.add("address.id");
		try {
			assertAllFieldsMappedExcept(fields);
			fail();
		} catch (NonPersistentFieldException e) {
		}
	}
}
