
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

import cellolution.cell.*;
import cellolution.util.*;

/**
 * Black smokers are H2S and CaCO3 emitting hot springs within the ocean seabed.
 */
public class Smokers {

	public static Color SMOKER_COLOR = new Color(80, 70, 70);
	public static int SMOKER_COLOR_RGB = SMOKER_COLOR.getRGB();
	public static int SMOKER_BUBBLE_SIZE_MAX = 18;

	private Ocean ocean;
	private Pixel pixels[][];
	private int cellColumns;
	private int cellRows;
	private long lastTimeSmokedVisible;
	private ArrayList<Rock> smokers;
	private ArrayList<Rock> smokerRocks;
	private int[] smokerBubbleSize;
	private int emittedH2sEaterCount;

	/**
	 * Smoker creation.
	 * 
	 * @param ocean
	 */
	public Smokers(Ocean ocean, int smokerCount) {
		
		this.ocean = ocean;
		cellColumns = Main.getCellColumns();
		cellRows = Main.getCellRows();
		pixels = ocean.getPixels();
		smokers = new ArrayList<Rock>();
		smokerRocks = new ArrayList<Rock>();
		smokerBubbleSize = new int[smokerCount];
		int distance = cellColumns / (smokerCount + 1);
		for (int i = 0; i < smokerCount; i++) {
			createSmoker(i, distance);
			smokerBubbleSize[i] = -FastRandom.nextIntStat(10) - 1;		// negative to start at different times
		}
	}

	/**
	 * Create one smoker.
	 * 
	 * @param number
	 * @param distance
	 */
	private void createSmoker(int number, int distance) {

		int col = 30 + number * distance + distance / 2 + FastRandom.nextIntStat(distance / 4);
		// search for a good place for the smoker
		boolean found = false;
		for (int i = 0; i < 30 && !found; i++) {
			int rowFound = -1;
			int row = cellRows - 1;
			for ( ; row > 10; row--) {
				if (pixels[col][row] instanceof Water) {
					rowFound = row;
					break;
				}
			}
			if (rowFound < 0) {
				continue;
			}
			// good place?
			if (rowFound > 0 
					&& pixels[col - 1][rowFound - 7] instanceof Water
					&& pixels[col + 1][rowFound - 7] instanceof Water
					&& pixels[col][rowFound - 8] instanceof Water
					&& pixels[col][rowFound + 1] instanceof Rock
					&& pixels[col - 1][rowFound + 2] instanceof Rock
					&& pixels[col + 1][rowFound + 2] instanceof Rock) {
				rowFound -= 7;
				Rock rock = new Rock(col, rowFound, SMOKER_COLOR_RGB);
				smokers.add(rock);
				smokerRocks.add(rock);
				pixels[col][rowFound] = rock;
				int colLeftSide = (col & 1) == 0 ? col - 1 : col;
				row = rowFound + 1;
				int width = 2;
				for (int j = 0; j < 4; j++) {
					createSmokerRocks(colLeftSide - j, row++, width++);
					createSmokerRocks(col - j, row++, width++);
				}
				width -= 2;
				createSmokerRocks(colLeftSide - 4, row++, width);
				// avoid water below smokers
				colLeftSide -= 4;
				for (int j = 0; j < 8 && row < cellRows - 3; j++) {
					for (int k = 0; k < width; k++) {
						if (pixels[colLeftSide + k][row]  instanceof Water) {
							rock = new Rock(colLeftSide + k, row, SMOKER_COLOR_RGB);
							pixels[colLeftSide + k][row] = rock;
							smokerRocks.add(rock);
						}
					}
					row++;
				}
				break;
			}
			col++;
		}
	}

	/**
	 * Create a smoker rock row line.
	 * 
	 * @param col
	 * @param row
	 * @param count
	 */
	private void createSmokerRocks(int col, int row, int count) {
		
		for (int i = 0; i < count; i++) {
			Rock rock = new Rock(col + i, row, SMOKER_COLOR_RGB);
			pixels[col + i][row] = rock;
			smokerRocks.add(rock);
		}
	}

