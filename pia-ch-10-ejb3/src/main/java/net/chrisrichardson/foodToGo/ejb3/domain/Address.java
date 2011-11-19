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
 




package net.chrisrichardson.foodToGo.ejb3.domain;

import java.io.*;

import javax.persistence.*;

import net.chrisrichardson.util.*;

@Embeddable(access = AccessType.FIELD)
public class Address implements Serializable {
    private String street1;

    private String street2;

    private String city;

    @Basic
    @Column(name="DELIVERY_STATE")
    private String state;

    private String zip;
    
    public Address() {
    }

    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof Address))
            return false;

        Address other = (Address) object;

        return ValueObjectUtil.safeEquals(street1, other.street1)
                && ValueObjectUtil.safeEquals(street2,
                        other.street2)
                && ValueObjectUtil.safeEquals(city, other.city)
                && ValueObjectUtil.safeEquals(state, other.state)
                && ValueObjectUtil.safeEquals(zip, other.zip);
    }

    public int hashCode() {
        return ValueObjectUtil.safeHash(street1)
                ^ ValueObjectUtil.safeHash(street2)
                ^ ValueObjectUtil.safeHash(city)
                ^ ValueObjectUtil.safeHash(state)
                ^ ValueObjectUtil.safeHash(zip);
    }

    public Address(String street1, String street2, String city,
            String state, String zip) {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public boolean isNull() {
        return street1 == null && street2 == null && city == null
                && state == null && zip == null;
    }
}