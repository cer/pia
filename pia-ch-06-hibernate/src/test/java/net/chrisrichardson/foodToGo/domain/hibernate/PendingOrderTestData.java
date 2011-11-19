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
 
package net.chrisrichardson.foodToGo.domain.hibernate;

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;


public class PendingOrderTestData {

	public static PaymentInformation PAYMENT_INFORMATION =
		new PaymentInformation(
			"VISA",
			"John Doe",
			"55555-777-777",
			1,
			2004,
			new Address("1 somestreet", null, "AnyTown", "CA", "56789"),
            "foo@bar.com",
            "515551212");
    
	public static int[] getTEST_QUANTITIES() {
		return new int[] { 5, 5 };
	}
	public static int[] getZERO_QUANTITIES() {
		return new int[] { 0, 0 };
	}
}
