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
 
package net.chrisrichardson.ormunit.hibernate;

import java.util.*;

import junit.framework.*;

import org.springframework.beans.factory.*;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;

public class LocalSessionFactoryBeanUtil {

    public static LocalSessionFactoryBean getLocalSessionFactoryBean(
            ApplicationContext applicationContext) {
        Map beans = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(
                        applicationContext,
                        LocalSessionFactoryBean.class);
        Assert.assertEquals("LocalSessionFactoryBean",
                1, beans.size());
        LocalSessionFactoryBean localSessionFactoryBean = (LocalSessionFactoryBean) beans
                .values().iterator().next();
        return localSessionFactoryBean;
    }

}
