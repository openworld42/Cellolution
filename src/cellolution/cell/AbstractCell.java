
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
import cellolution.util.*;

/**
 * Abstract class of all cells.
 * All cells belong to an organism and have a color, which may change.
 */
public abstract class AbstractCell extends Pixel {

	/** an interpolation of saturation values */
	public static final Interpolation SATURATION_100 = new Interpolation(new float[] {0, 0, 50, 40, 80, 60, 10000, 100});
	/** an interpolation of diffusion values */
	public static final Interpolation DIFFUSION_100 = new Interpolation(new float[] {0, 0, 50, 15, 80, 30, 1000, 50});
	
	/** the value for an agility of "1" -> as "standard" */
	public static final int AGILITY_FACTOR_ONE = 10000;

	/** index of props[]: the energy of the cell */
	public static final int PROP_ENERGY 				= 0;
	/** index of props[]: the amount of energy consumed to live a period */
	public static final int PROP_ENERGY_CONSUMTION		= 1;
	/** index of props[]: the amount of energy generated when hit by a sun beam */
	public static final int PROP_SUN_BEAM_INCREMENT		= 2;
	/** index of props[]: the amount of H2S to generate an energy amount: none, if zero */
	public static final int PROP_H2S_TO_ENERGY			= 3;
	/** index of props[]: weight (water: 10000) */
	public static final int PROP_WEIGHT 				= 4;
	/** index of props[]: the agility of the cell, "standard" is 10000, usually modified through the genome/replication */
	public static final int PROP_AGILITY 				= 5;
	
	/** index of props[]: the amount of it within the cell */
	public static final int PROP_CO2 					= 8;
	/** index of props[]: the rate of adsorption */
	public static final int PROP_CO2_ADSORBTION_RATE	= 9;
	/** index of props[]: energy consumption when adsorbing, e.g. 5: => 1/5 = 20% (5..10 => 20%-10%) */
	public static final int PROP_CO2_ADSORB_ENERGY		= 10;
	
	/** index of props[]: the amount of it within the cell */
	public static final int PROP_CaCO3 					= 11;
	/** index of props[]: the rate of adsorption */
	public static final int PROP_CaCO3_ADSORBTION_RATE	= 12; 
	/** index of props[]: energy consumption when adsorbing, e.g. 5: => 1/5 = 20% (5..10 => 20%-10%) */
	public static final int PROP_CaCO3_ADSORB_ENERGY	= 13;
	
	/** index of props[]: the amount of it within the cell */
	public static final int PROP_H2S 					= 14;
	/** index of props[]: the rate of adsorption */
	public static final int PROP_H2S_ADSORBTION_RATE	= 15;	// 
	/** index of props[]: energy consumption when adsorbing, e.g. 5: => 1/5 = 20% (5..10 => 20%-10%) */
	public static final int PROP_H2S_ADSORB_ENERGY		= 16;

	/** index of props[]: the amount of it within the cell */
	public static final int PROP_ORGANIC 				= 17;
	/** index of props[]: the rate of adsorption */
	public static final int PROP_ORGANIC_ADSORBTION_RATE = 18;	//	
	/** index of props[]: energy consumption when adsorbing, e.g. 5: => 1/5 = 20% (5..10 => 20%-10%) */
	public static final int PROP_ORGANIC_ADSORB_ENERGY	= 19;
	
	/** the size of the properties */
	public static final int SIZE_OF_PROPS = PROP_ORGANIC_ADSORB_ENERGY + 1;
	/** the properties of the cell */
	protected int props[] = new int[SIZE_OF_PROPS];
	
	/** a reference to the organism this cell belongs to */
	protected Organism organism;
	/** the current color of the cell */
	protected Color color;
	/** the current color of the cell, as RGB value */
	private int colorRGB;
	
	/**
	 * Construction of a cell.
	 * 
	 * @param column		the column of the cell within the ocean
	 * @param row			the row of the cell within the ocean
	 * @param energy 		the energy of this cell
	 * @param color			the color to display the cell
	 * @param organism		the organism the cell belongs to
	 */
	protected AbstractCell(int column, int row, int energy, Color color, Organism organism) {
		
		super(column, row);
		this.color = color;
		colorRGB = color.getRGB();
		this.organism = organism;
		props[PROP_ENERGY] = energy;
		adjustColorByEnergy();								// superclass will do that
		props[PROP_AGILITY] = AGILITY_FACTOR_ONE;			// usually modified through the genome/replication
	}
	
	/**
	 * Fill in cell type specific JSON values of a former simulation.
	 * 
	 * @param jsonCell			the JSONObject to fill in the values
	 */
	public abstract void addFrom(JSONObject jsonCell);

