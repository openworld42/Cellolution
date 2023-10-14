
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
package template.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * A JDialog specimen.
 * <p>
 */
public class Dialog extends JDialog {

	private JFrame parentFrame;

	/**
	 * Create the dialog and show it.
	 * 
	 * @param parentFrame
	 */
	public Dialog(JFrame parentFrame) {

		this.parentFrame = parentFrame;
		setTitle("my dialog");
		ImageIcon icon = new ImageIcon("src/images/size24x24/applications-utilities.png");
		setIconImage(icon.getImage());
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// create a panel for the components
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 350));
		getContentPane().add(panel, BorderLayout.CENTER);
		// Widgets
		panel.setLayout(new GridBagLayout());
		// add components
		int row = 0;
		JLabel label = new JLabel("Text:");
		panel.add(label, new Gbc(0, row));
		JTextField textField = new JTextField();
		panel.add(textField, new Gbc(1, row, "W"));
		textField.setPreferredSize(new Dimension(Gui.CHAR_WIDTH * 2, Gui.COMP_HEIGHT));
		textField.setText("1");
		textField.addFocusListener(new TxtFocusListener());
		row++;
		// row 1
		label = new JLabel(" ");
		panel.add(label, new Gbc(0, row++, "W"));		// spacer, to set the cancel button below the text field (row 2)
		// row 2
		JButton cancelBtn = new JButton("Abbrechen");
		panel.add(cancelBtn, new Gbc(1, row++));
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		pack();
		Gui.center(this);
		setVisible(true);
	}

	//   ************************   inner classes   *************************
	
	/**
	 * Default FocusListener for text components.
	 */
	public class TxtFocusListener extends FocusAdapter {
	
		public void focusGained(FocusEvent e) {
			
			((JTextField) e.getSource()).selectAll();
		}
	}

}
