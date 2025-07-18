/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.boottestrun.jvmargs;

import java.lang.management.ManagementFactory;

/**
 * Application used for testing {@code bootTestRun}'s JVM argument handling.
 *
 * @author Andy Wilkinson
 */
public class BootTestRunJvmArgsApplication {

	protected BootTestRunJvmArgsApplication() {

	}

	public static void main(String[] args) {
		int i = 1;
		for (String entry : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			System.out.println(i++ + ". " + entry);
		}
	}

}
