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
 
package net.chrisrichardson.foodToGo.modifyOrderService;

import net.chrisrichardson.foodToGo.domain.*;

public class ModifyOrderServiceResult {

    private int status;

    private PendingOrder pendingOrder;

    public static final int OK = 0;

    public static final int ALREADY_LOCKED = 1;

    public static final int NOT_LOCKED = 2;

    private Order order;

    public ModifyOrderServiceResult(int status,
            PendingOrder pendingOrder) {
        this.status = status;
        this.pendingOrder = pendingOrder;
    }

    public ModifyOrderServiceResult(int status, Order order) {
        this.status = status;
        this.order = order;
    }

    public ModifyOrderServiceResult(int status) {
        this.status = status;
    }

    public int getStatusCode() {
        return status;
    }

    public PendingOrder getPendingOrder() {
        return pendingOrder;
    }

	public Order getOrder() {
		return order;
	}

}