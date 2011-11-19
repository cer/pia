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

import javax.naming.*;

import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.context.*;

public class SpringBeanToJndiExporter implements
        InitializingBean, DisposableBean,
        BeanFactoryAware, ApplicationContextAware {

    private String jndiName;

    private String beanName;

    private BeanFactory beanFactory;

    private ApplicationContext applicationContext;

    public void afterPropertiesSet() throws Exception {
        InitialContext ctx = new InitialContext();
        FactoryBean factoryBean = (FactoryBean) beanFactory
                .getBean(beanName + "&");
        Class type = beanFactory.getType(beanName);
        ctx.bind(jndiName, new ReferenceToSpringBean(
                type, beanName));
    }

    public void destroy() throws Exception {
    }

    public void setBeanFactory(BeanFactory beanFactory)
            throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setApplicationContext(
            ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

}
