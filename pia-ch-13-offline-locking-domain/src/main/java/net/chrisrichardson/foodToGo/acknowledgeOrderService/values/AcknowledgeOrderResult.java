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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService.values;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.views.*;

/**
 * DTO returned by the AcknowledgeOrderService
 * @author cer
 *
 */
public class AcknowledgeOrderResult {

    public static final String CHANGED = "CHANGED";

    public static final String ILLEGAL_STATE = "ILLEGAL_STATE";

    public static final String OK = "OK";
    public static final String LOCKED = "LOCKED";

    private String status;

    OrderDetail orderValue;

    public AcknowledgeOrderResult(String status, OrderDetail details) {
        this.status = status;
        this.orderValue = details;
    }

    public AcknowledgeOrderResult(String status, Order detachedOrder) {
        this.status = status;
        this.orderValue = detachedOrder;
    }

    public String getStatus() {
        return status;
    }
    public OrderDetail getOrderValue() {
        return orderValue;
    }
}