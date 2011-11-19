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
 
package net.chrisrichardson.foodToGo.ejb3.client;

import java.util.*;

import javax.naming.*;

import net.chrisrichardson.foodToGo.ejb3.initializeDB.*;

/**
 * Quick way to initialize the HSQLDB DB embedded in server
 * @author cer
 *
 */

public class InitializeDB {
	public static void main(String[] args) throws Exception {
		Hashtable p = new Hashtable();
		p.put("java.naming.factory.initial",
				"org.jnp.interfaces.NamingContextFactory");
		p.put("java.naming.factory.url.pkgs",
				"org.jboss.naming:org.jnp.interfaces");
		p.put("java.naming.provider.url", "localhost");
		InitialContext ctx = new InitialContext(p);
		InitializeDBService cart = (InitializeDBService) ctx
				.lookup(InitializeDBService.class.getName());
		cart.initialize();
	}
}
