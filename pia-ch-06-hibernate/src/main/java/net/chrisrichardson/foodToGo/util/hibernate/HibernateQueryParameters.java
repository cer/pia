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
 
package net.chrisrichardson.foodToGo.util.hibernate;

import java.util.*;

import org.hibernate.*;

/**
 * Contains the data for executing a query
 * @author cer
 *
 */
public class HibernateQueryParameters {

    private Map map = new HashMap();

    private String queryName;

    private boolean justOne;

    private String lockAlias;

    private LockMode lockMode;

    public HibernateQueryParameters(String queryName) {
        this.queryName = queryName;
    }

    public boolean equals(Object x) {
        if (!(x instanceof HibernateQueryParameters))
            return false;
        HibernateQueryParameters other = (HibernateQueryParameters) x;
        return map.equals(other.map)
                && queryName.equals(other.queryName);
    }

    public int hashCode() {
        return map.hashCode() ^ queryName.hashCode();
    }

    public void setString(String name, String value) {
        map.put(name, value);
    }

    public void setInteger(String name, int value) {
        map.put(name, new Integer(value));
    }

    public Iterator iterator() {
        return map.entrySet().iterator();
    }

    public void setJustOne(boolean justOne) {
        this.justOne = justOne;
    }

    public String getQueryName() {
        return queryName;
    }

    public boolean isJustOne() {
        return justOne;
    }

    public Map getParamsAsMap() {
        return map;
    }

    public void setDate(String name, Date value) {
        map.put(name, value);
    }

    public void setLockMode(String alias, LockMode lockMode) {
        this.lockAlias = alias;
        this.lockMode = lockMode;
    }

    public String getLockAlias() {
        return lockAlias;
    }
    public LockMode getLockMode() {
        return lockMode;
    }
}