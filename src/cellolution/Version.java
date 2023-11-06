
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
package cellolution;

/**
 * Version information of the project.
 * Yes, this could be searched in a more complicated way in a manifest file.
 */
public class Version {

	/** !!! TODO: CHANGE BEFORE A NEW RELEASE !!! */

	public static final int MAJOR = 1;
	public static final int MINOR = 1;
	public static final int RELEASE = 7; 

	/**
	 * No external construction.
	 */
	private Version() {

	}

	/**
	 * Returns the version numbers as <code>String</code> (eg "2.0.13").
	 *
	 * @return the version numbers as <code>String</code>
	 */
	public static String getAsString() {

		return MAJOR + "." + MINOR + "." +  RELEASE;
	}

	/**
	 * Returns the major version number (eg the 2 of "2.0.13").
	 *
	 * @return the major version number
	 */
	public static int getMajor() {

		return MAJOR;
	}

	/**
	 * Returns the minor version number (eg the 0 of "2.0.13").
	 *
	 * @return the minor version number
	 */
	public static int getMinor() {

		return MINOR;
	}

	/**
	 * Returns the release version number (eg the 13 of "2.0.13").
	 *
	 * @return the release version number
	 */
	public static int getRelease() {

		return RELEASE;
	}

	/**
	 * Prints a version message.
	 */
	public static void print() {
		
		System.out.println(Main.APP_NAME + " version " + getAsString());
	}
}
