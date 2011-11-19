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
 
package net.chrisrichardson.foodToGo.util.locking.ibatis;

import java.util.*;

import net.chrisrichardson.foodToGo.util.locking.*;

import org.apache.commons.logging.*;
import org.springframework.dao.*;
import org.springframework.orm.ibatis.*;

/**
 * iBATIS implementation of a lock manager
 * @author cer
 *
 */
public class LockManagerIBatisImpl implements LockManager {

	private Log logger = LogFactory.getLog(getClass());
	
    private final SqlMapClientTemplate template;

    private final PessimisticOfflineLockingMonitor monitor;

    public LockManagerIBatisImpl(
            SqlMapClientTemplate sqlMapClientTemplate) {
        this(sqlMapClientTemplate,
                new NullPessmisticOfflineLockingMonitor());
    }

    public LockManagerIBatisImpl(
            SqlMapClientTemplate sqlMapClientTemplate,
            PessimisticOfflineLockingMonitor monitor) {
        this.template = sqlMapClientTemplate;
        this.monitor = monitor;
    }

    private Map makeParameterMap(String classId, String pk,
            String owner) {
        Map map = new HashMap();
        map.put("classId", classId);
        map.put("pk", pk);
        map.put("owner", owner);
        return map;
    }

    public boolean acquireLock(String classId, String pk, String owner) {
        Map map = makeParameterMap(classId, pk, owner);
        try {
        	logger.debug("acquiring lock: " + map);
            template.insert("acquireLock", map);
            monitor.noteAcquired(classId, pk, owner);
            logger.debug("lock acquired");
            return true;
        } catch (DataIntegrityViolationException e) {
            logger.debug("lock NOT acquired");
            return false;

        }
    }

    public boolean verifyLock(String classId, String pk, String owner) {
        Map map = makeParameterMap(classId, pk, owner);
    	logger.debug("verifying lock: " + map);
        Object result = template.queryForObject("verifyLock", map);
        monitor.noteVerified(classId, pk, owner);
        return new Integer(1).equals(result);
    }

    public void releaseLock(String classId, String pk, String owner) {
        Map map = makeParameterMap(classId, pk, owner);
    	logger.debug("releasing lock: " + map);
        int count = template.delete("releaseLock", map);
        if (count != 1)
            throw new LockManagerException("Count should ==1 "
                    + count);
        monitor.noteReleased(classId, pk, owner);
    }

    public boolean isLocked(String classId, String pk) {
        Map map = new HashMap();
		map.put("classId", classId);
		map.put("pk", pk);
    	logger.debug("is lock : " + map);
        Object result = template.queryForObject("isLocked", map);
        return !new Integer(0).equals(result);
   }
}