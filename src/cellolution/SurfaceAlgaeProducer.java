
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

import cellolution.cell.*;
import cellolution.util.*;

/**
 * Sometimes single algae organisms plunge into the ocean through the surface.
 */
public class SurfaceAlgaeProducer {

	private Ocean ocean;
	private Pixel pixels[][];
	private OrganismMgr organismMgr;
	private int cellColumns;
	private int cellRows;
	private long lastTimeAlgaeDropped;
	private int droppedAlgaeCount;

	/**
	 * Producer construction.
	 * 
	 * @param ocean
	 */
	public SurfaceAlgaeProducer(Ocean ocean) {
		
		this.ocean = ocean;
		cellColumns = Main.getCellColumns();
		cellRows = Main.getCellRows();
		pixels = ocean.getPixels();
		organismMgr = ocean.getOrganismMgr();
		lastTimeAlgaeDropped = System.currentTimeMillis();
	}

	/**
	 * Plunges a SingleAlgaeCell into the ocean.
	 */
	private void dropAlgaeOrganism() {

//		if (droppedAlgaeCount == 1) {
//			// for testing only, comment out otherwise
//			return;
//		}
		int row = 1;
		int col = 10 + FastRandom.nextIntStat(cellColumns - 20);
		for (;;) {
			if (pixels[col][row] instanceof Water && !organismMgr.hasCellOn(col, row)) {
				SingleAlgaeCell cell = SingleAlgaeCell.create(col, row, 18000 + FastRandom.nextIntStat(30000));
				cell.getProperties()[AbstractCell.PROP_ORGANIC] = 300 + FastRandom.nextIntStat(2500);
				Organism organism = cell.getOrganism();
				organism.setSpeedAndDirection(3 + FastRandom.nextIntStat(5), 120 + FastRandom.nextIntStat(121));	// 120 to 240 degrees
				droppedAlgaeCount++;
				break;
			}
			col = 10 + FastRandom.nextIntStat(cellColumns - 20);
		}
	}

	/**
	 * Drop anm algae cell from time to time at the surface of the ocean.
	 * 
	 * @param time
	 */
	public void plungeAlgae(long time) {

		if (time - lastTimeAlgaeDropped > 100) {
			if (droppedAlgaeCount < 5 || (droppedAlgaeCount < 25 && FastRandom.nextIntStat(70) == 0) 
					|| FastRandom.nextIntStat(300) == 0) {
				dropAlgaeOrganism();
			}
			lastTimeAlgaeDropped = time;
		}
	}
}
