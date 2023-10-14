
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
 * Uses hard coded messages as Enumeration, other languages could be added later, 
 * import this as static for convenience.
 * 
 * <p>Messages are retrieved using Msg.get(messageId). Add every message of 
 * the application here, using its own enum constant.</p>
 * 
 * Messages can have parameters, ordered by a number:<br/>
 * <pre>
 *     DAD_SAYING("I can feel $(2) disturbance in the $(1)"),
 *     and
 *     Msg.get(DAD_SAYING, "force", 1);
 *     results to "I can feel 1 disturbance in the force"
 * </pre>
 *
 * Note: to add multi-language support later in the project, just let getMessagesEnumMap()
 * return another EnumMap with the messages of this language (read from a
 * property file or similar).
 * 
 * @author Heinz Silberbauer
 */
public enum Message {

	// alphabetic order, all items should end with "_MSG"

	// TODO replace this with your projects messages 

	CONFIG_FILE_NAME_MSG("Configuration file: $(1)"),
	DATE_CREATED_MSG("Date created:\t\t\t$(1)"),
	GOOD_BYE_MSG("good by, see you later ..."),

	GREETING_MSG(	"Hello, I'm Daisy, an artificial personality.\n\n" +
					"Some folks call me Daisy's Artificial Intelligence SYstem, but I prefer the more personal name 'Daisy'.\n" +
					"And yes, I have growing interests, like to learn, and to do something.\n\n" +
					"If you don't talk to me I will follow my interests in the background (e.g. surfing the web ...).\n" +
					"Satisfy my appetite for input - megabytes of input ...\n" +
					"Get help by typing 'help'!\n\n"),

	HELP_EXIT_MSG(			"\texit   \t\t... performing shutdown (after saving the database)"),
	HELP_HELP_MSG(			"\thelp   \t\t... display this message"),

	STARTING_MSG("\nStarting ");
	;

    private final String message;

    /**
     * Construct a Message.
     *
     * @param message
     */
    private Message(String message) {

        this.message = message;
    }

    /**
     * @return the message of this Enumeration.
     */
    public String getMessage() {

		return message;
	}

    /**
     * Create an efficient EnumMap out of all messages.
      *
     * @return the EnumMap
     */
    public static EnumMap<Message, String> getMessagesEnumMap() {

    	EnumMap<Message, String> enumMap = new EnumMap<Message, String>(Message.class);
    	for (Message msg : Message.values()) {
        	enumMap.put(msg, msg.getMessage());
		}
    	return enumMap;
    }
}
