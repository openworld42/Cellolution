
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
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import cellolution.*;
import cellolution.util.*;

/**
 * The main view of this application.
 */
@SuppressWarnings("serial")
public class MainView extends JFrame implements ActionListener {

	// constants

	/** action command key */
	public final static String EXIT = 			"Exit";
	/** action command key */
	public final static String NEW_OCEAN = 		"NewOcean";
	/** action command key */
	public final static String NEW_OCEAN_SINGLE = "NewOceanSingleCreatures";
	/** action command key */
	public final static String OPEN_FILE = 		"OpenFile";
	/** action command key */
	public final static String PAUSE_OR_RUN = 	"PauseOrRun";
	/** action command key */
	public final static String PAUSE = 			"Pause Sim";
	/** action command key */
	public final static String RECENT_FILE = 	"RecentFile.";
	/** action command key */
	public final static String RUN = 			"Run Sim";
	/** action command key */
	public final static String SAVE_AS = 		"SaveAs";

	// members
	/** the main panel */
	private JPanel mainPanel;
	/** the ocean panel */
	private OceanPanel oceanPanel;
	/** the organism panel */
	private OrganismPanel organismPanel;
	/** the tool bar */
	private JToolBar toolBar;
	/** the paused-or-run buttomn */
	private JButton pausedOrRunBtn;
	/** the status bar */
	private JToolBar statusBar;
	/** the label to display the status within the status bar */
	private JLabel statusLbl;
	/** the exit button */
	private JButton exitBtn;
	/** the menu containing the recently opened files */
	private JMenu menuRecentFiles;
	/** if true, the application is paused */
	private boolean isPaused;

	/**
	 * Construct main view of an application.
	 * 
	 * @param organismPanel 		the organism panel
	 * @throws Exception in case of an unexpected exception
	 */
	public MainView(OrganismPanel organismPanel) throws Exception {

		super(Main.APP_NAME);
		this.organismPanel = organismPanel;
		URL imageURL = Main.class.getResource(Main.APP_ICON_IMG);
		try {
			BufferedImage iconImage = ImageIO.read(imageURL);
			setIconImage(iconImage);
		} catch (Exception e) { // intentionally falling through, no image displayed
		}
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                // delegate to enable subclass to overwrite
            	MainView.this.dispose();
            	SwingUtilities.invokeLater(() -> Main.instance().onExit());
            }
            public void windowOpened(WindowEvent event) {
             	Main.getOcean().startSwingWorker(oceanPanel);
            }});
 		initFrame();
		pack();
		Gui.center(this);
		Util.sleep(10);
