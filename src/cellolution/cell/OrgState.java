
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
package cellolution.cell;

import java.awt.*;
import java.util.EnumSet;

/**
 * Enumeration of the states an organism may have.
 */
public enum OrgState {
	
    GROWING(null),					// the growing "child" during replication, no color in general
    ALIVE(null),					// no color in general
    IN_REPLICATION(Color.GREEN),
    STARVING(Color.ORANGE),
    DYING(Color.LIGHT_GRAY),
    DEAD(Color.DARK_GRAY),
    DECOMPOSING(Color.BLACK),
    ;

	public static final EnumSet<OrgState> ALL = EnumSet.allOf(OrgState.class);
	public static final OrgState[] ARRAY = ALL.toArray(new OrgState[0]);

	private Color color;

	OrgState(Color colorRGB) {
		
		this.color = colorRGB;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		
		return color;
	}
}

