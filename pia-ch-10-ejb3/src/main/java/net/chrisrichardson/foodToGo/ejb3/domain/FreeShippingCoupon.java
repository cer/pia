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

import javax.persistence.*;



@Entity (access=AccessType.FIELD)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE, 
        discriminatorType = DiscriminatorType.STRING, discriminatorValue="FREE_SHIP")
public class FreeShippingCoupon extends AbstractCouponImpl {

    private double minimum;
    
    public FreeShippingCoupon() {
    }
    
    public FreeShippingCoupon(String code, double minimum) {
		super(code);
		this.minimum = minimum;
	}

	protected double getMinimum() {
		return minimum;
	}

    public double getDeliveryChargeDiscount(PendingOrder order) {
        double subtotal = order.getSubtotal();
        if (subtotal < getMinimum())
          return 0;
        else
          return order.getDeliveryCharges(); 
    }

    public double getSubtotalDiscount(PendingOrder order) {
        return 0;
    }
}
