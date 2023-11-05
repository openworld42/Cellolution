
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
package cellolution;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.UIManager.*;

import org.json.*;

import cellolution.ui.*;
import cellolution.util.*;

/**
 * Cellolution - simulated evolution of artificial cells in a water world: the ocean. 
 */
public class Main {

	public static final String APP_NAME = "Cellolution";
	public static final String GROUND_IMG = "/cellolution/images/OceanBedGreen.png";
	public static final String APP_ICON_IMG = "/cellolution/images/VelellaVellella128x128.png";
	public static final String APP_DATA_FILE_NAME = "Cellolution.json";
	public static final String SIM_DATA_FILE_NAME = "CellolutionSim.json";
	
	private static Main instance;				// the one and only instance of this application

	private Ocean ocean;
	private MainView mainView;
	private OrganismDisplayCtlr orgDisplayCtlr;
	private BufferedImage oceanImage;

	private CommandLineArgs args;				// command line arguments, if needed
	private Data data; 							// application data (serialization)
	private String currentJsonFile;				// the JSON file name during parsing, null otherwise

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
		// read in the JSON files properties and states
		// if a file does not exist, it will be created with default properties
		data = new Data();
		data.readAppData();
		data.readSimulationData(SIM_DATA_FILE_NAME);
		// create the world
		cellColumns = 800;
		cellRows = 450;
		URL imageURL = Main.class.getResource(GROUND_IMG);
		try {
			oceanImage = ImageIO.read(imageURL);
		} catch (Exception e) { // intentionally falling through, no ocean image displayed
		}
		ocean = new Ocean(cellColumns, cellRows, oceanImage, true);
		// start the GUI
		System.setProperty("awt.useSystemAAFontSettings","on");					// render fonts in a better way
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 	// in case LookAndFeel Nimbus is not found
    	String lookAndFeel = Data.getString(Keys.LOOK_AND_FEEL);
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
	 * Do some error handling when an exception has been thrown.
	 * 
	 * @param text 			the text to be displayed
	 * @param e				the exception 
	 * 
	 */
	public static void exceptionCaught(String text, Exception e) {
		
		System.out.println(text);
		JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
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
	 * Returns the data container.
	 * 
	 * @return the data container
	 */
	public static Data getData() {
		
		return instance.data;
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
		} catch (JSONException je) {
			String msg = "Error -> " + je.getMessage();
			if (instance.currentJsonFile != null) {
				msg += "\n\nThe error happened most likely in the file '" + instance.currentJsonFile + "'.\n"
						+ "If you delete it, Cellolution will create a new one using defaults,\n"
						+ "but the simulation may be lost.";
			}
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("\n*****  Exception caught, exit: " + je);
			je.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
            System.out.println("\n*****  Exception caught, exit: " + e);
			e.printStackTrace();
			System.exit(1);
		}		
	}

	/**
	 * Stops the current ocean simulation and start a new one.
	 * 
	 * @param hasManyOrganisms 	if true, many organisms are created, on 
	 * 							false create only one for each species 
	 */
	public void newOcean(boolean hasManyOrganisms) {

		ocean.setSwingWorkerPaused(true);
		int option = JOptionPane.showConfirmDialog(mainView, 
				"This will destroy the current simulation and start a new ocean sim\n"
				+ "Store the current simulation to a file?", "New Ocean", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		switch (option) {
		case JOptionPane.CANCEL_OPTION:
			ocean.setSwingWorkerPaused(false);
			return;
		case JOptionPane.NO_OPTION:
			ocean.stopSwingWorker();						// stop the current simulation
			break;
		case JOptionPane.YES_OPTION:
			FileChooserDlg dlg = new FileChooserDlg("Choose file", 
					JFileChooser.FILES_ONLY, System.getProperty("user.dir"), null);
			int retVal = dlg.showSaveDialog(mainView);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				String path = dlg.getSelectedFile().toString();
				if (!path.toLowerCase().endsWith(".json")) {
					path += ".json";
				}
				data.addRecentFile(path);
				ocean.stopSwingWorker();					// stop the current simulation
				data.writeSimulationData(path);
			} else {
				ocean.setSwingWorkerPaused(false);
				return;
			}
			//intentionally falling through
		}
		// start a new ocean, simulation has already been stopped
		data.removeSimulationData();
		ocean = new Ocean(cellColumns, cellRows, oceanImage, hasManyOrganisms);
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

		try {
			ocean.stopSwingWorker();
			data.writeOnExit();
		} catch (IOException e) {
			// no way out, just display the exception
			e.printStackTrace();
		}
		Util.verbose(APP_NAME + " - good bye!");
		System.exit(0);
	}
	
	/**
	 * Sets the JSON file name during parsing, or null after parsing
	 * 
	 * @param currentJsonFile 	the JSON file name to set or null (if no parsing)
	 */
	public void setCurrentJsonFile(String currentJsonFile) {
		
		this.currentJsonFile = currentJsonFile;
	}
}
