
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
import java.nio.file.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import cellolution.*;

/**
 * Serialization and deserialization of cellolution data using a JSON file.
 * Used to save the state of a cellolution run.
 */
public class JsonFile {

	// TODO use a file in the home directory 

	public static final String JSON_FILE_NAME = "Cellolution.json";

	// JSON file tokens/keys
	public static final String KEY_VERSION = "Version";
	public static final String KEY_SYSTEM = "System";
	public static final String KEY_ORGANISMS = "Organisms";
	public static final String KEY_OCEAN = "Ocean";
	
	private int identCount;

	/**
	 * Construction for reading a JSON file. 
	 */
	public JsonFile() {
		
	}

	/**
	 * Write a JSON file. 
	 * Due to unordered JSON handling and complicated solutions we do it light weight straight forward.
	 * 
	 * @param fileName		the name of the JSON file
	 * @throws IOException  if an I/O error happens
	 */
	public JsonFile(String fileName) throws IOException {

		FileWriter fileWriter = new FileWriter(fileName);
		identCount = 0;
		fileWriter.write("{\n");
		fileWriter.write("\"" + KEY_VERSION + "\":\"" + Version.getAsString() + "\"");
		JSONObject json = new JSONObject();
		
		// system data
		JSONObject system = new JSONObject();
		system.put(AppProperties.LOOK_AND_FEEL, Main.getProperty(AppProperties.LOOK_AND_FEEL));
		json.put(KEY_SYSTEM, system);
		write(fileWriter, json);
		
		// organism data
		JSONObject organisms = new JSONObject();
		organisms.put("cell1", 11);
		organisms.put("cell2", 12);
		json = new JSONObject();
		json.put(KEY_ORGANISMS, organisms);
		write(fileWriter, json);
		// ocean data
		JSONObject ocean = new JSONObject();
		ocean.put("cell1", 11);
		ocean.put("cell2", 12);
		json = new JSONObject();
		json.put(KEY_OCEAN, ocean);
		write(fileWriter, json);
		// complete and close JSON file
//		System.out.println(prettyJson(json.toJSONString()) + "\n\n");
		fileWriter.write("\n}\n");
		fileWriter.close();
	}

	/**
	 * @return an identation string
	 */
	private String indent() {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < identCount; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}
	
	/**
	 * JSON file tester.
	 * 
	 * @param arguments
	 */
//	public static void main(String[] arguments) {
//		
//		System.out.println("JSON writer");
//		try {
//			new JsonFile(JSON_FILE_NAME);
//			JsonFile file = new JsonFile();
//			file.readFrom(JSON_FILE_NAME);
//		} catch (Exception e) {
//            System.out.println("\n*****  Exception caught, exit: " + e);
//			e.printStackTrace();
//			System.exit(1);
//		}		
//	}

	/**
	 * Perform a pretty print of a JSON string.
	 *  
	 * @param jsonString	the string to be formatted
	 * @return the pretty formatted JSON string
	 */
	private String prettyJson(String jsonString) {

		int identCount = 0;
		StringTokenizer st = new StringTokenizer(jsonString, "{,}", true);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.equals("}")) {
				sb.append("\n");
				identCount--;
			}
			for (int i = 1; i < identCount; i++) {
				sb.append("  ");
			}
			sb.append(s);
			if (s.equals("{")) {
				sb.append("\n");
				identCount++;
			}
			if (s.equals(",")) {
				sb.append("\n");
			}
	     }
		return sb.toString();
	}

	/**
	 * Read in cellolution data of a former simulation run from a JSON file to restore an older state.
	 * 
	 * @param fileName		the name of the JSON file containing cellolution data
	 * @throws Exception  	if an I/O or parsing error happens
	 */
	public void readFrom(String fileName) throws Exception {

		String text = null;
		try {
			text = Files.readString(Path.of(fileName));
		} catch (NoSuchFileException e) {
			System.out.println("JSON parser: file '" + fileName + "' not found, using Cellolution defaults");
			return;
		}
		JSONParser parser = new JSONParser(); 
		JSONObject json = (JSONObject) parser.parse(text);
		System.out.println("Parser: reading existing Cellolution JSON file '" + fileName 
				+ "', version " + json.get(KEY_VERSION));
		JSONObject system = (JSONObject) json.get(KEY_SYSTEM);
		putProp(system, AppProperties.LOOK_AND_FEEL);
		
		Map m = (Map) json.get(KEY_SYSTEM);

		JSONObject organisms  = (JSONObject) json.get(KEY_ORGANISMS);

		
		JSONObject ocean  = (JSONObject) json.get(KEY_OCEAN);

	}
	
	/**
	 * Put a property read from JSON file to AppProperties.
	 * 
	 * @param jsonObj		a JSON objet read from JSON file
	 * @param key			the key of the property
	 */
	private void putProp(JSONObject jsonObj, String key) {

		String value = (String) jsonObj.get(key);
		if (value != null) {
			Main.getProperties().put(key, value);
		}
	}

	/**
	 * Write a JSON object to a file in an ordered way.
	 * 
	 * @param fileWriter
	 * @param json
	 * @throws IOException  if an I/O error happens
	 */
	private void write(FileWriter fileWriter, JSONObject json) throws IOException {
		
		String[] keys = (String[]) json.keySet().toArray(new String[] {});
		Arrays.sort(keys);
		boolean first = true;
		for (String key : keys) {
			Object value = json.get(key);
			if (!first) {
				fileWriter.write(",\n");
			}
			first = false;
			fileWriter.write(indent());
			if (value instanceof JSONObject) {
				fileWriter.write(",\n" + indent() + "\"" + key + "\":{\n");
				identCount++;
				write(fileWriter, (JSONObject) value);
				identCount--;
				fileWriter.write("\n" + indent() + "}");
			}
			else if (value instanceof String) {
				fileWriter.write("\"" + key + "\":");
				fileWriter.write("\"" + value + "\"");
			}
			else if (value instanceof Integer) {
				fileWriter.write("\"" + key + "\":");
				fileWriter.write("" + value);
			}
		}
	}
}
