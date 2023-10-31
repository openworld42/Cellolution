
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
 * Keys used for both serialization of the data container (JSON) and handling the Data Properties.
 */
public interface Keys {
	
	// property and JSON keys for application (alphabetical ordering)
	
	String VERSION = 		"Version";					// for both application and simulation
	String VERSION_MAJOR = 	"Version.major";			// for both application and simulation
	String VERSION_MINOR = 	"Version.minor";			// for both application and simulation
	String VERSION_RELEASE = "Version.release";			// for both application and simulation
	
	String LOOK_AND_FEEL = 	"LookAndFeel"; 
	String SYSTEM = 		"System"; 
	String UI = 			"UI"; 
	String VERBOSE = 		"Verbose"; 
	
	// property and JSON keys for the simulation data (ocean, smokers, organisms, etc) - alphabetical ordering
	
	String COLOR = 			"Color";
	String COLUMN = 		"Column";
	String OCEAN = 			"Ocean";
	String ROW = 			"Row";
	String SIM_VERSION_MAJOR = 		"Sim." + VERSION_MAJOR;		// property key only
	String SIM_VERSION_MINOR = 		"Sim." + VERSION_MINOR;		// property key only
	String SIM_VERSION_RELEASE = 	"Sim." + VERSION_RELEASE;	// property key only
	String SMOKER = 		"Smoker.";					// the consecutive number of a smoker will be added to this key
	String SMOKERS = 		"Smokers";
	String SMOKER_COUNT = 	"SmokerCount";
}
