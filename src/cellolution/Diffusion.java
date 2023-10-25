
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

import cellolution.cell.*;
import cellolution.util.*;

/**
 * Handle the diffusion of dissolved materials.
 */
public class Diffusion {

	public static final int NEIGHBOR_COUNT = 6;
	public static final int DIFFUSION_DIVIDER = (NEIGHBOR_COUNT +  1) * 2;	// half of the neighbor count plus the current cell

	private Ocean ocean;
	private int cellColumns;
	private int cellRows;
	private int step;						// the current ocean/diffusion step number
	private int rounding[];					// rounding probability if there are remainders of the diffusion division 
	private OceanBorders oceanBorders;
	private Pixel pixels[][];
	private Water diffCompPixels[][];		// an array of water pixels used as temporary storage during diffusion computation
	private OrganismMgr organismMgr;
	private boolean soluteOrganicMatter;	// true if there is diffusion of organic matter, false else 
	
	private boolean testFlag;				// set to true and uncomment test() for testing the diffusion 

	/**
	 * @param ocean
	 */
	public Diffusion(Ocean ocean) {
		
		this.ocean = ocean;
		cellColumns = Main.getCellColumns();
		cellRows = Main.getCellRows();
		oceanBorders = ocean.getOceanBorders();
		pixels = ocean.getPixels();
		rounding = new int[DIFFUSION_DIVIDER];
		diffCompPixels = new Water[cellColumns][cellRows];
		for (int col = 0; col < cellColumns; col++) {
			for (int row = 0; row < cellRows; row++) {
				diffCompPixels[col][row] = new Water(col, row);
			}
		}
	}

	/**
	 * Compute the diffusion of dissolved materials of a water pixel column. 
	 * Since this is done column by column, the result does not change the neighbor pixel containing water. 
	 * Difference values stored in a temporary array to be used later - like some other finite element methods.
	 * The dissolved materials of the borders have been set already.
	 * 
	 * @param col					the current column for the diffusion computation
	 * @param rowBottom				the last row (bottom row) of a water pixel in this column
	 */
	private void computeDiffusionCol(int col, int rowBottom) {

		boolean isEvenCol = (col & 1) == 0;				// even: column 0, 2, ...      odd:   column 1, 3, ...
		for (int row = 0; row <= rowBottom; row++) {
			Pixel p = pixels[col][row];
			if (!(p instanceof Water)) {
				continue;
			}
			computeDiffusionFor((Water) p, isEvenCol);
		}
	}

	/**
	 * Compute the diffusion for one Water pixel.
	 * For the neighbors surrounding order see the Pixel or Ocean comments.
	 * 
	 * @param water
	 * @param isEvenCol
	 */
	private void computeDiffusionFor(Water water, boolean isEvenCol) {
		
		int col = water.column;
		int row = water.row;
		int rowAbove = row - 1;
		int rowBelow = row + 1;
		int colAboveBelowLeft = isEvenCol ? col - 1 : col;
		int colAboveBelowRight = colAboveBelowLeft + 1;

//		test("Before ", col, row, rowAbove, rowBelow, colAboveBelowLeft, colAboveBelowRight);	// test the diffusion
		
		// neighbors above
		if (row > 0) {
			// neighbor above left, compute substance differences
			computeSubstances(water, colAboveBelowLeft, rowAbove);
			// neighbor above right
			computeSubstances(water, colAboveBelowRight, rowAbove);
		}
		// neighbor to the left
		computeSubstances(water, col - 1, row);
		// neighbor to the right
		computeSubstances(water, col + 1, row);
		// neighbor below left
		computeSubstances(water, colAboveBelowLeft, rowBelow);
		// neighbor below right
		computeSubstances(water, colAboveBelowRight, rowBelow);

//		test("After ", col, row, rowAbove, rowBelow, colAboveBelowLeft, colAboveBelowRight);	// test the diffusion 
		
		/*
		 * x.x.x.X.x.x   -> pixel row 0 (even, dot row 0 + 1)    x: 0 1 2 3 4   instead of 0 2 4 6 8
		 * .x.x.x.x.x.   -> pixel row 1 (odd,  dot row 2 + 3)    x: 0 1 2 3 4   instead of 1 3 5 7 9
		 * x.x.x.x.x.x   -> pixel row 2 (even, dot row 4 + 5)    x: 0 1 2 3 4   instead of 0 2 4 6 8
		 *  
		 * hexagon neighbour cells:		6 1       columns:	 odd row 	0 1      
		 *                             5 C 2                even row   0 1 2	 1 2
		 *                              4 3                   odd row   0 1    	0 1 2
		 *                              					even row	   		 1 2
		 */
	}

