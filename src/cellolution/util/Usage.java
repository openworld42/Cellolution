
/**
 * Copyright 2023 Heinz Silberbauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cellolution.util;

import cellolution.*;

/**
 * Prints a usage of this program.
 */
public class Usage {

	/**
	 * Prints a usage of this program and exits.
	 * 
	 * @param exitCode
	 */
	public static void exit(int exitCode) {
		
		print(); 
		System.exit(exitCode);
	}

	/**
	 * Prints a usage of this program.
	 */
	public static void print() {
		
        System.out.println("\n" + Main.APP_NAME + " usage:");
        System.out.println("java package.Main -t TEST_NUMBER [-url CONNECTION_URL]");
        System.out.println("    -h          ... display this message and exit");
        System.out.println("    -v          ... diplay version and exit");
        System.out.println("    -q          ... quiet, no verbose messages");
        System.out.println("    -t          ... do TTT");
        System.out.println("    -url <url>  ... use XY");
        System.out.println("");
	}
}
