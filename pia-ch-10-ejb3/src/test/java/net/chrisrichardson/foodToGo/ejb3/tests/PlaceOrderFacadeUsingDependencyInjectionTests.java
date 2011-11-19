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
 
package net.chrisrichardson.foodToGo.ejb3.tests;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.naming.*;

import junit.framework.*;
import net.chrisrichardson.foodToGo.ejb3.domain.*;
import net.chrisrichardson.foodToGo.ejb3.facade.*;
import net.chrisrichardson.foodToGo.ejb3.initializeDB.*;

import org.jboss.ejb3.embedded.*;

/**
 * Run test using JBoss Microcontainer
 * 
 * @author cer
 * 
 */
public class PlaceOrderFacadeUsingDependencyInjectionTests extends TestCase {

	private static InitialContext initialContext;

	private EJB3StandaloneDeployer deployer;

	public void setUp() throws Exception {
		EJB3StandaloneBootstrap.boot(null);
		deployer = new EJB3StandaloneDeployer();
		deployer.getDeployDirs().add(makeURLForDir("target/test-classes"));
		deployer.getDeployDirs().add(makeURLForDir("target/classes"));
		// This is simpler but it takes a lot longer
		// EJB3StandaloneBootstrap.scanClasspath()
		deployer.create();
		deployer.start();
		initialContext = new InitialContext();
	}

	private URL makeURLForDir(String dir) throws MalformedURLException,
			IOException {
		return new URL("file:///" + new File(dir).getCanonicalPath());
	}

	public void tearDown() throws Exception {
		deployer.stop();
		deployer.destroy();
		EJB3StandaloneBootstrap.shutdown();
	}

	public void testPlaceOrderFacadeUsingDependencyInjection() throws Exception {
		InitializeDBService initDBService = (InitializeDBService) initialContext
				.lookup(InitializeDBService.class.getName());
		initDBService.initialize();
		
		PlaceOrderFacadeUsingDependencyInjection facade = (PlaceOrderFacadeUsingDependencyInjection) initialContext
				.lookup(PlaceOrderFacadeUsingDependencyInjection.class
						.getName());
		Address address = new Address("1 somewhere", null, "Oakland", "CA",
				"94619");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 20);
		c.set(Calendar.MINUTE, 0);
		c.clear(Calendar.MILLISECOND);
		Date during = c.getTime();

		PlaceOrderFacadeResult result = facade.updateDeliveryInfo(null,
				address, during);
		assertEquals(PlaceOrderStatusCodes.OK, result.getStatusCode());
	}

}
