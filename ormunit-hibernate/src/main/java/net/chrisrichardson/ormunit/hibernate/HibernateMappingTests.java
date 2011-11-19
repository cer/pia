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
import java.util.Set;

import net.chrisrichardson.ormunit.*;

import org.hibernate.*;
import org.hibernate.mapping.*;
import org.hibernate.mapping.List;

/**
 * Base class for writing tests that make assertions about a Hibernate O/R
 * mapping document.
 * 
 * @author cer
 * 
 */
public abstract class HibernateMappingTests extends HibernateORMTestCase {

	private PersistentClass classMapping;

	private Class type;

	protected void onSetUp() throws Exception {
		super.onSetUp();
		this.type = null;
		this.classMapping = null;
	}

	/**
	 * Verify that the class is mapped to the specified table
	 * 
	 * @param type
	 * @param tableName
	 */
	protected void assertClassMapping(Class type, String tableName) {
		this.type = type;
		classMapping = getConfiguration().getClassMapping(type.getName());
		assertNotNull("No class mapping found for " + type.getName(), classMapping);
		if (!tableName.equalsIgnoreCase(classMapping.getTable().getName()))
			throw new ClassMappingException(type, tableName, classMapping
					.getTable().getName());
	}

	/**
	 * Verify that all classes and their fields are mapped
	 */

	protected void assertAllClassesMapped() {
            assertAllClassesMapped(Collections.EMPTY_SET);
        }

	protected void assertAllClassesMapped(Set fieldNames) {
		for (Iterator it = getConfiguration().getClassMappings(); it.hasNext();) {
			classMapping = (PersistentClass) it.next();
			type = classMapping.getMappedClass();
			assertAllFieldsMappedExcept(getFieldsForType(type, fieldNames));
		}

	}

        Set getFieldsForType(Class type, Set fields) {
            return mungePaths(type.getName(), fields);
        }


