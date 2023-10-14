
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
package template;

import static template.Message.*;			// for short code, using its constants in a short way

import template.ui.*;

/**
 * This application XXX does XXX (main entry) - it's your project and I don't know it. 
 * 
 * Delete or comment out all things you don't need (or believe so ;-).
 * The amount of empty lines in Main is for readability of the template - not my programming style. 
 */
public class Main {

	public static final String APP_NAME = "XXX";

	private static Main instance;				// the one and only instance of this application
	
	private CommandLineArgs args;				// command line arguments, if needed
	private AppProperties properties; 			// application properties (e.g. a config file), if needed
	private boolean isVerbose; 					// verbose messages to System.out

	/**
	 * Construction.
	 * 
	 * @param arguments			command line arguments
	 * @throws Exception
	 */
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
		
		// start logging feature (if the application has logging)
		// do'nt forget to close the logger before stop AND on error exits
		String logFilename = "log.txt";
		Logger.init(logFilename);
		Logger.info("reading configuration file: XXX" );

		// TODO  your stuff

		Logger.info("reading finished");

		// TODO  your stuff

		Util.verbose("Starting GUI ...");		// is displayed on System.out only if the verbose flag is on
		
		// if the application has a GUI, start it now ...
		System.setProperty("awt.useSystemAAFontSettings","on");	// render fonts in a better way with this property

		new MainView();
	}

	/**
	 * Logs an Exception and performs System.exit(1) WITHOUT closing 
	 * anything except the Logger.
	 * Add what you need (file closing, AppProperties storing, etc).
	 *
	 * @param e		the Exception
	 */
	public static void exitOnException(Exception e) {

		e.printStackTrace();
		
		// if there is logging in this project
		Logger.error("Exception caught, exit", e);
		Logger.close();
		
		System.exit(1);			// in any case, indicate an unexpected exception exit at least with an error code
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
	public Main instance() {
		
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

	/**
	 * Main entry.
	 * 
	 * @param args		command line arguments
	 */
	public static void main(String[] args) {

        try {
    		// there is a multi-language support infrastructure implemented, if needed
    		// STARTING_MSG stems from static import of Message, if we want to be prepared for multi-language support
            System.out.println(Msg.get(STARTING_MSG) + APP_NAME + " ...\n");
            // a message can have parameters
            System.out.println(Msg.get(CONFIG_FILE_NAME_MSG, "myfile.conf"));
            
            Main main = new Main(args);
            
            // if you are using AppProperties, you may want to store changed values (at least here) 
            // with a GUI, you may want to write any changes to the properties file  
            main.properties.storeToXML();
          
        } catch (Exception e) {
        	
        	// TODO your error handling

            System.out.println("\n*****  Exception caught, exit: " + e);
            e.printStackTrace();
            
			Logger.error("Unexpected exception, exit", e);		// if there is logging in this project OR do
			
			// generic error exit (so we don't forget anything, there are many possibilities to fail in a program)
			exitOnException(e);
			
			// if there is logging in this project and you don't use Main.exitOnException(e), you have to close the Logger
//			Logger.close();
			
			System.exit(2);		// needed for GUI
		}
        
        // if there is logging in this project, you have to close the Logger
        // uncomment this if there is no GUI
//		Logger.close();
//      System.out.println("\n" + APP_NAME + ", bye.");
	}
}
