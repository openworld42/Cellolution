
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
package cellolution.util;

import java.util.*;

/**
 * A very fast integer random generator with the drawback of some reused values - not important within this application.
 * It is based on java.utils SplittableRandom.
 * Instances of FastRandom are not thread-safe, the static methods are never thread-safe.
 * Due to the implementation, this should be one of the fastest pseudorandom generators ever.
 * The average cost is the method call with a return of buffer[i++] ^ changingValue. 
 * The buffer is repeated with another changingValue until it is filled again.
 * 
 */
public class FastRandom {

	private static int RANDOM_BUFFER_SIZE = 1000;

	private static FastRandom instance;
	
	private int buffer[] = new int[RANDOM_BUFFER_SIZE];
	private int bufferXor[]= new int[RANDOM_BUFFER_SIZE];
	private SplittableRandom random;
	private int bufferIndex;
	private int bufferXorIndex;
	private int valueXor;
	private int temp;
	
	/**
	 * Construction.
	 */
	public FastRandom() {

		instance = this;
		random = new SplittableRandom();
		nextFill();
	}
	
	/**
	 * Construction with a seed.
	 * 
	 * @param seed
	 */
	public FastRandom(long seed) {

		instance = this;
		random = new SplittableRandom(seed);
		nextFill();
	}

	/**
	 * Returns a pseudorandomly chosen Gaussian distributed int value.
	 * The result is meanValue + nextGaussian() * normalDistribution, limited to minimum and maximum.
	 * The statment new FastRandom() has to be called once before.
	 * 
	 * @param meanValue					the value of which the distribution takes place
	 * @param normalDistribution		1.0 is the standard Gaussian distribution
	 * 									(64% of all values between -1/1, 13.6% between -2/-1, 13.6% between 1/2, remaining 4,6% outside)
	 * @param minimumValue				minimum value limit
	 * @param maxmumValue				minimum value limit
	 * @return a pseudorandomly chosen Gaussian distributed int value
	 */
	public static int nextGaussian(double meanValue, double normalDistribution, int minimumValue, int maxmumValue) {
		
		int result = (int) (meanValue + instance.random.nextGaussian() * normalDistribution);
	    if (result < minimumValue) result = minimumValue;
	    if (result > maxmumValue) result = maxmumValue;
	    return result;
	}

	/**
	 * Returns a pseudorandomly chosen int value.
	 * 
	 * @return a pseudorandomly chosen int value
	 */
	public int nextInt() {
		
		if (bufferIndex < RANDOM_BUFFER_SIZE) {
			return buffer[bufferIndex++] ^ valueXor;
		}
		bufferIndex = 0;
		if (bufferXorIndex < RANDOM_BUFFER_SIZE) {
			valueXor = bufferXor[bufferXorIndex++];
			return buffer[bufferIndex++] ^ valueXor;
		}
		nextFill();
		return buffer[bufferIndex++] ^ valueXor;
	}

	/**
	 * Returns a pseudorandomly chosen int value, as a static method.
	 * The statment new FastRandom() has to be called once before.
	 * 
	 * @return a pseudorandomly chosen int value
	 */
	public static int nextIntStat() {
		
		return instance.nextInt();
	}

	/**
	 * Returns a pseudorandomly chosen int value between zero (inclusive) and a bound (exclusive).
	 * 
	 * @param bound
	 * @return a pseudorandomly chosen int value between zero (inclusive) and a bound (exclusive)
	 */
	public int nextInt(int bound) {
		
		if (bufferIndex < RANDOM_BUFFER_SIZE) {
			temp = buffer[bufferIndex++] ^ valueXor;
			return temp < 0 ? -temp % bound: temp % bound;
		}
		temp = nextInt();
		return temp < 0 ? -temp % bound: temp % bound;
	}

	/**
	 * Returns a pseudorandomly chosen int value between zero (inclusive) and a bound (exclusive).
	 * The statment new FastRandom() has to be called once before.
	 * 
	 * @param bound
	 * @return a pseudorandomly chosen int value between zero (inclusive) and a bound (exclusive)
	 */
	public static int nextIntStat(int bound) {
		
		return instance.nextInt(bound);
	}

	/**
	 * Fill the buffer with some random values.
	 */
	private void nextFill() {
		
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = random.nextInt();
		}
		for (int i = 0; i < bufferXor.length; i++) {
			bufferXor[i] = random.nextInt() & 0xeffffff;
		}
		bufferIndex = 0;
		bufferXorIndex = 0;
		valueXor = bufferXor[bufferXorIndex++];
	}
	
//	/**
//	 * Tester.
//	 * 
//	 * @param arguments
//	 */
//	public static void main(String[] arguments) {
//		
//		try {
//			int count[] = new int[100];
//			FastRandom random = new FastRandom();
//			
//			for (int i = 0; i < 10000; i++) {
//				int value = random.nextInt(1000);
//				System.out.println(value);
//				count[value / 100]++;
//			}
//			for (int i = 0; i < 10; i++) {
//				System.out.println("i: " + i + " " + count[i]);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}		
//	}
}
