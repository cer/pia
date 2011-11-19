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
 


package net.chrisrichardson.foodToGo.restaurantNotificationService.tsImpl.dao;

import java.sql.*;
import java.util.*;

import org.springframework.orm.ibatis.*;
import org.springframework.orm.ibatis.support.*;

public class OrderDAOIBatisImpl extends SqlMapClientDaoSupport implements
		OrderDAO {

	public static final String OPTIMISTIC_LOCKING = "OPTIMISTIC_LOCKING";

	public static final String PESSIMISTIC_LOCKING = "PESSIMISTIC_LOCKING";

	public static final String ISOLATED_TRANSACTION = "ISOLATED_TRANSACTION";

	private String lockingMode = ISOLATED_TRANSACTION;

	private int timeWindowInMinutes = 60;

	public OrderDAOIBatisImpl(SqlMapClientTemplate sqlMapClientTemplate) {
		setSqlMapClientTemplate(sqlMapClientTemplate);
	}

	public void setLockingMode(String string) {
		lockingMode = string;
	}

	public void setTimeWindowInMinutes(int i) {
		timeWindowInMinutes = i;
	}

	public List findOrdersToSend() {
		Timestamp cutOffTime = calculateCutOffTime();
		return getSqlMapClientTemplate().queryForList(
				"findOrdersToSend_" + lockingMode, cutOffTime);
	}

	Timestamp calculateCutOffTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, -timeWindowInMinutes);
		long cutOffTime = c.getTimeInMillis();
		return new Timestamp(cutOffTime);
	}

	public void markAsSent(List orders, List notifications) {

		MarkOrdersAsSentCallback callback = new MarkOrdersAsSentCallback(
				orders, notifications, lockingMode);
		getSqlMapClientTemplate().execute(callback);
	}

}