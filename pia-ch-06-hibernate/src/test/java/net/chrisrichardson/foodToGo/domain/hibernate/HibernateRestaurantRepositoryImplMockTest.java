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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.chrisrichardson.foodToGo.domain.Restaurant;
import net.chrisrichardson.foodToGo.util.Address;

import org.jmock.cglib.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernateRestaurantRepositoryImplMockTest extends
		MockObjectTestCase {

	private static final int EXPECTED_MINUTE = 6;

	private static final int EXPECTED_HOUR = 5;

	private static final int EXPECTED_DAY_OF_WEEK = 3;

	private Mock mockHibernateTemplate;

	private HibernateTemplate hibernateTemplate;

	private HibernateRestaurantRepositoryImpl repository;

	private Restaurant restaurant;

	Address deliveryAddress = new Address("1 somewhere", null, "Oakland", "CA",
			"94619");

	Date deliveryTime = makeDeliveryTime(EXPECTED_DAY_OF_WEEK, EXPECTED_HOUR,
			EXPECTED_MINUTE);

	Object[] expectedValues = new Object[] { deliveryAddress.getZip(),
			new Integer(EXPECTED_DAY_OF_WEEK), new Integer(EXPECTED_HOUR * 100 + EXPECTED_MINUTE) };

	String[] expectedNames = { "zipCode", "dayOfWeek", "timeOfDay"};

	public void setUp() {
		mockHibernateTemplate = new Mock(HibernateTemplate.class);
		hibernateTemplate = (HibernateTemplate) mockHibernateTemplate.proxy();

		repository = new HibernateRestaurantRepositoryImpl(hibernateTemplate);

		restaurant = new Restaurant("Test", "TestType",
				Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.EMPTY_LIST);

	}

	public void testFindRestaurant() {
		String restaurantId = "99";
		mockHibernateTemplate.expects(once()).method("load").with(
				eq(Restaurant.class), eq(new Integer(restaurantId))).will(
				returnValue(restaurant));
		Restaurant foundRestaurant = repository.findRestaurant(restaurantId);
		assertEquals(restaurant, foundRestaurant);
	}

	public void testFindAvailableRestaurants() {
		List expectedRestaurants = Collections.singletonList(restaurant);

		mockHibernateTemplate.expects(once()).method(
				"findByNamedQueryAndNamedParam").with(
				eq("findAvailableRestaurants"), eq(expectedNames),
				eq(expectedValues)).will(returnValue(expectedRestaurants));

		List foundRestaurants = repository.findAvailableRestaurants(
				deliveryAddress, deliveryTime);
		assertEquals(expectedRestaurants, foundRestaurants);
	}

	private Date makeDeliveryTime(int dayOfWeek, int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		return c.getTime();
	}

	public void testIsRestaurantAvailable() {
		Address deliveryAddress = new Address("1 somewhere", null, "Oakland",
				"CA", "94619");
		Date deliveryTime = makeDeliveryTime(EXPECTED_DAY_OF_WEEK,
				EXPECTED_HOUR, EXPECTED_MINUTE);
		List expectedRestaurants = Collections.singletonList(restaurant);

		mockHibernateTemplate.expects(once()).method(
				"findByNamedQueryAndNamedParam").with(
				eq("findAvailableRestaurants"), eq(expectedNames),
				eq(expectedValues)).will(returnValue(expectedRestaurants));

		assertTrue(repository.isRestaurantAvailable(deliveryAddress,
				deliveryTime));
	}

	public void testIsRestaurantAvailable_No() {
		Address deliveryAddress = new Address("1 somewhere", null, "Oakland",
				"CA", "94619");
		Date deliveryTime = makeDeliveryTime(EXPECTED_DAY_OF_WEEK,
				EXPECTED_HOUR, EXPECTED_MINUTE);

		mockHibernateTemplate.expects(once()).method(
				"findByNamedQueryAndNamedParam").with(
				eq("findAvailableRestaurants"), eq(expectedNames),
				eq(expectedValues)).will(returnValue(Collections.EMPTY_LIST));

		assertFalse(repository.isRestaurantAvailable(deliveryAddress,
				deliveryTime));
	}
}