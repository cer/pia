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
 
package net.chrisrichardson.foodToGo.pojoFacade.hibernate;

import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.pojoFacade.*;

import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;

/**
 * Hibernate implementation of the result factory for the PlaceOrderFacade
 * It calls Hibernate.initialize() to ensure that the objects and collections are loaded
 * @author cer
 *
 */
public class HibernatePlaceOrderFacadeResultFactory extends
        HibernateDaoSupport implements
        PlaceOrderFacadeResultFactory {

    public HibernatePlaceOrderFacadeResultFactory(
            HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public PlaceOrderFacadeResult make(int statusCode,
            PendingOrder pendingOrder, List restaurants) {
        getHibernateTemplate().initialize(pendingOrder.getLineItems());
        Restaurant restaurant = pendingOrder.getRestaurant();
        if (restaurant != null) {
            List menuItems = restaurant.getMenuItems();
            getHibernateTemplate().initialize(menuItems);
        }
        return new PlaceOrderFacadeResult(statusCode, pendingOrder,
                restaurants);
    }

    public PlaceOrderFacadeResult make(int statusCode,
            PendingOrder pendingOrder) {
        return make(statusCode, pendingOrder, null);
    }

    public PlaceOrderFacadeResult make(int statusCode, Order order) {
        getHibernateTemplate().initialize(order.getLineItems());
        getHibernateTemplate().initialize(order.getRestaurant());
        return new PlaceOrderFacadeResult(statusCode, order);
    }

}