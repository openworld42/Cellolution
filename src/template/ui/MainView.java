
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
import javax.swing.UIManager.*;

import template.*;
import template.xml.*;

/**
 * The main view of this application.
 */
public class MainView extends JFrame implements ActionListener {

	// constants

	public final static String EXIT = "Exit";
	public final static String XML_EXAMPLE = "XML example";

	// members
	private JPanel mainPanel;
	private JPanel centerPanel;
	private JToolBar toolBar;
	private JToolBar statusBar;
	private JButton exitBtn;

	/**
	 * Construct main view of an application.
	 * 
	 * @throws Exception 
	 */
	public MainView() throws Exception {

		super(Main.APP_NAME);				// or setTitle(XXX);
		ImageIcon icon = new ImageIcon("src/images/size24x24/applications-utilities.png");
		setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// or
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                // delegate to enable subclass to overwrite
//                onExit();
            }
            public void windowOpened(WindowEvent event) {
                // delegate to enable subclass to overwrite
//                onWindowOpened(args);
            }});
            
            
 		initFrame();
		pack();
		Gui.center(this);
//        consoleTextPane.requestFocusInWindow();
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
            System.exit(0);
		} else if (actionCmd.equals("Dialog")) {
            System.out.println("Dialog pressed\n");
            new Dialog(this);
		} else if (actionCmd.equals(XML_EXAMPLE)) {
            System.out.println("Run the XML example ...");
            try {
				new XmlExample();
			} catch (Exception e) {
				e.printStackTrace();
			}
            Gui.infoDlg(this, "The XML file " + XmlExample.FILENAME + " was written and parsed back.", XML_EXAMPLE);
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
		centerPanel.add(button, gbc);
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
		centerPanel.add(button, gbc);
		return(button);
	}

	/**
	 * Creates a JMenuBar for this view.
	 * 
	 * @return the JMenuBar
	 */
	public JMenuBar createMenu() {

		JMenu menu = new JMenu("XXX Menu");
		JMenuItem menuItem = createMenuItem(EXIT, true, EXIT);
		menu.add(menuItem);
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
		JLabel label1 = new JLabel();
		label1.setText("now XXX");;
		statusBar.add(label1);
		statusBar.addSeparator();
		
//		statusBar.add(anotherComponent);		// TODO  

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
		
//		buttonNew = createToolBarButton("New", null, KeyEvent.VK_N,
//			"Start a new XXX", ACTION_NEW, this);
//		tb.add(buttonNew);
		tb.addSeparator();
		JButton btn = createToolBarButton("MyToolBarComp",  null, KeyEvent.VK_M, "Hello, I am a tooltip", "myActionCmd");
//		btn.setEnabled(false);

		tb.add(btn);
		tb.addSeparator();
		btn = createToolBarButton(XML_EXAMPLE,  null, KeyEvent.VK_X, "Runs the XML example, watch System.out", XML_EXAMPLE);
		tb.add(btn);
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
		
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	// in case Nimbus is not found
    	String lookAndFeel = Main.getProperty(AppProperties.LOOK_AND_FEEL);
    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		if (lookAndFeel.equals(info.getName())) {
    			UIManager.setLookAndFeel(info.getClassName());
    			break;
    		}
    	}
    	
    	// TODO  if you want to see all the available look and feels, just uncomment this - else delete it
    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		System.out.println("Look&feel found: " + info.getName());
    	}
    	
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
		
		centerPanel = new JPanel();
		centerPanel.setPreferredSize(new Dimension(600, 300));
//      centerPanel.setPreferredSize(new Dimension(
//      	AppCtx.getIntProperty("main.window.size.x"),
//      	AppCtx.getIntProperty("main.window.size.y")));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		// here we use GridBagLayout with the  Gbc class
		centerPanel.setLayout(new GridBagLayout());
		
//		centerPanel.setLayout(new BorderLayout());			// TODO  consider other Layouts
//		GridBagLayout with my Gbc
//		BorderLayout using sub-panels and other Layouts
//		SpringLayout
//		Flowlayout
//		NULL Layout using absolute positioning (not recomended)
		
		int inset = 7;                          // inset to next grid cell
		int borderInset = 10;				    // inset to the view border
		Gbc.setDefaultInset(inset);
		Gbc.setDefaultBorderInset(borderInset);
		
		// view panel widgets
		// label
		JLabel label = new JLabel("This is a label");
		centerPanel.add(label, new Gbc(0, 0, 1, 1, 1.0, 0, "H"));
		// buttons
		addButton("XXX", 1, 0, "W t");
		addButton("Dialog", 1, 1, "W");
        centerPanel.add(Gbc.filler(), new Gbc(2, 2, 1, 1, 1.0, 100.0, "S B"));

		exitBtn = addButton(EXIT, 3, 2, "SE b r");
        centerPanel.add(Gbc.filler(), new Gbc(3, 0, 1, 2, 1.0, 10.0, "C B"));
		
		// enable / disable buttons
//		buttonXY.setEnabled(false);
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
    
    /** Some examples at the end of MainView: */
    
//    int response = JOptionPane.showConfirmDialog (parent, "Overwrite existing file?", 
//    		fileChooser.getDialogTitle(), JOptionPane.OK_CANCEL_OPTION, 
//    		JOptionPane.QUESTION_MESSAGE);
//    if (response == JOptionPane.CANCEL_OPTION) return;
    
    
//    JFileChooser fileChooser = new JFileChooser(defaultDirectoryName);
//    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
//    fileChooser.setSelectedFile(new File("XY.txt"));
//    fileChooser.addChoosableFileFilter(defaultFileFilter);
//    if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
//    	return;
//    }
//	  File file = fileChooser.getSelectedFile();
}
