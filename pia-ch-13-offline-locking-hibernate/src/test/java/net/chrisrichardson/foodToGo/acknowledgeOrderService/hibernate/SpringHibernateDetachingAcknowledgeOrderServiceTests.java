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

import java.util.*;

import net.chrisrichardson.foodToGo.acknowledgeOrderService.*;
import net.chrisrichardson.foodToGo.acknowledgeOrderService.values.*;
import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.ormunit.hibernate.*;
import net.chrisrichardson.util.*;

import org.springframework.orm.*;

// FIXME - this is not Hibernate-specific
// Introduce a transaction superclass and move doWithTransaction into that.

public class SpringHibernateDetachingAcknowledgeOrderServiceTests
        extends HibernatePersistenceTests {

    private DetachingAcknowledgeOrderService service;
    private Order order;
    private String orderId;
	
    @Override
    protected String[] getConfigLocations() {
        return OfflineLockingConstants.OFFLINE_LOCKING_CONTEXT;
    }
    
    
    public void setService(
            DetachingAcknowledgeOrderService service) {
        this.service = service;
    }

    private void initializeOrder() {
        final Restaurant r = RestaurantMother.makeRestaurant();
        doWithTransaction(new TxnCallback(){

            public void execute() throws Throwable {
                save(r);
            }});
        order = OrderMother.makeOrder(r);
        doWithTransaction(new TxnCallback(){

            public void execute() throws Throwable {
                order.noteSent("SomeMessageId", new Date());
                save(order);
            }});
        orderId = order.getId();
    }
    
    public void test() throws Exception {
        initializeOrder();
        
        logger.debug("---- getting order to acknowledge");
        AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
        assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());
        order = (Order) ar.getOrderValue();
        OrderLineItem lineItem = (OrderLineItem) order.getLineItems().get(0);
        assertEquals(RestaurantMother.SAMOSAS, lineItem.getMenuItem().getName());
        
        logger.debug("Original version number = " + order.getVersion());

        order.accept("My Notes");
        
        logger.debug("Original version number = " + order.getVersion());
        logger.debug("---- acknowledging order");

        ar = service.acknowledgeOrder(order);
        assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());
        
        logger.debug("final version number = " + ((Order)ar.getOrderValue()).getVersion());
        order = (Order) load(Order.class, orderId);
        assertEquals(Order.ACCEPTED, order.getState());
        assertEquals("My Notes", order.getNotes());
        logger.debug("db version number = " + order.getVersion());
    }

    public void testFailure() throws Exception {
        initializeOrder();
        
        AcknowledgeOrderResult ar = service.getOrderToAcknowledge(orderId);
        order = (Order) ar.getOrderValue();

        ar = service.getOrderToAcknowledge(orderId);
        Order order2 = (Order) ar.getOrderValue();

        order.accept("My Notes");
        
        ar = service.acknowledgeOrder(order);
        assertEquals(AcknowledgeOrderResult.OK, ar.getStatus());
        
        try {
            ar = service.acknowledgeOrder(order2);
            fail("expected failure");
        } catch (ObjectOptimisticLockingFailureException e) {
           assertEquals(new Integer(order.getId()), e.getIdentifier());
        }

    }

}