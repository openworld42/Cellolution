
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
package cellolution.ui;

import java.awt.*;
import java.util.*;

import cellolution.*;
import cellolution.cell.*;

/**
 * Controller to display an organism and its cells.
 */
public class OrganismDisplayCtlr {

	/** the color of the display cross hairs circle */
	private static final Color DISPLAY_CROSS_COLOR = new Color(200, 20, 20);	
	/** the radius cross hairs circle */
	public static final int DISPLAY_CROSS_RADIUS = 14;	
	
	/** the ocean */
	private Ocean ocean;
	/** the panel to display organisms */
	private OrganismPanel organismPanel;
	/** the organism to follow with the cross hairs circle */
	private Organism orgToFollow;
	/** if true, the organism is marked with a cross hairs circle */
	private boolean displayMarker;

	/**
	 * @param ocean			the ocea
	 */
	public OrganismDisplayCtlr(Ocean ocean) {

		this.ocean = ocean;
		organismPanel = new OrganismPanel(this);
	}

	/**
	 * Display the organism to follow, if any.
	 */
	public void displayAndRepaint() {
		
		if (orgToFollow == null) {
			return;
		}
		organismPanel.getOrganismLbl().setText("Organism " + orgToFollow.getNumber() 
			+ " (" + orgToFollow.getCenterColumn() + "/" + orgToFollow.getCenterRow() + ")");
		organismPanel.getOrgStateLbl().setText(orgToFollow.getState() + "    Energy: " + orgToFollow.getProperty(Organism.PROP_ENERGY));
		organismPanel.getOrganicLbl().setText("Organic: " + orgToFollow.getOrganicAmount());
		int cellCount = orgToFollow.getCellCount();
		organismPanel.getOrgCellCountLbl().setText("Cell count: " + cellCount);
		ArrayList<AbstractCell> cells = orgToFollow.getCells();
		int labelIndex = 0;
		for (int i = 0; i < cellCount; i++) {
			AbstractCell cell = cells.get(i);
			// display cell number based on one!
			organismPanel.setTextForLabelNumber(labelIndex++, "Cell: " + (i + 1) + " -> " + cell.getCellTypeName());
			int props[] = cell.getProperties();
			organismPanel.setTextForLabelNumber(labelIndex++, "  Agility: " + props[AbstractCell.PROP_AGILITY]);
			organismPanel.setTextForLabelNumber(labelIndex++, "  Energy: " + props[AbstractCell.PROP_ENERGY]
					+ " (Rate: " + props[AbstractCell.PROP_ENERGY_CONSUMTION] + ")");
			organismPanel.setTextForLabelNumber(labelIndex++, "  CO2: " + props[AbstractCell.PROP_CO2]
					+ " (Rate: " + props[AbstractCell.PROP_CO2_ADSORBTION_RATE] + "/" 
					+ (100 / props[AbstractCell.PROP_CO2_ADSORB_ENERGY]) + "%)");
			organismPanel.setTextForLabelNumber(labelIndex++, "  CaCO3: " + props[AbstractCell.PROP_CaCO3]
					+ " (Rate: " + props[AbstractCell.PROP_CaCO3_ADSORBTION_RATE] + "/" 
					+ (100 / props[AbstractCell.PROP_CaCO3_ADSORB_ENERGY]) + "%)");
			organismPanel.setTextForLabelNumber(labelIndex++, "  H2S: " + props[AbstractCell.PROP_H2S]
					+ " (Rate: " + props[AbstractCell.PROP_H2S_ADSORBTION_RATE] + "/" 
					+ (100 / props[AbstractCell.PROP_H2S_ADSORB_ENERGY]) + "%)");
			organismPanel.setTextForLabelNumber(labelIndex++, "  Organic: " + props[AbstractCell.PROP_ORGANIC]
					+ " (Rate: " + props[AbstractCell.PROP_ORGANIC_ADSORBTION_RATE] + "/" 
					+ (100 / props[AbstractCell.PROP_ORGANIC_ADSORB_ENERGY]) + "%)");
		}
		organismPanel.clearLabelTextsFrom(labelIndex);
	}

	/**
	 * Increase or decrease the energy of the followed organism.
	 * 
	 * @param increase			true to increase or false to decrease the energy of the followed organism
	 */
	public void energyIncrease(boolean increase) {

		int energyDiff = orgToFollow.getEnergy() / 4;
		if (!increase) {
			energyDiff = -energyDiff;
		}
		for (AbstractCell cell : orgToFollow.getCells()) {
			int props[] = cell.getProperties();
			props[AbstractCell.PROP_ENERGY] += energyDiff;
			if (increase && props[AbstractCell.PROP_ORGANIC] < 3000) {
				props[AbstractCell.PROP_ORGANIC] += 1000;
			}
		}
	}

	/**
	 * Follow an organism, display its properties and mark it.
	 * 
	 * @param organism		the organism
	 */
	public void follow(Organism organism) {
		
		this.orgToFollow = organism;
		displayMarker = organism != null;
		organismPanel.clearLabelTexts();
		organismPanel.setEnableButtons(organism != null);
		organismPanel.repaint();
	}

	/**
	 * @return the organismPanel
	 */
	public OrganismPanel getOrganismPanel() {
		
		return organismPanel;
	}

	/**
	 * Display a circle around an organism to follow, if any.
	 * 
	 * @param g2d 		the Graphics2D object
	 */
	public void paintDisplayOrgArc(Graphics2D g2d) {
		
		if (!displayMarker || orgToFollow == null) {
			return;
		}
		g2d.setColor(DISPLAY_CROSS_COLOR);
		int x = orgToFollow.getCenterX();
		int y = orgToFollow.getCenterY();
		g2d.drawArc(x - DISPLAY_CROSS_RADIUS + 1, y - DISPLAY_CROSS_RADIUS + 1, 
				DISPLAY_CROSS_RADIUS * 2, DISPLAY_CROSS_RADIUS * 2, 0, 360);
	}
}
