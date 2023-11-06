
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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.json.*;

/**
 * Cellolution serialization data container and handling.
 * Data serialization uses two different files: one for the Cellolution application itself and
 * another one for the ocean and evolution simulation, the latter may change if the user 
 * chose another simulation. 
 * The application will create new files if the don't exist using default values.
 * 
 * Note: Do not forget to handle additional data values if a new release is going to be rolled out.
 */
public class Data implements Keys {

	private static final String LOOK_AND_FEEL_DEFAULT = "Nimbus";
	
	/** a map containing application relevant data as key/value pairs */
	private final HashMap<String, Object> dataMap;
	/** a stack containing recently used files */
	private final Stack<String> recentFilesStack;
	/** a writer during writing, null otherwise */
	private Writer writer;
	/** a JSON representation of a simulation already stored in a file, if any */
	private JSONObject jsonObjSim;

	/**
	 * Construction with default values.
	 * The default values may be overwritten later by the contents of the JSON files.
	 */
	public Data() {

		super(); 
		JSONObject.setOrdered(true);						// prefer ordered JSON objects and files
		dataMap = new HashMap<>();
		recentFilesStack = new Stack<>();
		createAppDefaults();
	}

	/**
	 * Adds a file path to the recent files list (a Stack).
	 * if the file path is already stored, it is put to the top of the Stack.
	 * 
	 * @param path			the path to put to the recent files list
	 */
	public void addRecentFile(String path) {
		
		if (recentFilesStack.search(path) >= 0) {
			recentFilesStack.remove(path);
		}
		recentFilesStack.push(path);
	}

	/**
	 * Creates a version as JSON object and puts it in another JSON object.
	 * Uses the compiled version.
	 * 
	 * @param jsonObj				the parent JSON object to put the version JSON object
	 */
	private void addVersionToJSONObject(JSONObject jsonObj) {
		
		JSONObject jsonVersion = new JSONObject();
		jsonVersion.put(VERSION, Version.getAsString());
		jsonVersion.put(VERSION_MAJOR, Version.getMajor());
		jsonVersion.put(VERSION_MINOR, Version.getMinor());
		jsonVersion.put(VERSION_RELEASE, Version.getRelease());
		jsonObj.put(VERSION, jsonVersion);
	}

	/**
	 * Creates the default data for this application.
	 */
	protected void createAppDefaults() {
		
		dataMap.put(VERSION_MAJOR, Version.getMajor());
		dataMap.put(VERSION_MINOR, Version.getMinor());
		dataMap.put(VERSION_RELEASE, Version.getRelease());
		dataMap.put(LOOK_AND_FEEL, LOOK_AND_FEEL_DEFAULT);
//		dataMap.put(VERBOSE, Main.isVerbose());				// set by arguments, not saved
	}

	/**
	 * Gets a boolean data value (usually a flag).
	 * 
	 * @param key		the key of the associated value
	 * @return the boolean value
	 */
	public static boolean getBool(String key) {
		
		return ((Boolean) Main.getData().dataMap.get(key)).booleanValue();
	}

	/**
	 * Gets an integer data value.
	 * 
	 * @param key		the key of the associated value
	 * @return the integer value
	 */
	public static int getInt(String key) {
		
		return ((Integer) Main.getData().dataMap.get(key)).intValue();
	}

	/**
	 * @return the recentFilesStack
	 */
	public Stack<String> getRecentFilesStack() {
		
		return recentFilesStack;
	}

	/**
	 * Gets an old simulation object.
	 * 
	 * @return the old simulation object or null if none
	 */
	public JSONObject getSimObject() {
		
		return jsonObjSim;
	}
	
	/**
	 * Gets an String data value.
	 * 
	 * @param key		the key of the associated value
	 * @return the string
	 */
	public static String getString(String key) {
		
		return (String) Main.getData().dataMap.get(key);
	}
	
	/**
	 * Returns the current writer during writing, null otherwise.
	 * 
	 * @return the writer during writing, null otherwise
	 */
	public Writer getWriter() {
		
		return writer;
	}

