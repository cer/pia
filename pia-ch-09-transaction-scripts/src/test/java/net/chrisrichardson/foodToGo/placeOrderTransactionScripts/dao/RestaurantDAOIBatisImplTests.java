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

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.ormunit.ibatis.*;

public class RestaurantDAOIBatisImplTests extends IBatisTests {

	private RestaurantDAO dao;

	private Date goodTime;

	private Date badTime;

	private Address deliveryAddress;

	protected String[] getConfigLocations() {
		return IBatisDAOTestConstants.IBATIS_DAO_CONTEXT;
	}

	public void setDao(RestaurantDAO dao) {
		this.dao = dao;
	}

	@Override
	protected String getInitialDataSet() {
		return "initialize-restaurants.xml";
	}

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		deliveryAddress = RestaurantTestData.getADDRESS1();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		c.set(Calendar.HOUR_OF_DAY, 19);
		c.set(Calendar.MINUTE, 13);
		goodTime = c.getTime();

		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		badTime = c.getTime();
	}

	public void testFindAvailableRestaurants() throws Exception {
		assertEquals(1, dao.findAvailableRestaurants(deliveryAddress, goodTime).size());
		assertEquals(0, dao.findAvailableRestaurants(deliveryAddress, badTime).size());
	}

	public void testIsRestaurantAvailable() throws Exception {
		assertTrue(dao.isRestaurantAvailable(deliveryAddress, goodTime));
		assertFalse(dao.isRestaurantAvailable(deliveryAddress, badTime));
	}

	public void testIsInServiceArea() throws Exception {
		assertTrue(dao.isInServiceArea("99", deliveryAddress, goodTime));
		assertFalse(dao.isInServiceArea("99", deliveryAddress, badTime));
	}
}