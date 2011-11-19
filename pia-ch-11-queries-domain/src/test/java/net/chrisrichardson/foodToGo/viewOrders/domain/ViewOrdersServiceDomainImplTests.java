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
 
package net.chrisrichardson.foodToGo.viewOrders.domain;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.viewOrders.*;

import org.jmock.cglib.*;

public class ViewOrdersServiceDomainImplTests extends MockObjectTestCase {

	private Mock mockOrderDetacher;

	private Mock mockOrderRepository;

	private OrderDetacher orderDetacher;

	private OrderRepository orderRepository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockOrderDetacher = new Mock(OrderDetacher.class);
		mockOrderRepository = new Mock(OrderRepository.class);
		orderDetacher = (OrderDetacher) mockOrderDetacher.proxy();
		orderRepository = (OrderRepository) mockOrderRepository.proxy();
	}

	public void testViewOrdersService() {
		ViewOrdersService service = new ViewOrdersServiceDomainImpl(
				orderRepository, orderDetacher);
		OrderSearchCriteria criteria = new OrderSearchCriteria();

		List orders = Collections.singletonList(new Order());
		List detachedOrders = Collections.singletonList(new Order());

		PagedQueryResult pqr = new PagedQueryResult(orders, true);

		mockOrderRepository.expects(once()).method("findOrders").with(eq(0),
				eq(10), eq(criteria)).will(returnValue(pqr));
		mockOrderDetacher.expects(once()).method("detachOrders").with(
				eq(orders)).will(returnValue(detachedOrders));

		PagedQueryResult result = service.findOrders(0, 10, criteria);

		assertSame(detachedOrders, result.getResults());
		assertTrue(result.isMore());
	}
}
