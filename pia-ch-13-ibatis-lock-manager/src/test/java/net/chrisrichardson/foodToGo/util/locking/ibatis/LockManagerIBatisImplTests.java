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

import net.chrisrichardson.foodToGo.util.locking.*;
import net.chrisrichardson.ormunit.ibatis.*;

import org.springframework.orm.ibatis.*;

public class LockManagerIBatisImplTests extends IBatisTests {

	private LockManager lockManager;

	String classId = "classId";

	String pk = "pk";

	String owner = "foo";

	String ownerOther = "bar";

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

	public void setSqlMapClientTemplate(
			SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	protected void onSetUp() throws Exception {
		super.onSetUp();
		jdbcTemplate.execute("DROP TABLE FTGO_LOCK IF EXISTS");
		jdbcTemplate
				.execute("CREATE TABLE FTGO_LOCK(CLASS_ID VARCHAR, PK VARCHAR, OWNER VARCHAR, PRIMARY KEY (CLASS_ID, PK))");
	}

	protected String[] getConfigLocations() {
		return new String[] { "lock-manager.xml",
				"define-hsqldb-datasource.xml",
				"lock-manager-ibatis-config.xml" };

	}

	public void testAcquire() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
	}

	public void testAcquireWhenLocked() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		assertFalse(lockManager.acquireLock(classId, pk, ownerOther));
	}

	public void testVerify() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		assertTrue(lockManager.verifyLock(classId, pk, owner));
	}

	public void testVerifyWhenLockedBySomeoneElse() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		assertFalse(lockManager.verifyLock(classId, pk, ownerOther));
	}

	public void testVerifyWhenNotLocked() throws Exception {
		assertFalse(lockManager.verifyLock(classId, pk, ownerOther));
	}

	public void testRelease() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		lockManager.releaseLock(classId, pk, owner);
	}

	public void testReleaseWhenLockedBySomeoneElse() throws Exception {
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		try {
			lockManager.releaseLock(classId, pk, ownerOther);
			fail("Expected ApplicationRuntimeException");
		} catch (LockManagerException e) {

		}
	}

	public void testReleaseWhenNotLocked() throws Exception {
		try {
			lockManager.releaseLock(classId, pk, ownerOther);
			fail("Expected ApplicationRuntimeException");
		} catch (LockManagerException e) {

		}
	}

	public void testIsLocked() throws Exception {
		assertFalse(lockManager.isLocked(classId, pk));
		assertTrue(lockManager.acquireLock(classId, pk, owner));
		assertTrue(lockManager.isLocked(classId, pk));
	}

}
