
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

import java.util.*;

import cellolution.util.*;

/**
 * Sunshine simulation: sun energy penetrating through the water.
 */
public class Sunshine {

	public static int MAX_INTENSITY = 1500;
	public static int MAX_SUNSHINE_PIXELS = 100;
	public static int BEAM_LENGTH = 5;

	private Ocean ocean;
	private HashSet<Water> sunshinePixels;
	private long lastTime;
	private int maxSunshinePixels;

	/**
	 * Construction.
	 * 
	 * @param ocean 
	 */
	public Sunshine(Ocean ocean) {
		
		sunshinePixels = new HashSet<>();
		this.ocean = ocean;
		lastTime = System.currentTimeMillis();

	}

	/**
	 * Display something looking like a beam or remove a beam.
	 * 
	 * @param sunIntensity
	 * @param col
	 * @param row
	 * @param colIncrease
	 * @param rowIncrease
	 * @param pixels 
	 */
	private void beam(int sunIntensity, int col, int row, int colIncrease, int rowIncrease, Pixel[][] pixels) {
		
		for (int i = 1; i <= BEAM_LENGTH; i++) {
			int nextCol = col - colIncrease * i;
			int nextRow = row - rowIncrease * i;
			if (nextCol < 0 || nextRow < 0) {
				break;
			}			
			Water waterpixel = (Water) pixels[nextCol][nextRow];	
			sunIntensity = i == BEAM_LENGTH - 1 ? 0 : sunIntensity * 80 / 100;
			waterpixel.setSunIntensity(sunIntensity);
			ocean.setPixelRGB(nextCol, nextRow, waterpixel.getSunshineRGB());
		}
	}

	/**
	 * Let the current sunshine beams glide deeper in the ocean.
	 */
	private void glideDeeper() {

		Pixel pixels[][] = ocean.getPixels();
		ArrayList<Water> newSunshinePixels = new ArrayList<Water>();
		ArrayList<Water> sunshinePixelsToRemove = new ArrayList<Water>();
		sunshinePixels.forEach(waterpixel -> {
			int col = waterpixel.getColumn();
			int row = waterpixel.getRow();
			int colIncrease = 1;								// beam angle
			int rowIncrease = 3;
			int nextCol = col + colIncrease;
			int nextRow = row + rowIncrease;
			Pixel nextPixel = pixels[nextCol][nextRow];			
			// if the next pixel is not a rock, the beam will get weaker and will vanish a short time later
			if (nextPixel instanceof Water) {
				Water nextWaterPixel = (Water) nextPixel;
				int sunIntensity = waterpixel.getSunIntensity();
				if (sunIntensity > 600) {
					// decrease the sunshine energy
					nextWaterPixel.setSunIntensity((sunIntensity * 996) / 1000);
					newSunshinePixels.add(nextWaterPixel);
					ocean.setPixelRGB(nextCol, nextRow, nextWaterPixel.getSunshineRGB());
					// display some beam simulation
					beam(sunIntensity, col, row, colIncrease, rowIncrease, pixels);
				} else {
					beam(0, nextCol, nextRow, colIncrease, rowIncrease, pixels);
				}
			} else if (nextPixel instanceof Rock) {
				beam(0, nextCol, nextRow, colIncrease, rowIncrease, pixels);
//				} else if (nextPixel instanceof Cell) {
				
				// TODO if we hit a cell, let it absorb the energy if "suneater"
				
			}
			sunshinePixelsToRemove.add(waterpixel);
			
		});
		sunshinePixels.addAll(newSunshinePixels);
		sunshinePixels.removeAll(sunshinePixelsToRemove);
	}

	/**
	 * Next sunshine step, sunshine beams are gliding through the water, new ones hit the surface.
	 * Lets the sun create some energy that shines through the water, gliding into the deep.
	 * 
	 * @param cellColumns 
	 * @param pixels 
	 * @param time 
	 */
	public void next(int cellColumns, Pixel[][] pixels, long time) {

		if (time - lastTime < 100) {
			return;					// next sunshine step: only from time to time
		}
		lastTime = time;
		// let the sunshine beams glide deeper
		glideDeeper();
		// add some new sunhsine beams at the surface
		maxSunshinePixels = sunshinePixels.size() > MAX_SUNSHINE_PIXELS ? MAX_SUNSHINE_PIXELS : maxSunshinePixels + 1;
		int beamCount = sunshinePixels.size() > maxSunshinePixels ? 0 : 2;
		for (int i = 0; i < beamCount; i++) {
			// this is called only if ther are less than MAX_SUNSHINE_PIXELS beams, it will create a new beam
			int intensity = MAX_INTENSITY * 2 / 3 + FastRandom.nextIntStat(MAX_INTENSITY / 3);
			int column = (FastRandom.nextIntStat(cellColumns) * 7877) % cellColumns;
			Pixel pixel = pixels[column][0];
			if (pixel instanceof Water) {
				Water waterPixel = (Water) pixel;
				waterPixel.setSunIntensity(intensity);
				sunshinePixels.add(waterPixel);
				ocean.setPixelRGB(waterPixel.getColumn(), waterPixel.getRow(), waterPixel.getSunshineRGB());
			}
		}
	}

	/**
	 * Removes the sunshine from a water pixel, usually cause it is adsorbed by a cell/organism.
	 * 
	 * @param water
	 */
	public void remove(Water water) {

		water.setSunIntensity(0);
		sunshinePixels.remove(water);
	}

//	@Override
//	public String toString() {
//		return " Sunshine [sunshinePixels=" + sunshinePixels.size() + "]";
//	}
//	
}