	/**
	 * Emits a SimpleH2sEaterCell which has been living inside the smoker.
	 * 
	 * @param index				the index of the smoker
	 */
	private void emitH2sEater(int index) {

		Rock smoker = smokers.get(index); 
		int row = smoker.row - 3 - FastRandom.nextIntStat(20);
		
//		if (emittedH2sEaterCount == 1) {
//			// for testing only, comment out otherwise
//			return;
//		}
		
		if (pixels[smoker.column][row] instanceof Water) {
			// for testing under "real" life conditions
			// H2S eaters have a lot of speed and energy when pushed out of a smoker
			SingleH2sEaterCell cell = SingleH2sEaterCell.create(smoker.column, row, 19000 + FastRandom.nextIntStat(9)* 3000);
			Organism organism = cell.getOrganism();
			organism.setSpeedAndDirection(5 + FastRandom.nextIntStat(30), -20 + FastRandom.nextIntStat(41));	// -20 to +20 degrees
			emittedH2sEaterCount++;
		}
	}

	/**
	 * Smoke: emit H2S, a little bit CaCO3, and sometimes sulfur-based living cells.
	 */
	public void emitSubstances() {

		for (int i = 0; i < smokers.size(); i++) {
			Rock smoker = smokers.get(i);
			for (int j = 1; j < 10; j++) {
				Water waterpixel = (Water) pixels[smoker.column][smoker.row - j];
				waterpixel.setMatterValue(Water.H2S, (byte) 90);
				waterpixel.setMatterValue(Water.CaCO3, (byte) 80);
				waterpixel = (Water) pixels[smoker.column - 1][smoker.row - j];
				waterpixel.setMatterValue(Water.H2S, (byte) 90);
				waterpixel.setMatterValue(Water.CaCO3, (byte) 80);
				waterpixel = (Water) pixels[smoker.column - 2][smoker.row - j];
				waterpixel.setMatterValue(Water.H2S, (byte) 90);
				waterpixel.setMatterValue(Water.CaCO3, (byte) 80);
				waterpixel = (Water) pixels[smoker.column + 1][smoker.row - j];
				waterpixel.setMatterValue(Water.H2S, (byte) 90);
				waterpixel.setMatterValue(Water.CaCO3, (byte) 80);
				waterpixel = (Water) pixels[smoker.column + 2][smoker.row - j];
				waterpixel.setMatterValue(Water.H2S, (byte) 90);
				waterpixel.setMatterValue(Water.CaCO3, (byte) 80);
			}
		}
	}

	/**
	 * @param g2d
	 */
	public void paint(Graphics2D g2d) {

		g2d.setColor(SMOKER_COLOR);
		smokerRocks.forEach(rock -> {
			g2d.fillRect(rock.column << 1, rock.row << 1, 3, 3);
		});
		for (int i = 0; i < smokers.size(); i++) {
			Rock smoker = smokers.get(i);
			int bubbleSize = smokerBubbleSize[i];
			if (bubbleSize > 0) {
				g2d.drawArc((smoker.column << 1) - bubbleSize / 2, (smoker.row << 1) - bubbleSize * 3 / 2, 
						bubbleSize, bubbleSize, 300, 360);
			}
		}
	}

	/**
	 * Visually smoking and pushing H2sEater out.
	 * 
	 * @param time
	 */
	public void smoke(long time) {

		if (time - lastTimeSmokedVisible > 100) {
			for (int i = 0; i < smokers.size(); i++) {
				smokerBubbleSize[i]++;
				if (smokerBubbleSize[i] > SMOKER_BUBBLE_SIZE_MAX) {
					smokerBubbleSize[i] = -FastRandom.nextIntStat(70);
				}
				if (FastRandom.nextIntStat(10) == 0 && smokerBubbleSize[i] > 5) {
					if (emittedH2sEaterCount < 20) {
						emitH2sEater(i);
					} else if (FastRandom.nextIntStat(10) == 0) {
						emitH2sEater(i);
					}
				}
			}
			lastTimeSmokedVisible = time;
		}
	}
}
