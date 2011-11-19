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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details;

public class PendingOrderTotals {
    double subtotal;

    double deliveryCharge;

    double salesTax;

    double total;

    public boolean equals(Object other) {
        if (!(other instanceof PendingOrderTotals))
            return false;
        PendingOrderTotals otherTotals = (PendingOrderTotals) other;
        return subtotal == otherTotals.subtotal
                && deliveryCharge == otherTotals.deliveryCharge
                && salesTax == otherTotals.salesTax
                && total == otherTotals.total;
    }

    public int hashCode() {
        return (int) subtotal ^ (int) deliveryCharge ^ (int) salesTax
                ^ (int) total;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public double getSalesTax() {
        return salesTax;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotal() {
        return total;
    }
}