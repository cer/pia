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
 
package net.chrisrichardson.foodToGo.placeOrderTransactionScripts.details;

public class PendingOrderLineItemDTO {

	private int quantity;

	private MenuItemDTO menuItem;

	private int index;

	public void setIndex(int index) {
		this.index = index;
	}

	public void setMenuItem(MenuItemDTO menuItem) {
		this.menuItem = menuItem;
	}

	public PendingOrderLineItemDTO() {
		super();
	}

	public PendingOrderLineItemDTO(int quantity, MenuItemDTO menuItem) {
		this.quantity = quantity;
		this.menuItem = menuItem;
	}

	public PendingOrderLineItemDTO(int index, int quantity, MenuItemDTO menuItem) {
		this.index = index;
		this.quantity = quantity;
		this.menuItem = menuItem;
	}

	public int getQuantity() {
		return quantity;
	}

	public MenuItemDTO getMenuItem() {
		return menuItem;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getIndex() {
		return index;
	}
}
