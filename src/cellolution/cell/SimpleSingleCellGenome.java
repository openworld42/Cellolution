
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
 * The genome of a single cell organism: the definition how it works and what to do in a replication.
 * Single cell organisms have stem cell properties within the single cell by definition.
 */
public class SimpleSingleCellGenome extends Genome {
	
	public static final String CLASS_NAME = "SimpleSingleCellGenome";

	/**
	 * Construction.
	 */
	public SimpleSingleCellGenome() {

		super();
	}

	@Override
	protected boolean completeOrganism() {

		// TODO  SimpleSingleCellGenome: completeOrganism()

		System.out.println("TODO SimpleSingleCellGenome: completeOrganism()");
		
		return true;
	}

	/**
	 * Creates a new organism with the properties of this genome.
	 * 
	 * @param organismMgr 		the OrganismMgr
	 * @param oldOrganism 		the old organism (=parent)
	 * @param newEnergy			the energy of the new organism
	 * @return the new organism
	 */
	@Override
	protected Organism createNewOrganism(OrganismMgr organismMgr, Organism oldOrganism, int newEnergy) {

		Organism newOrganism = super.createNewOrganism(organismMgr, oldOrganism, newEnergy);
		return newOrganism;
	}

	/**
	 * Creates a evolutionary clone of this genome, which has been modified by 
	 * the "evolution" of the cellolution program.
	 * 
	 * @param oldOrganism 		the old organism
	 * @return the evolotionary clone of this genome
	 */
	@Override
	protected Genome evolotionaryClone(Organism oldOrganism) {
		
		SimpleSingleCellGenome newGenome = new SimpleSingleCellGenome();
		super.evolotionaryClone(newGenome, oldOrganism);
		
		// organism: up to now no values to change
//		PROP_ENERGY
//		PROP_WEIGHT

		// TODO  SimpleSingleCellGenom: evolotionaryClone()
		System.out.println("TODO  SimpleSingleCellGenom: evolotionaryClone()");

		
		return newGenome;
	}

	/**
	 * Creates a evolutionary clone of a cell, which is modified by 
	 * the "evolution" of the cellolution program.
	 * 
	 * @param oldCell			one cell of the old organism
	 * @return the evolotionary clone of the cell
	 */
	protected AbstractCell evolotionaryClone(AbstractCell oldCell) {

		AbstractCell newCell = oldCell.cloneCell();
		int props[] = newCell.getProperties();		// the properties of the cell
		// change some properties
		props[AbstractCell.PROP_AGILITY] = nextGaussian(props[AbstractCell.PROP_AGILITY]);
		props[AbstractCell.PROP_CO2_ADSORB_ENERGY] = nextGaussian(
				props[AbstractCell.PROP_CO2_ADSORB_ENERGY], MIN_ADSORB_ENERGY, MAX_ADSORB_ENERGY);
		props[AbstractCell.PROP_CaCO3_ADSORBTION_RATE] = nextGaussian(
				props[AbstractCell.PROP_CaCO3_ADSORBTION_RATE], MIN_ADSORB_ENERGY, MAX_ADSORB_ENERGY);
		props[AbstractCell.PROP_H2S_ADSORBTION_RATE] = nextGaussian(
				props[AbstractCell.PROP_H2S_ADSORBTION_RATE], MIN_ADSORB_ENERGY, MAX_ADSORB_ENERGY);
		props[AbstractCell.PROP_ORGANIC_ADSORB_ENERGY] = nextGaussian(
				props[AbstractCell.PROP_ORGANIC_ADSORB_ENERGY], MIN_ADSORB_ENERGY, MAX_ADSORB_ENERGY);
		

		// TODO  evolotionaryClone evtl auch in Genome verschieben und nach Zellart (instanceof) modifizieren
		// adsobtionrate mit Energie verbrauch koppeln

		
		// cells
//		PROP_WEIGHT
		// within limits
//		PROP_CO2_ADSORBTION_RATE
//		PROP_CaCO3_ADSORBTION_RATE
//		PROP_H2S_ADSORBTION_RATE
//		PROP_ORGANIC_ADSORBTION_RATE
		// within limits, can be optimized up to a certain extent
//		PROP_SUN_BEAM_INCREMENT

		return newCell;
	}
}
