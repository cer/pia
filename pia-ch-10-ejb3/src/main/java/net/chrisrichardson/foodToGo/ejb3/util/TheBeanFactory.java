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
 


package net.chrisrichardson.foodToGo.ejb3.util;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.access.*;
import org.springframework.context.*;
import org.springframework.context.access.*;

// FIXME Duplicated class

public class TheBeanFactory {

    public static Object getBean(String name, Class type) {
        return getBean(null, null, name, type);
    }

    public static Object getBean(String selector,
            String factoryKey, String name, Class type) {
        if (selector == null)
            selector = "configurations/default-spring-context.xml";
        if (factoryKey == null)
            factoryKey = "spring-context.xml";
        BeanFactoryReference reference = ContextSingletonBeanFactoryLocator
                .getInstance(
                        System.getProperty("spring.configuration",
                                selector)).useBeanFactory(
                        factoryKey);
        BeanFactory beanFactory = reference.getFactory();
        return beanFactory.getBean(name, type);
    }

    public static ApplicationContext getApplicationContext(String selector, String factoryKey) {
        if (selector == null)
            selector = "configurations/default-spring-context.xml";
        if (factoryKey == null)
            factoryKey = "spring-context.xml";
        BeanFactoryReference reference = ContextSingletonBeanFactoryLocator
                .getInstance(
                        System.getProperty("spring.configuration",
                                selector)).useBeanFactory(
                        factoryKey);
        BeanFactory beanFactory = reference.getFactory();
        return (ApplicationContext) beanFactory;
    }

}