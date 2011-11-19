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
import java.util.Date;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;

public class HibernateRestaurantRepositoryImpl extends HibernateDaoSupport
		implements RestaurantRepository {

	public HibernateRestaurantRepositoryImpl(HibernateTemplate template) {
		setHibernateTemplate(template);
	}

	public Restaurant findRestaurant(String restaurantId) {
		return (Restaurant) getHibernateTemplate().load(Restaurant.class,
				new Integer(restaurantId));
	}

	public List findAvailableRestaurants(Address deliveryAddress,
			Date deliveryTime) {
		String[] paramNames = { "zipCode", "dayOfWeek", "hour", "minute" };
		Object[] paramValues = makeParameterValues(deliveryAddress,
				deliveryTime);
		List result = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findAvailableRestaurants", paramNames, paramValues);
		return result;
	}

	Object[] makeParameterValues(Address deliveryAddress, Date deliveryTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryTime);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String zipCode = deliveryAddress.getZip();

		Object[] values = new Object[] { zipCode, new Integer(dayOfWeek),
				new Integer(hour), new Integer(minute) };
		return values;
	}

	public boolean isRestaurantAvailable(final Address deliveryAddress,
			final Date deliveryTime) {
		return !findAvailableRestaurants(deliveryAddress, deliveryTime)
				.isEmpty();
	}

	public Collection findAllRestaurants() throws HibernateException {
		return getHibernateTemplate().find("from " + Order.class.getName());
	}

	public List findAvailableRestaurantsInline(Address deliveryAddress,
			Date deliveryTime) {
		final String[] paramNames = { "zipCode", "dayOfWeek", "hour", "minute" };
		final Object[] paramValues = makeParameterValues(deliveryAddress,
				deliveryTime);
		return getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.getNamedQuery("findAvailableRestaurants");
				query.setCacheable(true);
				for (int i = 0; i < paramValues.length; i++) {
					Object value = paramValues[i];
					String name = paramNames[i];
					query.setParameter(name, value);
				}
				if (true)
					return query.list();
				else {
					Iterator it = query.iterate();
					List result = new ArrayList();
					while (it.hasNext()) {
						Restaurant restaurant = (Restaurant) it.next();
						result.add(restaurant);
					}
					return result;
				}
			}
		});
	}


}