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

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.web.context.support.*;
   
/**
 * Simple base class for servlets that makes available a getBean() method
 * @author cer
 *
 */
public class BaseSpringServlet extends HttpServlet {

    private ServletConfig sc;

    public void init(ServletConfig sc) throws ServletException {
        super.init(sc);
        this.sc = sc;
    }

    /**
     * Gets the bean from the web application context
     * @param name
     * @param type
     * @return
     */
    protected Object getBean(String name, Class type) {
        return WebApplicationContextUtils
                .getRequiredWebApplicationContext(
                        sc.getServletContext()).getBean(name, type);
    }

}