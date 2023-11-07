
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
import java.util.*;

import javax.swing.*;

import cellolution.*;

/**
 * Panel to display the state and properties of an organism and its cells.
 */
@SuppressWarnings("serial")
public class OrganismPanel extends JPanel implements ActionListener {

	/** the text for the the button to increase the energy of an organism */
	private static final String BUTTON_INCREASE = "Inc";
	/** the text for the the button to decrease the energy of an organism */
	private static final String BUTTON_DECREASE = "Dec";
	/** the text for the button to cancel following the organism */
	private static final String BUTTON_CANCEL = "Cancel";

	/** XXX */

	/** the ocean */
	/** the number of columns of the ocean */
	/** the number of rows of the ocean */
	/** the current ocean/diffusion simulation step number */
	/** the borders of the ocean: water <-> rock */
	/** all pixels of the ocean */
	/** the manager for all organisms */

	/** set to true if XXX */
	/** true if XXX */


	/** the controller of displaying organisms */
	private OrganismDisplayCtlr organismDisplayCtlr;
	/** an index of the row of labels */
	private int labelRow;
	/** the panel of organism data */
	private JPanel orgDataPanel;
	/** the panel of organism buttons */
	private JPanel buttonPanel;
	/** a label to display the organism */
	private JLabel organismLbl;
	/** a label to display the state of the organism */
	private JLabel orgStateLbl;
	/** a label to display the organic matter of the organism */
	private JLabel organicLbl;
	/** a label to display the number of cells of the organism */
	private JLabel orgCellCountLbl;
	/** a label to display the number of a cell of the organism */
	private JLabel orgCellNumberLbl;
	/** an array of labels to be displayed */
	private ArrayList<JLabel> orgLabelList;
	/** the button to increase the energy of an organism */
	private JButton increaseBtn;
	/** the button to decrease the energy of an organism */
	private JButton decreaseBtn;
	/** the button to cancel following the organism */
	private JButton cancelBtn;

	/**
	 * Create the OrganismPanel.
	 * 
	 * @param organismDisplayCtlr		the controller to display organisms
	 */
	public OrganismPanel(OrganismDisplayCtlr organismDisplayCtlr) {

		this.organismDisplayCtlr = organismDisplayCtlr;
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(240, Main.getCellRows() * 2 - Gbc.getDefaultBorderInset()));
        orgDataPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane (orgDataPanel, 
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       	this.add(scrollPane, BorderLayout.CENTER);
       	// button panel
       	buttonPanel = new JPanel();
      	this.add(buttonPanel, BorderLayout.NORTH);
      	buttonPanel.setLayout(new GridBagLayout());
      	
      	int col = 0;
      	increaseBtn = addButton(BUTTON_INCREASE, col++, 0, "C");
       	decreaseBtn = addButton(BUTTON_DECREASE, col++, 0, "C");
      	cancelBtn = addButton(BUTTON_CANCEL, col++, 0, "C");
      	setEnableButtons(false);
       	// place the components of orgDataPanel
        orgDataPanel.setLayout(new GridBagLayout());
        labelRow = 0;
        organismLbl = addOrgDataLabel("Left click: concentration", 0, labelRow++, "NW tl");
        orgStateLbl = addOrgDataLabel("Right click: organism state", 0, labelRow++, "NW tl");
        organicLbl = addOrgDataLabel("", 0, labelRow++, "NW tl");
        orgCellCountLbl = addOrgDataLabel("", 0, labelRow++, "NW tl");
        orgLabelList = new ArrayList<JLabel>();
        // add a filler at last to squeeze it in the right way
        orgDataPanel.add(Gbc.filler(), new Gbc(1, 100, 1, 1, 1.0, 100.0, "S B"));
 	}

