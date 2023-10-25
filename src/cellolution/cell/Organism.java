
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
import cellolution.ui.*;
import cellolution.util.*;

/**
 * An Organism consists of one or more cells grouping together: a being, a creation.
 * 
 * Each organism has an energy budget, the budget of a cell is the organism budget divided
 * through the number of cells. If the budget is too low, the organism is starving and will die.
 * The energy unit of "1" is, as with the other values, an integer of 10000.
 * 
 * Note: in Cellolution, a single cell may also form an organism.
 */
public class Organism {
	
	public static final int DISPLAY_POSITION_COUNT_DEFAULT = 10; 		// times slowUpdate() intervalls
	private static final int DECOMPOSE_COUNT_DEFAULT = 200;				// times slowUpdate() intervalls
	
	// Organism properties, for performance reasons: an array with constants as indices
	// (maybe we change this later to enums or a more dynamic approach)
	// for performance reasons: a floating point value of 1.0 equates to an integer value of 10000 (unless otherwise specified)
	public static final int PROP_ENERGY 		= 0;
	public static final int PROP_SPEED 			= 1;	// speed of one means one pixel per time slice, but resistance is quadratic		
	public static final int PROP_DIRECTION 		= 2;	// the direction of the speed, atlas-like, in degrees, 0 is north, clockwise
	public static final int PROP_WEIGHT 		= 3;	// sum of the cell weights divided through the number of cells
	public static final int PROP_MOVEABLE 		= 4;	// zero or one for false/true
	public static final int SIZE_OF_PROPS = PROP_MOVEABLE + 1;

	private int props[] = new int[SIZE_OF_PROPS];		// the properties of the organism
	
	private OrgState state;
	private OrgState lastState;							// the state before fi there was a state change
	private OrganismMgr organismMgr;
	private ArrayList<AbstractCell> cells;
	private ArrayList<AbstractCell> outerCells;			// the cells outside, with water contact
	private Replication replication;					// a replication instance, managing the replication process, if any
	// outline: max/min of row and column of all cells of this organism
	private int minColumn = Integer.MAX_VALUE;
	private int maxColumn = 0;
	private int minRow = Integer.MAX_VALUE;
	private int maxRow = 0;
	private int number;									// the individual number of this organism, starting with one
	private int displayPositionCount;					// if not zero, display the position of this organism
	private int decomposeCount;							// if decomposing, the number of steps until the organism will vanish
	private int organicAmount;							// the (average) amount of organic matter collected by this organism (needed for replication)

	/**
	 * Create a new organism.
	 * 
	 * @param state
	 * @param organismMgr
	 */
	public Organism(OrgState state, OrganismMgr organismMgr) {

		this.state = state;
		this.organismMgr = organismMgr;
		cells = new ArrayList<>();
		outerCells = new ArrayList<>();
		number = organismMgr.getOrganismCount();		// the number of this organism, starting with one
	}
	
	/**
	 * Adds a cell to this organism.
	 * This is usally called during creation or replication.
	 * 
	 * @param cell
	 */
	public void add(AbstractCell cell) {
		
		// compute the new outline of the organism
		int col = cell.getColumn();
		if (col < minColumn) {
			minColumn = col;
		}
		if (col > maxColumn) {
			maxColumn = col;
		}
		int row = cell.getRow();
		if (row < minRow) {
			minRow = row;
		}
		if (row > maxRow) {
			maxRow = row;
		}
		
		// TODO  outer Cell computing

		if (cells.size() >= 1) {
			throw new RuntimeException("Organisms with more than 1 cell not implemented (outer Cell computing, etc.)");
		}
		
		cells.add(cell);
		outerCells.add(cell);
	}

	/**
	 * @param energyAmount		the amount of energy to add to the organisms energy
	 * @return the new energy
	 */
	public int addEnergy(int energyAmount) {
		
		props[PROP_ENERGY] += energyAmount;
		return props[PROP_ENERGY];
	}

	/**
	 * Adds a (possible negative) value to the property value.
	 * 
	 * @param propertyIndex
	 * @param value
	 * @return the new value of this property
	 */
	public int addProperty(int propertyIndex, int value) {
		
		props[propertyIndex] += value;
		return props[propertyIndex];
	}

