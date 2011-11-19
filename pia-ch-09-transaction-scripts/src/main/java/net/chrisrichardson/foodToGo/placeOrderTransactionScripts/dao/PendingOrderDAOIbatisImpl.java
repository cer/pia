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

import org.springframework.orm.ibatis.*;
import org.springframework.orm.ibatis.support.*;

public class PendingOrderDAOIbatisImpl extends SqlMapClientDaoSupport implements
		PendingOrderDAO {

	public PendingOrderDAOIbatisImpl(SqlMapClientTemplate sqlMapClientTemplate) {
		setSqlMapClientTemplate(sqlMapClientTemplate);
	}

	public PendingOrderDTO createPendingOrder() {
		PendingOrderDTO pendingOrder = new PendingOrderDTO();
		getSqlMapClientTemplate().insert("insertPendingOrder", pendingOrder);
		return pendingOrder;
	}

	public PendingOrderDTO findPendingOrder(String pendingOrderId) {
		PendingOrderDTO pendingOrderDTO = (PendingOrderDTO) getSqlMapClientTemplate()
				.queryForObject("findPendingOrder", pendingOrderId);
		return pendingOrderDTO;
	}

	public void savePendingOrder(PendingOrderDTO pendingOrder) {
		savePendingOrderHeader(pendingOrder);
		deleteExistingLineItems(pendingOrder);
		saveNewLineItems(pendingOrder);
	}

	private void savePendingOrderHeader(PendingOrderDTO pendingOrder) {
		getSqlMapClientTemplate().update("savePendingOrder", pendingOrder);
	}

	private void deleteExistingLineItems(PendingOrderDTO pendingOrder) {

		getSqlMapClientTemplate().update("deleteLineItems", pendingOrder);

	}

	private void saveNewLineItems(final PendingOrderDTO pendingOrder) {
		final List lineItems = pendingOrder.getLineItems();
		if (lineItems == null || lineItems.isEmpty())
			return;
		getSqlMapClientTemplate().execute(
				new InsertLineItemsIBatisCallback(pendingOrder));
	}

	public void create(PendingOrderDTO pendingOrderDetails) {
		String pendingOrderId = createPendingOrder().getPendingOrderId()
				.toString();
		pendingOrderDetails.setPendingOrderId(pendingOrderId);
		savePendingOrder(pendingOrderDetails);
	}

}