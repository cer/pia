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
import java.util.*;

import javax.persistence.*;

@Entity(access = AccessType.FIELD)
@NamedQuery(name = "Restaurant.findAvailableRestaurants", queryString = "SELECT OBJECT(restaurant) "
		+ "FROM  Restaurant as restaurant, IN(restaurant.serviceArea) zip, "
		+ "IN(restaurant.timeRanges) tr "
		+ "WHERE zip.zipCode = :zipCode AND tr.dayOfWeek = :dayOfWeek "
		+ "AND (   (tr.openHour < :hour "
		+ "OR (tr.openHour = :hour AND tr.openMinute <= :minute)) "
		+ "AND (tr.closeHour > :hour "
		+ "OR (tr.closeHour = :hour AND tr.closeMinute > :minute) ))")
@Table(name = "FTGO_RESTAURANT")
public class Restaurant implements Serializable {

	@Id(generate = GeneratorType.AUTO)
	private int id;

	private String name;

	@ManyToMany
	@JoinTable(table = @Table(name = "RESTAURANT_ZIP_CODE"))
	private Set<ZipCode> serviceArea;

	private String notificationEmailAddress;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "RESTAURANT_ID")
	@org.hibernate.annotations.IndexColumn(name = "MENU_ITEM_INDEX")
	private List<MenuItem> menuItems;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "RESTAURANT_ID")
	private Set<TimeRange> timeRanges;

	public Restaurant() {

	}

	/*
	 * Creates a Restaurant
	 */

	public Restaurant(String name, Set<ZipCode> serviceArea,
			Set<TimeRange> timeRanges, List<MenuItem> menuItems) {
		this.name = name;
		this.serviceArea = serviceArea;
		this.timeRanges = timeRanges;
		this.menuItems = menuItems;
	}

	public String getName() {
		return name;
	}

	public String getNotificationEmailAddress() {
		return notificationEmailAddress;
	}

	public void setNotificationEmailAddress(String notificationEmailAddress) {
		this.notificationEmailAddress = notificationEmailAddress;
	}

	public Set<ZipCode> getServiceArea() {
		return serviceArea;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public String getRestaurantId() {
		return Integer.toString(id);
	}

	// end(methods)
	public boolean isInServiceArea(Address address, Date deliveryTime) {
		return isInServiceArea(address) && isOpenAtThisTime(deliveryTime);
	}

	private boolean isOpenAtThisTime(Date deliveryTime) {
		for (Iterator it = timeRanges.iterator(); it.hasNext();) {
			TimeRange range = (TimeRange) it.next();
			if (range.isOpenAtThisTime(deliveryTime))
				return true;
		}

		return false;
	}

	public boolean isInServiceArea(Address address) {
		for (Iterator it = serviceArea.iterator(); it.hasNext();) {
			ZipCode zipCode = (ZipCode) it.next();
			if (zipCode.getZipCode().equals(address.getZip()))
				return true;
		}
		return false;
	}

	public String getId() {
		return Integer.toString(id);
	}

}