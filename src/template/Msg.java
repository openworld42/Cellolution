
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

import java.util.*;

/**
 * Msg (short for Message): a utility to provide strings/messages for other languages.
 * At the beginning, messages are hard coded and may be replaced later by
 * database text or a resources bundle, containing other messages of other languages.
 * 
 * This approach is a simple message system for an easy startup with the ability to extend
 * it later for other languages (just store another EnumMap in the class Message of the other language).
 * 
 * Msg is always combined with the enumeration class Message, containing the tags and
 * their string values.
 *
 * @see Message
 */
public class Msg {

	private static Msg instance = new Msg();				// singleton instance

	private EnumMap<Message, String> msgEnumMap;			// a map containing all messages

	/**
	 * Singleton creation using hard coded messages.
	 */
	private Msg() {

		msgEnumMap = Message.getMessagesEnumMap();
	}

	/**
	 * @return the message by id (its Enumeration)
	 */
	public static String get(Message msg) {

		return instance.msgEnumMap.get(msg);
	}

	/**
	 * @return the message with parameters by id (its Enumeration)
	 */
	public static String get(Message msg, Object ... args) {

		String s = instance.msgEnumMap.get(msg);
		return Util.stringParam(s, args);
	}

 	/**
	 * @return the singleton instance
	 */
	public static Msg instance() {

		return instance;
	}

	/**
	 * Replace the current ui/system/status/info/error messages with another language or source.
	 *
	 * @param enumMap		set another enumMap for the messages (usually for other languages)
	 */
	public static void replaceMessages(EnumMap<Message, String> enumMap) {

//		Logger.info(getClass().getSimpleName() + ": replacing messages ...");

		instance.msgEnumMap = enumMap;
	}
}
