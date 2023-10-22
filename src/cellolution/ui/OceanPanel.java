
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
package cellolution.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import cellolution.*;
import cellolution.cell.*;

/**
 * Drawing the ocean.
 */
public class OceanPanel extends JPanel {

	private Ocean ocean;
	private int cellColumns;
	private int cellRows;
	private Pixel pixels[][];
	private Smokers smokers;
	private OrganismMgr organismMgr;
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
	 * @param ocean
	 * @param smokers 
	 * @param organismMgr 
	 * @param orgDisplayCtlr 
	 */
	public void set(Ocean ocean, Smokers smokers, OrganismMgr organismMgr, OrganismDisplayCtlr orgDisplayCtlr) {
		
		this.ocean = ocean;
		pixels = ocean.getPixels();
		this.smokers = smokers;
		this.organismMgr = organismMgr;
		this.orgDisplayCtlr = orgDisplayCtlr;
	}  
}
