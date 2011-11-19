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

import java.io.*;
import java.util.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.domain.hibernate.*;
import net.chrisrichardson.foodToGo.util.hibernate.*;
import net.chrisrichardson.ormunit.hibernate.*;
import net.chrisrichardson.util.*;

import org.springframework.dao.*;

public class HibernateOrderAttachmentManagerTests
        extends HibernatePersistenceTests {

    private HibernateOrderAttachmentManager am;

    @Override
    protected String[] getConfigLocations() {
        return HibernateTestConstants.HIBERNATE_DOMAIN_CONTEXT;

    }

    protected void onSetUp() throws Exception {
        super.onSetUp();
        System.out
                .println("starting another test=================================");
        HibernateObjectAttacher attacher = new HibernateObjectAttacher(
                getHibernateTemplate());

        HibernateWrapper initializer = new HibernateQueryExecutor(
                getHibernateTemplate());
        am = new HibernateOrderAttachmentManager(
                getHibernateTemplate());
    }

    public void test() throws Exception {

        Order po = OrderMother.makeOrder();
        save(po.getRestaurant());
        save(po);

        Order detachedOrder = am.detach(po);
        detachedOrder.getVersion();
        detachedOrder.noteSent("msgid", new Date());
        detachedOrder.accept("x");

        Order attachedOrder = am.attach(detachedOrder);
        assertFalse(attachedOrder.isAcknowledgable());
    }

    public void testChanged() throws Exception {

        Order po = OrderMother.makeOrder();
        save(po.getRestaurant());
        save(po);
        final Serializable orderId = po.getId();

        Order detachedOrder = am.detach(po);

        doWithTransaction(new TxnCallback() {
            public void execute() throws Exception {
                Order po = (Order) load(Order.class,
                        orderId.toString());
                po.noteSent("msgid", new Date());
                po.accept("x");
            }
        });

        try {
            Order attachedOrder = am
                    .attach(detachedOrder);
            fail("Expected exception");
        } catch (OptimisticLockingFailureException e) {

        }
    }

}