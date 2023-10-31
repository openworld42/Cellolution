
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

import java.util.*;

/**
 * Contains the values of an linear interpolation.
 * It can be used to define a linear interpolated function by passing some x/y value pairs.
 * 
 * <pre>
 * Usage:
 * 
 * 	Interpolation intpol = new Interpolation(new float[] {0, 2, 10, 16, 20, 23, 30, 31});
 * 	float y = intpol.getY(5);		// y results to the above function: 9
 * </pre>
 */
public class Interpolation {
	
	private float x[];
	private float y[];
	
	/**
	 * Construction with x/y pairs: x0, y0, x1, y1, ... etc.
	 * 
	 * @param valuePairs		e.g. 0, 2, ...  10, 16, ... 20, 23
	 */
	public Interpolation(float valuePairs[]) {

		if (valuePairs.length % 2 != 0) {
			throw new ArithmeticException("Not an even number of values (x/y pairs): " + valuePairs);
		}
		x = new float[valuePairs.length / 2];
		y = new float[valuePairs.length / 2];
		int index = 0;
		for (int i = 0; i < valuePairs.length; i += 2) {
			x[index] = valuePairs[i];
			y[index++] = valuePairs[i + 1];
		}
	}
	
	/**
	 * Returns the y value of a x value.
	 * 
	 * @param xValue
	 * @return the y value of a x value
	 */
	public int intY(float xValue) {
		
		if (xValue < x[0] || xValue > x[x.length - 1]) {
			throw new ArithmeticException("xValue not within the defined range: " + xValue + "[" + x.toString() + "]");
		}
		int index = Arrays.binarySearch(x, xValue);
		if (index >= 0) {
			return (int) y[index];
		}
		int lowerIndex = -index - 2;
		int higherIndex = -index - 1;
		float position = xValue / (x[higherIndex] - x[lowerIndex]);
		return (int) (y[lowerIndex] + (y[higherIndex] - y[lowerIndex]) * (xValue - x[lowerIndex]) / (x[higherIndex] - x[lowerIndex]));
	}
}
