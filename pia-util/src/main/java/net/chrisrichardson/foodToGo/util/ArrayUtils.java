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
 
package net.chrisrichardson.foodToGo.util;

import java.util.*;

public class ArrayUtils {

    public static String[] concatenate(
            String[] facadeFiles,
            String[] hibernate_domain_context) {
        String[] result = new String[facadeFiles.length
                + hibernate_domain_context.length];
        System.arraycopy(facadeFiles, 0, result, 0,
                facadeFiles.length);
        System.arraycopy(hibernate_domain_context, 0,
                result, facadeFiles.length,
                hibernate_domain_context.length);
        return result;
    }

    public static String[] trim(String[] strings) {
        if (strings == null)
            return null;
        String[] result = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            result[i] = s.trim();
        }
        return result;
    }

    public static Map trim(Map fetchGroupConfig) {
        Map result = new HashMap();
        for (Iterator it = fetchGroupConfig.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Object value = entry.getValue();
            if (value instanceof String)
                value = ((String) value).trim();
            else if (value != null
                    && value.getClass().isArray()
                    && value.getClass()
                            .getComponentType() == String.class)
                value = trim((String[]) value);
            else if (value instanceof List)
                value = trim((List)value);
            result.put(entry.getKey(), value);
        }
        return result;
    }

    private static List trim(List list) {
        List result = new ArrayList();
        for (Iterator it = list.iterator(); it
                .hasNext();) {
            String s = (String) it.next();
            result.add(s.trim());
        }
        return result;
    }

}
