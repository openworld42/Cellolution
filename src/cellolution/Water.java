
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
package cellolution;

import java.awt.*;
import java.util.*;

/**
 * A pixel containing water in the ocean.
 * Water can dissolve matter in the range of 0 to 100 (for each material): salts (e.g. NaCl, lime), gases (O2, H2S).
 */
public class Water extends Pixel {

	public static int RGB_DEFAULT = new Color(147, 167, 187).getRGB();
	
	// due to memory savings: matter defintion as follows: indices and byte[]
	public static int CO2 = 0;							// carbon dioxide, for "plant breathing"
	public static int CaCO3 = 1;						// lime, to build hard matter
	public static int H2S = 2;							// hydrogen sulfide, as energy in the deep
	public static int ORGANIC = 3;						// organic matter, to build cells and cell parts
	public static int SUBSTANCES_SIZE = ORGANIC + 1;

	private byte[] substances = new byte[SUBSTANCES_SIZE];

	private short sunIntensity;

	/**
	 * @param column
	 * @param row
	 */
	public Water(int column, int row) {

		super(column, row);
	}

	/**
	 * Add the computed substance differences for this water pixel and clear the 
	 * differences for the next difference computation.
	 * 
	 * @param difference		the computed difference for this water pixel
	 */
	public void addAndZeroDifference(Water difference) {
		
		byte[] diffSubstances = difference.substances;
		for (int i = 0; i < substances.length; i++) {
			substances[i] += diffSubstances[i];
			diffSubstances[i] = 0;				// clear it for the next difference computation
		}
	}

	/**
	 * Copy the properties of another water pixel into this pixel.
	 * 
	 * @param water		the water pixel
	 */
	public void copyFrom(Water water) {
		
		column = water.column;
		row = water.row;
//		sunIntensity = water.sunIntensity;
		substances[CO2] = water.substances[CO2];
		substances[CaCO3] = water.substances[CaCO3];
		substances[H2S] = water.substances[H2S];
		substances[ORGANIC] = water.substances[ORGANIC];
	}

	/**
	 * @return the substances
	 */
	public byte[] getSubstances() {
		
		return substances;
	}

	/**
	 * @return the sunIntensity
	 */
	public short getSunIntensity() {
		
		return sunIntensity;
	}

	/**
	 * Initialize the values of dissolved substances.
	 */
	public void initMatterValues() {
		
		int depth = Main.getCellRows();
		byte deeperMoreVal = (byte) (80 * row / depth);
		byte deeperLessVal = (byte) (100 - deeperMoreVal);
		substances[CO2] = deeperLessVal;
		substances[CaCO3] = (byte) (deeperMoreVal / 3 + 23);
		substances[H2S] = (byte) (10 + deeperMoreVal / 2 + 1);
		substances[ORGANIC] = (byte) 20;
	}

	/**
	 * Returns the amount of a specified matter dissolved within this water pixel (e.g. matterValue(Water.CaCO3);).
	 * 
	 * @param matterIndexConstant		one of the defined constants
	 */
	public int matterValue(int matterIndexConstant) {
		
		return substances[matterIndexConstant];
	}

	/**
	 * @return the RGB value 
	 */
	public int getSunshineRGB() {
		
		if (sunIntensity == 0) {
			return RGB_DEFAULT;
		}
		int value = sunIntensity * 100 / Sunshine.MAX_INTENSITY;	// 250: green/blue value (yellow), 2000: greater than maximum intensity
		return new Color(value + 150, value + 130, 10).getRGB();
	}

	/**
	 * Set a matter of this water pixel (e.g. setMatter(Water.CaCO3, 42);).
	 * 
	 * @param matterIndexConstant		one of the defined constants
	 * @param value						a value ranging from 0 to 100
	 */
	public void setMatterValue(int matterIndexConstant, byte value) {
		
		substances[matterIndexConstant] = value;
	}

	/**
	 * @param sunIntensity 			the amount of sunshine energy within this pixel
	 */
	public void setSunIntensity(int sunIntensity) {
		
		this.sunIntensity = (short) sunIntensity;
	}

	@Override
	public String toString() {
		
		return "Water (" + column + "/" + row + ")   [CO2=" + substances[CO2] 
				+ ", CaCO3=" + substances[CaCO3] 
				+ ", H2S=" + substances[H2S] 
				+ ", Organic=" + substances[ORGANIC] 
				+ ", sunIntensity=" + sunIntensity + "]";
	}

	/**
	 * Set all substances to zero.
	 */
	public void zeroSubstances() {

		for (int i = 0; i < substances.length; i++) {
			substances[i] = 0;
		}
	}
}
