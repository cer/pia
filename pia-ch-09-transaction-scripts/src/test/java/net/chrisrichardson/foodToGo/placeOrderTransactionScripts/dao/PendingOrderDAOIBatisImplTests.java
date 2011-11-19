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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.dao;

import java.text.*;
import java.util.*;

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.ormunit.ibatis.*;

import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;

public class PendingOrderDAOIBatisImplTests extends IBatisTests {

	PendingOrderDAO dao;

	protected String[] getConfigLocations() {
		return IBatisDAOTestConstants.IBATIS_DAO_CONTEXT;
	}

	public void setDao(PendingOrderDAO dao) {
		this.dao = dao;
	}

	public void testFindAndCreatePendingOrder_new() throws Exception {

		cleanInsert("empty-database.xml");

		PendingOrderDTO pendingOrder = dao.createPendingOrder();
		assertNotNull(pendingOrder);
		assertNull(pendingOrder.getDeliveryAddress());
		assertNull(pendingOrder.getDeliveryTime());
		assertNull(pendingOrder.getRestaurant());
		assertTrue(pendingOrder.getLineItems().isEmpty());

		assertExpected("expected-new-pending-order.xml", "FTGO_PENDING_ORDER");

	}

	public void testFindExistingPendingOrder() throws Exception {

		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-0.xml");

		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);

		ITable expectedTable = assertExpected("pending-order-0.xml",
				"FTGO_PENDING_ORDER");

		assertNotNull(pendingOrder);
		assertNull(pendingOrder.getDeliveryAddress());
		assertNull(pendingOrder.getDeliveryTime());
		assertNull(pendingOrder.getRestaurant());
		assertTrue(pendingOrder.getLineItems().isEmpty());
		// ...
	}

	protected String cleanInsertAndGetPendingOrderId(String initialDataSetName)
			throws Exception {
		FlatXmlDataSet initialDataSet = cleanInsert(initialDataSetName);

		ITable initialPendingOrderTable = initialDataSet
				.getTable("FTGO_PENDING_ORDER");
		String pendingOrderId = (String) initialPendingOrderTable.getValue(0,
				"PENDING_ORDER_ID");
		return pendingOrderId;
	}

	public void testFindExistingPendingOrderWithAddress() throws Exception {

		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-1.xml");

		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);

		ITable expectedTable = assertExpected("pending-order-1.xml",
				"FTGO_PENDING_ORDER");

		assertNotNull(pendingOrder);
		assertEquals(expectedTable.getValue(0, "DELIVERY_STREET1"),
				pendingOrder.getDeliveryAddress().getStreet1());
		assertEquals(expectedTable.getValue(0, "DELIVERY_STREET2"),
				pendingOrder.getDeliveryAddress().getStreet2());
		assertTrue(pendingOrder.getLineItems().isEmpty());
		// ...
	}

	public void testFindExistingPendingOrderWithRestaurant() throws Exception {

		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-with-restaurant.xml");

		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);

		ITable expectedTable = assertExpected(
				"pending-order-with-restaurant.xml", "FTGO_PENDING_ORDER");

		assertNotNull(pendingOrder);
		assertEquals(expectedTable.getValue(0, "DELIVERY_STREET1"),
				pendingOrder.getDeliveryAddress().getStreet1());
		assertEquals(expectedTable.getValue(0, "DELIVERY_STREET2"),
				pendingOrder.getDeliveryAddress().getStreet2());
		assertTrue(pendingOrder.getLineItems().isEmpty());
		assertNotNull(pendingOrder.getRestaurant());
		assertFalse(pendingOrder.getRestaurant().getMenuItems().isEmpty());
	}

	public void testSavePendingOrder_deliveryAddress() throws Exception {
		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-0.xml");
		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);
		pendingOrder.setDeliveryAddress(new Address("1 myStreet", "2 myStreet",
				"OakTown", "CA", "94619"));
		SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat
				.getInstance();
		format.applyPattern("yyyy-MM-dd hh:mm:ss");
		Date deliveryTime = format.parse("2005-06-21 10:21:30");
		pendingOrder.setDeliveryTime(deliveryTime);
		dao.savePendingOrder(pendingOrder);
		assertExpected("pending-order-1.xml", "FTGO_PENDING_ORDER");
	}

	public void testSavePendingOrder_restaurant() throws Exception {
		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-1.xml");
		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);
		pendingOrder.setRestaurant(new RestaurantDTO("99", "y"));
		dao.savePendingOrder(pendingOrder);
		assertExpected("pending-order-with-restaurant.xml",
				"FTGO_PENDING_ORDER");
	}

	public void testSavePendingOrder_lineItems() throws Exception {
		String pendingOrderId = cleanInsertAndGetPendingOrderId("pending-order-with-restaurant.xml");
		PendingOrderDTO pendingOrder = dao.findPendingOrder(pendingOrderId);
		MenuItemDTO menuItem = new MenuItemDTO("2", "foo", 10.0);
		PendingOrderLineItemDTO lineItem = new PendingOrderLineItemDTO(1,
				menuItem);
		pendingOrder.add(lineItem);
		dao.savePendingOrder(pendingOrder);
		assertExpected("pending-order-with-lineItems.xml", "FTGO_PENDING_ORDER");
		assertExpected("pending-order-with-lineItems.xml",
				"FTGO_PENDING_ORDER_LINE_ITEM");
	}

}