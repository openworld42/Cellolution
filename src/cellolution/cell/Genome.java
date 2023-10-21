
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
package cellolution.cell;

import java.util.*;

import cellolution.*;
import cellolution.util.*;

/**
 * The genome of an organism: the definition how it works and what to do in a replication.
 * In cellolution, the genome is usually contained in one or more stem cells. Single cell
 * organisms have stem cell properties within the single cell by definition.
 */
public abstract class Genome {
	
	public static final double NORMAL_DISTRIBUTION_DEFAULT = 1.5;
	
	public static final int MIN_ADSORB_ENERGY = 5;			// adsorbing needs energy: 5 => 1/5 (20%)
	public static final int MAX_ADSORB_ENERGY = 10;		// adsorbing needs energy: 10 => 1/10 (10%)
	
	protected Organism oldOrganism;
	protected ArrayList<AbstractCell> oldOrganismCellCopy;
	protected AbstractCell stemCell;

	/**
	 * Construction.
	 */
	protected Genome() {
		
	}

	/**
	 * Performs the cleanup of this genome after cell duplication.
	 * Releases the allocated instances memory.
	 */
	public void cleanup() {
		
		oldOrganism = null;
		oldOrganismCellCopy = null;
	}

	/**
	 * Complete an organism after starting to copy.
	 * The stem cell of the new organism has been creted already.
	 * 
	 * @return true, if the completion has been finished, false otherwise
	 */
	protected abstract boolean completeOrganism();

	/**
	 * Creates a new organism with the properties of this genome.
	 * 
	 * @param organismMgr 
	 * @param oldOrganism 		the old organism (=parent)
	 * @param newEnergy			the energy of the new organism
	 * @return the new organism
	 */
	protected Organism createNewOrganism(OrganismMgr organismMgr, Organism oldOrganism, int newEnergy) {

		Organism newOrganism = new Organism(OrgState.ALIVE, organismMgr);
		newOrganism.addEnergy(newEnergy);
		newOrganism.setProperty(Organism.PROP_WEIGHT, oldOrganism.getProperty(Organism.PROP_WEIGHT));	
		newOrganism.setProperty(Organism.PROP_MOVEABLE, 1);	
		return newOrganism;
	}

	/**
	 * Duplicates the old stem cell with evolutionary modifications for a new organism.
	 * 
	 * @param stemCell
	 * @param newStemCellPixelPos		column and row
	 * @param newOrganism 
	 * @return the duplicated stem cell with evolutionary modified properties and genome
	 */
	public AbstractCell duplicateStemCell(AbstractCell stemCell, Pixel newStemCellPixelPos, Organism newOrganism) {
		
		AbstractCell newStemCell = evolotionaryClone(stemCell);
		newStemCell.setOrganism(newOrganism);
		newStemCell.setColRow(newStemCellPixelPos.getColumn(), newStemCellPixelPos.getRow());
		stemCell = newStemCell;
		((StemCellCarrier) newStemCell).setGenome(this);
		return newStemCell;
	}
	
	/**
	 * Creates a evolotionary clone of this genome, which has been modified by 
	 * the "evolution" of the cellolution program.
	 * 
	 * @param oldOrganism 		the old organism
	 * @return the evolotionary clone of this genome
	 */
	protected abstract Genome evolotionaryClone(Organism oldOrganism);

	/**
	 * Creates a evolutionary clone of a cell, which is modified by 
	 * the "evolution" of the cellolution program.
	 * 
	 * @param oldCell			one cell of the old organism
	 * @return the evolotionary clone of the cell
	 */
	protected abstract AbstractCell evolotionaryClone(AbstractCell oldCell);

	/**
	 * Creates a evolotionary clone of this genome, which has been modified by 
	 * the "evolution" of the cellolution program.
	 * 
	 * @param newGenome 		the new genome
	 * @param oldOrganism 		the old organism
	 */
	protected void evolotionaryClone(Genome newGenome, Organism oldOrganism) {
		
		newGenome.oldOrganism = oldOrganism;
		ArrayList<AbstractCell> oldCells = oldOrganism.getCells();
		newGenome.oldOrganismCellCopy = new ArrayList<AbstractCell>(oldCells.size());
		oldCells.forEach(oldCell -> newGenome.oldOrganismCellCopy.add(evolotionaryClone(oldCell)));
	}

	/**
	 * Returns a pseudorandomly chosen Gaussian distributed int value with predefined distribution and limits.
	 * 
	 * @param meanValue		the mean value (the center of the distribution)
	 * @return the resulting value
	 */
	public static int nextGaussian(int meanValue) {
		
		return FastRandom.nextGaussian(meanValue, 1.5, meanValue * 8 / 10, meanValue * 12 / 10);
	}

	/**
	 * Returns a pseudorandomly chosen Gaussian distributed integer value with predefined distribution and limits.
	 * 
	 * @param meanValue		the mean value (the center of the distribution)
	 * @param minimumValue				minimum value limit
	 * @param maxmiumValue				maximum value limit
	 * @return the resulting value
	 */
	public static int nextGaussian(int meanValue, int minimumValue, int maxmiumValue) {
		
		return FastRandom.nextGaussian(meanValue, 1.5, minimumValue, maxmiumValue);
	}

	/**
	 * Sets the stem cell containing this genom.
	 * 
	 * @param stemCell
	 */
	public void setStemCell(AbstractCell stemCell) {
		
		this.stemCell = stemCell;
	}
}
