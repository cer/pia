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


public class NotYetImplementedException extends RuntimeException {
    private Exception e;

    public NotYetImplementedException(Exception e) {
    	this.e = e;
    }

    public NotYetImplementedException() {
        super("Not Yet Implemented");
    }

    public NotYetImplementedException(String m) {
        super("Not Yet Implemented:" + m);
    }
	public void printStackTrace() {
		super.printStackTrace();
		if (e != null) {
			System.out.println("Nested exception -----");
			e.printStackTrace();
		}

	}

	public void printStackTrace(PrintStream arg0) {
		super.printStackTrace(arg0);
		if (e != null) {
			arg0.println("Nested exception -----");
			e.printStackTrace(arg0);
		}
	}

	public void printStackTrace(PrintWriter arg0) {
		super.printStackTrace(arg0);
		if (e != null) {
			arg0.println("Nested exception -----");
			e.printStackTrace(arg0);
		}
	}
}
