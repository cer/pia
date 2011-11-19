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
 
package net.chrisrichardson.foodToGo.creditCardProcessing.testImpl;

import java.io.*;
import java.util.*;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;

/** 
 * Dummy implementation
 */

public class AuthorizationTransactionTestImpl
    implements Serializable, AuthorizationTransaction {

    private Date date = new Date();
    private PaymentInformation paymentInformation;

    public AuthorizationTransactionTestImpl(PaymentInformation paymentInformation) {
        this.paymentInformation = paymentInformation;
    }

    public int getStatus() {
        return AUTHORIZED;
    }

    public Date getCreationDate() {
        return date;
    }

    public Date getAuthorizationDate() {
        return date;
    }

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }

}
