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
 
package net.chrisrichardson.foodToGo.ui.domain.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.chrisrichardson.foodToGo.domain.*;
import net.chrisrichardson.foodToGo.util.*;

/**
 * Handles the submission of the delivery information Currently, it uses
 * hardwired values - doesn't get them from the request
 * 
 * @author cer
 * 
 */
public class UpdateDeliveryInfoServlet extends BaseSpringServlet {

	private PlaceOrderService service;

	private RestaurantRepository restaurantRepository;

	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		service = (PlaceOrderService) getBean("PlaceOrderService",
				PlaceOrderService.class);
		restaurantRepository = (RestaurantRepository) getBean(
				"RestaurantRepositoryImpl", RestaurantRepository.class);
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (!validateParameters(request, response))
			return;

		Address deliveryAddress = makeDeliveryAddress(request);
		Date deliveryTime = makeDeliveryTime(request);

		HttpSession session = request.getSession();
		String pendingOrderId = (String) session.getAttribute("pendingOrderId");

		PlaceOrderServiceResult result = service.updateDeliveryInfo(
				pendingOrderId, deliveryAddress, deliveryTime);

		PendingOrder pendingOrder = result.getPendingOrder();

		session.setAttribute("pendingOrderId", pendingOrder.getId());

		switch (result.getStatusCode()) {
		case PlaceOrderStatusCodes.OK:
			displayAvailableRestaurants(request, response, pendingOrder);
			break;
		default:
			throw new RuntimeException("bad result: " + result.getStatusCode());
		}
	}

	private void displayAvailableRestaurants(HttpServletRequest request,
			HttpServletResponse response, PendingOrder pendingOrder)
			throws ServletException, IOException {
		request.setAttribute("restaurants", restaurantRepository
				.findAvailableRestaurants(pendingOrder.getDeliveryAddress(),
						pendingOrder.getDeliveryTime()));
		request.setAttribute("pendingOrder", pendingOrder);
		request.getRequestDispatcher("/availableRestaurants.jsp").forward(
				request, response);
	}

	private boolean validateParameters(HttpServletRequest request,
			HttpServletResponse response) {
		return true;
	}

	private Date makeDeliveryTime(HttpServletRequest request) {
		return RestaurantTestData.makeGoodDeliveryTime();
	}

	private Address makeDeliveryAddress(HttpServletRequest request) {
		return RestaurantTestData.getADDRESS1();
	}

}