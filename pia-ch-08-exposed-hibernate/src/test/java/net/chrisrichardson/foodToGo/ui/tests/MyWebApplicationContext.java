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

import org.mortbay.jetty.servlet.*;
import org.mortbay.util.*;

/**
 * Enables the deployed of a web application without creating the correct
 * directory structure Specifically, it looks int the src/main/webapp directory
 * for JSP pages
 * 
 * @author cer
 * 
 */
public class MyWebApplicationContext extends WebApplicationContext {

	private Resource[] jspDirResource;
	private final String war;

	public MyWebApplicationContext(String contextRoot, String war) {
		super(war);
		this.war = war;
		setContextPath(contextRoot);
	}

	public void start() throws Exception {
		jspDirResource = new Resource[] { Resource.newResource(war) };
		super.start();
	}

	public Resource getResource(String resourceName) throws IOException {
		if (resourceName.endsWith(".jsp")) {
			for (int i = 0; i < jspDirResource.length; i++) {
				Resource resource = jspDirResource[i].addPath(resourceName
						.substring(1));
				if (resource.exists())
					return resource;

			}
		}
		return super.getResource(resourceName);
	}

}