//		setResizable(false);
		setVisible(true);
	}

	/**
	 * Action queue dispatcher.
	 * 
	 * @param event 		the action event
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCmd = event.getActionCommand();
		if (actionCmd.equals(EXIT)) {
            dispose();
		} else if (actionCmd.equals(NEW_OCEAN)) {
			Main.instance().newOcean(true);
		} else if (actionCmd.equals(NEW_OCEAN_SINGLE)) {
			Main.instance().newOcean(false);
		} else if (actionCmd.equals(OPEN_FILE)) {
			FileChooserDlg dlg = new FileChooserDlg("Choose file", 
					JFileChooser.FILES_ONLY, System.getProperty("user.dir"), null);
			int retVal = dlg.showOpenDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				String path = dlg.getSelectedFile().toString();
				SwingUtilities.invokeLater(() -> Main.instance().newOceanFromFile(path));
			}
		} else if (actionCmd.equals(PAUSE_OR_RUN)) {
			Main.getOcean();
			if (isPaused) {
				pausedOrRunBtn.setText(PAUSE);
				isPaused = false;
				Main.getOcean().setSwingWorkerPaused(false);
			} else {
				pausedOrRunBtn.setText(RUN);
				isPaused = true;
				Main.getOcean().setSwingWorkerPaused(true);
			}
		} else if (actionCmd.startsWith(RECENT_FILE)) {
			Component[] components = menuRecentFiles.getMenuComponents();
			for (Component component : components) {
				JMenuItem menuItem = (JMenuItem) component;
				if (menuItem.getActionCommand().equals(actionCmd)) {
					// found
					String fileName = menuItem.getText();
	            	SwingUtilities.invokeLater(() -> Main.instance().newOceanFromFile(fileName));
					break;
				}
			}
		} else if (actionCmd.equals(SAVE_AS)) {
			Main.instance().saveAs();
        } else {
            System.out.println("ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName() 
            		+ ": " + actionCmd);
		}
	}

	/**
	 * Adds a button with Gbc and specified insets.
	 * 
	 * @param name					the label of the button (and its action command)
	 * @param col					the column (Gbc) for the component
	 * @param row					the row (Gbc) for the component
	 * @param control				a string to control the placement
	 * @param insetTop				an inset
	 * @param insetLeft				an inset
	 * @param insetBottom			an inset
	 * @param insetRight			an inset
	 * @return the created button
	 * @see Gbc
	 */
	public JButton addButton(String name, int col, int row, String control, 
			int insetTop, int insetLeft, int insetBottom, int insetRight) {

		Gbc gbc = new Gbc(col, row, 1, 1, 0.0, 0.0, control, new Insets(insetTop, insetLeft, insetBottom, insetRight));
		JButton button = new JButton(name);
		button.addActionListener(this);
		oceanPanel.add(button, gbc);
		return(button);
	}

	/**
	 * Adds a button with Gbc and specified insets.
	 * 
	 * @param name					the label of the button (and its action command)
	 * @param col					the column (Gbc) for the component
	 * @param row					the row (Gbc) for the component
	 * @param control				a string to control the placement
	 * @return the created button
	 * @see Gbc
	 */
	public JButton addButton(String name, int col, int row, String control) {

		Gbc gbc = new Gbc(col, row, 1, 1, 0.0, 0.0, control);
		JButton button = new JButton(name);
		button.addActionListener(this);
		oceanPanel.add(button, gbc);
		return(button);
	}

	/**
	 * Creates a JMenuBar for this view.
	 * 
	 * @return the JMenuBar
	 */
	public JMenuBar createMenu() {

		// menu "File"
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = createMenuItem("New Ocean", true, NEW_OCEAN);
		menu.add(menuItem);
		menuItem = createMenuItem("New Ocean (single creatures)", true, NEW_OCEAN_SINGLE);
		menu.add(menuItem);
		menuItem = createMenuItem("Open File", true, OPEN_FILE);
		menu.add(menuItem);
		menuItem = createMenuItem("Save As ...", true, SAVE_AS);
		menu.add(menuItem);
		menuRecentFiles = new JMenu("Recent Files");
		updateRecentFiles();
		menu.add(menuRecentFiles);
		menu.addSeparator();
		menuItem = createMenuItem(EXIT, true, EXIT);
		menu.add(menuItem);
		// menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		return menuBar;
    }

	/**
	 * Create a menu item.
	 * 
	 * @param text					the text of the menu item
	 * @param enabled				true if the item is enabled, false otherwise
	 * @param actionCmd				the action command for the event, if clicked
	 * @return the menu item
	 */
	private JMenuItem createMenuItem(String text, boolean enabled, String actionCmd) {
		
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.setEnabled(enabled);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(actionCmd);
		return menuItem;
	}

	/**
	 * Creates a JToolBar as a status bar for this view.
	 * 
	 * @return the status bar
	 */
	protected JToolBar createStatusBar() {
		
		JToolBar statusBar = new JToolBar();
		statusBar.setFloatable(false);
		statusLbl = new JLabel();
		statusLbl.setText("Starting ...");;
		statusBar.add(statusLbl);
		statusBar.addSeparator();
		return statusBar;
	}

	/**
	 * Creates a JToolBar for this view.
	 * 
	 * @return the JToolBar
	 */
	protected JToolBar createToolBar() {
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		
		JButton btn = createToolBarButton("New Ocean", null, KeyEvent.VK_N, 
				"Dump the current ocean, start a new one", NEW_OCEAN);
//		btn.setEnabled(false);
		tb.add(btn);
		tb.addSeparator();
		pausedOrRunBtn = createToolBarButton("Pause Sim",  null, KeyEvent.VK_P, 
				"Pauses or Runs the simulation", PAUSE_OR_RUN);
		tb.add(pausedOrRunBtn);
		return tb;
	}
	
	/**
	 * Creates a toolbar button.
	 * 
	 * @param label					the label of the button
	 * @param icon					the icon of the button or <code>null</code>
	 * @param mnemonic				the mnemonic of the button
	 * @param toolTip				the toolTip of the button
	 * @param actionCommand			the action command of the button
	 * @return the tool bar button
	 */
	protected JButton createToolBarButton(String label, ImageIcon icon, int mnemonic,
		String toolTip, String actionCommand) {
		
		JButton button = new JButton(label);
		if (icon != null) {
			button.setIcon(icon);
		}
		if (mnemonic != 0) {
			button.setMnemonic(mnemonic);
		}
	    button.setToolTipText(toolTip);
		button.setActionCommand(actionCommand);
	    button.addActionListener(this);
		return button;
	}

	/**
	 * GUI init.
	 * 
	 * @throws Exception on unexpected exceptions
	 */
	private void initFrame() throws Exception {
		
 		mainPanel = new JPanel();
 		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
        // menu
		JMenuBar menuBar = createMenu();
        setJMenuBar(menuBar);

        // key input
//        addKeyListener(new ViewMainKeyListener());
		
		// images
		//URL iconURL = ClassLoader.getSystemResource("at/mypackage/gui/Main.gif");
		//JLabel iconLabel = new JLabel(new ImageIcon(iconURL));
 
		toolBar = createToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		statusBar = createStatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		oceanPanel = new OceanPanel(Main.getCellColumns(), Main.getCellRows());
		oceanPanel.setPreferredSize(new Dimension(Main.getCellColumns() * 2, Main.getCellRows() * 2));
//      centerPanel.setPreferredSize(new Dimension(
//      	AppCtx.getIntProperty("main.window.size.x"),
//      	AppCtx.getIntProperty("main.window.size.y")));
		mainPanel.add(oceanPanel, BorderLayout.CENTER);
		mainPanel.add(organismPanel, BorderLayout.EAST);
		
		// setup the Gbc class
		int inset = 7;                          // inset to next grid cell
		int borderInset = 10;				    // inset to the view border
		Gbc.setDefaultInset(inset);
		Gbc.setDefaultBorderInset(borderInset);
	}

	/**
	 * Displays a text within the status bar.
	 * 
	 * @param text			the text for the displayed status
	 */
	public void setStatusText(String text) {
		
		statusLbl.setText(text);
	}

    // ****************   inner classes   ************************

    /**
     * Listener for CheckBox.
     */
    class MyCheckBoxListener implements ItemListener {

        /**
         * A change of the component happened.
         * 
         * @param e			the event
         */
        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == ItemEvent.SELECTED) {
//                myLabel.setText("SELECTED");
            } else {
//                myLabel.setText("UNSELECTED");
            }
        }
    }

	/**
	 * Update the menu of the list of recent files.
	 */
	public void updateRecentFiles() {
		
		menuRecentFiles.removeAll();
		Stack<String> recentFilesStack = Main.getData().getRecentFilesStack();
		for (int i = 0; i < recentFilesStack.size(); i++) {
			File file = new File(recentFilesStack.get(i));
			if (!file.exists() || !file.canRead()) {
				recentFilesStack.remove(i);
			}
		}
		menuRecentFiles.setEnabled(recentFilesStack.size() != 0);
		for (int i = recentFilesStack.size() - 1; i >= 0; i--) {
			JMenuItem menuItem = createMenuItem(recentFilesStack.get(i), true, RECENT_FILE + i);
			menuRecentFiles.add(menuItem);
		}
	}
}
