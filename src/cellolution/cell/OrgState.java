
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
	
	/** OrgState: growing "child" during replication, no color in general */
    GROWING(null),
	/** OrgState: alive, no color in general */
    ALIVE(null),
	/** OrgState: organism replicates itself, a "child" will be built  */
    IN_REPLICATION(Color.GREEN),
	/** OrgState: the organism is starving */
    STARVING(Color.ORANGE),
	/** OrgState: the organism is dying, caused by starving too long */
    DYING(Color.LIGHT_GRAY),
	/** OrgState: the organism is dead */
    DEAD(Color.DARK_GRAY),
	/** OrgState: the dead organism is decomposing, its matter is dissolved */
    DECOMPOSING(Color.BLACK),
    ;

	/** an EnumSet of all states */
	public static final EnumSet<OrgState> ALL = EnumSet.allOf(OrgState.class);
	/** an array of all states */
	public static final OrgState[] ARRAY = ALL.toArray(new OrgState[0]);

	/** the color of the state, if any: sometimes it is computed */
	private Color color;

	/**
	 * Construction of one of the states.
	 * 
	 * @param colorRGB		the color or null
	 */
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

