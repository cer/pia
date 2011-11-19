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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.spring;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.*;
import net.chrisrichardson.foodToGo.placeOrderTransactionScripts.dao.*;
import net.chrisrichardson.foodToGo.util.*;
import net.chrisrichardson.ormunit.ibatis.*;

public class SpringPlaceOrderTransactionScriptTests extends IBatisTests {

	private PlaceOrderTransactionScripts transactionScripts;

	protected String[] getConfigLocations() {
		return ArrayUtils.concatenate(
				IBatisDAOTestConstants.IBATIS_DAO_CONTEXT,
				new String[] { "place-order-transaction-script.xml" });

	}

	public void setTransactionScripts(
			PlaceOrderTransactionScripts transactionScripts) {
		this.transactionScripts = transactionScripts;
	}

	public void testUpdateDeliveryInfoIBatis() throws Exception {
		cleanInsert("initialize-for-place-order.xml");
		Address deliveryAddress = RestaurantTestData.getADDRESS1();
		Date deliveryTime = RestaurantTestData.makeGoodDeliveryTime();
		UpdateDeliveryInfoResult result = transactionScripts.updateDeliveryInfo(null, deliveryAddress, deliveryTime);
		assertEquals(UpdateDeliveryInfoResult.SELECT_RESTAURANT, result.getStatusCode());
		String pendingOrderId = result.getPendingOrder().getPendingOrderId();
		assertNotNull(pendingOrderId);
		
		PlaceOrderResult result2 = transactionScripts.updateRestaurant(pendingOrderId, "99");
		assertEquals(PlaceOrderResult.OK, result2.getStatusCode());
		
		assertFalse(result2.getPendingOrder().getRestaurant().getMenuItems().isEmpty());
	}

}