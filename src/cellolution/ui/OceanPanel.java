
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
import java.awt.event.*;

import javax.swing.*;

import cellolution.*;
import cellolution.cell.*;

/**
 * Panel to draw the ocean.
 */
@SuppressWarnings("serial")
public class OceanPanel extends JPanel {

	/** the ocean */
	private Ocean ocean;
	/** the number of columns of the ocean */
	private int cellColumns;
	/** the number of rows of the ocean */
	private int cellRows;
	/** all pixels of the ocean */
	private Pixel pixels[][];
	/** the manager of the black smokers */
	private Smokers smokers;
	/** the manager for all organisms */
	private OrganismMgr organismMgr;
	/** the controller to display organisms */
	private OrganismDisplayCtlr orgDisplayCtlr;

	/**
	 * Construct the view of the ocean.
	 * 
	 * @param cellRows 		number of rows for cells
	 * @param cellColumns 	number of columns for cells
	 */
	public OceanPanel(int cellColumns, int cellRows) {

		super();
		this.cellColumns = cellColumns;
		this.cellRows = cellRows;
		setDoubleBuffered(true);
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseReleased(MouseEvent e) {
		    	ocean.mouseReleased(e);
		    }
		});
	}
	
    /**
     * Paint the component.
     * 
 	 * @param g 		the Graphics object
    */
    public void paintComponent(Graphics g) {
    	
    	super.paintComponent(g);
    	// draw the elements
        Graphics2D g2d = (Graphics2D) g;
        Ocean ocean = Main.getOcean();
        g2d.drawImage(ocean.getImage(), null, 0, 0);
        if (smokers == null) {
			// most likely a new ocean in progress, smokers have to be created again
//        	System.out.println("smokers == null");
        	return;
		}
		smokers.paint(g2d);
		organismMgr.paint(g2d);
		orgDisplayCtlr.paintDisplayOrgArc(g2d);
    }
  
	/**
	 * @param ocean				the ocean
	 * @param smokers 			the manager of the smokers
	 * @param organismMgr 		the manager of the 			the manager of the smokers
	 * @param orgDisplayCtlr 	the controller to display organisms
	 */
	public void set(Ocean ocean, Smokers smokers, OrganismMgr organismMgr, OrganismDisplayCtlr orgDisplayCtlr) {
		
		this.ocean = ocean;
		pixels = ocean.getPixels();
		this.smokers = smokers;
		this.organismMgr = organismMgr;
		this.orgDisplayCtlr = orgDisplayCtlr;
	}  
}
