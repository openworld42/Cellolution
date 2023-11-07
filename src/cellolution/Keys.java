
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
	
	/** key for version, for both application and simulation */
	String VERSION = 		"Version";
	/** key for version, for both application and simulation */
	String VERSION_MAJOR = 	"Version.major";
	/** key for version, for both application and simulation */
	String VERSION_MINOR = 	"Version.minor";
	/** key for version, for both application and simulation */
	String VERSION_RELEASE = "Version.release";
	
	/** key for the application JSON file */
	String LOOK_AND_FEEL = 	"LookAndFeel"; 
	/** key for the application JSON file */
	String RECENT_FILES = 	"RecentFiles";
	/** key for the application JSON file */
	String SYSTEM = 		"System"; 
	/** key for the application JSON file */
	String UI = 			"UI"; 
	/** key for the application JSON file */
	String VERBOSE = 		"Verbose"; 
	
	// property and JSON keys for the simulation data (ocean, smokers, organisms, etc) - alphabetical ordering
	
	/** key for the simulation JSON file */
	String AGILITY = 		"Agility";
	/** key for the simulation JSON file */
	String CaCO3 = 					"CaCO3";
	/** key for the simulation JSON file */
	String CaCO3_ADSORBTION_RATE = 	"CaCO3AdsorbtionRate";
	/** key for the simulation JSON file */
	String CaCO3_ADSORB_ENERGY = 	"CaCO3AdsorbEnergy";
	/** key for the simulation JSON file */
	String CELL = 			"Cell";
	/** key for the simulation JSON file */
	String CELLS = 			"Cells";
	/** key for the simulation JSON file */
	String CO2 = 			"CO2";
	/** key for the simulation JSON file */
	String CO2_ADSORBTION_RATE = 	"CO2AdsorbtionRate";
	/** key for the simulation JSON file */
	String CO2_ADSORB_ENERGY = 		"CO2AdsorbEnergy";
	/** key for the simulation JSON file */
	String COLOR = 			"Color";
	/** key for the simulation JSON file */
	String COLUMN = 		"Column";
	/** key for the simulation JSON file */
	String ENERGY = 		"Energy";
	/** key for the simulation JSON file */
	String ENERGY_CONSUMTION = 	"EnergyConsumption";
	/** key for the simulation JSON file */
	String DECOMPOSE_COUNT = 	"DecomposeCount";
	/** key for the simulation JSON file */
	String GENOME = 		"Genome";
	/** key for the simulation JSON file */
	String H2S = 			"H2S";
	/** key for the simulation JSON file */
	String H2S_ADSORBTION_RATE = 	"H2SAdsorbtionRate";
	/** key for the simulation JSON file */
	String H2S_ADSORB_ENERGY = 		"H2SAdsorbEnergy";
	/** key for the simulation JSON file */
	String H2S_TO_ENERGY = 	"H2SToEnergy";
	/** key for the simulation JSON file */
	String LAST_STATE = 	"LastState";
	/** key for the simulation JSON file */
	String MOVEABLE = 		"Movable";
	/** key for the simulation JSON file */
	String OCEAN = 			"Ocean";
	/** key for the simulation JSON file */
	String ORGANIC = 		"Organic";
	/** key for the simulation JSON file */
	String ORGANIC_ADSORBTION_RATE = "OrganicAdsorbtionRate";
	/** key for the simulation JSON file */
	String ORGANIC_ADSORB_ENERGY = 	"OrganicAdsorbEnergy";
	/** key for the simulation JSON file */
	String ORGANIC_MATTER_RESERVOIR = 	"OrganicMatterReservoir";
	/** key for the simulation JSON file */
	String ORGANISMS = 		"Organisms";
	/** key for the simulation JSON file */
	String ORGANISM_STATE = "OrganismState";
	/** key for the simulation JSON file */
	String ROW = 			"Row";
	/** key for the simulation, property key only */
	String SIM_VERSION_MAJOR = 		"Sim." + VERSION_MAJOR;
	/** key for the simulation, property key only */
	String SIM_VERSION_MINOR = 		"Sim." + VERSION_MINOR;
	/** key for the simulation, property key only */
	String SIM_VERSION_RELEASE = 	"Sim." + VERSION_RELEASE;
	/** key for the simulation JSON file */
	String SMOKERS = 		"Smokers";
	/** key for the simulation JSON file */
	String SMOKER_COUNT = 	"SmokerCount";
	/** key for the simulation JSON file */
	String SUN_BEAM_INCREMENT = 	"SunbeamIncrement";
	/** key for the simulation JSON file */
	String WEIGHT = 		"Weight";
}
