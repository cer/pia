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
 
package net.chrisrichardson.foodToGo.acknowledgeOrderService.hibernate;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.impl.*;
import net.chrisrichardson.foodToGo.domain.*;

import org.springframework.orm.hibernate3.*;
import org.springframework.orm.hibernate3.support.*;

public class HibernateOrderAttachmentManager extends
        HibernateDaoSupport implements OrderAttachmentManager {

    public HibernateOrderAttachmentManager(
            HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    public Order detach(Order order) {
//        HibernateTemplate template = getHibernateTemplate();
//        template.initialize(order);
//        template.initialize(order.getLineItems());
//        for (Iterator it = order.getLineItems().iterator(); it.hasNext();) {
//            OrderLineItem lineItem = (OrderLineItem) it.next();
//            MenuItem menuItem = lineItem.getMenuItem();
//            template.initialize(menuItem);
//        }
        return order;
    }

    public Order attach(Order order) {
        getHibernateTemplate().update(order);
        return order;
    }

}