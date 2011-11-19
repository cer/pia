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
 
package net.chrisrichardson.foodToGo.domain.hibernate;

import java.sql.*;
import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.ormunit.hibernate.*;

import org.hibernate.*;

public class FoodToGoHibernateMappingTests extends HibernateMappingTests {

	@Override
	protected String[] getConfigLocations() {
		return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

	}

	public void testPendingOrderMapping() throws SQLException,
			HibernateException {

		assertClassMapping(PendingOrder.class, "FTGO_PENDING_ORDER");
		assertIdField("id", "PENDING_ORDER_ID");
		assertVersionField("version", "VERSION");
		assertField("state", "STATE");

		assertManyToOneField("restaurant", "RESTAURANT_ID");

		assertComponentField("deliveryAddress");
		ComponentFieldMapping deliveryAddress = getComponentFieldMapping("deliveryAddress");
		deliveryAddress.assertNonPersistentFields(Collections.EMPTY_SET);

		assertCompositeListField("lineItems");
		CompositeListFieldMapping lineItems = getCompositeListFieldMapping("lineItems");
		lineItems.assertTable("FTGO_PENDING_ORDER_LINE_ITEM");
		lineItems.assertForeignKey("PENDING_ORDER_ID");
		lineItems.assertIndexColumn("LINE_ITEM_INDEX");
		lineItems.assertField("quantity", "QUANTITY");
		lineItems.assertManyToOneField("menuItem", "MENU_ITEM_ID");
		lineItems.assertNonPersistentFields(Collections.singleton("id"));

		assertAllFieldsMappedExcept("lineItems.id");
	}

	public void testRestaurantMapping() throws SQLException, HibernateException {
		assertClassMapping(Restaurant.class, "FTGO_RESTAURANT");
		assertIdField("id", "RESTAURANT_ID");
		assertField("name", "NAME");
		assertOneToManyListField("menuItems", "RESTAURANT_ID",
				"MENU_ITEM_INDEX");

		assertSetField("timeRanges");
		CompositeSetFieldMapping timeRanges = getCompositeSetFieldMapping("timeRanges");
		timeRanges.assertField("dayOfWeek", "DAY_OF_WEEK");
		// ...
		timeRanges.assertNonPersistentFields("id");

		assertAllFieldsMappedExcept("notificationEmailAddress");
	}
	
	public void testOrderMapping() throws Exception {
		assertClassMapping(Order.class, "FTGO_PLACED_ORDER");
		assertAllFieldsMapped();
	}

}