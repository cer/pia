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

public interface Coupon extends Serializable {
    public double getDeliveryChargeDiscount(PendingOrder order);

    public double getSubtotalDiscount(PendingOrder order);

    public String getCode();
    
    public int getId();
}

/*
@Entity(access = AccessType.FIELD)
@Table(name = "FTGO_COUPON")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE, discriminatorType = DiscriminatorType.STRING)
@DiscriminatorColumn(name = "COUPON_TYPE", nullable = true)
public abstract class Coupon implements Serializable {

    @Id(generate = GeneratorType.AUTO)
    private int id;

    private String code;

    public Coupon() {
    }

    private Coupon(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public abstract double getDeliveryChargeDiscount(
            PendingOrder pendingOrder);

    public abstract double getSubtotalDiscount(
            PendingOrder pendingOrder);

}

*/
