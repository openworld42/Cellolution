
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
package cellolution.cell;

import cellolution.*;
import cellolution.util.*;

/**
 * Movement computations for organisms within the ocean, mainly static methods.
 */
public class Mover {

	private static int BROWNIAN_SPEED_MAX = 5;

	/**
	 * Avoid external construction.
	 */
	private Mover() {
		
	}

	/**
	 * Ensures a degree range from 0 to 359 degrees.
	 * 
	 * @param directionDegrees
	 * @return
	 */
	public static int adjustDegrees(int directionDegrees) {
		
		directionDegrees = (directionDegrees + 360000) % 360;		// 0..359 degrees
		return directionDegrees;
	}

	/**
	 * Tests if the organism can move in the direction the organism points.
	 * 
	 * @param organism
	 * @param minColumn
	 * @param maxColumn
	 * @param minRow
	 * @param maxRow
	 * @return true if there is no rock in the direction, false otherwise
	 */
	public static boolean canMoveDueToRocks(Organism organism, int minColumn, int maxColumn, int minRow, int maxRow) {
		
		Pixel pixels[][] = Main.getOcean().getPixels();
		int direction = organism.getProperty(Organism.PROP_DIRECTION);
		// 180 .. 360 degrees, left side
		int col = minColumn - 1;
		if (direction >= 180) {			
			col = col < 0 ? 0 : col;
			for (int row = minRow; row <= maxRow; row++) {
				if (!(pixels[col][row] instanceof Water)) {
					return false;
				}
			}
		}
		// 0 .. 180 degrees, right side
		col = maxColumn + 1;
		if (direction <= 180) {			
			col = col < Main.getCellColumns() ? col : Main.getCellColumns();
			for (int row = minRow; row <= maxRow; row++) {
				if (!(pixels[col][row] instanceof Water)) {
					return false;
				}
			}
		}
		// 90 .. 270 degrees, bottom
		if (direction >= 90 && direction <= 270) {			
			int row = maxRow + 1;
			row = row < Main.getCellRows() ? row : Main.getCellRows();
			for (col = minColumn; col <= maxColumn; col++) {
				if (!(pixels[col][row] instanceof Water)) {
					return false;
				}
			}
		}
		// 270 .. 90 degrees, top, in some rare cases
		if (direction >= 270 || direction <= 90) {			
			int row = minRow - 1;
			if (row <= 0) {
				return false;
			}
			for (col = minColumn; col <= maxColumn; col++) {
				if (!(pixels[col][row] instanceof Water)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Move: adjust speed and direction of an organism.
	 * Some kind of Brownian movement and turning behavior is added.
	 * The speed of an organism is reduced by its resistance (growing by the square of speed) within the water.
	 * 
	 * @param organism
	 * @return the number of steps to take (number of cells to move). The speed of the 
	 * 		organism is already reduced and the direction adjusted
	 */
	public static int moveAndTurnWithBrownianMovement(Organism organism) {

		int cellCount = organism.getCellCount();
		if (cellCount > 2 && FastRandom.nextIntStat(cellCount) == 0) {
			// bigger cells have less brownian movement
			return 0;
		}
		int speed = organism.getProperty(Organism.PROP_SPEED);
		int direction = organism.getProperty(Organism.PROP_DIRECTION);
		int weight = organism.getProperty(Organism.PROP_WEIGHT);
		// speed an direction reduction: bigger organisms are less influenced by brownian movement
		int reduction = cellCount < 7 ? cellCount / 2 + 1 : cellCount / 3 + 2; 
		if (speed == 0 && FastRandom.nextIntStat(weight) > 10000) {
			// for simplicity in Cellolution: sometimes the weight will influence more than the brownian movement
			speed = 1;
			direction = 170 + FastRandom.nextIntStat(21);		// down
		} else {
			// brownian movement
			int brownianSpeed = FastRandom.nextIntStat(2) / (FastRandom.nextIntStat(reduction) + 1);
			if (brownianSpeed != 0 && brownianSpeed != 0) {
				if (speed < BROWNIAN_SPEED_MAX) {
					// slow, brownian movement dominates
					direction = FastRandom.nextIntStat(360);
					speed += brownianSpeed;
				} else {
					// browning speed is not important, but the direction may change
					int brownianDirection = -20 + FastRandom.nextIntStat(41);
					direction = adjustDegrees(direction + brownianDirection);
				}
				organism.setProperty(Organism.PROP_DIRECTION, direction);
			}
		}
		if (speed == 0) {
			return 0;
		}
		int stepSpeed = 1;
		speed--;
		if (speed > 50) {
			stepSpeed = 4;
			speed = speed / 4;
		} else if (speed > 20) {
			stepSpeed = 3;
			speed -= speed / 3;
		} else if (speed > 6) {
			stepSpeed = 2;
			speed -= speed / 2;
		} else if (speed > 1) {
			stepSpeed = 1;
			speed -= speed / 2;
		}
		organism.setProperty(Organism.PROP_SPEED, speed);
//		System.out.print("\nstepSpeed " + stepSpeed + ", speed " + speed + ", direction " + direction);
		return stepSpeed; 
	}

	/**
	 * Move the organism some steps in its direction.
	 * 
	 * @param organism
	 * @param stepsToGo
	 */
	public static void move(Organism organism, int stepsToGo) {
		
        int direction = organism.getProperty(Organism.PROP_DIRECTION);
		for (int step = 0; step < stepsToGo; step++) {
			organism.getCells().forEach(cell -> {
				cell.move(direction);
			});
		}
		
		// TODO steps in other directions ? eg 4 steps in 30 degree: 2 steps in 0 degree, 2 in 60 degrees 

	}

	/**
	 * Find the neighbor pixel of a pixel by its number (see Pixel or Ocean comment, 1 is top right).
	 * 
	 * @param pixel
	 * @param neighborNr
	 * @param pixels
	 * @return the neighbor pixel defined by the neighbor number [1..6]
	 */
	public static Pixel neighbor(Pixel pixel, int neighborNr, Pixel[][] pixels) {
		
		int col = pixel.getColumn();
		int row = pixel.getRow();
//		int rowAbove = row - 1;
//		int rowBelow = row + 1;
		int colAboveBelowLeft = (col & 1) == 0 ? col - 1 : col;		// (col & 1) == 0 ? even : odd
		int colAboveBelowRight = colAboveBelowLeft + 1;
		switch (neighborNr) {
		case 1: 
			// neighbor above right
			return pixels[colAboveBelowRight][row - 1];
		case 2: 
			// neighbor to the right
			return pixels[col + 1][row];
		case 3: 
			// neighbor below right
			return pixels[colAboveBelowRight][row + 1];
		case 4: 
			// neighbor below left
			return pixels[colAboveBelowLeft][row + 1];
		case 5: 
			// neighbor to the left
			return pixels[col - 1][row];
		case 6: 
			// neighbor above left
			return pixels[colAboveBelowLeft][row - 1];				// TODO:  Index -1 out of bounds for length 450
		default:
			throw new IllegalArgumentException("Unexpected value: " + neighborNr);
		}
	}
}
