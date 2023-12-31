
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
package cellolution.cell;

/**
 * Implementers provide properties and features of a stem cell.
 */
public interface StemCellCarrier {
	
	/**
	 * @return the genome of the cell (usually a simple cell or a stem cell)
	 */
	public Genome getGenome();
	
	/**
	 * Sets the genome of the cell (usually a simple cell or a stem cell)
	 */
	public void setGenome(Genome genome);
}
