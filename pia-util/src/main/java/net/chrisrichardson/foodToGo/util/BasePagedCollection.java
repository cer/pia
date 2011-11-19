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

import java.io.*;
import java.util.*;

public class BasePagedCollection implements Serializable {
    int totalSize;
    int pageSize;
    int startingIndex;
    Collection items;

    public BasePagedCollection(
        Collection items,
        int startingIndex,
        int totalSize,
        int pageSize) {
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.startingIndex = startingIndex;
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getStartingIndex() {
        return startingIndex;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalSize() {
        return totalSize;
    }

    public boolean isFirstPage() {
        return startingIndex == 1;
    }

    public boolean isLastPage() {
        return totalSize != -1 && startingIndex >= totalSize - pageSize;
    }

    public int getPreviousPageIndex() {
        return startingIndex - pageSize;
    }

    public int getNextPageIndex() {
        return startingIndex + pageSize;
    }

    public Iterator iterator() {
        return items.iterator();
    }

    public Collection getItems() {
        return items;
    }

}