	/**
	 * Change the color according to the cell's energy.
	 */
	public abstract void adjustColorByEnergy();

	/**
	 * Adsorb some substances of the underlying water pixel.
	 * In general, substances are within the range of [0..100].
	 * 
	 * @param substances 		substances to adsorb
	 */
	public void adsorbSustances(byte substances[]) {

		// water substances in general: 0..100
		int value = substances[Water.CO2] * props[PROP_CO2_ADSORBTION_RATE] / 100;	
		value = props[PROP_CO2] + value > 10000 ? 10000 - props[PROP_CO2] : value;
		substances[Water.CO2] -= value;
		props[PROP_CO2] += value;
		props[PROP_ENERGY] -= value / props[PROP_CO2_ADSORB_ENERGY];	// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		value = substances[Water.CaCO3] * props[PROP_CaCO3_ADSORBTION_RATE] / 100;
		value = props[PROP_CaCO3] + value > 10000 ? 10000 - props[PROP_CaCO3] : value;
		substances[Water.CaCO3] -= value;
		props[PROP_CaCO3] += value;
		props[PROP_ENERGY] -= value / props[PROP_CaCO3_ADSORB_ENERGY];	// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		value = substances[Water.H2S] * props[PROP_H2S_ADSORBTION_RATE] / 100;
		value = props[PROP_H2S] + value > 10000 ? 10000 - props[PROP_H2S] : value;
		substances[Water.H2S] -= value;
		props[PROP_H2S] += value;
		props[PROP_ENERGY] -= value / props[PROP_H2S_ADSORB_ENERGY];	// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
		value = substances[Water.ORGANIC] * props[PROP_ORGANIC_ADSORBTION_RATE] / 100;
		value = props[PROP_ORGANIC] + value > 10000 ? 10000 - props[PROP_ORGANIC] : value;
		substances[Water.ORGANIC] -= value;
		props[PROP_ORGANIC] += value;
		props[PROP_ENERGY] -= value / props[PROP_ORGANIC_ADSORB_ENERGY];// energy consumption when adsorbing, e.g. 5: => 1/5 = 20%
	}

	/**
	 * @return a clone of this cell
	 */
	public abstract AbstractCell cloneCell();

	/**
	 * Intentionally does nothing.
	 * 
	 * @param water		the water pixel
	 */
	@Override
	public void copyFrom(Water water) {}

	/**
	 * Copy all properties and other attributes from another cell (for replication).
	 * 
	 * @param otherCell		the other cell
	 */
	protected void copyAttributes(AbstractCell otherCell) {
		
		int otherProps[] = otherCell.getProperties();
		for (int i = 0; i < otherProps.length; i++) {
			props[i] = otherProps[i];
		}
		setColorAndRGB(otherCell.getColor());
	}

	/**
	 * Creates a cell from a JSONObject representation.
	 * 
	 * @param jsonCell			the JSONObject
	 * @param organism 			the organism the cell belongs to
	 * @return the cell
	 */
	public static AbstractCell createFrom(JSONObject jsonCell, Organism organism) {
		
		Genome genome = null;
		if (jsonCell.has(Keys.GENOME)) {
			genome = Genome.createFrom(jsonCell.getJSONObject(Keys.GENOME));
		}
		String className = jsonCell.getString(Keys.CELL);
		int col = jsonCell.getInt(Keys.COLUMN);
		int row = jsonCell.getInt(Keys.ROW);
		int energy = jsonCell.getInt(Keys.ENERGY); 
		int colorRGB = JsonUtil.toColorRGBFrom(jsonCell);
		// we can use java.lang.reflect here, but possibly we need to make individual decisions
		AbstractCell cell = null;
		switch (className) {
		case SingleAlgaeCell.CLASS_NAME: 
			cell = new SingleAlgaeCell(col, row, energy, organism, genome);
			break;
		case SingleH2sEaterCell.CLASS_NAME: 
			cell = new SingleH2sEaterCell(col, row, energy, organism, genome);
			break;
		case StemCell.CLASS_NAME: 
			cell = new StemCell(col, row, energy, organism, genome);
			break;
		default:
			throw new IllegalArgumentException("Unexpected cell name: " + className);
		}
		cell.props[PROP_ENERGY_CONSUMTION] = jsonCell.getInt(Keys.ENERGY_CONSUMTION);
		cell.props[PROP_SUN_BEAM_INCREMENT] = jsonCell.getInt(Keys.SUN_BEAM_INCREMENT);
		cell.props[PROP_H2S_TO_ENERGY] = jsonCell.getInt(Keys.H2S_TO_ENERGY);
		cell.props[PROP_WEIGHT] = jsonCell.getInt(Keys.WEIGHT);
		cell.props[PROP_AGILITY] = jsonCell.getInt(Keys.AGILITY);
		// substances
		cell.props[PROP_CO2] = jsonCell.getInt(Keys.CO2);
		cell.props[PROP_CO2_ADSORBTION_RATE] = jsonCell.getInt(Keys.CO2_ADSORBTION_RATE);
		cell.props[PROP_CO2_ADSORB_ENERGY] = jsonCell.getInt(Keys.CO2_ADSORB_ENERGY);
		cell.props[PROP_CaCO3] = jsonCell.getInt(Keys.CaCO3);
		cell.props[PROP_CaCO3_ADSORBTION_RATE] = jsonCell.getInt(Keys.CaCO3_ADSORBTION_RATE);
		cell.props[PROP_CaCO3_ADSORB_ENERGY] = jsonCell.getInt(Keys.CaCO3_ADSORB_ENERGY);
		cell.props[PROP_H2S] = jsonCell.getInt(Keys.H2S);
		cell.props[PROP_H2S_ADSORBTION_RATE] = jsonCell.getInt(Keys.H2S_ADSORBTION_RATE);
		cell.props[PROP_H2S_ADSORB_ENERGY] = jsonCell.getInt(Keys.H2S_ADSORB_ENERGY);
		cell.props[PROP_ORGANIC] = jsonCell.getInt(Keys.ORGANIC);
		cell.props[PROP_ORGANIC_ADSORBTION_RATE] = jsonCell.getInt(Keys.ORGANIC_ADSORBTION_RATE);
		cell.props[PROP_ORGANIC_ADSORB_ENERGY] = jsonCell.getInt(Keys.ORGANIC_ADSORB_ENERGY);
		// cell type specific settings, if any
		cell.addFrom(jsonCell);
		return cell;
	}

