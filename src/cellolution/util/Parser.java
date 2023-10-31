
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
package cellolution.util;

import java.io.*;
import java.util.*;

/**
 * Simple parser for parsing text files.
 * 
 * <p>Empty lines (no characters or only white space) or comment lines (starting with a '#')
 * are ignored.</p>
 * 
 * Implement your parsing logic in parseFile().
 */
public class Parser {

	private String filename;
	private BufferedReader in;
	private int lineNr;					// the line number of the parsed file, starting from one

	/**
	 * Parser construction.
	 *
	 * @param filename			the file to parse
	 */
	public Parser(String filename) {

		this.filename = filename;
	}

	/**
	 * Parse the file.
	 *
	 * @throws Exception
	 */
	public void parseFile() throws Exception {

		lineNr = 0;						// the line number of the parsed file, starting from one
		in = new BufferedReader(new FileReader(filename));
		String line = readLineOfFile(in);
		lineNr++;
		// comment lines and empty lines are already skipped, in front should be the first data line
		if (!line.startsWith("XXX")) {
			parserErrorFatal("XXX expected!");
		}
		while (line != null) {
			// comment lines and empty lines are already skipped
			if (line.startsWith("XXX")) {
				// next tags
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();							//  next tag
				if (!st.hasMoreTokens()) {
					parserErrorFatal("XXX expected!");
			    }
				
//				try {
//					value = Integer.parseInt(st.nextToken());
//				} catch (NumberFormatException e) {
//					parserErrorFatal("'<number>' expected!");
//				}
				
				// TODO
				
			} else {
				
				
//				System.out.println(lineNr + " " + line);
				
			}
			line = readLineOfFile(in);
			lineNr++;
		}
		in.close();
	}

	/**
	 * Parser error output handling.
	 *
	 * @param errorMsg			the error message
	 */
	public void parserErrorFatal(String errorMsg) {

		String msg = "Error parsing file " + filename + ", line " + lineNr + ": " + errorMsg;
		System.err.println("\n" + msg + "\n");
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();			
		}
		throw new RuntimeException(msg);
	}

	/**
	 * Parses a line skipping white space.
	 * Note: use parseLineToTokens(String line, char comment) to parse files that may contain comment.
	 *
	 * @param line
	 * @return an ArrayList of Strings containing the tokens in the line
	 */
	public static ArrayList<String> parseLineToTokens(String line) {

		ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			tokens.add(s);
	    }
		return tokens;
	}

	/**
	 * Parses a line skipping white space. If a comment character is parsed, the remaining
	 * line will be skipped.
	 * Note: a line can start with a comment, resulting in an ArrayList with the size of zero.
	 *
	 * @param line
	 * @param comment		a comment character (e.g. shells use "#")
	 * @return an ArrayList of Strings containing the tokens in the line
	 */
	public static ArrayList<String> parseLineToTokens(String line, char comment) {

		ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line, " \t\n\r\f,");
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.charAt(0) == comment) {
				break;
			}
			tokens.add(s);
	    }
		return tokens;
	}

	/**
	 * Reads a line of a file, skipping comment lines and empty lines.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public String readLineOfFile(BufferedReader in) throws IOException {

		String line = in.readLine();
		// skip comment and empty lines
		while (line != null) {
			String trimmedLine = line.trim();
			lineNr++;
			if (trimmedLine.equals("") || trimmedLine.charAt(0) == '#') {
				line = in.readLine();
				continue;
			}
			return trimmedLine;
		}
		return line;
	}

	/**
	 * Clears an ArrayList and adds all tokens of the given StringTokenizer to it.
	 *
	 * @param tokenizer		the StringTokenizer containing the tokens, must not be null
	 * @param tokens		the ArrayList, must not be null
	 */
	public static void tokenizeToArrayList(StringTokenizer tokenizer, ArrayList<String> tokens) {

		tokens.clear();
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
	    }
	}
}