	/**
	 * The state of the oragnism has changed, adjust all and change cell colors if needed.
	 * 
	 * @param newState			the state the organism changed to
	 */
	private void changeToState(OrgState newState) {
		
		if (newState == state) {
			return;						// nothing to do
		}
		lastState = state;
		state = newState;
		System.out.println("Organism " + number + " at (" + getCenterColumn() + "/" + getCenterRow() + ") changed from "
				+ lastState + " to " + state);
		switch (state) {
		case STARVING:
		case DYING:
		case DEAD:
		case DECOMPOSING:
			// display the position
			displayPositionCount = DISPLAY_POSITION_COUNT_DEFAULT;
		case ALIVE:
			displayPositionCount = 0;	// not any longer marked, back to normality
			break;				
		case IN_REPLICATION:
			// display the position, but nothing else to do
			displayPositionCount = DISPLAY_POSITION_COUNT_DEFAULT;
			break;						
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}

	/**
	 * Computethe maximum and minimum of row and column for this organism.
	 */
	public void computeMaxMin() {
		
		minColumn = cells.get(0).getColumn();
		maxColumn = minColumn;
		minRow = cells.get(0).getRow();
		maxRow = minRow;
		for (int i = 1; i < cells.size(); i++) {
			AbstractCell cell = cells.get(i);
			int col = cell.getColumn();
			int row = cell.getRow();
			if (col < minColumn) {
				minColumn = col;
			}
			if (col > maxColumn) {
				maxColumn = col;
			}
			if (row < minRow) {
				minRow = row;
			}
			if (row > maxRow) {
				maxRow = row;
			}
		}
	}

	/**
	 * Organism is dead and/or decomposing: on ground, still sinking, losing energy and all substances.
	 */
	private void deadOrDecomposing() {

		if (state == OrgState.DEAD) {
			// lose energy
			int energy = props[PROP_ENERGY] * 95 / 100;
			props[PROP_ENERGY] = energy;
			cells.forEach(cell -> cell.getProperties()[AbstractCell.PROP_ENERGY] = energy);
			if (energy == 0) {
				changeToState(OrgState.DECOMPOSING);
				decomposeCount = DECOMPOSE_COUNT_DEFAULT;
			}
			return;
		}
		// decompose
		cells.forEach(cell -> cell.decompose());
		decomposeCount--;
		if (decomposeCount <= 0) {
			// save the remaining organic matter
			int organic = 0;
			for (AbstractCell cell : cells) {
				organic += cell.getProperties()[AbstractCell.PROP_ORGANIC];
			}
			Main.getOcean().getOrganismMgr().addToOrganicMatterReservoir(organic);
			// everything useful has been decomposed, the organism should vanish
			organismMgr.remove(this);
		}
	}

	/**
	 * @return the cells
	 */
	public ArrayList<AbstractCell> getCells() {
		
		return cells;
	}

	/**
	 * @return the number of cells belonging to this organism
	 */
	public int getCellCount() {

		return cells.size();
	}

	/**
	 * @return the column of the center of this organism
	 */
	public int getCenterColumn() {
		
		return (maxColumn + minColumn) / 2;
	}

	/**
	 * @return the row of the center of this organism
	 */
	public int getCenterRow() {

		return (maxRow + minRow) / 2;
	}

	/**
	 * @return the x value of the center of this organism
	 */
	public int getCenterX() {

		return maxColumn + minColumn;
	}

	/**
	 * @return the y value of the center of this organism
	 */
	public int getCenterY() {

		return maxRow + minRow;
	}

	/**
	 * @return the maximum dimension of this organism
	 */
	public int getDimensionMax() {
		
		int dimColMax = maxColumn - minColumn + 1;
		int dimRowMax = maxRow - minRow + 1;
		return dimColMax > dimRowMax ? dimColMax : dimRowMax;
	}

	/**
	 * @return the energy of the organism
	 */
	public int getEnergy() {
		
		return props[PROP_ENERGY];
	}

	/**
	 * @return the number of this organism
	 */
	public int getNumber() {
		
		return number;
	}

	/**
	 * @return the organicAmount
	 */
	public int getOrganicAmount() {
		
		return organicAmount;
	}

	/**
	 * @param propertyIndex
	 * @return the property
	 */
	public int getProperty(int propertyIndex) {
		
		return props[propertyIndex];
	}
	
	/**
	 * @return the state
	 */
	public OrgState getState() {
		
		return state;
	}

	/**
	 * Test if the organism has a cell on the given column and row.
	 * 
	 * @param col
	 * @param row
	 * @return true if there is a cell on the given column and row, false otherwise
	 */
	public boolean hasCellOn(int col, int row) {
		
		if (col < minColumn || col > maxColumn || row < minRow || row > maxRow) {
			return false;
		}
		for (AbstractCell cell : cells) {
			if (col == cell.getColumn() && row == cell.getRow()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if there is enough space around this organism, false otherwise.
	 * 
	 * @param distance		the distance of free space around the organism
	 * @return true if there is enough space around this organism, false otherwise
	 */
	public boolean hasFreeSpace(int distance) {

		int colMin, colMax, rowMin, rowMax;
		int cellColumns = Main.getCellColumns();
		int cellRows = Main.getCellRows();
		Ocean ocean =  Main.getOcean();
		for (int i = 1; i <= distance; i++) {
			// north
			colMin = minColumn - i;
			colMin = colMin >= 0 ? colMin : 0;
			colMax = maxColumn + i;
			colMax = colMax < cellColumns ? colMax : cellColumns - 1;
			rowMin = minRow - i;
			rowMin = rowMin >= 0 ? rowMin : 0;
			rowMax = maxRow + i;
			rowMax = rowMax < cellRows ? rowMax : cellRows - 1;
			for (int col = colMin; col <= colMax; col++) {
				if (!ocean.isWater(col, rowMin) || !ocean.isWater(col, rowMax)) {
					return false;
				}
			}
			for (int row = rowMin; row <= rowMax; row++) {
				if (!ocean.isWater(colMin, row) || !ocean.isWater(colMax, row)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true, if the organism is touched, false otherwise.
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @return true, if the organism is touched, false otherwise
	 */
	public boolean isTouched(int left, int right, int top, int bottom) {
		
		for (int col = left; col <= right; col++) {
			for (int row = top; row <= bottom; row++) {
				if (col < minColumn || col > maxColumn || row < minRow || row > maxRow) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Move this organism if it is moveable, either due to speed or Brownian movement.
	 * Rocks will stop the speed, maybe some bouncing will happen.
	 * 
	 * @param time		the current time
	 */
	public void move(long time) {
		
		if (props[PROP_MOVEABLE] == 0) {
			// agglutinated organism, does not move
			return;
		}
		int stepsToGo = 1;
		switch (state) {
		case DECOMPOSING: 
		case DEAD: 
			// sink a little from time to time, or do some Brownian movement
			if (FastRandom.nextIntStat(5) == 0) {
				props[Organism.PROP_DIRECTION] = 180;		// down
			} else {
				Mover.moveAndTurnWithBrownianMovement(this);
			}
			break;
		case IN_REPLICATION: 
//			stepsToGo = 0;									// for simplicity, no movement
//			break;
		default:
			// all other states
			stepsToGo = Mover.moveAndTurnWithBrownianMovement(this);
		}
		for (int step = 0; step < stepsToGo; step++) {
			boolean canMove = Mover.canMoveDueToRocks(this, minColumn, maxColumn, minRow, maxRow);
			if (!canMove) {
				// organism would touch something, therefore it has stopped
				props[PROP_SPEED] = 0;
				break;
			}
			Mover.move(this, 1);
			computeMaxMin();
		}
	}

	/**
	 * Perform one time step of life for this organism.
	 */
	public void oneStepOfLife() {

		if (state == OrgState.DEAD || state == OrgState.DECOMPOSING) {
			return;				// nothing to do
		}
		Pixel pixels[][] = Main.getOcean().getPixels();
		outerCells.forEach(cell -> {
			Pixel p = pixels[cell.getColumn()][cell.getRow()];
			if (!(p instanceof Water)) {
				return;
			}
			cell.adsorbSustances(((Water) p).getSubstances());
		});
	}

	/**
	 * Paint this organism.
	 * 
	 * @param g2d
	 */
	public void paint(Graphics2D g2d) {
		
		cells.forEach(cell -> {
			cell.paint(g2d);
		});
		// possibly we have to draw an arc around the organism to mark it
		if (displayPositionCount != 0) {
			if (state != OrgState.ALIVE) {
				g2d.setColor(state.getColor());
			}
			int x = getCenterX();
			int y =getCenterY();
			g2d.drawArc(x -  OrganismDisplayCtlr.DISPLAY_CROSS_RADIUS + 1, y - OrganismDisplayCtlr.DISPLAY_CROSS_RADIUS + 1, 
					OrganismDisplayCtlr.DISPLAY_CROSS_RADIUS * 2, OrganismDisplayCtlr.DISPLAY_CROSS_RADIUS * 2, 0, 360);
		}
	}

	/**
	 * @param propertyIndex		
	 * @param value
	 */
	public void setProperty(int propertyIndex, int value) {
		
		props[propertyIndex] = value;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(OrgState state) {
		
		this.state = state;
		if (state == OrgState.ALIVE) {
			// if there was an replication, delete it
			replication = null;
		}
	}

	/**
	 * Sets the speed and the direction this organism moves.
	 * 
	 * @param speed
	 * @param directionDegrees		
	 */
	public void setSpeedAndDirection(int speed, int directionDegrees) {
		
		props[PROP_SPEED] = speed;
		props[PROP_DIRECTION] = Mover.adjustDegrees(directionDegrees);
	}

	/**
	 * Slow update for this organism: changes not in the need to be to fast (due to performance reasons).
	 * 
	 * @param time			the current time
	 */
	public void slowUpdate(long time) {
		
		try {
			if (displayPositionCount > 0) {
				// if this organism is marked, it will disappear after a while
				displayPositionCount--;
			}
			// compute the average amount of organic material of this organism
			int sumOrganic = 0;
			for (AbstractCell cell : cells) {
				sumOrganic += cell.getProperties()[AbstractCell.PROP_ORGANIC];
			}
			organicAmount = sumOrganic / cells.size();
			switch (state) {
			case ALIVE:
			case STARVING:
			case DYING:
				changeToState(state);
				break;						// do the usual computation
			case IN_REPLICATION:
				replication.nextStep(time);
				return;
			case DEAD:
			case DECOMPOSING:
				deadOrDecomposing();
				return;
			default:
				throw new IllegalArgumentException("Unexpected value: " + state);
			}
			Ocean ocean = Main.getOcean();
			Pixel pixels[][] = ocean.getPixels();
			// energy
			int sumEnergy = 0;
			for (AbstractCell cell : cells) {
				int cellProps[] = cell.getProperties();
				// if the cell is almost full of energy, some of the energy is passed to the organism
				sumEnergy += cellProps[AbstractCell.PROP_ENERGY];
				cell.slowUpdate(time);
				
				// TODO sunintensity gehört in Algen-Cell (oder in Organism wenn der solche enthält)

//			Pixel p = pixels[cell.getColumn()][cell.getRow()];
//			if (p instanceof Water) {
//				Water water = (Water) p;
//				int sunEnergie = water.getSunIntensity();
//				if (sunEnergie > 0) {
//					sum += sunEnergie * cellProps[AbstractCell.PROP_SUN_BEAM_INCREMENT];
//					ocean.getSunshine().remove(water);
//				}
//			}
				
				
			}
			int energy = sumEnergy / cells.size();
			props[PROP_ENERGY] = energy;
			if (energy < 7000) {
				if (energy < 3000) {
					// dying or dead
					if (energy < 2000) {
						// dead or decomposing
						changeToState(OrgState.DEAD);
						deadOrDecomposing();
					} else {
						// dying
						changeToState(OrgState.DYING);
					}
				} else {
					// starving
					changeToState(OrgState.STARVING);
				}
			} else if (energy > 50000 && organicAmount > 3000) {
				// cell division and replication of the organism -> result: 2 organisms
				changeToState(OrgState.IN_REPLICATION);
				replication = new Replication(this, organismMgr, cells, ocean, pixels, time);
			} else {
				// alive or changed to alive
				if (lastState != OrgState.ALIVE) {
					changeToState(OrgState.ALIVE);
				}
			}
		} catch (Exception e) {
			// something did not work, display information to debug
			e.printStackTrace();
			System.out.println("---> " + this);
			System.exit(1);
		}
	}

	@Override
	public String toString() {
		String s = "\nOrganism [state=" + state + ", minColumn=" + minColumn + ", maxColumn=" + maxColumn + ", minRow="
				+ minRow + ", maxRow=" + maxRow + "] -> cells:";
		for (int i = 0; i < cells.size(); i++) {
			s += "\n\tCell[" + i + "]: " + cells.get(i);
		}
		return s;
	}
}
