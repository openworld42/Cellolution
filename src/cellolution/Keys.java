
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
	String RECENT_FILES = 	"RecentFiles";
	String SYSTEM = 		"System"; 
	String UI = 			"UI"; 
	String VERBOSE = 		"Verbose"; 
	
	// property and JSON keys for the simulation data (ocean, smokers, organisms, etc) - alphabetical ordering
	
	String AGILITY = 		"Agility";
	String CaCO3 = 					"CaCO3";
	String CaCO3_ADSORBTION_RATE = 	"CaCO3AdsorbtionRate";
	String CaCO3_ADSORB_ENERGY = 	"CaCO3AdsorbEnergy";
	String CELL = 			"Cell";
	String CELLS = 			"Cells";
	String CO2 = 			"CO2";
	String CO2_ADSORBTION_RATE = 	"CO2AdsorbtionRate";
	String CO2_ADSORB_ENERGY = 		"CO2AdsorbEnergy";
	String COLOR = 			"Color";
	String COLUMN = 		"Column";
	String ENERGY = 		"Energy";
	String ENERGY_CONSUMTION = 	"EnergyConsumption";
	String DECOMPOSE_COUNT = 	"DecomposeCount";
	String GENOME = 		"Genome";
	String H2S = 			"H2S";
	String H2S_ADSORBTION_RATE = 	"H2SAdsorbtionRate";
	String H2S_ADSORB_ENERGY = 		"H2SAdsorbEnergy";
	String H2S_TO_ENERGY = 	"H2SToEnergy";
	String LAST_STATE = 	"LastState";
	String MOVEABLE = 		"Movable";
	String OCEAN = 			"Ocean";
	String ORGANIC = 		"Organic";
	String ORGANIC_ADSORBTION_RATE = "OrganicAdsorbtionRate";
	String ORGANIC_ADSORB_ENERGY = 	"OrganicAdsorbEnergy";
	String ORGANIC_MATTER_RESERVOIR = 	"OrganicMatterReservoir";
	String ORGANISMS = 		"Organisms";
	String ORGANISM_STATE = "OrganismState";
	String ROW = 			"Row";
	String SIM_VERSION_MAJOR = 		"Sim." + VERSION_MAJOR;		// property key only
	String SIM_VERSION_MINOR = 		"Sim." + VERSION_MINOR;		// property key only
	String SIM_VERSION_RELEASE = 	"Sim." + VERSION_RELEASE;	// property key only
	String SMOKERS = 		"Smokers";
	String SMOKER_COUNT = 	"SmokerCount";
	String SUN_BEAM_INCREMENT = 	"SunbeamIncrement";
	String WEIGHT = 		"Weight";
}