	/**
	 * Read in the application specific data.
	 */
	public void readAppData() {

		// remember the filename for error messages during parsing
		Main.instance().setCurrentJsonFile(Main.APP_DATA_FILE_NAME);
		String text = null;
		try {
			text = Files.readString(Path.of(Main.APP_DATA_FILE_NAME));
		} catch (NoSuchFileException e) {
			String message = "JSON parser: error reading file '" + Main.APP_DATA_FILE_NAME 
					+ "', using Cellolution defaults.\n" + e.getMessage();
			writeAppData();
			return;
		} catch (IOException e) {
			String message = "JSON parser: error reading file '" + Main.APP_DATA_FILE_NAME 
					+ "', using Cellolution defaults.\n" + e.getMessage();
			Main.exceptionCaught(message, e);
			// just display the exception and go further
			return;
		}
		JSONObject jsonObjApp = null;
		try {
			jsonObjApp = new JSONObject(text);
			// ignore VERSION (the compilation sets it), start with UI
			JSONObject jsonUi = jsonObjApp.getJSONObject(UI);
			dataMap.put(LOOK_AND_FEEL, jsonUi.get(LOOK_AND_FEEL));
			// SYSTEM
			JSONObject jsonSys = jsonObjApp.getJSONObject(SYSTEM);
//			dataMap.put(VERBOSE, jsonSys.get(VERBOSE));			// set by arguments, not saved
			// recent files
			if (jsonSys.has(Keys.RECENT_FILES)) {
				JSONArray jsonFiles = jsonSys.getJSONArray(Keys.RECENT_FILES);
				for (int i = jsonFiles.length() - 1; i >= 0; i--) {
					String path = jsonFiles.getString(i);
					File file = new File(path);
					if (file.exists() && file.canRead()) {
						recentFilesStack.push(jsonFiles.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			String message = "JSON parser: error reading file '" + Main.APP_DATA_FILE_NAME 
					+ "', malformed JSON file? Using defaults.\n" + e.getMessage();
			Main.exceptionCaught(message, e);
			// just display the exception and go further
			return;
		}
		Main.instance().setCurrentJsonFile(null);
	}

	/**
	 * Reads a complete simulation from a file. 
	 * The resulting JSON simulation object is containing within this object
	 * 
	 * @param simDataFileName		the name of the file containing the simulation data
	 */
	public void readSimulationData(String simDataFileName) {
		
		// delete any current simulation traces before reading a new one
		removeSimulationData();
		// remember the filename for error messages during parsing
		Main.instance().setCurrentJsonFile(simDataFileName);
		String text = null;
		try {
			text = Files.readString(Path.of(simDataFileName));
		} catch (NoSuchFileException e) {
			String message = "JSON parser: no old simulation file '" + simDataFileName 
					+ "', starting a new simulation.\n" + e.getMessage();
			return;
		} catch (IOException e) {
			String message = "JSON parser: error reading file '" + Main.APP_DATA_FILE_NAME 
					+ "', starting a new simulation.\n" + e.getMessage();
			Main.exceptionCaught(message, e);
			// just display the exception and go further
			return;
		}
		jsonObjSim = new JSONObject(text);
		// store the sim version, to distinguish for old simulation files
		JSONObject jsonVersion = jsonObjSim.getJSONObject(VERSION);
		dataMap.put(SIM_VERSION_MAJOR, jsonVersion.get(VERSION_MAJOR));
		dataMap.put(SIM_VERSION_MINOR, jsonVersion.get(VERSION_MINOR));
		dataMap.put(SIM_VERSION_RELEASE, jsonVersion.get(VERSION_RELEASE));
	}

	/**
	 * Delete any current simulation traces.
	 * This may also be used to start a new simulation.
	 */
	public void removeSimulationData() {
		
		jsonObjSim = null;
		dataMap.remove(SIM_VERSION_MAJOR);
		dataMap.remove(SIM_VERSION_MINOR);
		dataMap.remove(SIM_VERSION_RELEASE);
	}

	/**
	 * Writes the application data file.
	 */
	private void writeAppData() {
		
		JSONObject jsonObjApp = new JSONObject();
		// version, use compiled Version instead of data
		addVersionToJSONObject(jsonObjApp);
		// UI
		JSONObject jsonUi = new JSONObject();
		jsonUi.put(LOOK_AND_FEEL, Data.getString(LOOK_AND_FEEL));
		jsonObjApp.put(UI, jsonUi);
		// system
		JSONObject jsonSys = new JSONObject();
//		jsonSys.put(VERBOSE, Data.getBool(VERBOSE));				// set by arguments, not saved
		jsonObjApp.put(SYSTEM, jsonSys);
		// recent files
		int count = recentFilesStack.size();
		if (count > 0) {
			JSONArray jsonFiles = new JSONArray();
			count = count > 12 ? 12 : count;
			for (int i = 0; i < count; i++) {
				jsonFiles.put(recentFilesStack.pop());
			}
			jsonSys.put(Keys.RECENT_FILES, jsonFiles);
		}
		// write it out
		writeToFile(Main.APP_DATA_FILE_NAME, jsonObjApp);
	}

	/**
	 * Saves application and simulation data on system exit.
	 * 
	 * @throws IOException on an IO error
	 */
	public void writeOnExit() throws IOException {
		
		writeAppData();
		writeSimulationData(Main.SIM_DATA_FILE_NAME);
	}

	/**
	 * Writes the simulation and the ocean data to a file.
	 * 
	 * @param simDataFileName		the name of the file
	 */
	public void writeSimulationData(String simDataFileName) {

		JSONObject jsonObjSim = new JSONObject();
		addVersionToJSONObject(jsonObjSim);
		jsonObjSim.put(OCEAN, Main.getOcean().toJSONObject());
		// write it out
		writeToFile(simDataFileName, jsonObjSim);
	}

	/**
	 * Creates or overwrites a file and writes a JSONObject to the file.
	 * Any exceptions caught are displayed and then ignored by intention.
	 * 
	 * @param fileName				the name of the file
	 * @param jsonObj 				the JSONObject to write
	 */
	private void writeToFile(String fileName, JSONObject jsonObj) {
		
		try {
			writer = new FileWriter(fileName);
			jsonObj.write(writer, 2, 0);
			writer.close();
		} catch (IOException e) {
			String message = "Cellolution: error writing file '" + fileName + "':\n" + e.getMessage();
			Main.exceptionCaught(message, e);
			// just display the exception and go further
		}
		writer = null;
	}
}
