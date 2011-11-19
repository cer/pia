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
 
package net.chrisrichardson.foodToGo.viewOrdersTransactionScripts.dao;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.ormunit.ibatis.*;

public class OrderDAOIBatisImplTests extends IBatisTests {

	private OrderDAOIBatisImpl dao;

	protected String[] getConfigLocations() {
		return new String[] {
				"define-hsqldb-datasource.xml",
				"domain-common-session-factory.xml",
				"domain-hsqldb-session-factory.xml", 
				"view-orders-ibatis-dao.xml",
				"view-orders-ibatis-config.xml" };

	}

	public void setDao(OrderDAOIBatisImpl dao) {
		this.dao = dao;
	}

	@Override
	protected String getInitialDataSet() {
		return "initialize-orders.xml";
	}
	
	public void testFindOrdersWithoutRownum() throws Exception {
		OrderSearchCriteria criteria = new OrderSearchCriteria();
		PagedQueryResult result = dao.findOrdersInlineWithoutRownum(0, 10,
				criteria);
		assertFalse(result.isMore());
		assertFalse(result.getResults().isEmpty());
		OrderSummaryDTO order = (OrderSummaryDTO) result.getResults().get(0);
		assertNotNull(order.getRestaurantName());
	}

}