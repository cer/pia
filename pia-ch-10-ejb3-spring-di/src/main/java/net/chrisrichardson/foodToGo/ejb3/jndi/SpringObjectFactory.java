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
 
package net.chrisrichardson.foodToGo.ejb3.jndi;

import java.util.*;

import javax.naming.*;
import javax.naming.spi.*;

import org.apache.commons.logging.*;

public class SpringObjectFactory implements ObjectFactory {

	private Log logger = LogFactory.getLog(getClass());

	public Object getObjectInstance(Object reference, Name name,
			Context nameCtx, Hashtable<?, ?> environment) throws Exception {
		String beanName = (String) ((Reference) reference).get(0).getContent();
		logger.debug("Someone is getting a bean: " + beanName);
		logger.debug("Using: " + "ejb3-spring-context.xml");
		try {
			return TheBeanFactory.getBean("ejb3-spring-context.xml",
					"spring-context.xml", beanName, Object.class);
		} catch (RuntimeException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

}
