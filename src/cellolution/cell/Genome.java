
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

import java.util.*;

import org.json.*;

import cellolution.*;
import cellolution.util.*;

/**
 * The abstract genome of an organism: a basic definition how it works and what to do in a replication.
 * In Cellolution, the genome is usually contained in one or more stem cells. Single cell
 * organisms have stem cell properties within the single cell by definition.
 */
public abstract class Genome {
	
	/** the distribution default for genome variations */
	public static final double NORMAL_DISTRIBUTION_DEFAULT = 1.5;
	
	/** adsorbing needs energy, the minimum: 5 => 1/5 (20%) */
	public static final int MIN_ADSORB_ENERGY = 5;			// adsorbing needs energy: 5 => 1/5 (20%)
	/** adsorbing needs energy, the maximum: 5 => 1/5 (20%) */
	public static final int MAX_ADSORB_ENERGY = 10;		// adsorbing needs energy: 10 => 1/10 (10%)
	
	/** the parent organism which is the source of replication */
	protected Organism parentOrganism;
	/** a list of cells of the parent organism as a copy */
	protected ArrayList<AbstractCell> oldOrganismCellCopy;
	/** the stem cell, containing a genome */
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
		
		parentOrganism = null;
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
	 * Creates a cell from a JSONObject representation.
	 * 
	 * @param jsonGenome			the JSONObject
	 * @return the genome or null, if none
	 */
	public static Genome createFrom(JSONObject jsonGenome) {
		
		if (!jsonGenome.has(Keys.GENOME)) {
			return null;
		}
		Genome genome = null;
		String className = jsonGenome.getString(Keys.GENOME);
		switch (className) {
		case SimpleSingleCellGenome.CLASS_NAME: 
			genome = new SimpleSingleCellGenome();
			break;
		default:
			throw new IllegalArgumentException("Unexpected cell name: " + className);
		}
		return genome;
	}

	/**
	 * Creates a new organism with the properties of this genome.
	 * 
	 * @param organismMgr 		the organismMgr
	 * @param oldOrganism 		the old organism (= parent)
	 * @param newEnergy			the energy of the new organism
	 * @return the new organism
	 */
	protected Organism createNewOrganism(OrganismMgr organismMgr, Organism oldOrganism, int newEnergy) {

		Organism newOrganism = new Organism(OrgState.GROWING, 
				oldOrganism.getProperty(Organism.PROP_WEIGHT), 
				oldOrganism.getProperty(Organism.PROP_MOVEABLE),
				organismMgr);
		newOrganism.addEnergy(newEnergy);
		return newOrganism;
	}

	/**
	 * Duplicates the old stem cell with evolutionary modifications for a new organism.
	 * 
	 * @param stemCell					the old stem cell with evolutionary modifications
	 * @param newStemCellPixelPos		column and row
	 * @param newOrganism 				the new organism
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
		
		newGenome.parentOrganism = oldOrganism;
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
	 * @param stemCell		the stem cell containing this genom
	 */
	public void setStemCell(AbstractCell stemCell) {
		
		this.stemCell = stemCell;
	}

	/**
	 * Creates a JSONObject from this object.
	 * 
	 * @return the JSONObject containing the data of this object
	 */
	public JSONObject toJSONObject() {

		JSONObject jsonGenome = new JSONObject();
		jsonGenome.put(Keys.GENOME, this.getClass().getSimpleName());
		jsonGenome.put("TODO", "TODO");
		return jsonGenome;
	}
}
