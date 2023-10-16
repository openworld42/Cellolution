
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

import javax.swing.*;

import cellolution.*;

/**
 * Utility class for the GUI (Graphical User Interface), supporting the project's GUI with several static methods.
 */
public class Gui {
	
	public static final int COMP_HEIGHT = 21;		// the default component height within the GUI
	public static final int CHAR_WIDTH = 15;		// an assumed character width in a text component (for width estimations)

	/**
	 * Deny external construction.
	 */
	private Gui() {

	}

	/**
     * Centers a window (JFrame, JDialog, etc.) on the screen.
     *
     * @param window 		the window object (JFrame, JDialog, etc.) to center
	 */
	public static void center(Window window) {

		Dimension screenSize = window.getToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		screenSize.width -= windowSize.width;
		screenSize.height -= windowSize.height;
		window.setLocation(screenSize.width / 2, screenSize.height / 2);
	}
	
	/**
	 * Convenience method: show an error dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void errorDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Convenience method: show an info dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void infoDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Convenience method: show a warning dialog.
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 */
	public static void warnDlg(Component parent, String message, String title) {
		
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
