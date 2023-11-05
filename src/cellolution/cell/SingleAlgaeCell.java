
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

import org.json.*;

import cellolution.*;

/**
 * An algae cell get its energy from sunlight, transforming CO2 to energy and organic matter.
 * Sometimes, algae cells are dropped on the oceans surface from the atmosphere above.
 * H2S is a little bit toxically to algae.
 */
public class SingleAlgaeCell extends AbstractCell implements StemCellCarrier {

	public static final String CLASS_NAME = "SingleAlgaeCell";
	public static final Color RGB_BASE = new Color(128, 218, 128);

	public static final int INITIAL_WEIGHT = 10000;					// like water (=10000)

	private int cellRows;
	private Genome genome;

	/**
	 * Construction, use create() to create this cell.
	 * 
	 * @param column		the column of this cell
	 * @param row			the row of this cell
	 * @param energy 		the energy of this cell
	 * @param organism		the organism the cell belongs to
	 * @param genome  		the genome of this cell
	 */
	protected SingleAlgaeCell(int column, int row, int energy, Organism organism, Genome genome) {
		
		super(column, row, energy, RGB_BASE, organism);
		this.genome = genome;
		cellRows = Main.getCellRows();
		// energy is set outside, the genome of this organism may change the following values
		props[PROP_ENERGY_CONSUMTION] = 80;
		props[PROP_SUN_BEAM_INCREMENT] = 1000;				// photosynthesis
		props[PROP_H2S_TO_ENERGY] = 0;						// does not use H2S to gererate energy
		props[PROP_WEIGHT] = INITIAL_WEIGHT;
		props[PROP_CO2] = 100;
		props[PROP_CO2_ADSORBTION_RATE] = 10;
		props[PROP_CO2_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_CaCO3] = 0;
		props[PROP_CaCO3_ADSORBTION_RATE] = 0;
		props[PROP_CaCO3_ADSORB_ENERGY] = 5;				// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_H2S] = 0;
		props[PROP_H2S_ADSORBTION_RATE] = 0;
		props[PROP_H2S_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_ORGANIC] = 300;
		props[PROP_ORGANIC_ADSORBTION_RATE] = 10;
		props[PROP_ORGANIC_ADSORB_ENERGY] = 8;				// energy consumption when adsorbing, e.g. 8: => 1/8 = 12%
	}
	
	/**
	 * Fill in cell type specific JSON values of a former simulation.
	 */
	@Override
	public void addFrom(JSONObject jsonCell) {
		
		// nothing to do here
	}
	
	/**
	 * Change the color according to the cell's energy (more energy -> brighter yellow).
	 */
	@Override
	public void adjustColorByEnergy() {
		
		int red = 90 + props[PROP_ENERGY] * 50 / 50000;
		red = red > 128 ? 128 : red;
		int green = 130 + props[PROP_ENERGY] * 120 / 50000;
		green = green > 230 ? 230 : green;
		int blue = 60 + props[PROP_ENERGY] * 60 / 50000;
		blue = blue > 128 ? 128 : blue;
		setColorAndRGB(new Color(red, green, blue));
	}

	/**
	 * @return a clone of this cell, with all attributes and properties copied
	 */
	public AbstractCell cloneCell() {
		
		SingleAlgaeCell cell = new SingleAlgaeCell(0, 0, 0, null, null);
		cell.copyAttributes(this);
		// copy all other attributes here
		adjustColorByEnergy();
		return cell;
	}

	/**
	 * Creates a this cell.
	 * 
	 * @param column				the column of this cell
	 * @param row					the row of this cell
	 * @param energy				the energy of this cell
	 * @return the created cell
	 */
	public static SingleAlgaeCell create(int column, int row, int energy) {
		
		Ocean ocean = Main.getOcean();
		OrganismMgr organismMgr = ocean.getOrganismMgr();
		Organism organism = new Organism(OrgState.ALIVE, INITIAL_WEIGHT, 1, organismMgr);
		SimpleSingleCellGenome genome = new SimpleSingleCellGenome();
		SingleAlgaeCell cell = new SingleAlgaeCell(column, row, energy, organism, genome);
		genome.setStemCell(cell);
		organism.add(cell);
		organismMgr.addOrganism(organism);
		return cell;
	}

	/**
	 * @return the public name of this cell type
	 */
	public String getCellTypeName() {
		
		return "Single Algae Cell";
	}

	/**
	 * Returns the genom of this cell.
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
	
	/**
	 * Slow update for organism changes not needed to be to fast.
	 * 
	 * @param time			the current time
	 */
	@Override
	public void slowUpdate(long time) {
		
		// to live costs some energy, regulated by agility
		int energy = props[PROP_ENERGY] - props[PROP_ENERGY_CONSUMTION] * props[PROP_AGILITY] / AGILITY_FACTOR_ONE;
		// use the sun and some of the CO2 collected to generate energy, regulated by agility
		// sunlight generates energy in both depth dependent and a sunbeam hit
		int sunBrightness = computeSunshineBrightness(cellRows);
		int energyDiff = (sunBrightness + props[PROP_AGILITY] / AGILITY_FACTOR_ONE) / 1;
		energy += energyDiff;
		props[PROP_ENERGY] = energy;
		// change color depending on energy
		adjustColorByEnergy();
	}

	/**
	 * Creates a JSONObject from this object.
	 * 
	 * @return the JSONObject containing the data of this object
	 */
	@Override
	public JSONObject toJSONObject() {
		
		JSONObject jsonCell = new JSONObject();
		super.toJSONObject(jsonCell);
		jsonCell.put(Keys.GENOME, genome.toJSONObject());
		return jsonCell;
	}
}
