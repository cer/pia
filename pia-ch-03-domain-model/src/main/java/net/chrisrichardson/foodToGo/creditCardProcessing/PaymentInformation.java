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
 


package net.chrisrichardson.foodToGo.creditCardProcessing;

import java.io.*;

import net.chrisrichardson.foodToGo.util.*;

/**
 * Contains the payment information of an order or pending order
 */

public class PaymentInformation implements
        Serializable {
    protected String type;

    protected String name;

    protected String number;

    protected int month;

    protected int year;

    private Address address;

    private String email;

    private String phoneNumber;

    public PaymentInformation() {
    }

    public PaymentInformation(String type,
            String name, String number, int month,
            int year, Address address,
            String email, 
            String phoneNumber) {
        this.type = type;
        this.name = name;
        this.number = number;
        this.month = month;
        this.year = year;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
