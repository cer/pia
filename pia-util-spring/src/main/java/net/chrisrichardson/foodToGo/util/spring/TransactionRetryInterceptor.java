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
 
package net.chrisrichardson.foodToGo.util.spring;

import org.aopalliance.intercept.*;
import org.springframework.aop.framework.*;
import org.springframework.dao.*;

/**
 * Automatically retries a transaction if a ConcurrencyFailureException is thrown 
 * @author cer
 *
 */
public class TransactionRetryInterceptor implements MethodInterceptor {

	protected int maxRetryCount = 3;

	/**
	 * Specifies how many time to retry, defaults to three
	 * @param maxRetryCount
	 */
	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		int retryCount = 0;
		while (true)
			try {
				ReflectiveMethodInvocation inv = (ReflectiveMethodInvocation) invocation;
				MethodInvocation anotherInvocation = inv.invocableClone();
				return anotherInvocation.proceed();
			} catch (ConcurrencyFailureException e) {
				if (retryCount++ > maxRetryCount)
					throw e;
				else {
					cleanupBeforeRetrying();
					continue;
				}
			}
	}

	protected void cleanupBeforeRetrying() {
	}
}