
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
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.ext.*;

/**
 * An example for writing and reading XML, copy, modify and divide this class according it to your needs.<br/>
 * 
 * It uses three different inner class data objects (one containing another and a wrapped HashMap) 
 * to demonstrate the features of the template.xml packages.<br/>
 * 
 * Reading back needs a handler for the XML tags, this example uses org.xml.sax.ext.DefaultHandler2.<br/>
 * 
 * A real world implementation would not do all the stuff with inner classes, this is just for demonstration.
 * BTW, imho it is very useful to use states and keep every XML tag as a static final String constant, often defined in
 * an interface just for the XML tags.
 */
public class XmlExample {

	
	static enum State {										// for XmlReadHandler, state of parsing items
		DB,
		OBJ_A,
		OBJ_B,
		MAP,
	}
	
	public static final String FILENAME = "myExample.xml";			
	private static final String XML_DATA = "myxmldata";				// enclosing XML tag
	
	// the objects to write and parse back
	private ObjectA objA;
	private XmlWrappedHashMap map;

	/**
	 * Construction, write and XML file from two objects and a wrapped HashMap and read it back.
	 */
	public XmlExample() throws Exception {

		// create some objects to stare as XML
		objA = new ObjectA(42, "Why XML?", new ObjectB(4242, "Therefore!"));	// inner classes (at the end)
		// we can also wrap collections or other objects
		map = new XmlWrappedHashMap();								// inner class  (at the end)
		map.put("key1", "value1");
		map.put("key2", "value2");
		// write something, set verbose to true
		XmlWriter writer = new XmlWriter(FILENAME, true);
		writer.writelnIndentedComment("This is the example");
		writer.writelnIndentedStartTag(XML_DATA);
		objA.toXml(writer, true);
		map.toXml(writer, true);
		writer.writelnIndentedEndTag(XML_DATA);
		writer.close();
		// and read it back ... 
		objA = null;			// just to demonstrate the parsing
		map = null;
		// I assume something big, so a sax parser will do better than a DOM parser.
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	SAXParser saxParser = factory.newSAXParser();
    	XmlReadHandler readhandler = new XmlReadHandler(this);		// inner class  (at the end)
        try {
        	// the handler will store the objects parsed using setters (here: setObjA(), setMap())
			saxParser.parse("myExample.xml", readhandler);
		} catch (SAXParseException e) {
			System.out.println("Exception data: line " + e.getLineNumber()
					+ ", column " + e.getColumnNumber()
					+ ", public id " + e.getPublicId()
					+ ", system id " + e.getSystemId()
					);
			e.printStackTrace();
		}
        // verify what we have parsed
        System.out.println("A: id=" + objA.id + ", text=" + objA.text + ", A->B.id=" + objA.b.id);
        System.out.println("Map: key=key1, value=" + map.get("key1") + ",  map.size()=" + map.size());
        System.out.println("ok, thats what we expected ...");
	}
	
	public void setMap(XmlWrappedHashMap map) {
		
		this.map = map;
	}
	
	public void setObjA(ObjectA objA) {
	
		this.objA = objA;
	}

	// Tester.
//	public static void main(String[] args) {
//
//		try {
//			new XmlExample();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
    // ****************   inner classes, just to keep everything in one file for demonstration   ************************

	/** Container for an int, a String and ObjectB */
	class ObjectA implements XmlWritable {
		
		public static final String XML_ID = "id";
		public static final String XML_TEXT = "text";
		
		private int id;
		private String text;
		private ObjectB b;				// this class owns another instance of ObjectB
		
		public ObjectA(int id, String text, ObjectB objectB) {		// needed for XmlReadHandler construction
			this.id = id;
			this.text = text;
			b = objectB;
		}

		public void toXml(XmlWriter xmlwriter, boolean verbose) throws IOException {
			String clazz = this.getClass().getSimpleName();
			xmlwriter.writelnObjIndentedStartTag(clazz);
			xmlwriter.writelnIndentedXmlElement(XML_ID, "" + id);
			xmlwriter.writelnIndentedXmlElement(XML_TEXT, text);
			b.toXml(xmlwriter, verbose);
			xmlwriter.writelnObjIndentedEndTag(clazz);
		}
	}

	/** Container for an int and a String */
	class ObjectB implements XmlWritable {
		
		public static final String XML_ID = "id";
		public static final String XML_ANOTHER_TEXT = "anothertext";
		
		private int id ;
		private String anothertext;
		
		public ObjectB(int id, String anothertext) {
			this.id = id;
			this.anothertext = anothertext;
		}

		public void toXml(XmlWriter xmlwriter, boolean verbose) throws IOException {
			String clazz = this.getClass().getSimpleName();
			xmlwriter.writelnObjIndentedStartTag(clazz);
			xmlwriter.writelnIndentedXmlElement(XML_ID, "" + id);
			xmlwriter.writelnIndentedXmlElement(XML_ANOTHER_TEXT, anothertext);
			if (verbose) {
				xmlwriter.writelnIndentedComment("Sorry, no explanation!");
			}
			xmlwriter.writelnObjIndentedEndTag(clazz);
		}
	}
	