	protected ComponentFieldMapping getComponentFieldMapping(String fieldName)
			throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		Component value = (Component) property.getValue();
		return new ComponentFieldMapping(property, value);
	}

	protected void assertAllFieldsMapped() throws MappingException {
		assertAllFieldsMappedExcept(Collections.EMPTY_SET, false);
	}
	protected void assertAllFieldsMapped(boolean includeSuper) throws MappingException {
		assertAllFieldsMappedExcept(Collections.EMPTY_SET, includeSuper);
	}

	protected void assertAllFieldsMappedExcept(Set fieldNames)
	throws MappingException {
		assertAllFieldsMappedExcept(fieldNames, false);
	}
	
	protected void assertAllFieldsMappedExcept(Set fieldNames, boolean includeSuper)
			throws MappingException {
		Iterator propertyIterator = classMapping.getPropertyIterator();
		walkComponentProperties(propertyIterator, fieldNames);

		assertNoMappingForFields(type, getFieldsWithNonSubProps(fieldNames));
		assertNoOtherUnmappedFields(type, getRoots(fieldNames), includeSuper);
		assertFieldsExists(type, getRoots(fieldNames), includeSuper);
	}

	private Set getFieldsWithNonSubProps(Set fieldNames) {
		Set result = new HashSet();
		for (String field : (Set<String>) fieldNames) {
			int n = field.indexOf('.');
			if (n == -1)
				result.add(field);
		}
		return result;
	}

	private void walkComponentProperties(Iterator propertyIterator,
			Set fieldNames) {
		for (Iterator it = propertyIterator; it.hasNext();) {
			Property property = (Property) it.next();
			String name = property.getName();
			if (property.getValue() instanceof Component) {
				Component cv = (Component) property.getValue();

				Set mungedFieldNames = mungePaths(name, fieldNames);

				assertAllFieldsMapped(cv, mungedFieldNames);
				assertFieldsExists(cv.getComponentClass(),
						getRoots(mungedFieldNames), false);
				walkComponentProperties(cv.getPropertyIterator(),
						mungedFieldNames);
			} else if (isListOfComponents(property)) {

				List value = (List) property.getValue();
				Component cv = (Component) value.getElement();

				// Duplicate
				Set mungedFieldNames = mungePaths(name, fieldNames);

				assertAllFieldsMapped(cv, mungedFieldNames);
				assertFieldsExists(cv.getComponentClass(),
						getRoots(mungedFieldNames), false);
				walkComponentProperties(cv.getPropertyIterator(),
						mungedFieldNames);

			}
		}
	}

	private boolean isListOfComponents(Property property) {
		return property.getValue() instanceof List
				&& ((List) property.getValue()).getElement() instanceof Component;
	}

	private Set getRoots(Set mungedFieldNames) {
		Set result = new HashSet();
		for (String field : (Set<String>) mungedFieldNames) {
			int n = field.indexOf('.');
			if (n == -1)
				result.add(field);
			else
				result.add(field.substring(0, n));

		}
		return result;
	}

	private Set mungePaths(String name, Set fieldNames) {
		String prefixToChop = name + ".";
		Set result = new HashSet();
		for (String fieldName : (Set<String>) fieldNames) {
			if (fieldName.startsWith(prefixToChop))
				result.add(fieldName.substring(prefixToChop.length()));
		}
		return result;
	}

	private void assertAllFieldsMapped(Component cv, Set fieldsToIgnore) {
		Set unmappedFields = new HashSet();
		Set mappedFields = new HashSet();
		for (Iterator it = HibernateAssertUtil.getPersistableFields(
				cv.getComponentClass(), false).iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			try {
				cv.getProperty(fieldName);
				mappedFields.add(fieldName);
			} catch (MappingException e) {
				unmappedFields.add(fieldName);
			}
		}
		unmappedFields.removeAll(fieldsToIgnore);
		if (!unmappedFields.isEmpty())
			throw new UnmappedFieldsException(cv.getComponentClass(),
					unmappedFields);
		Set x = intersection(mappedFields, fieldsToIgnore);
		if (!x.isEmpty()) {
			throw new NonPersistentFieldException(cv.getComponentClass(), x);

		}
	}

	private Set intersection(Set mappedFields, Set fieldsToIgnore) {
		Set result = new HashSet(mappedFields);
		result.retainAll(fieldsToIgnore);
		return result;
	}

	private void assertFieldsExists(Class type, Set fieldNames, boolean includeSuper) {
		Set persistableFields = HibernateAssertUtil.getPersistableFields(type, includeSuper);
		if (!persistableFields.containsAll(fieldNames)) {
			fieldNames.removeAll(persistableFields);
			throw new NonexistentFieldsException(type, fieldNames);
		}
	}

	private void assertNoOtherUnmappedFields(Class type, Set nonPersistentFields, boolean includeSuper)
			throws MappingException {
		Property idProperty = classMapping.getIdentifierProperty();
		Set unmappedFields = new HashSet();
		Set fieldsToIgnore = new HashSet(nonPersistentFields);
                if (idProperty != null) {
                    fieldsToIgnore.add(idProperty.getName());
                }
		for (Iterator it = HibernateAssertUtil.getPersistableFields(type, includeSuper)
				.iterator(); it.hasNext();) {
			String fieldName = (String) it.next();
			if (!fieldsToIgnore.contains(fieldName))
				try {
					classMapping.getProperty(fieldName);
				} catch (MappingException e) {
					unmappedFields.add(fieldName);
				}
		}
		if (!unmappedFields.isEmpty())
			throw new UnmappedFieldsException(type, unmappedFields);
	}

	private void assertNoMappingForFields(Class type, Set fieldNames) {
		for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
			String fieldName = (String) iter.next();
			try {
				Property r = classMapping.getProperty(fieldName);
				if (r.getName().equals(fieldName))
					throw new NonPersistentFieldException(type, Collections
							.singleton(fieldName));
			} catch (MappingException e) {

			}
		}
	}

	protected void assertCompositeListField(String fieldName)
			throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		Value v = property.getValue();
		org.hibernate.mapping.List value = (org.hibernate.mapping.List) v;
	}

	protected CompositeListFieldMapping getCompositeListFieldMapping(
			String fieldName) throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		Value v = property.getValue();
		org.hibernate.mapping.List value = (org.hibernate.mapping.List) v;
		return new CompositeListFieldMapping(property, value);
	}

	protected void assertComponentField(String fieldName)
			throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		if (!(property.getValue() instanceof Component))
			fail("field name is not mapped as a component: " + property.getValue());
	}

	protected void assertSetField(String fieldName) throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		org.hibernate.mapping.Set value = (org.hibernate.mapping.Set) property
				.getValue();
	}

	protected void assertManyToOneField(String fieldName,
			String foreignKeyColumnName) throws MappingException {
		Property subProperty = classMapping.getProperty(fieldName);
		ManyToOne value = (ManyToOne) subProperty.getValue();
		HibernateAssertUtil.assertColumn(foreignKeyColumnName, value);
	}

	protected void assertVersionField(String fieldName, String columnName) {
		Property versionProperty = classMapping.getVersion();
		assertEquals(fieldName, versionProperty.getName());
		HibernateAssertUtil.assertPropertyColumn(columnName, versionProperty);
	}

	protected void assertIdField(String idField, String idColumn)
			throws PropertyNotFoundException, MappingException {
		Property idProperty = classMapping.getIdentifierProperty();
		HibernateAssertUtil.assertFieldAccess(idProperty);
	}

	public void assertField(String fieldName, String columnName)
			throws MappingException {
		Property subProperty = classMapping.getProperty(fieldName);
		HibernateAssertUtil.assertPropertyColumn(columnName, subProperty);
	}

	protected void assertOneToManyListField(String fieldName,
			String foreignKeyColumn, String indexColumn)
			throws MappingException {
		Property property = classMapping.getProperty(fieldName);
		org.hibernate.mapping.List value = (org.hibernate.mapping.List) property
				.getValue();
		HibernateAssertUtil.assertColumn(foreignKeyColumn, value.getKey());
		HibernateAssertUtil.assertColumn(indexColumn, value.getIndex());
		assertTrue(value.getElement() instanceof OneToMany);
	}

	protected void assertAllFieldsMappedExcept(String field1, String field2,
			String field3) throws MappingException {
		Set s = new HashSet();
		s.add(field1);
		s.add(field2);
		s.add(field3);
		assertAllFieldsMappedExcept(s);
	}

	protected void assertAllFieldsMappedExcept(String field1, String field2)
			throws MappingException {
		Set s = new HashSet();
		s.add(field1);
		s.add(field2);
		assertAllFieldsMappedExcept(s);
	}

	protected void assertAllFieldsMappedExcept(String field1)
			throws MappingException {
		Set s = new HashSet();
		s.add(field1);
		assertAllFieldsMappedExcept(s);
	}

	public CompositeSetFieldMapping getCompositeSetFieldMapping(String fieldName) {
		Property property = classMapping.getProperty(fieldName);
		Value v = property.getValue();
		org.hibernate.mapping.Set value = (org.hibernate.mapping.Set) v;
		return new CompositeSetFieldMapping(property, value);
	}

}