	/**
	 * Perform the computation of substances diffusion of a neighbor water pixel at the right side.
	 * The differences/deltas are saved for future computations of right side pixel columns.
	 * 
	 * @param water					the water pixel to be computed
	 * @param neighborCol			the column of the neighbor
	 * @param neighborRow			the row of the neighbor
	 */
	private void computeSubstances(Water water, int neighborCol, int neighborRow) {

		int difference, delta, remainder;
		Pixel pixel = pixels[neighborCol][neighborRow];
		if (pixel instanceof Water) {
			Water neighbor = (Water) pixel;
			byte neighborSubstances[] = neighbor.getSubstances();
			byte diffSubstances[] = diffCompPixels[water.column][water.row].getSubstances();
			byte waterSubstances[] = water.getSubstances();
			for (int i = 0; i < waterSubstances.length; i++) {
				difference = neighborSubstances[i] - waterSubstances[i];
				remainder = difference % DIFFUSION_DIVIDER;
				delta = difference / DIFFUSION_DIVIDER + (remainder >= 0 ? 
						rounding[remainder] : -rounding[-remainder]);		
				diffSubstances[i] += delta;		// update value
			}
		}
	}

	/**
	 * Perform one diffusion step for the whole ocean, according to the hexagon pixels ordering.
	 * 
	 * @param step
	 */
	public void nextOceanDiffusionStep(int step) {
		
		this.step = step;
		// diffusion from the water surface
		for (int col = 1; col < cellColumns; col++) {
			if (pixels[col][0] instanceof Water) {
				Water waterPixel = (Water) pixels[col][0];
				waterPixel.setMatterValue(Water.CO2, (byte) 100);
				// H2S diffusion into the atmosphere
				int h2s = waterPixel.matterValue(Water.H2S);
				if (h2s > 20) {
					h2s -= h2s / 20;
				}
				waterPixel.setMatterValue(Water.H2S, (byte) (h2s));			
			}
		}
		// diffusion from left wall
		int border[] = oceanBorders.getLeftBorderCols();
		for (int row = 0; row < border.length; row++) {
			Pixel pixel = pixels[border[row]][row];
			if (pixel instanceof Water) {
				solutionRock((Water) pixel);
			}
		}
		// if there is enough organic matter (in the reservoir), solute it from the walls and ground
		organismMgr = ocean.getOrganismMgr();
		soluteOrganicMatter = organismMgr.getOrganicMatterReservoir() > 5000 && step % 5 == 0;
		// diffusion from right wall
		border = oceanBorders.getRigthBorderCols();
		for (int row = 0; row < border.length; row++) {
			Pixel pixel = pixels[border[row]][row];
			if (pixel instanceof Water) {
				solutionRock((Water) pixel);
			}
		}
		// diffusion from bottom
		border = oceanBorders.getBottomBorderRows();
		for (int col = 0; col < border.length; col++) {
			int row = border[col];
			if (row <= 0) {
				continue;				// all pixels are rock in this row (row < 0) or no water above (row == 0)
			}
			Pixel pixel = pixels[col][row];
			if (pixel instanceof Water) {
				solutionRock((Water) pixel);
			}
		}
		// smokers emit some substances
		ocean.getSmokers().emitSubstances();
		// perform the diffusion of all water pixels, from left to the right
		border = oceanBorders.getBottomBorderRows();
		int col = 0;
		int rowBottom = 0;
		for ( ; col < cellColumns; col++) {
			rowBottom = border[col];
			if (rowBottom < 0) {
				continue;				// all pixels are rock within this column (rowBottom < 0)
			}
			break;
		}

//		if (step == 1) {
//			testArea();
//			System.out.println("\n ************ next step: " + step + "\n");
//		}
		
		updateRoundingByChance();
		// set the base of temporary diffusion storage to an even column to the left
		for (; col < cellColumns - 2; col++) {
			rowBottom = border[col];
			if (rowBottom < 0) {
				break;					// all pixels are rock in this column (rowBottom < 0), no more diffusion for this step
			}
			// change rounding by chance and compute this column
			computeDiffusionCol(col, rowBottom);
		}
		// add/subtract the computed differences of substances to the ocean water pixels and clear the differences
		for (col = 0; col < cellColumns; col++) {
			for (int row = 0; row < cellRows; row++) {
				Pixel pixel = pixels[col][row];
				if (pixel instanceof Water) {
					((Water) pixel).addAndZeroDifference(diffCompPixels[col][row]);
				}
			}
		}
	}