	/** A wrapped HashMap to write it to a XML file, we can also wrap collections or other objects */
	class XmlWrappedHashMap extends HashMap<String, String> {
		
		private static final long serialVersionUID = 1L;
		
		public static final String XML_KEY = "key";
		public static final String XML_VALUE = "value";
		public static final String XML_ELEMENT = "mapelement";

		public void toXml(XmlWriter xmlwriter, boolean verbose) throws IOException {
			String clazz = this.getClass().getSimpleName();
			xmlwriter.writelnObjIndentedStartTag(clazz);
			for (String s : keySet()) {
				xmlwriter.writelnObjIndentedStartTag(XML_ELEMENT);
				xmlwriter.writelnIndentedXmlElement(XML_KEY, s);
				xmlwriter.writelnIndentedXmlElement(XML_VALUE, get(s));
				xmlwriter.writelnObjIndentedEndTag(XML_ELEMENT);
			}
			xmlwriter.writelnObjIndentedEndTag(clazz);
		}
	}

	/**
	 * A handler to read in the XML file just written.
	 * Better do this in an own file, this is just an example.
	 */
	class XmlReadHandler extends DefaultHandler2 {	

		private XmlExample main;
		
		// we need the class names (best for debugging, if needed)
		private String xmlA = XmlExample.ObjectA.class.getSimpleName();		
		private String xmlB = XmlExample.ObjectB.class.getSimpleName();
		private String xmlHashMap = XmlWrappedHashMap.class.getSimpleName();
		
		private State state = null;								// the state of parsing a top tag element
	    private Stack<State> stateStack = new Stack<State>();	// a stack of states during parsing
	    private String lastStr;									// the last data string parsed
	    // we need instances for every parsed object or variable
	    private int idA;
	    private String text;
	    private int idB;
	    private String anothertext;
	    private ObjectA objectA;
	    private ObjectB objectB;
	    private XmlWrappedHashMap map;
	    private String key;										// wrapped HashMap
	    private String value;

		public XmlReadHandler(XmlExample example) {
			super();
			this.main = example;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

//			System.out.println("startElement '" + qName + "'");
			if (qName.equals(xmlA)) {
				stateStack.push(state);
				state = State.OBJ_A;
			} else if (qName.equals(xmlB)) {
				stateStack.push(state);
				state = State.OBJ_B;
			} else if (qName.equals(xmlHashMap)) {
				stateStack.push(state);
				state = State.MAP;
				map = new XmlWrappedHashMap();
			} else if (qName.equals(XML_DATA)) {
				state = State.DB;
			}
			// we don't care about the other starting tags
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {

//			System.out.println("endElement '" + qName + "'");
			if (qName.equals(ObjectA.XML_ID)) {
				// id exists for objectA AND objectB, but we have the state machine to decide
				switch (state) {
				case OBJ_A:
					idA = Integer.parseInt(lastStr);
					break;
				case OBJ_B:
					idB = Integer.parseInt(lastStr);
					break;
				default:
					throw new RuntimeException("State " + state + " not implemented");
				}
			} else if (qName.equals(ObjectA.XML_TEXT)) {
				text = lastStr;
			} else if (qName.equals(ObjectB.XML_ANOTHER_TEXT)) {
				anothertext = lastStr;
			} else if (qName.equals(xmlA)) {
				objectA = new ObjectA(idA, text, objectB);
				// now objectA could be set in main
				main.setObjA(objectA);
				state = stateStack.pop();
			} else if (qName.equals(xmlB)) {
				objectB = new ObjectB(idB, anothertext);
				// now objectB could be set in main, but it's only referenced in objectA
				state = stateStack.pop();
			} else if (qName.equals(XmlWrappedHashMap.XML_KEY)) {
				key = lastStr;
			} else if (qName.equals(XmlWrappedHashMap.XML_VALUE)) {
				value = lastStr;
			} else if (qName.equals(XmlWrappedHashMap.XML_ELEMENT)) {
				map.put(key, value);
			} else if (qName.equals(xmlHashMap)) {
				// now objectA could be set in main
				main.setMap(map);
				state = stateStack.pop();
			} else if (qName.equals(XML_DATA)) {
				// do nothing, the data should exist in the memory of main, we are finished
			} else {
				throw new SAXException("endElement(): handling of '" + qName + "' not implemented");
			}
		}

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {

			String str = new String(ch, start, length);
			str = str.trim();
	        if (str.length() == 0) {
	        	// ignore white space
	            return;
	        }
	        lastStr = str;

//			System.out.println("String '" + str + "'");
		}
	}
}
