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

import java.io.*;

import net.chrisrichardson.foodToGo.views.*;

public class OrderSummaryDTO implements Serializable, OrderSummary {

    private String orderId;
    private String restaurantName;
    
    public OrderSummaryDTO() {
    }

    public OrderSummaryDTO(String orderId, String restaurantName) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
    }

    public String getOrderId() {
        return orderId;
    }
    
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getOrderIdAsInt() {
        return Integer.parseInt(orderId);
    }
}
