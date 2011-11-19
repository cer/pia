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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService.impl;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.*;
import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;
import net.chrisrichardson.foodToGo.domain.*;

public class DomainAcknowledgeOrderService implements AcknowledgeOrderService {

	AcknowledgeOrderResultFactory resultFactory;

	OrderRepository orderRepository;

	public DomainAcknowledgeOrderService(OrderRepository orderRepository,
			AcknowledgeOrderResultFactory resultFactory) {
		this.orderRepository = orderRepository;
		this.resultFactory = resultFactory;
	}

	public AcknowledgeOrderResult getOrderToAcknowledge(String orderId) {
		Order order = orderRepository.findOrder(orderId);
		if (order.isAcknowledgable())
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.OK, order);
		else
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.ILLEGAL_STATE, order);
	}

	public AcknowledgeOrderResult verifyOrderUnchanged(String orderId,
			int originalVersion) {
		Order order = orderRepository.findOrder(orderId);
		if (order.getVersion() == originalVersion)
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.OK, order);
		else if (order.isAcknowledgable())
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.CHANGED, order);
		return resultFactory.makeAcknowledgeOrderResult(
				AcknowledgeOrderResult.ILLEGAL_STATE, order);
	}

	public AcknowledgeOrderResult acceptOrder(String orderId, String notes,
			int originalVersion) {
		Order order = orderRepository.findOrder(orderId);
		if (order.getVersion() == originalVersion) {
			order.accept(notes);
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.OK, order);
		} else if (order.isAcknowledgable())
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.CHANGED, order);
		else
			return resultFactory.makeAcknowledgeOrderResult(
					AcknowledgeOrderResult.ILLEGAL_STATE, order);
	}

}