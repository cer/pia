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

import org.hibernate.mapping.*;
import org.hibernate.mapping.Collection;


public class ComponentCollectionFieldMapping {

    protected final Property property;
    private final Collection value;
    private ComponentFieldMapping componentMapping;

    public ComponentCollectionFieldMapping(Property property, Collection value) {
        this.property = property;
        this.value = value;
        this.componentMapping = new ComponentFieldMapping(property, (Component)value.getElement());        
    }

    public void assertField(String fieldName, String columnName) {
        componentMapping.assertField(fieldName, columnName);
    }

    public void assertManyToOneField(String fieldName, String foreignKeyColumnName) {
        componentMapping.assertManyToOneField(fieldName,
                foreignKeyColumnName);
    }

    public void assertNonPersistentFields(Set nonPersistentFields) {
        componentMapping
                .assertNonPersistentFields(nonPersistentFields);
    }
    
    public void assertNonPersistentFields(String field) {
        assertNonPersistentFields(Collections.singleton(field));
    }



}
