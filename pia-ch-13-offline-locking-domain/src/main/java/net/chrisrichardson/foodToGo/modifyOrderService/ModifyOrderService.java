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

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;

public interface ModifyOrderService {
    public ModifyOrderServiceResult getOrderToModify(String caller,
            String orderId);

    public ModifyOrderServiceResult updateDeliveryInfo(
            String caller, String orderId, String pendingOrderId,
            Address deliveryAddress, Date deliveryTime);

    public ModifyOrderServiceResult updateQuantities(String caller,
            String orderId, String pendingOrderId, int[] quantities)
            throws InvalidPendingOrderStateException;

    public ModifyOrderServiceResult saveChangesToOrder(
            String caller, String orderId, String pendingOrderId);

    public ModifyOrderServiceResult cancelModifyOrder(
            String caller, String orderId, String pendingOrderId);
}