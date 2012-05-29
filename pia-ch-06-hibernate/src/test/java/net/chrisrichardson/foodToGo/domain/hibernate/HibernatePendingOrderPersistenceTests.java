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

import net.chrisrichardson.foodToGo.creditCardProcessing.PaymentInformation;
import net.chrisrichardson.foodToGo.domain.Coupon;
import net.chrisrichardson.foodToGo.domain.FreeShippingCoupon;
import net.chrisrichardson.foodToGo.domain.PendingOrder;
import net.chrisrichardson.foodToGo.domain.PercentageDiscountCoupon;
import net.chrisrichardson.foodToGo.domain.PlaceOrderStatusCodes;
import net.chrisrichardson.foodToGo.domain.Restaurant;
import net.chrisrichardson.foodToGo.domain.RestaurantMother;
import net.chrisrichardson.foodToGo.util.Address;
import net.chrisrichardson.ormunit.hibernate.HibernatePersistenceTestsWithResetDatabase;
import net.chrisrichardson.util.TxnCallback;

import org.hibernate.Hibernate;

public class HibernatePendingOrderPersistenceTests extends
		HibernatePersistenceTestsWithResetDatabase {

	private String pendingOrderId;

	private String restaurantId;

	private HibernateRestaurantRepositoryImpl restaurantRepository;

	public HibernatePendingOrderPersistenceTests() {
		super();
	}

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
				"domain-hibernate-repositories.xml",
				"hsqldb-ormunit-hibernate-strategies.xml",
				"domain-hibernate-local-transaction-manager.xml",
				"exception-translator.xml",
				"define-hsqldb-datasource.xml",
				"domain-common-session-factory.xml",
				"domain-hsqldb-session-factory.xml",
        };

    }


	public void onSetUp() throws Exception {
		super.onSetUp();
		restaurantRepository = new HibernateRestaurantRepositoryImpl(
				getHibernateTemplate());
		Restaurant r = RestaurantMother.makeRestaurant();
		save(r);
		restaurantId = r.getRestaurantId();
	}

	public void testSimple() throws Exception {

		createPendingOrder();

		logger.debug("-------------------Calling updateDeliveryInfo");
		updateDeliveryInfo();

		System.out
				.println("-------------------Calling updateDeliveryInfo again");
		updateDeliveryInfo();

		logger.debug("-------------------Calling updateRestaurant");
		updateRestaurant(restaurantId);

		System.out
				.println("-------------------Calling updateRestaurant - again");
		updateRestaurant(restaurantId);

		logger.debug("-------------------Calling updateQuantities1");
		updateQuantities1();

		logger.debug("-------------------Calling updateQuantities2");
		updateQuantities2();

		logger.debug("-------------------Calling updatePaymentInfo");
		updatePaymentInfo();

		System.out
				.println("-------------------Calling updateQuantities1 - again");
		updateQuantities1();

		System.out
				.println("-------------------Calling updatePaymentInfo - again");
		updatePaymentInfo();

		logger.debug("-------------------Calling deletePendingOrder");
		deletePendingOrder();

		// ...

	}

	private void createPendingOrder() {
		PendingOrder po = new PendingOrder();
		save(po);
		pendingOrderId = po.getId();
	}

	private void updateDeliveryInfo() {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				Date deliveryTime = RestaurantMother.makeDeliveryTime();
				logger.debug("delvieryTime=" + deliveryTime);
				Address deliveryAddress = new Address("go", null, "city", "CA",
						"94619");

				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				int updateDeliveryInfoResult = po.updateDeliveryInfo(
						restaurantRepository, deliveryAddress, deliveryTime,
						true);

				assertEquals(PlaceOrderStatusCodes.OK, updateDeliveryInfoResult);
			}
		});
	}

	private void updateRestaurant(final String restaurantId) {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				Restaurant r = (Restaurant) load(Restaurant.class, restaurantId);
				boolean updateRestaurantResult = po.updateRestaurant(r);
				assertTrue(updateRestaurantResult);
			}
		});
	}

	private void updateQuantities1() {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = loadPendingOrder();
				po.updateQuantities(new int[] { 1, 2 });
			}

		});
	}

	private void updateQuantities2() {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = loadPendingOrder();
				po.updateQuantities(new int[] { 0, 3 });
			}
		});
	}

	private void updatePaymentInfo() {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = loadPendingOrder();
				PaymentInformation paymentInfo = PendingOrderTestData.PAYMENT_INFORMATION;
				po.updatePaymentInformation(paymentInfo, null);
			}

		});
	}

	private PendingOrder loadPendingOrder() {
		logger.debug("Executing load:");
		PendingOrder po = (PendingOrder) load(PendingOrder.class,
				pendingOrderId);
		return po;
	}

	private void deletePendingOrder() {
		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = loadPendingOrder();
				delete(po);
			}
		});
	}

	public void testWithRestaurant() throws Exception {
		createPendingOrder();

		updateDeliveryInfo();

		updateRestaurant(restaurantId);

		logger.debug("Loading");

		loadPendingOrder();

	}

	public void testWithCoupon() throws Exception {

		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				PendingOrder po = new PendingOrder();
				Coupon coupon = new FreeShippingCoupon("xx", 100);
				save(coupon);
				po.setCoupon(coupon);
				save(po);
				pendingOrderId = po.getId();
			}
		});

		doWithTransaction(new TxnCallback() {
			public void execute() throws Exception {
				logger.debug("loading po");
				PendingOrder po = (PendingOrder) load(PendingOrder.class,
						pendingOrderId);
				logger.debug("getting coupon");
				Coupon c = po.getCoupon();
				logger.debug("got coupon");
				assertNotNull(c);
				assertTrue(c instanceof Coupon);
				logger.debug("getting class");
				Class trueClass = Hibernate.getClass(c);
				logger.debug("got class");
				assertTrue(FreeShippingCoupon.class.isAssignableFrom(trueClass));
				// This will fail because of the proxy problem
				assertFalse(c instanceof FreeShippingCoupon);
				assertFalse(c instanceof PercentageDiscountCoupon);
			}
		});

	}

}