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
 
package net.chrisrichardson.foodToGo.ejb3.initializeDB;

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;

import net.chrisrichardson.foodToGo.ejb3.domain.*;

@Stateless
@PersistenceContexts( { @PersistenceContext(name = "em2") })
public class InitializeDBServiceBean implements InitializeDBService {
	@PersistenceContext
	private EntityManager manager;

	private PendingOrder order;

	public static final int OPENING_HOUR = 18;

	public static final int OPENING_MINUTE = 12;

	public static final int CLOSING_MINUTE = 50;

	public static final int CLOSING_HOUR = 22;

	public static final int GOOD_HOUR = 19;

	public static final int BAD_HOUR = 12;

	public static final String SAMOSAS = "Samosas";

	public void initialize() {
		ZipCode zipCode = new ZipCode("94619");
		manager.persist(zipCode);

		Set<TimeRange> hours = new HashSet<TimeRange>();
		TimeRange tr;
		tr = new TimeRange(Calendar.TUESDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		tr = new TimeRange(Calendar.WEDNESDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		tr = new TimeRange(Calendar.THURSDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		tr = new TimeRange(Calendar.FRIDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		tr = new TimeRange(Calendar.SATURDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		tr = new TimeRange(Calendar.SUNDAY, OPENING_HOUR, OPENING_MINUTE,
				CLOSING_HOUR, CLOSING_MINUTE);
		hours.add(tr);
		List menuItems = new ArrayList();
		MenuItem mi1 = new MenuItem("Samosas", 5.00);
		MenuItem mi2 = new MenuItem("Chicken Tikka", 6.50);
		menuItems.add(mi1);
		menuItems.add(mi2);

		Set<ZipCode> serviceArea = new HashSet<ZipCode>();
		serviceArea.add(zipCode);

		Restaurant r = new Restaurant("Ajanta", serviceArea, hours, menuItems);

		manager.persist(r);

	}
}