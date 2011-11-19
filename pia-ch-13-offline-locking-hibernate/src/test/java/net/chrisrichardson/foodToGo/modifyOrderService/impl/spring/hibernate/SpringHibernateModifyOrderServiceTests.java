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
 
package net.chrisrichardson.foodToGo.modifyOrderService.impl.spring.hibernate;

import java.sql.*;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.hibernate.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.modifyOrderService.*;
import net.chrisrichardson.ormunit.hibernate.*;

import org.hibernate.*;
import org.springframework.orm.hibernate3.*;

public class SpringHibernateModifyOrderServiceTests extends
		HibernatePersistenceTests {

    @Override
    protected String[] getConfigLocations() {
        return OfflineLockingConstants.OFFLINE_LOCKING_CONTEXT;
    }

	private ModifyOrderService service;

	private Order order;

	public void setFacade(ModifyOrderService service) {
		this.service = service;
	}

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		order = OrderMother.makeOrder();
		save(order.getRestaurant());
		save(order);

		// FIXME - ugliness to create FTGO_LOCK table - need to change the name of the table
		hibernateTemplate.execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Connection con = session.connection();
				con.createStatement().execute("DROP TABLE FTGO_LOCK IF EXISTS");
				con
						.createStatement()
						.execute(
								"create table FTGO_LOCK (CLASS_ID VARCHAR(100) NOT NULL,PK VARCHAR(100) NOT NULL,OWNER VARCHAR(100) NOT NULL,CONSTRAINT FTGO_LOCK_PK PRIMARY KEY (CLASS_ID, PK))");
				return null;
			}
		});
	}

	public void testModifyOrderService() throws InvalidPendingOrderStateException {

		ModifyOrderServiceResult result = service.getOrderToModify("Test",
				order.getId());

		assertEquals(ModifyOrderServiceResult.OK, result.getStatusCode());

		result = service.updateQuantities("Test", order.getId(), result
				.getPendingOrder().getId(), new int[] { 0, 5 });

		assertEquals(ModifyOrderServiceResult.OK, result.getStatusCode());

		result = service.saveChangesToOrder("Test", order.getId(), result
				.getPendingOrder().getId());

		assertEquals(ModifyOrderServiceResult.OK, result.getStatusCode());
	}
	
	public void testModifyOrderService_AlreadyLocked() throws InvalidPendingOrderStateException {

		ModifyOrderServiceResult result = service.getOrderToModify("Test",
				order.getId());
		assertEquals(ModifyOrderServiceResult.OK, result.getStatusCode());

		ModifyOrderServiceResult result2 = service.getOrderToModify("Test2",
				order.getId());
		assertEquals(ModifyOrderServiceResult.ALREADY_LOCKED, result2.getStatusCode());
	}

}