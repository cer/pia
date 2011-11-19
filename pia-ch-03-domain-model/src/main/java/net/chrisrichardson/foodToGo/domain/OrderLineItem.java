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

public class OrderLineItem
	implements QuantityAndMenuItem, Serializable {

	// begin(fields)

	private MenuItem menuItem;
    private String name;
	private int quantity;
	private double extendedPrice;
    private double price;

	// end(fields)

	// begin(methods)

	public OrderLineItem() {
	}

	public OrderLineItem(
		int quantity,
		MenuItem menuItem) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.name = menuItem.getName();
        this.price = menuItem.getPrice();
        this.extendedPrice = price * this.price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public String getName() {
		return name;
	}
	
	public double getPrice() {
		return price;
	}

    public double getExtendedPrice() {
        return extendedPrice;
    }

    public void setExtendedPrice(double extendedPrice) {
        this.extendedPrice = extendedPrice;
    }
}
