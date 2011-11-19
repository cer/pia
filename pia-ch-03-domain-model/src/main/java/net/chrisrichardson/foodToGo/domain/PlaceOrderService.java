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

import net.chrisrichardson.foodToGo.creditCardProcessing.*;
import net.chrisrichardson.foodToGo.util.*;

public interface PlaceOrderService {

	public PlaceOrderServiceResult updateDeliveryInfo(String pendingOrderId,
			Address deliveryAddress, Date deliveryTime);

	public PlaceOrderServiceResult updateRestaurant(String pendingOrderId,
			String restaurantId);

	public PlaceOrderServiceResult updateQuantities(String pendingOrderId,
			int[] quantities);

	public PlaceOrderServiceResult checkout(String pendingOrderId);

	public PlaceOrderServiceResult updatePaymentInformation(
			String pendingOrderId, PaymentInformation paymentInformation,
			String couponCode);

	public PlaceOrderServiceResult placeOrder(String pendingOrderId);

}