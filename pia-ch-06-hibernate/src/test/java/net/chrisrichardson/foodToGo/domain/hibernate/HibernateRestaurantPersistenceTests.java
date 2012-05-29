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

import java.util.List;

import net.chrisrichardson.foodToGo.domain.MenuItem;
import net.chrisrichardson.foodToGo.domain.Restaurant;
import net.chrisrichardson.foodToGo.domain.RestaurantMother;
import net.chrisrichardson.ormunit.hibernate.HibernatePersistenceTests;
import net.chrisrichardson.util.TxnCallback;

public class HibernateRestaurantPersistenceTests extends
		HibernatePersistenceTests {

	private Restaurant r;

	@Override
	protected String[] getConfigLocations() {
		return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

	}

	public void testSimple() throws Exception {
		r = RestaurantMother.makeRestaurant();
		save(r);
		String id = r.getRestaurantId();
		logger.debug("Loading");
		r = (Restaurant) load(Restaurant.class, id);
	}

	public void testChangeMenuItem() throws Exception {
		r = RestaurantMother.makeRestaurant();
		save(r);
		final String id = r.getRestaurantId();
		logger.debug("Loading");
		doWithTransaction(new TxnCallback() {

			public void execute() throws Throwable {
				r = (Restaurant) load(Restaurant.class, id);
				List menuItems = r.getMenuItems();
				menuItems.remove(menuItems.size() - 1);
				menuItems.add(new MenuItem("Spicy crab cakes", 5.50));
			}
		});
		logger.debug("Final step****");
		doWithTransaction(new TxnCallback() {

			public void execute() throws Throwable {
				r = (Restaurant) load(Restaurant.class, id);
				List menuItems = r.getMenuItems();
				menuItems.clear();
			}
		});
	}
}