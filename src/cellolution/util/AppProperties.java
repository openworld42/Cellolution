
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
 * It is the application responsibility to call 
 * 
 * Note: Do not forget to handle additional properties if a new release is going to be rolled out.
 */
public class AppProperties extends Properties {

	private static final long serialVersionUID = 1L;		// for the compiler
	
	// property keys
	
	public static final String VERSION = "version";
	public static final String VERSION_MAJOR = "version.major";
	public static final String VERSION_MINOR = "version.minor";
	public static final String VERSION_RELEASE = "version.release";
	
	public static final String LOOK_AND_FEEL = "lookandfeel"; 
	public static final String VERBOSE = "verbose"; 
	
	
	public static final String KEY1 = "key1";				// TODO: delete  
	public static final String KEY_INT = "keyInt";			// TODO: delete, just an integer example  
	
	private String pathname;
	private boolean useXmlFile;

	/**
	 * Construction from file or it takes the default values.
	 * The default values are written to the file (and may be overwritten later).
	 * 
	 * @param pathname			the path to the properties file
	 * @param useXmlFile		if true, use loadFromXML(), key/value pairs using load() otherwise
	 * @throws Exception
	 */
	public AppProperties(String pathname, boolean useXmlFile) throws Exception {

		super(); 
		this.pathname = pathname;
		this.useXmlFile = useXmlFile;
		File file = new File(pathname);
		if (file.exists()) {
			if (useXmlFile) {
				loadFromXML(new FileInputStream(file));
				
			} else {
				load(new FileInputStream(file));
			}
			releaseCheck();
		} else {
			createDefault();
			if (useXmlFile) {
				storeToXML();
			} else {

			}
		}
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
	 * @return the pathname
	 */
	public String getPathname() {
	
		return pathname;
	}

	/**
	 * Gets an boolean property (a flag).
	 * 
	 * @param key
	 * @return true id the value is "true", false otherwise
	 */
	public boolean getPropertyBool(String key) {
		
		return Boolean.parseBoolean(getProperty(key));
	}

	/**
	 * Gets an integer property.
	 * 
	 * @param key
	 * @return the integer value
	 */
	public int getPropertyInt(String key) {
		
		return Integer.parseInt(getProperty(key));
	}

	/**
	 * Check if all properties for this release are present, the user may use an older release.
	 */
	protected void releaseCheck() {
		
		getProperty(KEY1, "default1");					// TODO 
		getProperty(KEY_INT, "42");						// TODO
	}

	/**
	 * Sets an integer property.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, int value) {
		
		setProperty(key, "" + value);
	}

	/**
	 * Store the properties to the key/value properties file, to persist them.
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void store() throws FileNotFoundException, IOException {
		
		File file = new File(pathname);
		store(new FileOutputStream(file), "Version " + Version.getAsString());
	}

	/**
	 * Store the properties to the XML properties file, to persist them.
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void storeToXML() throws FileNotFoundException, IOException {
		
		File file = new File(pathname);
		storeToXML(new FileOutputStream(file), "Version " + Version.getAsString());
	}

//	/**
//	 * Tester, to be deleted.
//	 * 
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
//		try {
//			new AppProperties("myconfig.xml");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}
}
