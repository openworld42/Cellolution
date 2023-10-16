
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

/**
 * A pixel of the ocean.
 * The shape of a pixel is a regular hexagon. Therefore, the distances within the structure are different from squares.
 * 
 * <pre>
 * If each "x" (dot size 2x2) is the center of a hexagon on a quad paper (sketched paper):     ..
 *                                                                                             ..
 *  Pixel cell, visual dots on screen are    ..	  
 *                                           .. 
 *                             
 * hexagon neighbour cells:		6 1       columns:	 odd row 	0 1      
 *                             5 C 2                even row   0 1 2	 1 2
 *                              4 3                   odd row   0 1    	0 1 2
 *                              					even row	   		 1 2
 *  
 *  reduced to Cellolution representation (each cell has 2*2 dots, each "x" is the center of a hexagon):
 *  
 *  x.x.x.X.x.x   -> pixel row 0 (even, dot row 0 + 1)    x: 0 1 2 3 4   instead of 0 2 4 6 8
 *  .x.x.x.x.x.   -> pixel row 1 (odd,  dot row 2 + 3)    x: 0 1 2 3 4   instead of 1 3 5 7 9
 *  x.x.x.x.x.x   -> pixel row 2 (even, dot row 4 + 5)    x: 0 1 2 3 4   instead of 0 2 4 6 8
 *  
 * Pixels (column/row):
 * row 0: dot row 0+1    | | | |      cells:   (0/0 to 1/1)   (2/0 to 3/1)   (4/0 to 5/1)
 * row 1: dot row 2+3     | | | |     cells:   (1/2 to 2/3)   (3/2 to 4/3)   (5/2 to 6/3)
 * row 2: dot row 4+5    | | | |      cells:   (0/4 to 1/5)   (2/4 to 3/5)   (4/4 to 5/5)
 *                      
 * </pre>
 */
public abstract class Pixel {

	protected short column;
	protected short row;

	/**
	 */
	protected Pixel() {

		this(-1, -1);
	}

	/**
	 * @param column
	 * @param row
	 */
	protected Pixel(int column, int row) {

		this.column = (short) column;
		this.row = (short) row;
	}

	/**
	 * Intentionally does nothing except this pixel is water.
	 * 
	 * @param water		the water pixel
	 */
	public abstract void copyFrom(Water water);

	/**
	 * @return the column
	 */
	public int getColumn() {
		
		return column;
	}

	/**
	 * @return the row
	 */
	public short getRow() {
		
		return row;
	}

	/**
	 * Move a pixel one step in a direction.
	 * Usually the pixel would be a cell.
	 * 
	 * @param direction
	 */
	public void move(int direction) {
		
		boolean isEvenCol = (column & 1) == 0;				// even: column 0, 2, ...      odd:   column 1, 3, ...
		int colAboveBelowLeft = isEvenCol ? column - 1 : column;
		int colAboveBelowRight = colAboveBelowLeft + 1;

//		System.out.print("\ndir " + direction + ", col " + column + ", row " + row);
		
		// compute row/column deltas for an even or odd column
		if (direction < 60) {			
			// 0 .. 60 degrees, right above
			column = (short) colAboveBelowRight;
			row--;
		} else if (direction < 120) {	
			// right
			column++;
		} else if (direction < 180) {	
			// right below
			column = (short) colAboveBelowRight;
			row++;
		} else if (direction < 240) {	
			// left below
			column = (short) colAboveBelowLeft;
			row++;
		} else if (direction < 300) {	
			// left 
			column--;
		} else {						
			// left above
			column = (short) colAboveBelowLeft;
			row--;
		}
//		System.out.print(", new col " + column + ", new row " + row);
	}

}
