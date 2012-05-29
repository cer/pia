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

import java.util.Date;

import net.chrisrichardson.foodToGo.domain.PendingOrder;
import net.chrisrichardson.foodToGo.domain.PendingOrderLineItem;
import net.chrisrichardson.foodToGo.domain.PlaceOrderStatusCodes;
import net.chrisrichardson.foodToGo.domain.Restaurant;
import net.chrisrichardson.foodToGo.domain.RestaurantMother;
import net.chrisrichardson.foodToGo.util.Address;
import net.chrisrichardson.ormunit.hibernate.HibernatePersistenceTests;
import net.chrisrichardson.util.TxnCallback;

public class HibernatePendingOrderRepositoryTests extends
		HibernatePersistenceTests {

	@Override
	protected String[] getConfigLocations() {
		return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

	}

	private HibernatePendingOrderRepositoryImpl repository;

	protected PendingOrder pendingOrder;

	protected void onSetUp() throws Exception {
		super.onSetUp();
		repository = new HibernatePendingOrderRepositoryImpl(
				getHibernateTemplate());
	}

	public void testCreatePendingOrder() throws Exception {
		// doWithTransaction(new Callback() {
		// public void execute() throws Exception {
		pendingOrder = repository.findOrCreatePendingOrder(null);

		// }
		//
		// });

		String id = pendingOrder.getId();
		PendingOrder pendingOrder2 = (PendingOrder) load(PendingOrder.class, id);
		assertNotNull(pendingOrder2);
	}

	public void testFindPendingOrderWithRestaurantLineItemsAndMenuItems()
			throws Exception {
		Restaurant r = RestaurantMother.makeRestaurant();
		save(r);
		final String restaurantId = r.getRestaurantId();

		PendingOrder po1 = repository.findOrCreatePendingOrder(null);
		final String pendingOrderId = po1.getId();

		// FIXME - duplicated code
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				Date deliveryTime = RestaurantMother.makeDeliveryTime();
				Address deliveryAddress = new Address("go", null, "city", "CA",
						"94619");

				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				HibernateRestaurantRepositoryImpl restaurantRepository = new HibernateRestaurantRepositoryImpl(
						getHibernateTemplate());
				int updateDeliveryInfoResult = po.updateDeliveryInfo(
						restaurantRepository, deliveryAddress, deliveryTime,
						true);

				assertEquals(PlaceOrderStatusCodes.OK, updateDeliveryInfoResult);
			}
		});

		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				Restaurant r = (Restaurant) load(Restaurant.class, restaurantId);
				boolean updateRestaurantResult = po.updateRestaurant(r);
				assertTrue(updateRestaurantResult);
			}
		});

		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				po.updateQuantities(new int[] { 1, 2 });
			}

		});

		// End duplication

		logger.debug("loading");
		PendingOrder po2 = repository
				.findPendingOrderWithRestaurantLineItemsAndMenuItems(pendingOrderId);
		assertEquals(po2.getId(), pendingOrderId);
		logger.debug("navigating");
		assertNotNull(((PendingOrderLineItem) po2.getLineItems().get(0))
				.getMenuItem().getName());
		logger.debug("done");
	}

	public void testFindPendingOrderWithRestaurantAndMenuItems()
			throws Exception {
		PendingOrder po1 = repository.findOrCreatePendingOrder(null);
		String poId = po1.getId();
		PendingOrder po2 = repository
				.findPendingOrderWithRestaurantAndMenuItems(poId);
	}

	public void testFindPendingOrderWithLineItems() throws Exception {
		PendingOrder po1 = repository.findOrCreatePendingOrder(null);
		String poId = po1.getId();
		PendingOrder po2 = repository.findPendingOrderWithLineItems(poId);
	}
}