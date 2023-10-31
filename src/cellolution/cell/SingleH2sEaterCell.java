
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

import cellolution.*;

/**
 * A H2S eater cell get its energy from H2S (hydrogen sulfid) emitted by the seabed or (black) smokers.
 * As in reality, this is a very slow process, resulting in a low replication rate or even dying if there
 * is a low H2S concentration.
 * Sometimes, a (insisible) SimpleH2sEaterCell living within a smoker is pushed out into the ocean.
 * H2S eating cells consume a small amount of CaCO3 and therefore are slightely heavier than water.
 */
public class SingleH2sEaterCell extends AbstractCell implements StemCellCarrier {

	public static final Color RGB_BASE = new Color(255, 70, 20);
	
	// TODO  INITIAL_WEIGHT aufgrund von CaCO3 auflösen -> erzeugt weight Berechnung des Organismus

	public static final int INITIAL_WEIGHT = 10500;					// slightly heavier than water (=10000)

	private Genome genome;

	/**
	 * Construction, use create() to create this cell.
	 * 
	 * @param column
	 * @param row
	 * @param organism		the organism the cell belongs to
	 * @param genome  
	 */
	private SingleH2sEaterCell(int column, int row, Organism organism, Genome genome) {
		
		super(column, row, RGB_BASE, organism);
		this.genome = genome;
		// energy is set outside, the genome of this organism may change the following values
		props[PROP_ENERGY_CONSUMTION] = 80;
		props[PROP_SUN_BEAM_INCREMENT] = 0;					// a SimpleH2sEaterCell has no photosynthesis
		props[PROP_H2S_TO_ENERGY] = 2;						// but can use H2S to gererate energy
		props[PROP_WEIGHT] = INITIAL_WEIGHT;
		props[PROP_CO2] = 100;
		props[PROP_CO2_ADSORBTION_RATE] = 0;
		props[PROP_CO2_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_CaCO3] = 0;
		props[PROP_CaCO3_ADSORBTION_RATE] = 0;
		props[PROP_CaCO3_ADSORB_ENERGY] = 5;				// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_H2S] = 1000;
		props[PROP_H2S_ADSORBTION_RATE] = 25;
		props[PROP_H2S_ADSORB_ENERGY] = 5;					// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		props[PROP_ORGANIC] = 50;
		props[PROP_ORGANIC_ADSORBTION_RATE] = 10;
		props[PROP_ORGANIC_ADSORB_ENERGY] = 8;				// energy consumption when adsorbing, e.g. 8: => 1/8 = 12%
	}
	
	/**
	 * Change the color according to the cell's energy (more energy -> brighter yellow).
	 */
	public void adjustColorByEnergy() {
		
		int green = 70 + props[PROP_ENERGY] * 184 / 50000;
		green = green > 240 ? 240 : green;
		int blue = 20 + props[PROP_ENERGY] * 40 / 50000;
		blue = blue > 60 ? 60 : blue;
		setColorAndRGB(new Color(255, green, blue));
	}

	/**
	 * @return a clone of this cell, with all attributes and properties copied
	 */
	public AbstractCell cloneCell() {
		
		SingleH2sEaterCell cell = new SingleH2sEaterCell(0, 0, null, null);
		cell.copyAttributes(this);
		// copy all other attributes here
		adjustColorByEnergy();
		return cell;
	}

	/**
	 * Creates a SimpleH2sEaterCell.
	 * 
	 * @param column
	 * @param row
	 * @param energy
	 * @return the created cell
	 */
	public static SingleH2sEaterCell create(int column, int row, int energy) {
		
		Ocean ocean = Main.getOcean();
		OrganismMgr organismMgr = ocean.getOrganismMgr();
		Organism organism = organismMgr.createOrganism(OrgState.ALIVE);
		organism.setProperty(Organism.PROP_WEIGHT, INITIAL_WEIGHT);	
		organism.setProperty(Organism.PROP_MOVEABLE, 1);	
		SimpleSingleCellGenome genome = new SimpleSingleCellGenome();
		SingleH2sEaterCell cell = new SingleH2sEaterCell(column, row, organism, genome);
		genome.setStemCell(cell);
		cell.getProperties()[AbstractCell.PROP_ENERGY] = energy;
		cell.adjustColorByEnergy();
		organism.add(cell);
		organismMgr.addOrganism(organism);
		return cell;
	}

	/**
	 * @return the public name of this cell type
	 */
	public String getCellTypeName() {
		
		return "Single H2S Eater";
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
		// use some of the H2S collected to generate energy, regulated by agility
		int h2sDiff = (props[PROP_H2S] * props[PROP_AGILITY] / AGILITY_FACTOR_ONE) / 10;						
		energy += h2sDiff * props[PROP_H2S_TO_ENERGY];
		props[PROP_H2S] -= h2sDiff;
		props[PROP_ENERGY] = energy;
		// change color depending on energy
		adjustColorByEnergy();
	}
}
