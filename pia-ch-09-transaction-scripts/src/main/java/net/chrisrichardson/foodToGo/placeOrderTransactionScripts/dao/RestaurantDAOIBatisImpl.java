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

import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.util.*;

import org.springframework.orm.ibatis.*;
import org.springframework.orm.ibatis.support.*;

public class RestaurantDAOIBatisImpl extends SqlMapClientDaoSupport implements
		RestaurantDAO {

	public RestaurantDAOIBatisImpl(SqlMapClientTemplate sqlMapClientTemplate) {
		setSqlMapClientTemplate(sqlMapClientTemplate);
	}

	public boolean isRestaurantAvailable(Address deliveryAddress,
			Date deliveryTime) {
		return !findAvailableRestaurants(deliveryAddress, deliveryTime).isEmpty();
	}

	public List findAvailableRestaurants(Address deliveryAddress,
			Date deliveryTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryTime);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String zipCode = deliveryAddress.getZip();

		Map deliveryInfo = new HashMap();
		deliveryInfo.put("zipCode", zipCode);
		deliveryInfo.put("dayOfWeek", new Integer(dayOfWeek));
		deliveryInfo.put("hour", new Integer(hour));
		deliveryInfo.put("minute", new Integer(minute));
		return getSqlMapClientTemplate().queryForList(
				"findAvailableRestaurants", deliveryInfo);
	}

	public RestaurantDTO findRestaurant(String restaurantId) {
		return (RestaurantDTO) getSqlMapClientTemplate().queryForObject(
				"findRestaurant", restaurantId);
	}

	public boolean isInServiceArea(String restaurantId,
			Address deliveryAddress, Date deliveryTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryTime);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String zipCode = deliveryAddress.getZip();

		Map params = new HashMap();
		params.put("zipCode", zipCode);
		params.put("dayOfWeek", new Integer(dayOfWeek));
		params.put("hour", new Integer(hour));
		params.put("minute", new Integer(minute));
		params.put("restaurantId", restaurantId);
		
		Object result = getSqlMapClientTemplate().queryForObject("isInServiceArea", params);
		return !new Integer(0).equals(result);
	}

}