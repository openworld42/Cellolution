
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

import java.awt.*;

/**
 * A stem cell of an organism, containing the genom.
 * Note: specialized kind of a stem cell is the (temporary) narrowing cell, acting as a bridge
 * when a cell is in replication state.
 */
public class StemCell extends AbstractCell implements StemCellCarrier {
	
	public static final int INITIAL_WEIGHT = 10000;					// like water (=10000)

	private static Color NARROWING_CELL_COLOR = new Color(100, 120, 255);
	private static Color STEM_CELL_COLOR = new Color(40, 40, 255);

	private Genome genome;

	/**
	 * @param column
	 * @param row
	 * @param organism
	 * @param genome			if null, this is a (temporary) narrowing cell between the replication
	 */
	public StemCell(int column, int row, Organism organism, Genome genome) {
		
		super(column, row, genome == null ? NARROWING_CELL_COLOR : STEM_CELL_COLOR, organism);
		this.genome = genome;
		// energy is set outside, the genome of this organism may change the following values
		props[PROP_ENERGY_CONSUMTION] = 80;
		props[PROP_SUN_BEAM_INCREMENT] = 0;					// no photosynthesis
		props[PROP_H2S_TO_ENERGY] = 0;						
		props[PROP_WEIGHT] = INITIAL_WEIGHT;
		props[PROP_CO2] = 0;
		props[PROP_CO2_ADSORBTION_RATE] = 0;
		props[PROP_CO2_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_CaCO3] = 0;
		props[PROP_CaCO3_ADSORBTION_RATE] = 0;
		props[PROP_CaCO3_ADSORB_ENERGY] = 5;				// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_H2S] = 0;
		props[PROP_H2S_ADSORBTION_RATE] = 0;
		props[PROP_H2S_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_ORGANIC] = 50;
		props[PROP_ORGANIC_ADSORBTION_RATE] = 10;
		props[PROP_ORGANIC_ADSORB_ENERGY] = 8;				// energy consumption when adsorbing, e.g. 8: => 1/8 = 12%
	}

	/**
	 * @return a clone of this cell
	 */
	@Override
	public AbstractCell cloneCell() {
		
		StemCell cell = new StemCell(0, 0, null, null);
		cell.copyAttributes(this);
		// copy all other attributes here
		return cell;
	}

	@Override
	public String getCellTypeName() {
		
		return genome == null ? "Narrowing cell" : "Stem cell";
	}

	/**
	 * Returns the genome of this stem cell, if any
	 */
	@Override
	public Genome getGenome() {
		
		return genome;
	}

	/**
	 * Sets the genome of the cell (usually a simple cell or a stem cell)
	 */
	@Override
	public void setGenome(Genome genome) {
		
		this.genome = genome;
	}

	@Override
	protected void slowUpdate(long time) {
		
		// nothing to do
	}
}
