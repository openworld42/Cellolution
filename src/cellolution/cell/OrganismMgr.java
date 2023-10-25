
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

/**
 * The manager of all organisms.
 */
public class OrganismMgr {

	private Ocean ocean;
	private int cellColumns;
	private int cellRows;
	private int organicMatterReservoir;							// the amount of organic matter in the ocean should stay constant
	private ArrayList<Organism> organisms;
	private ArrayList<Organism> organismsToRemove;
	private ArrayList<Organism> organismsToAdd;
	private long lastTimeMoved;
	private long lastTimeSlowUpdate;

	/**
	 * @param ocean 
	 */
	public OrganismMgr(Ocean ocean) {
		
		this.ocean = ocean;
		cellColumns = Main.getCellColumns();
		cellRows = Main.getCellRows();
		organisms = new ArrayList<>();
		organismsToRemove = new ArrayList<>();
		organismsToAdd = new ArrayList<>();
		// the amount of organic matter in the ocean should stay constant, initialize the reservoir
		organicMatterReservoir = (cellColumns + cellRows * 2) * 50;
		lastTimeMoved = System.currentTimeMillis();
	}

	/**
	 * Adds an organism to the ocean.
	 * 
	 * @param organism
	 */
	public void addOrganism(Organism organism) {

		organisms.add(organism);
	}

	/**
	 * Adds (or subtracts) organic matter to the reservoir of the ocean.
	 * 
	 * @param amount
	 */
	public void addToOrganicMatterReservoir(int amount) {
		
		organicMatterReservoir += amount;
	}

	/**
	 * Find the nearest organism and display its values/properties.
	 * 
	 * @param pixel
	 */
	public void findAndDisplayNearestOrganism(Pixel pixel) {

		Organism organism = null;
		int col = pixel.getColumn();
		int row = pixel.getRow();
		for (int i = 1; i < 70 && organism == null; i++) {
			int left = col - i;
			if (left < 0) {
				left = 0;
			}
			int right = col + i;
			if (right >= cellColumns) {
				right = cellColumns - 1;
			}
			int top = row - i;
			if (top < 0) {
				top = 0;
			}
			int bottom = row + i;
			if (bottom >= cellRows) {
				bottom = cellRows - 1;
			}
			for (Organism org : organisms) {
				if (org.isTouched(left, right, top, bottom)) {
					organism = org;
					break;
				}
			}
		}
		if (organism == null) {
			return;
		}
		Main.getOrgDisplayCtlr().follow(organism);
	}

	/**
	 * Creates a new organism.
	 * 
	 * @param state
	 * @return the created organism
	 */
	public Organism createOrganism(OrgState state) {
		
		return new Organism(state, this);
	}

	/**
	 * @return the organicMatterReservoir
	 */
	public int getOrganicMatterReservoir() {
		
		return organicMatterReservoir;
	}

	/**
	 * @return the number of organisms within the ocean
	 */
	public int getOrganismCount() {
		
		return organisms.size();
	}

	/**
	 * @return the organismsToAdd
	 */
	public ArrayList<Organism> getOrganismsToAdd() {
		
		return organismsToAdd;
	}

	/**
	 * Test if any organism has a cell on the given column and row.
	 * 
	 * @param col
	 * @param row
	 * @return true if there is a cell on the given column and row, false otherwise
	 */
	public boolean hasCellOn(int col, int row) {

		for (Organism organism : organisms) {
			if (organism.hasCellOn(col, row)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Most organisms are moving around, either through some gained speed or by Brownian motion.
	 * 
	 * @param time		the current time
	 */
	public void moveOrganisms(long time) {
		
		if (time - lastTimeMoved < 130) {
			// we do not move too often (saving CPU power) or too fast
			return;						
		}
		lastTimeMoved = time;
		organisms.forEach(org -> {
			org.move(time);
		});
	}

	/**
	 * Perform one time step of life for all organisms.
	 * 
	 * @param time		the current time
	 */
	public void organismsOneStepOfLife(long time) {

		organisms.forEach(org -> {
			org.oneStepOfLife();
		});
	}

	/**
	 * Paint all organisms of the ocean.
	 * 
	 * @param g2d
	 */
	public void paint(Graphics2D g2d) {
		
		// paint all organisms
		organisms.forEach(org -> {
			org.paint(g2d);
		});
	}

	/**
	 * Removes an organism from the ocean, usually it is completely decomposed.
	 * 
	 * @param organism
	 */
	public void remove(Organism organism) {

		organismsToRemove.add(organism);
	}

	/**
	 * Slow update for organism changes not in the need to be to fast (due to performance reasons).
	 * 
	 * @param time			the current time
	 */
	public void slowUpdate(long time) {
		
		if (time - lastTimeSlowUpdate < 500) {
			// we do not update too often (saving CPU power)
			return;						
		}
		lastTimeSlowUpdate = time;
		for (int i = 0; i < organisms.size(); i++) {
			organisms.get(i).slowUpdate(time);
		}
//		organisms.forEach(org -> {
//			org.slowUpdate(time);
//		});
		organismsToRemove.forEach(org -> {
			organisms.remove(org);
		});
		organismsToRemove.clear();
		organismsToAdd.forEach(org -> {
			addOrganism(org);
		});
		organismsToAdd.clear();
	}
}
