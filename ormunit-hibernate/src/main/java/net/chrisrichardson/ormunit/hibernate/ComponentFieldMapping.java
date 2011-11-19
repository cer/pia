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

import junit.framework.*;

import org.hibernate.mapping.*;


public class ComponentFieldMapping {

    private final Property property;
    private final Component componentValue;

    public ComponentFieldMapping(Property property, Component value) {
        this.property = property;
        this.componentValue = value;
    }

    public void assertField(String fieldName, String columnName) {
        Property subProperty = findProperty(fieldName);
        HibernateAssertUtil.assertPropertyColumn(columnName, subProperty);
    }

    public void assertManyToOneField(String fieldName, String foreignKeyColumnName) {
        Property subProperty = findProperty(fieldName);
        ManyToOne value = (ManyToOne) subProperty.getValue();
        HibernateAssertUtil.assertColumn(foreignKeyColumnName, value);
    }

    public void assertNonPersistentFields(Set nonPersistentFields) {
    	assertNonPersistentFields(nonPersistentFields, false);
    }
    
    public void assertNonPersistentFields(Set nonPersistentFields, boolean includeSuper) {
        Class type = componentValue.getComponentClass();
        Set unmappedFields = new HashSet();
        for (Iterator it = HibernateAssertUtil.getPersistableFields(type, includeSuper).iterator(); it.hasNext();) {
            String fieldName = (String) it.next();
            if (!nonPersistentFields.contains(fieldName)) {
                Property subProperty = findProperty(fieldName);
                if (subProperty == null)
                    unmappedFields.add(fieldName);
            }
        }
        if (!unmappedFields.isEmpty())
            Assert.fail("unmapped fields: " + unmappedFields);

    }

    public Property findProperty(String fieldName) {
        for (Iterator it = componentValue.getPropertyIterator(); it.hasNext(); ) {
            Property subProperty = (Property) it.next();
            if (subProperty.getName().equals(fieldName)) {
                return subProperty;
            }
        }
        return null;
    }

    public void assertCompositeSetField(String fieldName) {
        Property property = findProperty(fieldName);
        Value v = property.getValue();
        org.hibernate.mapping.Set value = (org.hibernate.mapping.Set) v;
    }

    public CompositeSetFieldMapping getCompositeSetFieldMapping(String fieldName) {
        Property property = findProperty(fieldName);
        Value v = property.getValue();
        org.hibernate.mapping.Set value = (org.hibernate.mapping.Set) v;
        return new CompositeSetFieldMapping(property, value);
    }

}
