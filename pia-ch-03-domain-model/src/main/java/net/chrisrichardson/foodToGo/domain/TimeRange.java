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

import java.util.*;

public class TimeRange {
	private int id = -1;

	private int dayOfWeek;

	private int openHour;

	private int openMinute;

	private int closeHour;

	private int closeMinute;

	public TimeRange() {
	}

	public TimeRange(int dayOfWeek, int openHour, int openMinute,
			int closeHour, int closeMinute) {
		this.dayOfWeek = dayOfWeek;
		this.openHour = openHour;
		this.openMinute = openMinute;
		this.closeHour = closeHour;
		this.closeMinute = closeMinute;
	}

	public boolean equals(Object x) {
		if (x == null)
			return false;
		if (!(x instanceof TimeRange))
			return false;

		TimeRange other = (TimeRange) x;

		return dayOfWeek == other.dayOfWeek && openHour == other.openHour
				&& openMinute == other.openMinute
				&& closeHour == other.closeHour
				&& closeMinute == other.closeMinute;
	}

	public int hashCode() {
		return dayOfWeek ^ openHour ^ openMinute ^ closeHour ^ closeMinute;
	}

	public int getCloseMinute() {
		return closeMinute;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public int getOpenHour() {
		return openHour;
	}

	public int getOpenMinute() {
		return openMinute;
	}

	public int getCloseHour() {
		return closeHour;
	}

	public boolean isOpenAtThisTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		boolean result = day == getDayOfWeek()
				&& (hour > openHour || (hour == openHour && minute <= openMinute))
				&& (hour < closeHour || (hour == closeHour && minute < closeMinute));
		return result;
	}
}