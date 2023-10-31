
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

import java.awt.*;

import org.json.*;

import cellolution.*;

/**
 * Utility class, supporting the project with JSON handling.
 */
public class JsonUtil {
	
	/**
	 * Deny external construction.
	 */
	private JsonUtil() {
		
	}

	/**
	 * Adds a Color to a JSONObject.
	 * 
	 * @param jsonObj		the JSONObject
	 * @param rgb			the color as RGB value
	 */
	public static void addColorTo(JSONObject jsonObj, int rgb) {
		
		addColorRGBTo(jsonObj, new Color(rgb));
	}

	/**
	 * Adds a Color to a JSONObject.
	 * 
	 * @param jsonObj		the JSONObject
	 * @param color			the color
	 */
	public static void addColorRGBTo(JSONObject jsonObj, Color color) {
		
		JSONArray jsonArr = new JSONArray(new int[] {color.getRed(), color.getGreen(), color.getBlue()});
		jsonArr.setNewlineAfterElements(3);
		jsonObj.put(Keys.COLOR, jsonArr);
	}

	/**
	 * Adds a column and a row to a JSONObject.
	 * 
	 * @param jsonObj		the JSONObject
	 * @param column		the column
	 * @param row			the row
	 */
	public static void addColRowTo(JSONObject jsonObj, short column, short row) {
		
		jsonObj.put(Keys.COLUMN, column);
		jsonObj.put(Keys.ROW, row);
	}

	/**
	 * Create a RGB value from a <code>"Color": [80,70,70]<code> JSON entry.
	 * 
	 * @param jsonObject	the parent JSONObject containing the color
	 * @return the RGB value similar to a Color object using getRGB()
	 * @see java.awt.Color
	 */
	public static int toColorRGBFrom(JSONObject jsonObject) {
		
		JSONArray arr = jsonObject.getJSONArray(Keys.COLOR);
		return new Color(arr.getInt(0), arr.getInt(1), arr.getInt(1)).getRGB();
	}
}