	/**
	 * Action queue dispatcher.
	 * 
	 * @param event			the action event
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCmd = event.getActionCommand();
		if (actionCmd.equals(BUTTON_INCREASE)) {
			organismDisplayCtlr.energyIncrease(true);
		} else if (actionCmd.equals(BUTTON_DECREASE)) {
			organismDisplayCtlr.energyIncrease(false);
		} else if (actionCmd.equals(BUTTON_CANCEL)) {
			organismDisplayCtlr.follow(null);
			clearLabelTexts();
		} else {
            System.out.println("ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName()  + ": " + actionCmd);
		}
	}

	/**
	 * Adds a button with Gbc and specified insets.
	 * 
	 * @param text			the label of the button (and its action command)
	 * @param col			the Gbc column
	 * @param row			the Gbc row
	 * @param control		the control string for Gbc placement
	 * @return the created button
	 * @see Gbc
	 */
	public JButton addButton(String text, int col, int row, String control) {

		Gbc gbc = new Gbc(col, row, 1, 1, 0.0, 0.0, control);
		JButton button = new JButton(text);
		button.addActionListener(this);
		buttonPanel.add(button, gbc);
		return(button);
	}

	/**
	 * Adds a label with Gbc and specified insets.
	 * 
	 * @param text			the text of the label
	 * @param col			the Gbc column
	 * @param row			the Gbc row
	 * @param control		the control string for Gbc placement
	 * @return the created button
	 * @see Gbc
	 */
	public JLabel addOrgDataLabel(String text, int col, int row, String control) {

		Gbc gbc = new Gbc(col, row, 1, 1, 0.0, 0.0, control);
		JLabel label = new JLabel(text);
		orgDataPanel.add(label, gbc);
		return(label);
	}

	/**
	 * Clear the text for the labels.
	 */
	public void clearLabelTexts() {
		
        organismLbl.setText("Left click: concentration");
        orgStateLbl.setText("Right click: organism state");
        organicLbl.setText("");
        orgCellCountLbl.setText("");
        for (JLabel label : orgLabelList) {
        	label.setText("");
		}
	}

	/**
	 * Clear the text for the labels starting with an index.
	 * 
	 * @param index		the index to start
	 */
	public void clearLabelTextsFrom(int index) {
		
		for (int i = index; i < orgLabelList.size(); i++) {
			orgLabelList.get(i).setText("");
		}
	}

	/**
	 * @return the organicLbl
	 */
	public JLabel getOrganicLbl() {
		
		return organicLbl;
	}

	/**
	 * @return the organismLbl
	 */
	public JLabel getOrganismLbl() {
		
		return organismLbl;
	}

	/**
	 * @return the orgCellCountLbl
	 */
	public JLabel getOrgCellCountLbl() {
		
		return orgCellCountLbl;
	}

	/**
	 * @return the orgCellNumberLbl
	 */
	public JLabel getOrgCellNumberLbl() {
		
		return orgCellNumberLbl;
	}

	/**
	 * @return the orgStateLbl
	 */
	public JLabel getOrgStateLbl() {
		
		return orgStateLbl;
	}

	/**
	 * Enables or disables the buttons if there is an organism to follow.
	 *  
	 * @param isEnabled			if true, the button will be enabled, disabled otherwise
	 */
	public void setEnableButtons(boolean isEnabled) {
		
     	increaseBtn.setEnabled(isEnabled);
      	decreaseBtn.setEnabled(isEnabled);
      	cancelBtn.setEnabled(isEnabled);
      	updateUI();
	}

	/**
	 * Sets the text of a label defined by an index stored in a label array list.
	 * If the label at the specified index does not exist, it will be created with Gbc and default insets.
	 * 
	 * @param labelIndex		the index of the label in the label array (zero based)
	 * @param text				the text of the label
	 */
	public void setTextForLabelNumber(int labelIndex, String text) {

		JLabel label = null;
		if (labelIndex < orgLabelList.size()) {
			label = orgLabelList.get(labelIndex);
		} else {
			label = addOrgDataLabel("", 0, labelRow + labelIndex, "NW tl");;
			orgLabelList.add(label);
		}
		label.setText(text);
	}
}
