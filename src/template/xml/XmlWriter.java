
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
package template.xml;

import java.io.*;

/**
 * Writes data (usually of an object) to a XML file.
 *
 * Note: it is the callers responsibility to close() the XmlWriter.
 *
 * @author Heinz Silberbauer
 */
public class XmlWriter extends BufferedWriter {

	public static final int BUFFER_SIZE = 16 * 1024;	// make it big, if writing large files ...

	private String fileName;
	private int indentLevel;
    private String indentString = "\t";
    private boolean verbose;

	/**
	 * Instantiates a new xml writer to make something persistent.
	 * Uses stand alone writing without complexity: no DTD or XML schema, UTF-8 character encoding.
	 *
	 * @param fileName 		the file name of the XML file
	 * @param verbose 		true if the XML writer should create verbose information in the XML file to make it more
	 * 						human readable, false otherwise
	 * @throws IOException an I/O exception has occurred
	 */
	public XmlWriter(String fileName, boolean verbose) throws IOException {

		this(fileName, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>", verbose);
	}

	/**
	 * Instantiates a new xml writer to make something persistent.
	 * The caller decides about the XML definition line(s), e.g. a XML schema, or a DTD.
	 *
	 * @param fileName 		the file name of the XML file
	 * @param verbose 		true if the XML writer should create verbose information in the XML file to make it more
	 * 						human readable, false otherwise
	 * @throws IOException an I/O exception has occurred
	 */
	public XmlWriter(String fileName, String xmlDefinitonLine, boolean verbose) throws IOException {

		super(new FileWriter(fileName), BUFFER_SIZE);
		this.fileName = fileName;
		this.verbose = verbose;
		write(xmlDefinitonLine);
		newLine();
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {

		return fileName;
	}

	/**
	 * @return the indentation Level
	 */
	public int getIndentLevel() {

		return indentLevel;
	}

	/**
	 * @return the indentation String
	 */
	public String getIndentString() {

		return indentString;
	}

	/**
	 * Returns if the writer creates verbose information in the XML file to make it more
	 * human readable.
	 *
	 * @return the verbose
	 */
	public boolean isVerbose() {

		return verbose;
	}

	/**
	 * Decrease the indentation level by one.
	 */
	public void indentLess() {

		indentLevel--;
	}

	/**
	 * Increase the indentation level by one.
	 */
	public void indentMore() {

		indentLevel++;
	}

    /**
     * Sets the indent string used for indenting the XML output to get it formatted.
     * This is usually either an empty string or null to avoid additional characters in the XML output,
     * a tab character or some blanks.
     * <p>If <code>setIndentString</code> is not called before writing the XML
     * output, the default is the tab character.</p>
     *
     * @param   indentString    the string used for one indent level
     */
	public void setIndentString(String indentString) {

		this.indentString = (indentString == null) ? "" : indentString;
	}

	/**
	 * Writes a XML comment.
	 *
	 * @param comment			the text of the comment
	 * @throws IOException
	 */
	public void writeComment(String comment) throws IOException {

		write("<!-- " + comment + " -->");
	}

    /**
     * Writes an XML comment ending.
     *
	 * @throws IOException
	 */
	public void writeEndComment() throws IOException {

		write(" -->");
	}

	/**
     * Write an ending tag of a XML item.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writeEndTag(String tag) throws IOException {

		write("</" + tag + ">");
	}

    /**
     * Write an int[] with a given tag.
     *
	 * @param tag				the tag enclosing the integers (e.g. "id" for <id>12345</id>)
	 * @param ints
	 * @throws IOException
	 */
	public void writeArray(String tag, int[] ints) throws IOException {

		for (int i = 0; i < ints.length; i++) {
			writeXmlElement(tag, "" + ints[i]);
 		}
	}

    /**
     * Write an array of integers.
     *
	 * @param xmlTag		the name of the XML tag
	 * @param ints			the array of integers
	 * @param oneLine		if true, write in one line without making newlines
	 * @throws IOException
	 */
	public void writeIds(String xmlTag, int[] ints, boolean oneLine) throws IOException {

		int elemPerLine = 8;
		if (!oneLine) {
			writeIndents();
		}
		for (int i = 0; i < ints.length; i++) {
			if (i % elemPerLine == 0) {
				if (!oneLine) {
					newLine();
					writeIndents();
				}
			}
			writeXmlElement(xmlTag, "" + ints[i]);
		}
	}

    /**
     * Writes an indented XML end tag.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writeIndentedEndTag(String tag) throws IOException {

		writeIndents();
		writeEndTag(tag);
	}

    /**
     * Writes an indented id array, each id in one line.
     *
     * @param tag         	the name of the tag
	 * @param ints			the array
	 * @param verbose
	 * @throws IOException
	 */
	public void writeIndentedIds(String tag, int[] ints, boolean verbose) throws IOException {

		for (int i = 0; i < ints.length; i++) {
			writeIndentedXmlElement(tag, "" + ints[i]);
//			if (verbose) {
//				writeComment("" + ints[i] + " " + value);
//			}
			newLine();
		}
	}

    /**
     * Writes an indented XML start tag.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writeIndentedStartTag(String tag) throws IOException {

		writeIndents();
		write("<" + tag + ">");
	}

    /**
     * Writes an indented line with a complete XML tag including start tag, value, end ta.
     *
     * @param   tag         the name of the tag
     * @param   value       the value of the XML tag
     * @throws  IOException
     */
	public void writeIndentedXmlElement(String tag, String value) throws IOException {

		writeIndents();
		writeXmlElement(tag, value);
	}

    /**
     * Writes  indentation level times the indentation string.
     *
     * @throws IOException
     */
	public void writeIndents() throws IOException {

		for (int i = 0; i < indentLevel; i++) {
		    write(indentString);
		}
	}

    /**
     * Writes an indented text and a line separator.
     * Note: used for comment lines.
     *
	 * @throws IOException
	 */
	public void writelnIndentedText(String text) throws IOException {

		writeIndents();
		write(text);
		newLine();
	}

    /**
     * Writes an XML comment ending and a line separator.
     *
	 * @throws IOException
	 */
	public void writelnEndComment() throws IOException {

		writeEndComment();
		newLine();
	}

	/**
     * Write an ending tag of a XML item and a line separator.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writelnEndTag(String tag) throws IOException {

		write("</" + tag + ">");
		newLine();
	}

    /**
     * Writes an indented line with a XML comment and a line separator.
     *
	 * @param comment			the text of the comment
	 * @throws IOException
	 */
	public void writelnIndentedComment(String comment) throws IOException {

		writeIndents();
		write("<!-- " + comment + " -->");
		newLine();
	}

    /**
     * Writes an indented XML comment ending and a line separator.
     *
	 * @throws IOException
	 */
	public void writelnIndentedEndComment() throws IOException {

		writeIndents();
		write("-->");
		newLine();
	}

    /**
     * Writes an indented XML end tag and a line separator.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writelnIndentedEndTag(String tag) throws IOException {

		writeIndents();
		writeEndTag(tag);
		newLine();
	}

	/**
     * Writes an indented XML comment opening and a line separator.
     *
	 * @throws IOException
	 */
	public void writelnIndentedStartComment() throws IOException {

		writeIndents();
		write("<!--");
		newLine();
	}

    /**
     * Writes an indented XML start tag and a line separator.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writelnIndentedStartTag(String tag) throws IOException {

		writeIndents();
		write("<" + tag + ">");
		newLine();
	}

    /**
     * Writes an indented line with a complete XML tag including start tag, value, end tag and a line separator.
     *
     * @param   tag         the name of the tag
     * @param   value       the value of the XML tag
     * @throws  IOException
     */
	public void writelnIndentedXmlElement(String tag, String value) throws IOException {

		writeIndents();
		writeXmlElement(tag, value);
		newLine();
	}

    /**
     * Indents and Writes an indented line with a complete XML end tag and a line separator, indents less afterwards.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writelnObjIndentedEndTag(String tag) throws IOException {

		indentLess();
		writelnIndentedEndTag(tag);
	}

    /**
     * Writes an indented line with a complete XML end tag and a line separator, unindents afterwards.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writelnObjIndentedStartTag(String tag) throws IOException {

		writelnIndentedStartTag(tag);
		indentMore();
	}

    /**
     * Writes an XML comment opening and a line separator.
     *
	 * @throws IOException
	 */
	public void writelnStartComment() throws IOException {

		writeStartComment();
		newLine();
	}

    /**
     * Writes an XML comment opening.
     *
	 * @throws IOException
	 */
	public void writeStartComment() throws IOException {

		write("<!-- ");
	}

    /**
     * Writes a XML start tag.
     *
     * @param   tag         the name of the tag
     * @throws  IOException
     */
	public void writeStartTag(String tag) throws IOException {

		write("<" + tag + ">");
	}

    /**
     * Writes a complete XML tag including start tag, value and end tag.
     *
     * @param   tag         the name of the tag
     * @param   value       the value of the XML tag
     * @throws  IOException
     */
	public void writeXmlElement(String tag, String value) throws IOException {

		writeStartTag(tag);
		if (value != null) {
		    write(value);
		}
		writeEndTag(tag);
	}
}
