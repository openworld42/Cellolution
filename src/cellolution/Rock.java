
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

import java.awt.*;
import java.util.*;

import org.json.*;

import cellolution.util.*;

/**
 * A solid rock pixel, forming the border of the ocean.
 */
public class Rock extends Pixel {

	public static int RGB_DEFAULT = Color.DARK_GRAY.getRGB();
	
	private int rgb;

	/**
	 * @param column
	 * @param row
	 * @param rgb		RGB color value of the rock
	 */
	public Rock(int column, int row, int rgb) {

		super(column, row);
		this.rgb = rgb;
	}

	/**
	 * Intentionally does nothing, rock is not made to get copied.
	 * 
	 * @param water		the water pixel
	 */
	@Override
	public void copyFrom(Water water) {
	}
	
	/**
	 * @return the rgb
	 */
	public int getRgb() {
		
		return rgb;
	}

	/**
	 * @param rgb the rgb to set
	 */
	public void setRgb(int rgb) {
		
		this.rgb = rgb;
	}

	/**
	 * @param column
	 * @param row
	 */
	public Rock(int column, int row) {
		
		this(column, row, RGB_DEFAULT);
	}


	@Override
	public String toString() {
		
		return "Solid rock (" + column + "/" + row + ")";
	}

	/**
	 * Creates a JSONObject from this object.
	 * 
	 * @return the JSONObject containing data of this object
	 */
	public JSONObject toJSONObject() {
		
		JSONObject jsonObj = new JSONObject();
		JsonUtil.addColRowTo(jsonObj, column, row);
		JsonUtil.addColorRGBTo(jsonObj, new Color(rgb));
		return jsonObj;
	}
}
