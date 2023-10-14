
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

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Logger, a simple logging utility, could be a wrapper for java.util.Logger or log4j, 
 * you may be change to it in the future, of course.
 * 
 * An application may call Util.renameToBackupFile() to save older log files.
 * Any error() call will flush the log to the log file, to ensure exceptions or severe errors
 * are persisted.
 * 
 * Note: each application has to call init() before writing anything to the log and to
 * call close() before the exit to save pending log entries.
 *
 * @author Heinz Silberbauer
 */
//public class Logger extends java.util.logging.Logger {
public class Logger {

	public static final int BUFFER_SIZE = 8 * 1024;	// make it big if writing large files ...
	public static final String lineSep = System.lineSeparator();
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

	private static final Logger instance = new Logger();

	private static int logCount;
	private static boolean logErrorsToConsole;
	private static boolean isDirty;

	private static String logFilename;
	private static BufferedWriter writer;

	/**
	 * Deny external construction, singleton.
	 */
	private Logger() {

	}

	/**
	 * Closes the log file.
	 * 
	 * Note: each application HAS to close the logger before exit to save pending log entries.
	 */
	public static void close() {

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal logger error: " + e.getMessage());
		}
	}

	/**
	 * Overwrite this method to change the logging prefix for log entries.
	 * 
	 * @return the prefix of a log entry
	 */
	public String createPrefix() {

		return dateFormat.format(new Date());
	}

	/**
	 * Log an error.
	 * 
	 * @param message
	 */
	public static void error(String message) {

		try {
			writer.write(instance.createPrefix() + "E " + message + lineSep);
			writer.flush();						// flush the logger file after error and/or Exception
			logCount++;
			if (logErrorsToConsole) {
				System.err.println(lineSep + message + lineSep);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal logger error: " + e.getMessage());
		}
	}

	/**
	 * Log an error looking like "MyClass: <message>".
	 * 
	 * @param clazz			the calling class 
	 * @param message
	 */
	public static void error(Class<?> clazz, String message) {

		error(clazz.getSimpleName() + ": " + message);
	}

	/**
	 * Log an error, usually caused by an exception.
	 * 
	 * @param message
	 * @param e
	 */
	public static void error(String message, Throwable e) {

		try {
			writer.write(instance.createPrefix() + "E " + message + lineSep);
			String stackTrace = Util.stackTraceToString(e);
			writer.write(stackTrace + lineSep);
			writer.flush();						// flush the logger file after error and/or Exception
			logCount++;
			if (logErrorsToConsole) {
				System.err.println(lineSep + message + " " + e.getMessage() + lineSep);
				e.printStackTrace();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
			throw new RuntimeException("Fatal logger error: " + e2.getMessage());
		}
	}

	/**
	 * Logs an error with the Exception and the stack trace.
	 *
	 * @param e			the Exception to log
	 */
	public static void exception(Exception e) {

		error("", e);
	}

	/**
	 * Flush the log file if dirty, so all log content is persistent.
	 */
	public static void flushIfDirty() {

		if (isDirty) {
			try {
				writer.flush();
			} catch (IOException e) {
				// do nothing
			}
			isDirty = false;
		}
	}

	/**
	 * @return the log file name
	 */
	public static String getFileName() {

		return logFilename;
	}

	/**
	 * @return the number of log entries
	 */
	public static int getLogCount() {

		return logCount;
	}

	/**
	 * Log an information message.
	 * 
	 * @param message
	 */
	public static void info(String message) {

		try {
			writer.write(instance.createPrefix() + "I " + message + lineSep);
			logCount++;
			isDirty = true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal logger error: " + e.getMessage());
		}
	}

	/**
	 * Log an information message looking like "MyClass: <message>".
	 * 
	 * @param clazz			a calling class 
	 * @param message
	 */
	public static void info(Class<?> clazz, String message) {

		info(clazz.getSimpleName() + ": " + message);
	}

	/**
	 * Initializes (opens) the log file.
	 * An application has to call init() before writing anything to the log.
	 * 
	 * @param logFilename
	 */
	public static void init(String logFilename) {

		try {
			writer = new BufferedWriter(new FileWriter(logFilename), BUFFER_SIZE);
			Logger.logFilename = logFilename;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error initilizing Logger: " + e.getMessage());
		}
	}

	/**
	 * @param logErrorsToConsole if true, any error will also be written to  System.err
	 */
	public static void logErrorsToConsole(boolean logErrorsToConsole) {

		Logger.logErrorsToConsole = logErrorsToConsole;
	}

	/**
	 * Logs the end of a time measurement.
	 *
	 * @param obj		either a message string or the calling object takes the simple class name
	 * @param start		the time when the measurement started (System.currentTimeMillis())
	 */
	public static void timestampEnd(Object obj, long start) {

		long duration = System.currentTimeMillis() - start;
		if (obj instanceof String) {
			info((obj.toString() + " duration " + duration + "ms"));
		} else {
			info((obj.getClass().getSimpleName() + " duration " + duration + "ms"));
		}
	}

	/**
	 * Log a warning message.
	 *
	 * @param message
	 */
	public static void warning(String message) {

		try {
			writer.write(instance.createPrefix() + "W " + message + lineSep);
			logCount++;
			isDirty = true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal logger error: " + e.getMessage());
		}
	}

	/**
	 * Log an warning message looking like "MyClass: <message>".
	 * 
	 * @param clazz			a calling class 
	 * @param message
	 */
	public static void warning(Class<?> clazz, String message) {

		warning(clazz.getSimpleName() + ": " + message);
	}
}
