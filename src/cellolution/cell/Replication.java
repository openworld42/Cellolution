
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

import java.awt.*;
import java.util.*;

import cellolution.*;
import cellolution.util.*;

/**
 * The replication (duplication) of an organism, depending on its genom.
 * It performs the cell division and replication of the organism -> result: 2 organisms,
 * each with parts of the energy and substances.
 */
public class Replication {

	public static final Color IN_REPLICATION_COLOR = Color.GREEN;

	private enum State {
		START, COPY_CELLS, COPY_END
	};
	
	private State state;
	private Organism organism;
	private OrganismMgr organismMgr;
	private ArrayList<AbstractCell> cells;
	private Ocean ocean;
	private Pixel pixels[][];
	private long lastTime;
	private AbstractCell stemCell;
	private Color oldStemCellColor;
	private StemCell narrowingCell;
	private int neighborNr;						// the number of the neighbor direction for the new cell and the narrowing
	private Pixel newStemCellPixel;
	private AbstractCell newStemCell;
	private Genome genome;
	private Genome newGenome;
	private int cellCopyIndex;					// the index of the cell within the organism to copy
	private int newEnergy;						// the energy each of the cells/organisms has after replication
	private int direction;						// the direction to replicate
	private Organism newOrganism;
	private ArrayList<AbstractCell> newCells;
	private ArrayList<AbstractCell> replicationCells;

	/**
	 * Construction.
	 * 
	 * @param organism
	 * @param organismMgr
	 * @param cells
	 * @param ocean
v	 * @param pixels
	 * @param time 				the time when the replication has started
	 */
	public Replication(Organism organism, OrganismMgr organismMgr, ArrayList<AbstractCell> cells,
			Ocean ocean, Pixel[][] pixels, long time) {
		
		this.organism = organism;
		this.organismMgr = organismMgr;
		this.cells = cells;
		this.ocean = ocean;
		this.lastTime = time;
		pixels = ocean.getPixels();
		// find the cell with genom, either a single cell organism or a stem cell
		for (AbstractCell cell : cells) {
			if (cell instanceof StemCellCarrier) {
				stemCell = cell;
				genome = ((StemCellCarrier) stemCell).getGenome();
				break;
			}
		}
		// start the replication process
		state = State.START;
		int energy = organism.getEnergy();
		newEnergy = energy / 2 + energy / 10;		// divided in 2 cells and some energy consumption for replication
		organism.setProperty(Organism.PROP_ENERGY, newEnergy);
		cells.forEach(cell -> cell.getProperties()[AbstractCell.PROP_ENERGY] = newEnergy);
		// we need three cell lists: new and old cell list after replication, and the one showing replication
		newCells = new ArrayList<>();
		replicationCells = new ArrayList<>(cells);
	}

	/**
	 * @param col1
	 * @param col2
	 * @param row1
	 * @param row2
	 * @return true if the pixels are free of rock and other organisms
	 */
	private boolean isFree(int col1, int col2, int row1, int row2) {
		
		int temp;
		// sort columns, rows
		if (col1 > col2) {
			temp = col1;
			col1 = col2;
			col2 = temp;
		}
		if (row1 > row2) {
			temp = row1;
			row1 = row2;
			row2 = temp;
		}
		for (int col = col1; col <= col2; col++) {
			for (int row = row1; row <= row2; row++) {
				Pixel p = pixels[col][row];
				if (!(p instanceof Water)) {
					return false;
				}
				if (organismMgr.hasCellOn(col, row)) {
					return false;
				} 
			}
		}
		return true;
	}

	/**
	 * Perform the next step of replication.
	 * 
	 * @param time
	 */
	public void nextStep(long time) {
		
		switch (state) {
		case START: 
			// wait a period
			if (time - lastTime > 1000) {
				// enough free space around?
				int sizeMax = organism.getDimensionMax();
				if (!organism.hasFreeSpace(sizeMax + 1)) {
					// await free space (Brownian movement)
					return;
				}
				oldStemCellColor = stemCell.getColor();
				stemCell.setColorAndRGB(IN_REPLICATION_COLOR);
				// find a direction where to replicate
				neighborNr = FastRandom.nextIntStat(6) + 1;
				// display a narrowing cell between the old and the new stem cell
				Pixel narrowing = Mover.neighbor(stemCell, neighborNr, ocean.getPixels());	
				narrowingCell = new StemCell(narrowing.getColumn(), narrowing.getRow(), organism, null);
				narrowingCell.getProperties()[AbstractCell.PROP_ENERGY] = 4000;		// just to display some energy
				cells.add(narrowingCell);			// not a real part of the organism, not using organism.add()
				// presets
				newGenome = genome.evolotionaryClone(organism);
				newOrganism = newGenome.createNewOrganism(organismMgr, organism, newEnergy);
				// finish START
				cellCopyIndex = 0;
				state = State.COPY_CELLS;
				lastTime = time;
			}
			return;
		case COPY_CELLS: 
			if (time - lastTime > 600) {
				// enough free space around?
				int sizeMax = organism.getDimensionMax();
				if (!organism.hasFreeSpace(sizeMax + 1)) {
					// await free space (Brownian movement)
					return;
				}
				if (newStemCell == null) {
					Pixel newStemCellPixel = Mover.neighbor(narrowingCell, neighborNr, ocean.getPixels());
					newStemCell = newGenome.duplicateStemCell(stemCell, newStemCellPixel, newOrganism);
					newOrganism.add(newStemCell);
					cellCopyIndex++;
					return;				// next cycle
				}
				// new stem cell is existing
				if (newGenome.completeOrganism()) {
					cellCopyIndex = 0;
					state = State.COPY_END;
					lastTime = time;
				}
			}
			return;
		case COPY_END: 
			if (time - lastTime < 600) {
				// wait a period
				return;
			}
			newGenome.cleanup();				// release old instances and their memory
			cells.remove(narrowingCell)	;		// remove the bridge cell
			stemCell.setColorAndRGB(oldStemCellColor);
			organismMgr.getOrganismsToAdd().add(newOrganism);
			organism.setState(OrgState.ALIVE);			// this also clears the replication in the organism
			return;
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}
}
