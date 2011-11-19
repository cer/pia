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
 
package net.chrisrichardson.foodToGo.ui.tests;

import java.io.*;

import junit.framework.TestCase;

import org.mortbay.http.*;
import org.mortbay.jetty.*;
import org.mortbay.jetty.servlet.*;
import org.mortbay.util.*;

import com.meterware.httpunit.*;

/**
 * Simple test for the exposed domain model UI code Starts up jetty, "submits"
 * the delivery information, stops jetty
 * 
 * @author cer
 * 
 */
public class HibernateExposedDomainModelTests extends TestCase {

	private Server server;

	public void setUp() throws Exception {

		server = new Server();

		SocketListener listener = new SocketListener(new InetAddrPort(7001));
		server.addListener(listener);

		// Ugly hack - since the path depends on how we run the tests :-(
		WebApplicationContext context = null;

		if (new File("pia-ch-08-exposed-hibernate").exists())
			context = new WebApplicationContext(
					"pia-ch-08-exposed-hibernate/src/main/webapp/");
		else
			context = new WebApplicationContext("src/main/webapp/");
		context.setContextPath("/example");
		server.addContext(context);
		server.start();

	}

	@Override
	protected void tearDown() throws Exception {
		server.stop();
		super.tearDown();
	}

	public void test() throws Exception {
		WebConversation wc = new WebConversation();
		WebResponse restaurants = wc
				.getResponse("http://localhost:7001/example/updateDeliveryInfo");
		assertTrue(restaurants.getText().indexOf("Ajanta") > -1);
	}
}
