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

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;

@Stateless
public class EJB3RestaurantRepository implements
        RestaurantRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EJB3RestaurantRepository() {
        
    }
    
    public EJB3RestaurantRepository(
            EntityManager entityManager) {
        this.entityManager = entityManager;

    }

    public Restaurant findRestaurant(
            String restaurantId) {
        return entityManager.find(Restaurant.class,
                new Integer(restaurantId));
    }

    public List findAvailableRestaurants(
            Address deliveryAddress, Date deliveryTime) {
        Query query = entityManager
                .createNamedQuery("Restaurant.findAvailableRestaurants");
        Calendar c = Calendar.getInstance();
        c.setTime(deliveryTime);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String zipCode = deliveryAddress.getZip();

        query.setParameter("dayOfWeek", new Integer(
                dayOfWeek));
        query.setParameter("hour", new Integer(hour));
        query.setParameter("minute", new Integer(
                minute));
        query.setParameter("zipCode", new Integer(
                zipCode));
        return query.getResultList();

    }

    public boolean isRestaurantAvailable(
            Address deliveryAddress, Date deliveryDate) {
        return !findAvailableRestaurants(
                deliveryAddress, deliveryDate)
                .isEmpty();
    }

    public void setEntityManager(
            EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}