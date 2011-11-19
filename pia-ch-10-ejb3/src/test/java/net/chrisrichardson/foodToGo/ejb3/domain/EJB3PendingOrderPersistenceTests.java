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
 


package net.chrisrichardson.foodToGo.ejb3.domain;

import java.util.*;

import javax.persistence.*;

import junit.framework.*;
import net.chrisrichardson.util.*;

import org.hibernate.ejb.*;

public class EJB3PendingOrderPersistenceTests extends TestCase {

	private EntityManager em;

	private EntityTransaction transaction;

	private EntityManagerFactory emf;

	protected String poId;

	protected String restaurantId;

	protected int lineItemId2;

	protected int oldLineItemThatWillBeDeletedId;

	protected int newlyCreatedLineItemId;

	private PendingOrder po;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Properties props = new Properties();

		props.setProperty(Persistence.PERSISTENCE_PROVIDER,
				HibernatePersistence.class.getName());

		emf = Persistence.createEntityManagerFactory("pia", props);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (emf != null)
			emf.close();
	}

	protected void doWithTransaction(TxnCallback cb) throws Throwable {

		em = emf.createEntityManager(PersistenceContextType.EXTENDED);

		transaction = em.getTransaction();
		try {
			transaction.begin();
			cb.execute();
			transaction.commit();
		} finally {
			if (transaction != null && transaction.isActive())
				transaction.rollback();
			if (em != null)
				em.close();
		}

	}

	private void initializeTestData() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {

				po = new PendingOrder();
				em.persist(po);
				poId = po.getId();

				Restaurant restaurant = RestaurantMother.makeRestaurant();
				for (ZipCode zc : restaurant.getServiceArea())
					em.persist(zc);

				em.persist(restaurant);

				restaurantId = restaurant.getId();

			}
		});
	}

	private void updateDeliveryAddress() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {

				RestaurantRepository rr = new EJB3RestaurantRepository(em);
				po = findPendingOrder();
				assertEquals(PlaceOrderStatusCodes.OK, po.updateDeliveryInfo(
						rr, RestaurantTestData.getADDRESS1(),
						RestaurantTestData.makeGoodDeliveryTime(), false));

			}

		});
	}

	private void updateRestaurant() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {
				po = findPendingOrder();
				Restaurant r = em.find(Restaurant.class, new Integer(
						restaurantId));
				assertTrue(po.updateRestaurant(r));
			}

		});
	}

	private void updateQuantities() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {
				po = findPendingOrder();
				po.updateQuantities(new int[] { 1, 2 });

			}

		});

		oldLineItemThatWillBeDeletedId = po.getLineItems().get(0).getId();
	}

	private void verifyLineItemExists() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {
				PendingOrderLineItem lineItemThatShouldExist = em.find(
						PendingOrderLineItem.class,
						oldLineItemThatWillBeDeletedId);
				assertNotNull(lineItemThatShouldExist);
			}

		});
	}

	private void updateQuantitiesAgain() throws Throwable {
		doWithTransaction(new TxnCallback() {

			public void execute() throws Exception {
				po = findPendingOrder();
				po.updateQuantities(new int[] { 3, 0 });
			}

		});
	}

	private PendingOrder findPendingOrder() {
		Class<PendingOrder> type = PendingOrder.class;
		PendingOrder po = em.find(type, new Integer(poId));
		return po;
	}

	public void test() throws Throwable {

		initializeTestData();

		updateDeliveryAddress();

		updateRestaurant();

		updateQuantities();

		verifyLineItemExists();

		updateQuantitiesAgain();

	}

}
