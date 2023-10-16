
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
		// read in the project XML properties from a configuration file (if the application has properties)
		// if the file does not exist, it will be created with your default properties
		properties = new AppProperties("config.xml", true);
		// create the world
		cellColumns = 800;
		cellRows = 450;
		BufferedImage img = null;
		URL imageURL = Main.class.getResource(GROUND_IMG);
		try {
			img = ImageIO.read(imageURL);
		} catch (Exception e) { // intentionally falling through 
		}
		ocean = new Ocean(cellColumns, cellRows, img);
		// start the GUI
		System.setProperty("awt.useSystemAAFontSettings","on");					// render fonts in a better way
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 	// in case Nimbus is not found
    	String lookAndFeel = "Nimbus";
    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    		if (lookAndFeel.equals(info.getName())) {
    			UIManager.setLookAndFeel(info.getClassName());
    			break;
    		}
    	}
		Util.verbose("Starting GUI ...");		// is displayed on System.out only if the verbose flag is on
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
	 * Exit handler.
	 */
	public void onExit() {

		Util.verbose(APP_NAME + " - good bye!");
		System.exit(0);
	}
}