	/**
	 * Creates a key for this cell, using its coordinates.
	 * 
	 * @return the key
	 */
	public int createKey() {
		
		return createKey(column, row);
	}
	
	/**
	 * Creates a key for a cell, using coordinates.
	 * 
	 * @param column		the column of the cell within the ocean
	 * @param row		the row of the cell within the ocean
	 * @return the key
	 */
	public static int createKey(int column, int row) {
		
		return column * 10000 + row;
	}

	/**
	 * Organism and its cells are dead and decomposing, lose substances etc.
	 */
	public void decompose() {
		
		// water substances in general: 0..100
		Ocean ocean = Main.getOcean();
		Pixel pixel[][] = ocean.getPixels();
		Pixel p = pixel[column][row];
		if (!(p instanceof Water)) {
			props[PROP_CO2] = props[PROP_CO2] * 95 / 100;
			props[PROP_CaCO3] = props[PROP_CO2] * 95 / 100;
			props[PROP_H2S] = props[PROP_H2S] * 95 / 100;
			int organic = props[PROP_ORGANIC];
			props[PROP_ORGANIC] = props[PROP_ORGANIC] * 95 / 100;
			ocean.addToOrganicMatterReservoir(organic - props[PROP_ORGANIC]);
			return;
		}
		Water water = (Water) p;
		byte substances[] = water.getSubstances();
		int diff = props[PROP_CO2] * 95 / 100;
		diff = substances[Water.CO2] + diff > 100 ? 100 - substances[Water.CO2] : diff;
		substances[Water.CO2] += diff;
		props[PROP_CO2] -= diff;
		//
		diff = props[PROP_CaCO3] * 95 / 100;
		diff = substances[Water.CaCO3] + diff > 100 ? 100 - substances[Water.CaCO3] : diff;
		substances[Water.CaCO3] += diff;
		props[PROP_CaCO3] -= diff;
		//
		diff = props[PROP_H2S] * 95 / 100;
		diff = substances[Water.H2S] + diff > 100 ? 100 - substances[Water.H2S] : diff;
		substances[Water.H2S] += diff;
		props[PROP_H2S] -= diff;
		//
		diff = props[PROP_ORGANIC] * 90 / 100;
		diff = substances[Water.ORGANIC] + diff > 100 ? 100 - substances[Water.ORGANIC] : diff;
		substances[Water.ORGANIC] += diff;
		props[PROP_ORGANIC] -= diff;
	}

	/**
	 * @return the cell type name
	 */
	public abstract String getCellTypeName();

	/**
	 * @return the color
	 */
	public Color getColor() {
		
		return color;
	}
	
	/**
	 * @return the RGB value of the cells color
	 */
	public int getColorRGB() {
		
		return colorRGB;
	}

	/**
	 * @return the organism to to which the cell belongs
	 */
	public Organism getOrganism() {

		return organism;
	}

	/**
	 * @return the properties of the cell
	 */
	public int[] getProperties() {

		return props;
	}

