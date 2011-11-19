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

import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;
import net.chrisrichardson.foodToGo.domain.*;

import org.jmock.cglib.*;

public class DomainAcknowledgeOrderServiceTests extends MockObjectTestCase {

	private DomainAcknowledgeOrderService service;

	private Mock mockAcknowledgeOrderResultFactory;

	private Mock mockOrderRepository;

	String orderId = "99";

	int originalVersion = 1;

	String notes = "XYZ";

	Order orderFromRepository;

	private Mock mockOrderFromRepository;

	private AcknowledgeOrderResult returnedResult;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mockAcknowledgeOrderResultFactory = new Mock(
				AcknowledgeOrderResultFactory.class);
		mockOrderRepository = new Mock(OrderRepository.class);

		mockOrderFromRepository = new Mock(Order.class);

		AcknowledgeOrderResultFactory resultFactory = (AcknowledgeOrderResultFactory) mockAcknowledgeOrderResultFactory
				.proxy();
		OrderRepository orderRepository = (OrderRepository) mockOrderRepository
				.proxy();

		orderFromRepository = (Order) mockOrderFromRepository.proxy();

		returnedResult = new AcknowledgeOrderResult(AcknowledgeOrderResult.OK,
				orderFromRepository);

		service = new DomainAcknowledgeOrderService(orderRepository,
				resultFactory);
	}
	
	// Here are some positive test cases

	public void testGetOrderToAcknowledge() {
		String orderId = "99";

		mockOrderRepository.expects(once()).method("findOrder").with(
				eq(orderId)).will(returnValue(orderFromRepository));
		mockOrderFromRepository.expects(once()).method("isAcknowledgable")
				.will(returnValue(true));
		mockAcknowledgeOrderResultFactory.expects(once()).method(
				"makeAcknowledgeOrderResult").with(
				eq(AcknowledgeOrderResult.OK), eq(orderFromRepository)).will(
				returnValue(returnedResult));

		AcknowledgeOrderResult result = service.getOrderToAcknowledge(orderId);
		assertSame(returnedResult, result);
	}

	public void testVerifyOrderUnchanged() {

		mockOrderRepository.expects(once()).method("findOrder").with(
				eq(orderId)).will(returnValue(orderFromRepository));
		mockOrderFromRepository.expects(once()).method("getVersion").will(
				returnValue(originalVersion));
		mockAcknowledgeOrderResultFactory.expects(once()).method(
				"makeAcknowledgeOrderResult").with(
				eq(AcknowledgeOrderResult.OK), eq(orderFromRepository)).will(
				returnValue(returnedResult));

		AcknowledgeOrderResult result = service.verifyOrderUnchanged(orderId,
				originalVersion);
		assertSame(returnedResult, result);
	}

	public void testAcceptOrder() {
		mockOrderRepository.expects(once()).method("findOrder").with(
				eq(orderId)).will(returnValue(orderFromRepository));
		mockOrderFromRepository.expects(once()).method("getVersion").will(
				returnValue(originalVersion));
		mockOrderFromRepository.expects(once()).method("accept")
				.with(eq(notes));
		mockAcknowledgeOrderResultFactory.expects(once()).method(
				"makeAcknowledgeOrderResult").with(
				eq(AcknowledgeOrderResult.OK), eq(orderFromRepository)).will(
				returnValue(returnedResult));

		AcknowledgeOrderResult result = service.acceptOrder(orderId, notes,
				originalVersion);
		assertSame(returnedResult, result);
	}
	
	// TODO - write some negative tests

}
