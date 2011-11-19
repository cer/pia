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
 




package net.chrisrichardson.foodToGo.ejb3.domain;

import java.io.*;

import javax.persistence.*;

@Entity(access = AccessType.FIELD)
@Table(name = "FTGO_PENDING_ORDER_LINE_ITEM")
public class PendingOrderLineItem implements
        Serializable {

    @Id(generate = GeneratorType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem menuItem;

    private int quantity;

//    @SuppressWarnings("unused")
//    @Column(name="LINE_ITEM_INDEX")
//    private int index;

    public PendingOrderLineItem() {
    }

    public int getId() {
        return id;
    }

    public PendingOrderLineItem(int quantity,
            MenuItem menuItem) {
        this.quantity = quantity;
        this.menuItem = menuItem;
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
        return menuItem.getName();
    }

    public double getPrice() {
        return menuItem.getPrice();
    }

    public void setMenuItem(MenuItem item) {
        this.menuItem = (MenuItem) item;
    }

    public double getExtendedPrice() {
        return getPrice() * getQuantity();
    }


}