	/**
	 * Paint the cell.
	 * 
	 * @param g2d	the Graphics2D object
	 */
	protected void paint(Graphics2D g2d) {
		
		OrgState state = organism.getState();
		switch (state) {
		case STARVING: 
		case DYING: 
			Color stateColor = state.getColor();
			g2d.setColor(new Color((color.getRed() + stateColor.getRed()) / 2,
					(color.getBlue() + stateColor.getBlue()) / 2,
					(color.getGreen() + stateColor.getGreen()) / 2));
			break;
		case DEAD: 
		case DECOMPOSING: 
			g2d.setColor(state.getColor());
			break;
		default:
			g2d.setColor(color);
		}
		g2d.fillRect(column << 1, row << 1, 3, 3);
	}

	/**
	 * @param color the color to set
	 */
	public void setColorAndRGB(Color color) {
		
		this.color = color;
		colorRGB = color.getRGB();
	}

	/**
	 * Sets the coordinates.
	 * 
	 * @param newCol		the new column of the cell within the ocean
	 * @param newRow		the new row of the cell within the ocean
	 */
	public void setColRow(int newCol, int newRow) { 

		column = (short) newCol;
		row = (short) newRow;
	}

	/**
	 * Sets the genome of the cell (usually of a single cell organism or a stem cell).
	 * 
	 * @param genome		the genome of the cell
	 */
	public abstract void setGenome(Genome genome);

	/**
	 * Sets the organism of the cell (usually during the replication process).
	 * 
	 * @param organism		the organism to which the cell belongs
	 */
	public void setOrganism(Organism organism) {

		this.organism = organism;
	}

	/**
	 * Slow update for organism changes not needed to be to fast.
	 * 
	 * @param time		the current time
	 */
	protected abstract void slowUpdate(long time);

	@Override
	public String toString() {
		return "Cell [type=" + this.getClass().getSimpleName() + ", Column=" + column + ", Row=" + row + "]";
	}

	/**
	 * Creates a JSONObject from this object.
	 * 
	 * @return the JSONObject containing the data of this object
	 */
	public abstract JSONObject toJSONObject();

	/**
	 * Adds the data of the AbstractCell to JSONObject.
	 * Cells extending AbstractCell do the same by overwriting toJSONObject().
	 * 
	 * @param jsonObject	the JSONObject to add the data	
	 */
	public void toJSONObject(JSONObject jsonObject) {

		jsonObject.put(Keys.CELL, this.getClass().getSimpleName());
		JsonUtil.addColRowTo(jsonObject, column, row);
		JsonUtil.addColorTo(jsonObject, colorRGB);
		jsonObject.put(Keys.ENERGY, props[PROP_ENERGY]);
		jsonObject.put(Keys.ENERGY_CONSUMTION, props[PROP_ENERGY_CONSUMTION]);
		jsonObject.put(Keys.SUN_BEAM_INCREMENT, props[PROP_SUN_BEAM_INCREMENT]);
		jsonObject.put(Keys.H2S_TO_ENERGY, props[PROP_H2S_TO_ENERGY]);
		jsonObject.put(Keys.WEIGHT, props[PROP_WEIGHT]);
		jsonObject.put(Keys.AGILITY, props[PROP_AGILITY]);
		jsonObject.put(Keys.CO2, props[PROP_CO2]);
		jsonObject.put(Keys.CO2_ADSORBTION_RATE, props[PROP_CO2_ADSORBTION_RATE]);
		jsonObject.put(Keys.CO2_ADSORB_ENERGY, props[PROP_CO2_ADSORB_ENERGY]);
		jsonObject.put(Keys.CaCO3, props[PROP_CaCO3]);
		jsonObject.put(Keys.CaCO3_ADSORBTION_RATE, props[PROP_CaCO3_ADSORBTION_RATE]);
		jsonObject.put(Keys.CaCO3_ADSORB_ENERGY, props[PROP_CaCO3_ADSORB_ENERGY]);
		jsonObject.put(Keys.H2S, props[PROP_H2S]);
		jsonObject.put(Keys.H2S_ADSORBTION_RATE, props[PROP_H2S_ADSORBTION_RATE]);
		jsonObject.put(Keys.H2S_ADSORB_ENERGY, props[PROP_H2S_ADSORB_ENERGY]);
		jsonObject.put(Keys.ORGANIC, props[PROP_ORGANIC]);
		jsonObject.put(Keys.ORGANIC_ADSORBTION_RATE, props[PROP_ORGANIC_ADSORBTION_RATE]);
		jsonObject.put(Keys.ORGANIC_ADSORB_ENERGY, props[PROP_ORGANIC_ADSORB_ENERGY]);
	}
}
