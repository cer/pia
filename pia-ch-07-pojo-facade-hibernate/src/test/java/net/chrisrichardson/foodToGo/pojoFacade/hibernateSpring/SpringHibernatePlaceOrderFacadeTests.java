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
 
package net.chrisrichardson.foodToGo.pojoFacade.hibernateSpring;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.hibernate.*;
import net.chrisrichardson.foodToGo.pojoFacade.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.foodToGo.views.*;
import net.chrisrichardson.ormunit.hibernate.*;

public class SpringHibernatePlaceOrderFacadeTests extends
		HibernatePersistenceTests {

	private PlaceOrderFacade facade;

	public void setFacade(PlaceOrderFacade facade) {
		this.facade = facade;
	}

	@Override
	protected String[] getConfigLocations() {
		String[] facadeFiles = { "placeOrderFacade-generic-beans.xml",
				"placeOrderFacade-hibernate-beans.xml",
				"domain-hibernate-local-transaction-manager.xml", };

		return ArrayUtils.concatenate(facadeFiles,
				HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT);
	}

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		Restaurant r = RestaurantMother.makeRestaurant();
		hibernateTemplate.save(r);

	}

	public void testUpdateDeliveryInfo() throws Exception {
		Address address = RestaurantTestData.getADDRESS1();
		Date time = RestaurantTestData.makeGoodDeliveryTime();

		PlaceOrderFacadeResult result = facade.updateDeliveryInfo(null,
				address, time);

		assertEquals(PlaceOrderStatusCodes.OK, result.getStatusCode());

		PendingOrderDetail pendingOrder = result.getPendingOrder();
		String id = pendingOrder.getId();

		assertNotNull(hibernateTemplate
				.get(PendingOrder.class, new Integer(id)));
	}

}