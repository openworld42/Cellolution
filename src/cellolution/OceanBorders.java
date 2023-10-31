
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

import java.util.*;

/**
 * The borders of the ocean: sides (rock), bottom of the sea (rock), surface (air).
 * The pixel coordinates is the first pixel that is Water.
 */
public class OceanBorders {

	private Ocean ocean;
	private int leftBorderCols[];
	private int rigthBorderCols[];
	private int bottomBorderRows[];

	/**
	 * Construct / compute the borders.
	 * 
	 * @param ocean 
	 */
	public OceanBorders(Ocean ocean) {
		
		this.ocean = ocean;
		computeBorders();
	}

	/**
	 * Compute the borders of the ocean. Surface is row zero from left to right.
	 * It also creates rock pixels on the left side, the right side and the bottom.
	 */
	public void computeBorders() {
		
		int cellColumns = Main.getCellColumns();
		int cellRows = Main.getCellRows();
		Pixel pixels[][] = ocean.getPixels();
		// left side
		leftBorderCols = new int[cellRows];
		for (int row = 0; row < cellRows; row++) {
			pixels[0][row] = new Rock(0, row);									// borders are always rock
			boolean found = false;
			for (int col = 1; col < cellColumns; col++) {
				if (pixels[col][row] instanceof Water) {
					leftBorderCols[row] = col;
					found = true;
					break;
				}
			}
			if (!found) {
				leftBorderCols = Arrays.copyOf(leftBorderCols, row);
				break;
			}
		}
		// right side
		rigthBorderCols = new int[cellRows];
		for (int row = 0; row < cellRows; row++) {
			pixels[cellColumns - 1][row] = new Rock(cellColumns - 1, row);		// borders are always rock
			boolean found = false;
			for (int col = cellColumns - 2; col >= 0; col--) {
				if (pixels[col][row] instanceof Water) {
					rigthBorderCols[row] = col;
					found = true;
					break;
				}
			}
			if (!found) {
				rigthBorderCols = Arrays.copyOf(rigthBorderCols, row);
				break;
			}
		}
		// bottom
		bottomBorderRows = new int[cellColumns];
		for (int col = 0; col < cellColumns; col++) {
			pixels[col][cellRows - 1] = new Rock(col, cellRows - 1);	// borders are always rock
			bottomBorderRows[col] = -1;				// in case there is only rock within this column
			for (int row = cellRows - 2; row >= 0; row--) {
				if (pixels[col][row] instanceof Water) {
					bottomBorderRows[col] = row;
					break;
				}
			}
		}
	}

	/**
	 * @return the bottomBorderRows
	 */
	public int[] getBottomBorderRows() {
		
		return bottomBorderRows;
	}
	
	/**
	 * @return the leftBorderCols
	 */
	public int[] getLeftBorderCols() {
		
		return leftBorderCols;
	}

	/**
	 * @return the rigthBorderCols
	 */
	public int[] getRigthBorderCols() {
		
		return rigthBorderCols;
	}
}
