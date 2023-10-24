
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
package cellolution;

import java.awt.image.*;
import java.io.*;
import java.net.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.UIManager.*;

import cellolution.ui.*;
import cellolution.util.*;

/**
 * Cellolution - simulated evolution of artificial cells in a water world: the ocean. 
 */
public class Main {

	public static final String APP_NAME = "Cellolution";
	public static final String GROUND_IMG = "/cellolution/images/OceanBedGreen.png";		
	
	private static Main instance;				// the one and only instance of this application
	
	private Ocean ocean;
	private MainView mainView;
	private OrganismDisplayCtlr orgDisplayCtlr;
	private BufferedImage oceanImage;

	private CommandLineArgs args;				// command line arguments, if needed
	private AppProperties properties; 			// application properties (e.g. a config file), if needed

	private boolean isVerbose; 					// verbose messages to System.out
	private int cellColumns;
	private int cellRows;

	public Main(String[] arguments) throws Exception {
		
		instance = this;
		// parse command line arguments (if the application works with arguments)
		args = new CommandLineArgs(arguments);
		if (!args.isValid()) {
			Usage.exit(1);
		}
		isVerbose = args.isVerbose();
		// read in the JSON file properties and states
		// if the file does not exist, it will be created with default properties
		properties = new AppProperties();
		JsonFile file = new JsonFile();
		try {
			file.readFrom(JsonFile.JSON_FILE_NAME);
		} catch (Exception e) {
			// intentionally falling through
			e.printStackTrace();
			System.out.println("\n*** Cellolution JSON parser: errors reading file '" 
			+ JsonFile.JSON_FILE_NAME + "', using defaults\n");
		}
		// create the world
		cellColumns = 800;
		cellRows = 450;
		URL imageURL = Main.class.getResource(GROUND_IMG);
		try {
			oceanImage = ImageIO.read(imageURL);
		} catch (Exception e) { // intentionally falling through, no ocean image displayed
		}
		ocean = new Ocean(cellColumns, cellRows, oceanImage);
		// start the GUI
		System.setProperty("awt.useSystemAAFontSettings","on");					// render fonts in a better way
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 	// in case LookAndFeel Nimbus is not found
    	String lookAndFeel = getProperty(AppProperties.LOOK_AND_FEEL);
    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		if (lookAndFeel.equals(info.getName())) {
    			UIManager.setLookAndFeel(info.getClassName());
    			break;
    		}
    	}
		Util.verbose("Starting GUI and evolution ...");		// is displayed on System.out only if the verbose flag is on
		orgDisplayCtlr = new OrganismDisplayCtlr(ocean);
    	mainView = new MainView(orgDisplayCtlr.getOrganismPanel());
	}

	/**
	 * @return the cellColumns
	 */
	public static int getCellColumns() {
		
		return instance.cellColumns;
	}
	
	/**
	 * @return the cellRows
	 */
	public static int getCellRows() {
		
		return instance.cellRows;
	}
	
	/**
	 * @return the mainView
	 */
	public static MainView getMainView() {
		
		return instance.mainView;
	}
	
	/**
	 * @return the properties
	 */
	public static AppProperties getProperties() {
		
		return instance.properties;
	}

	/**
	 * @return the ocean
	 */
	public static Ocean getOcean() {
		
		return instance.ocean;
	}

	/**
	 * @return the orgDisplayCtlr
	 */
	public static OrganismDisplayCtlr getOrgDisplayCtlr() {
		
		return instance.orgDisplayCtlr;
	}

	/**
	 * @return the parsed command line arguments
	 */
	public static CommandLineArgs getArgs() {
	
		return instance.args;
	}

	/**
	 * Gets a property.
	 * 
	 * @param key
	 * @return the property value
	 */
	public static String getProperty(String key) {
		
		return instance.properties.getProperty(key);
	}

	/**
	 * Gets an boolean property (a flag).
	 * 
	 * @param key
	 * @return true id the value is "true", false otherwise
	 */
	public static boolean getPropertyBool(String key) {
		
		return instance.properties.getPropertyBool(key);
	}

	/**
	 * Gets an integer property.
	 * 
	 * @param key
	 * @return the integer value
	 */
	public static int getPropertyInt(String key) {
		
		return Integer.parseInt(instance.properties.getProperty(key));
	}

	/**
	 * @return the main instance of this application
	 */
	public static Main instance() {
		
		return instance;
	}

	/**
	 * Flag for verbose messages sent to System.out.
	 * 
	 * @return true, if in verbose mode, false otherwise
	 */
	public static boolean isVerbose() {
		
		return instance.isVerbose;
	}

	public static void main(String[] arguments) {
		
		System.out.println("\n" + APP_NAME + " - simulated evolution of artificial cells in a water world.\n");
		try {
			new Main(arguments);
		} catch (Exception e) {
            System.out.println("\n*****  Exception caught, exit: " + e);
			e.printStackTrace();
			System.exit(1);
		}		
	}

	/**
	 * Stops the current ocean simulation and start a new one.
	 */
	public void newOcean() {

		int option = JOptionPane.showConfirmDialog(mainView, 
				"This will destroy the current simulation and start a new ocean", "New Ocean", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option != JOptionPane.OK_OPTION) {
			return;
		}
		// start a new ocean
		ocean.stopSwingWorker();						// stop the current simulation
		ocean = new Ocean(cellColumns, cellRows, oceanImage);
		Util.verbose("Starting a new ocean ...");		// is displayed on System.out only if the verbose flag is on
		orgDisplayCtlr = new OrganismDisplayCtlr(ocean);
		mainView.dispose();
		try {
			mainView = new MainView(orgDisplayCtlr.getOrganismPanel());
		} catch (Exception e) {
            System.out.println("\n*****  Exception caught creating a new ocean, exit: " + e);
			e.printStackTrace();
			System.exit(1);
		}		
	}

	/**
	 * Exit handler.
	 */
	public void onExit() {

		Util.verbose(APP_NAME + " - good bye!");
		try {
			new JsonFile(JsonFile.JSON_FILE_NAME);
		} catch (IOException e) {
			// no way out, just display the exception
			e.printStackTrace();
		}
		System.exit(0);
	}
}
