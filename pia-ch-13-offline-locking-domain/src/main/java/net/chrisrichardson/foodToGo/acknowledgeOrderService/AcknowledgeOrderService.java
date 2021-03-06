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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;

/*
 * AcknowledgeOrderService that uses a version number
 * rather than detached objects
 */
public interface AcknowledgeOrderService {
    public AcknowledgeOrderResult getOrderToAcknowledge(
            String orderId);

    public AcknowledgeOrderResult verifyOrderUnchanged(
            String orderId, int originalVersion);

    public AcknowledgeOrderResult acceptOrder(
            String orderId, String notes, int originalVersion);

}