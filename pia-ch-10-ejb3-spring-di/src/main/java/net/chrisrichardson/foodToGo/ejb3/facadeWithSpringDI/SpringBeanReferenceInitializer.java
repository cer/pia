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
 
package net.chrisrichardson.foodToGo.ejb3.facadeWithSpringDI;

import java.util.*;

import javax.ejb.Local;
import javax.naming.*;

import net.chrisrichardson.foodToGo.ejb3.domain.*;
import net.chrisrichardson.foodToGo.ejb3.facade.*;
import net.chrisrichardson.foodToGo.ejb3.jndi.*;

import org.apache.commons.logging.*;
import org.jboss.annotation.ejb.*;

@Service
@Management(SpringBeanReferenceInitializerManagement.class)
@Local(SpringBeanReferenceInitializerLocal.class)
public class SpringBeanReferenceInitializer implements
		SpringBeanReferenceInitializerLocal,
		SpringBeanReferenceInitializerManagement {

	private Log logger = LogFactory.getLog(getClass());

	private Set<String> jndiNames = new HashSet<String>();

	public void create() throws Exception {
		logger.debug("Inside ServicePOJOImpl.create()");
		InitialContext ctx = new InitialContext();
		bind(ctx, "PlaceOrderService", PlaceOrderService.class,
				"PlaceOrderService");
		bind(ctx, "RestaurantRepository", RestaurantRepository.class,
				"RestaurantRepositoryImpl");
		bind(ctx, "PlaceOrderFacadeResultFactory",
				PlaceOrderFacadeResultFactory.class,
				"PlaceOrderFacadeResultFactoryImpl");

	}

	private void bind(InitialContext ctx, String jndiName, Class type,
			String beanName) throws NamingException {
		Reference reference = new Reference(type.getName(), new StringRefAddr(
				"beanName", beanName), SpringObjectFactory.class.getName(),
				null);
		ctx.bind(jndiName, reference);
		jndiNames.add(jndiName);
		logger.debug("Bound it: " + jndiName);
	}

	public void destroy() throws Exception {
		logger.debug("Inside ServicePOJOImpl.destroy()");
		InitialContext ctx = new InitialContext();
		for (String jndiName : jndiNames) {
			ctx.unbind(jndiName);
			logger.debug("unbind: " + jndiName);
		}
	}

}
