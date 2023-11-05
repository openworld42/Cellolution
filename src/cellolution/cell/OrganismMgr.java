
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
import java.util.*;

import org.json.*;

import cellolution.*;

/**
 * The manager of all organisms.
 */
public class OrganismMgr {

	private Ocean ocean;
	private int cellColumns;
	private int cellRows;
	private java.util.List<Organism> organisms;
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
		organisms = Collections.synchronizedList(new ArrayList<Organism>());
		organismsToRemove = new ArrayList<>();
		organismsToAdd = new ArrayList<>();
		lastTimeMoved = System.currentTimeMillis();
		JSONObject jsonSimObj = Main.getData().getSimObject();
		if (jsonSimObj != null) {
			// create organisms from an existing simulation (file), instead of being empty
			JSONObject jsonOcean = jsonSimObj.getJSONObject(Keys.OCEAN);
			JSONArray jsonOrganisms = jsonOcean.getJSONArray(Keys.ORGANISMS);
			for (int i = 0; i < jsonOrganisms.length(); i++) {
				addOrganismFrom(jsonOrganisms.getJSONObject(i));
			}
		}
	}

	/**
	 * Adds an organism from a JSON representation (e.g. from a previous simulation file).
	 * 
	 * @param jsonOrg		the JSON representation of the organism
	 */
	public void addOrganismFrom(JSONObject jsonOrg) {

		String state = jsonOrg.getString(Keys.ORGANISM_STATE);
		// LAST_STATE may be null for young, living organisms: if so, no JSON entry
		OrgState lastState = null;
		if (jsonOrg.has(Keys.LAST_STATE)) {
			lastState =  OrgState.valueOf(jsonOrg.getString(Keys.LAST_STATE));
		}
		Organism organism = new Organism(OrgState.valueOf(state), 
				lastState, 
				jsonOrg.getInt(Keys.WEIGHT), 
				jsonOrg.getInt(Keys.MOVEABLE), 
				jsonOrg.getInt(Keys.DECOMPOSE_COUNT), this);
		// cells
		JSONArray jsonCells = jsonOrg.getJSONArray(Keys.CELLS);
		for (int i = 0; i < jsonCells.length(); i++) {
			AbstractCell cell = AbstractCell.createFrom(jsonCells.getJSONObject(i), organism);
			organism.add(cell);
		}
		addOrganism(organism);

			
			// TODO Organism.toJSONObject() check issues below, any computations?:
			// simply drop replication? -> set ALIVE
			// compute PROP_WEIGHT
			// check PROP_SPEED, PROP_DIRECTION
			// compute organicAmount

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

	/**
	 * Creates a JSONArray from this object.
	 * 
	 * @return the JSONArray containing the data of this object
	 */
	public JSONArray toJSONArray() {
		
		JSONArray jsonOrganisms = new JSONArray();
		for (Organism org : organisms) {
			switch (org.getState()) {
			case GROWING: 
				// too complicated due to Replication state machine, revert replication
				// and don't serialize
				continue;
			case IN_REPLICATION: 
				// too complicated due to Replication state machine, revert replication
				org.revertReplication();
				// intentionally falling through
			}
			jsonOrganisms.put(org.toJSONObject());
		}
		return jsonOrganisms;
	}
}
