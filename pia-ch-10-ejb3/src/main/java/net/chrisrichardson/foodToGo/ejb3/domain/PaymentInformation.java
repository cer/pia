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
/**
 * Contains the payment information of an order or pending order
 */

@Embeddable(access = AccessType.FIELD)
public class PaymentInformation implements Serializable {
    private String type;
    private String name;
    private String number;
    private int month;
    private int year;
    
    @Embedded
    @AttributeOverrides
    ( {
        @AttributeOverride(name = "street1", column = @Column(name = "PAYMENT_STREET1") ),
        @AttributeOverride(name = "street2", column = @Column(name = "PAYMENT_STREET2") ),
        @AttributeOverride(name = "city", column = @Column(name = "PAYMENT_CITY")),
        @AttributeOverride(name = "state", column = @Column(name = "PAYMENT_STATE")),
        @AttributeOverride(name = "zip", column = @Column(name = "PAYMENT_ZIP")) })
    private Address address;

    public PaymentInformation() {
    }

    public PaymentInformation(String type, String name, String number, int month, int year, Address address) {
        this.type = type;
        this.name = name;
        this.number = number;
        this.month = month;
        this.year = year;
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Address getAddress() {
        return address;
    }
}
