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

import org.jmock.cglib.*;

public class DetachingAcknowledgeOrderServiceTests extends MockObjectTestCase {

	private DetachingAcknowledgeOrderService service;

	private Mock mockOrderAttachmentManager;

	private Mock mockOrderRepository;

	String orderId = "99";

	int originalVersion = 1;

	String notes = "XYZ";

	Order orderFromRepository;

	private Mock mockOrderFromRepository;

	private Order detachedOrder;

	private Mock mockDetachedOrder;

	private Mock mockAttachedOrder;

	private Order attachedOrder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mockOrderAttachmentManager = new Mock(OrderAttachmentManager.class);
		mockOrderRepository = new Mock(OrderRepository.class);

		mockOrderFromRepository = new Mock(Order.class);

		OrderAttachmentManager orderAttachmentManager = (OrderAttachmentManager) mockOrderAttachmentManager
				.proxy();
		OrderRepository orderRepository = (OrderRepository) mockOrderRepository
				.proxy();

		orderFromRepository = (Order) mockOrderFromRepository.proxy();

		mockDetachedOrder = new Mock(Order.class);
		detachedOrder = (Order)mockDetachedOrder.proxy();

		mockAttachedOrder = new Mock(Order.class);
		attachedOrder = (Order)mockAttachedOrder.proxy();

		service = new DetachingAcknowledgeOrderServiceImpl(orderRepository,
				orderAttachmentManager);
	}

	// Here are some positive test cases

	public void testGetOrderToAcknowledge() {
		String orderId = "99";

		mockOrderRepository.expects(once()).method("findOrder").with(
				eq(orderId)).will(returnValue(orderFromRepository));
		mockOrderFromRepository.expects(once()).method("isAcknowledgable")
				.will(returnValue(true));
		mockOrderAttachmentManager.expects(once()).method("detach").with(
				eq(orderFromRepository)).will(returnValue(detachedOrder));

		AcknowledgeOrderResult result = service.getOrderToAcknowledge(orderId);
		assertEquals(AcknowledgeOrderResult.OK, result.getStatus());
		assertSame(detachedOrder, result.getOrderValue());
	}

	public void testVerifyOrderUnchanged() {

		mockOrderAttachmentManager.expects(once()).method("attach").with(
				eq(orderFromRepository)).will(returnValue(attachedOrder));
		mockOrderAttachmentManager.expects(once()).method("detach").with(
				eq(attachedOrder)).will(returnValue(detachedOrder));

		AcknowledgeOrderResult result = service
				.verifyOrderUnchanged(orderFromRepository);
		assertEquals(AcknowledgeOrderResult.OK, result.getStatus());
		assertSame(detachedOrder, result.getOrderValue());
	}

	public void testAcceptOrder() {
		mockOrderAttachmentManager.expects(once()).method("attach").with(
				eq(orderFromRepository)).will(returnValue(attachedOrder));
		mockOrderAttachmentManager.expects(once()).method("detach").with(
				eq(attachedOrder)).will(returnValue(detachedOrder));

		AcknowledgeOrderResult result = service
				.acknowledgeOrder(orderFromRepository);
		assertEquals(AcknowledgeOrderResult.OK, result.getStatus());
		assertSame(detachedOrder, result.getOrderValue());
	}

	// TODO - write some negative tests

}
