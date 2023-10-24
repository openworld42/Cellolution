
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
import cellolution.util.*;

/**
 * The main view of this application.
 */
public class MainView extends JFrame implements ActionListener {

	// constants

	public final static String EXIT = "Exit";
	public final static String NEW_OCEAN = "NewOcean";

	// members
	private JPanel mainPanel;
	private OceanPanel oceanPanel;
	private OrganismPanel organismPanel;
	private JToolBar toolBar;
	private JToolBar statusBar;
	private JLabel statusLbl;
	private JButton exitBtn;

	/**
	 * Construct main view of an application.
	 * 
	 * @param organismPanel 
	 * @throws Exception 
	 */
	public MainView(OrganismPanel organismPanel) throws Exception {

		super(Main.APP_NAME);
		this.organismPanel = organismPanel;
		ImageIcon icon = new ImageIcon("src/images/size24x24/applications-utilities.png");
		setIconImage(icon.getImage());
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// or
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
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCmd = event.getActionCommand();
		if (actionCmd.equals(EXIT)) {
            dispose();
		} else if (actionCmd.equals(NEW_OCEAN)) {
			Main.instance().newOcean();
        } else {
            System.out.println("ActionListener: unknown component, it's me -> "
            		+ event.getSource().getClass().getSimpleName() 
            		+ ": " + actionCmd);
		}
	}

	/**
	 * Adds a button with Gbc and specified insets.
	 * 
	 * @param name			the label of the button (and its action command)
	 * @param col
	 * @param row
	 * @param control
	 * @param insetTop
	 * @param insetLeft
	 * @param insetBottom
	 * @param insetRight
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
	 * @param name			the label of the button (and its action command)
	 * @param col
	 * @param row
	 * @param control
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
	 * @param name
	 * @param enabled
	 * @param actionCmd
	 * @return the menu item
	 */
	private JMenuItem createMenuItem(String name, boolean enabled, String actionCmd) {
		
		JMenuItem menuItem = new JMenuItem(name);
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
		
		JButton btn = createToolBarButton("New Ocean",  null, KeyEvent.VK_N, "Dump the current ocean, start a new one", NEW_OCEAN);
//		btn.setEnabled(false);
		tb.add(btn);
		tb.addSeparator();
//		
//		btn = createToolBarButton(XML_EXAMPLE,  null, KeyEvent.VK_X, "Runs the XML example, watch System.out", XML_EXAMPLE);
//		tb.add(btn);
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
	 * @return
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
	 * @throws Exception 
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
	 * @param text
	 */
	public void setStatusText(String text) {
		
		statusLbl.setText(text);
	}

    // ****************   inner classes   ************************

    /**
     * Listener for CheckBox
     */
    class MyCheckBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == ItemEvent.SELECTED) {
//                myLabel.setText("SELECTED");
            } else {
//                myLabel.setText("UNSELECTED");
            }
        }
    }
}