	/**
	 * Compute the amount of dissolved matter for a Water pixel touching a rock.
	 * 
	 * @param waterPixel
	 */
	private void solutionRock(Water waterPixel) {

		int value = waterPixel.matterValue(Water.CaCO3);
		if (value < 40) {
			waterPixel.setMatterValue(Water.CaCO3, (byte) (value + 1));			// dissolve a little lime of the rock
		} else if (value > 80) {
			waterPixel.setMatterValue(Water.CaCO3, (byte) (value - 1));			// deposit some lime
		}
		value = waterPixel.matterValue(Water.H2S);
		if (value < 20) {
			// dissolve some minimum H2S
			value += 20 / (value + 1);
			waterPixel.setMatterValue(Water.H2S, (byte) value);	
		}	
		if (soluteOrganicMatter) {
			// dissolve some collected organic matter from decomposed organisms
			waterPixel.increaseOrganicMatter();
			organismMgr.addToOrganicMatterReservoir(-1);
		}
	}

	/**
	 * Test a small diffusion area ((100/100) to (colRowMax/colRowMax)) during a real run.
	 * 
	 * @param text
	 * @param col
	 * @param row
	 * @param rowAbove
	 * @param rowBelow
	 * @param colAboveBelowLeft
	 * @param colAboveBelowRight
	 */
	private void test(String text, int col, int row, int rowAbove, int rowBelow, int colAboveBelowLeft, int colAboveBelowRight) {

		int colRowMax = 104;
		int colRowMiddle = (100 + colRowMax) / 2;
		if (!testFlag) {
			testArea();
			testFlag = true;
		}
		if (col < 100 || col > 103 || row < 100 || row > 103) {
			return;
		}
		System.out.print("\n\n" + text + ": col " + col + ", row " + row + "          ");
//		if (col == colRowMiddle && row == colRowMiddle) {			// water in the middle
		// display the values
		System.out.print("rowAbove " + rowAbove + ", rowBelow " + colAboveBelowLeft 
				+ ", colAboveBelowLeft " + colAboveBelowLeft + ", colAboveBelowRight " + colAboveBelowRight);
		for (int rowArea = 100; rowArea <= colRowMax; rowArea++) {
			System.out.print("\nrow " + rowArea + ": ");
			for (int colArea = 100; colArea <= colRowMax; colArea++) {
				System.out.print("   " + colArea + ": " + ((Water) pixels[colArea][rowArea]).matterValue(Water.H2S));
			}
		}
		System.out.print("\ndifferences: ");
		for (int rowArea = 100; rowArea <= colRowMax; rowArea++) {
			System.out.print("\nrow " + rowArea + ": ");
			for (int colArea = 100; colArea <= colRowMax; colArea++) {
				System.out.print("   " + colArea + ": " + (diffCompPixels[colArea][rowArea]).matterValue(Water.H2S));
			}
		}
		if (col == 100 && row == 102) {			// water in the above left (first touch)
			
			// set breakpoint below to test the water pixel
			
			int dummy = 42;
			
		}
		return;			// for a breakpoint
	}

	/**
	 * Creates a test area around 100/100 in the middle.
	 */
	private void testArea() {

		int diffusionComputeAreaSize = 8;

		int colRowMax = 104;
		int colRowMiddle = (100 + colRowMax) / 2;
		// clean the area and set the water pixel in the middle
		for (int colArea = 90; colArea <= colRowMax + 10; colArea++) {
			for (int rowArea = 90; rowArea <= colRowMax + 10; rowArea++) {
				((Water) pixels[colArea][rowArea]).zeroSubstances();
			}
		}
		for (int col2 = 0; col2 < diffusionComputeAreaSize; col2++) {
			for (int row2 = 0; row2 < cellRows; row2++) {
				diffCompPixels[col2][row2].zeroSubstances();
			}
		}
		((Water) pixels[colRowMiddle][colRowMiddle]).setMatterValue(Water.H2S, (byte) 80);
	}

	/**
	 * Changes the probability (by chance) for rounding up or down - depending on the 
	 * remainder - when dividing matter differences.
	 * Each remainder (0..5) has a rounding probability to get rounded.
	 */
	private void updateRoundingByChance() {

		rounding[0] = 0;			// no remainder, no rounding
		for (int i = 1; i < rounding.length; i++) {
			rounding[i] = (FastRandom.nextIntStat(rounding.length) + i) / rounding.length;
		}
	}
}
