
/**
 * Copyright 2020 Heinz Silberbauer
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

import java.io.*;
import java.util.*;

import cellolution.*;

/**
 * Application properties.
 * 
 * Note: Do not forget to handle additional properties if a new release is going to be rolled out.
 */
@SuppressWarnings("serial")
public class AppProperties extends Properties {

	// property keys
	
	public static final String VERSION = "Version";
	public static final String VERSION_MAJOR = "Version.major";
	public static final String VERSION_MINOR = "Version.minor";
	public static final String VERSION_RELEASE = "Version.release";
	
	public static final String LOOK_AND_FEEL = "LookAndFeel"; 
	public static final String VERBOSE = "Verbose"; 
	
	/**
	 * Construction with default values.
	 * The default values may be overwritten later by the contents of the JSON file.
	 */
	public AppProperties() {

		super(); 
		createDefault();
	}

	/**
	 * Creates the default properties.
	 * A subclass may overwrite this method.
	 */
	protected void createDefault() {
		
		setProperty(VERSION, Version.getAsString());
		setProperty(VERSION_MAJOR, Version.getMajor());
		setProperty(VERSION_MINOR, Version.getMinor());
		setProperty(VERSION_RELEASE, Version.getRelease());
		setProperty(LOOK_AND_FEEL, "Nimbus");
		setProperty(VERBOSE, "false");
	}

	/**
	 * Gets an boolean property (a flag).
	 * 
	 * @param key	the key of the property
	 * @return true id the value is "true", false otherwise
	 */
	public boolean getPropertyBool(String key) {
		
		return Boolean.parseBoolean(getProperty(key));
	}

	/**
	 * Gets an integer property.
	 * 
	 * @param key	the key of the property
	 * @return the integer value
	 */
	public int getPropertyInt(String key) {
		
		return Integer.parseInt(getProperty(key));
	}

	/**
	 * Sets an integer property.
	 * 
	 * @param key			the key of the property
	 * @param value			the value of the property
	 */
	public void setProperty(String key, int value) {
		
		setProperty(key, "" + value);
	}
}
