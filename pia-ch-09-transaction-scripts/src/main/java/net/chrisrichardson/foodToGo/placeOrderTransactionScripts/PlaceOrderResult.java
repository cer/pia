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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;

/**
 * DTO returned from the Place Order use cse transaction scripts
 * @author cer
 *
 */

public class PlaceOrderResult  {

    public static final int BAD_STATE = 0;
    public static final int OK = 1;
    public static final int INCOMPATIBLE = 3;

    protected int statusCode;

    protected PendingOrderDTO pendingOrder;

    public PlaceOrderResult(int statusCode,
            PendingOrderDTO pendingOrder) {
        this.statusCode = statusCode;
        this.pendingOrder = pendingOrder;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public PendingOrderDTO getPendingOrder() {
        return pendingOrder;
    }

}