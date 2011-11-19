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
 


package net.chrisrichardson.foodToGo.domain;

import java.io.*;

public class PendingOrderLineItem
	implements QuantityAndMenuItem, Serializable {


    private int id = -1;
	private MenuItem menuItem;
	private int quantity;

	public PendingOrderLineItem() {
	}

	public PendingOrderLineItem(
		int quantity,
		MenuItem menuItem) {
		this.quantity = quantity;
		this.menuItem = (MenuItem) menuItem;
	}

	public int getQuantity() {
		return quantity;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public String getName() {
		return menuItem.getName();
	}

	public double getPrice() {
		return menuItem.getPrice();
	}

    public double getExtendedPrice() {
        return menuItem.getPrice() * quantity;
    }

}

