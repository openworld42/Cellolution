
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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import org.json.*;

import cellolution.cell.*;
import cellolution.ui.*;
import cellolution.util.*;

/**
 * Ocean: the water world all cells are living in.
 * An ocean consists of an array of pixels, each being either rock, or water or a part of a living cell.
 * 
 * The shape of a pixel is a regular hexgon. Therefore, the distances within the structure are different from squares.
 * 
 * <pre>
 * If each "x" (dot size 2x2) is the center of a hexagon on a quad paper (sketched paper):
 * 
 *  Pixel cell, visual dots on screen are    ..	  
 *                                           .. 
 *                             
 * hexagon neighbour cells:		6 1       columns:	 odd row 	0 1      
 *                             5 C 2                even row   0 1 2	 1 2
 *                              4 3                   odd row   0 1    	0 1 2
 *                              					even row	   		 1 2
 *                              
 *  reduced to Cellolution representation (each cell has 2*2 dots, each "x" is the center of a hexagon):
 *  
 *  x.x.x.X.x.x   -> pixel row 0 (even, dot row 0 + 1)    x: 0 1 2 3 4   instead of 0 2 4 6 8
 *  .x.x.x.x.x.   -> pixel row 1 (odd,  dot row 2 + 3)    x: 0 1 2 3 4   instead of 1 3 5 7 9
 *  x.x.x.x.x.x   -> pixel row 2 (even, dot row 4 + 5)    x: 0 1 2 3 4   instead of 0 2 4 6 8
 *  
 * Pixels (column/row):
 * row 0: dot row 0+1    | | | |      cells:   (0/0 to 1/1)   (2/0 to 3/1)   (4/0 to 5/1)
 * row 1: dot row 2+3     | | | |     cells:   (1/2 to 2/3)   (3/2 to 4/3)   (5/2 to 6/3)
 * row 2: dot row 4+5    | | | |      cells:   (0/4 to 1/5)   (2/4 to 3/5)   (4/4 to 5/5)
 *                      
 * </pre>
 */
public class Ocean {

	private OceanPanel oceanPanel;
	private int step;											// # of evolution steps
	private int statusLineChangeStopCounter;
	private int cellColumns;
	private int cellRows;
	private BufferedImage image;
	private Pixel pixels[][];
	private Sunshine sunshine;
	private OceanBorders oceanBorders;
	private Smokers smokers;
	private SurfaceAlgaeProducer algaeProducer;
	private Diffusion diffusion;	
	private int organicMatterReservoir;							// the amount of organic matter in the ocean should stay constant
	private OrganismMgr organismMgr;
	private OrganismDisplayCtlr orgDisplayCtlr;
	private SwingWorker<Object, Object> oceanSimSwingWorker;
	private boolean stopSwingWorker;							// set on stopping SwingWoker

	/**
	 * Construct the ocean.
	 * 
	 * @param cellRows 			number of rows for cells
	 * @param cellColumns 		number of columns for cells
	 * @param bufferedImage 	a buffered image of the ocean
	 */
	public Ocean(int cellColumns, int cellRows, BufferedImage bufferedImage) {

		this.cellColumns = cellColumns;
		this.cellRows = cellRows;
		new FastRandom();									// needs initialization
		pixels = new Pixel[cellColumns][cellRows];			// an ocean contains an array of pixels
		// scale the image
		if (bufferedImage != null) {
	       	image = new BufferedImage(cellColumns * 2, cellRows * 2, bufferedImage.getType());
	        Graphics2D g2d = image.createGraphics();
	        g2d.drawImage(bufferedImage, 0, 0, cellColumns * 2, cellRows * 2, null);
	        g2d.dispose();
	        // create the pixels from image
			for (int col = 0; col < cellColumns; col++) {
				for (int row = 0; row < cellRows; row++) {
					int rgb = image.getRGB(col * 2, row * 2) & 0xffffff;
					if (rgb == 0) {
						pixels[col][row] = new Water(col, row);
						setPixelRGB(col, row,  Water.RGB_DEFAULT);
					} else {
						pixels[col][row] = new Rock(col, row, rgb);
					}
				}
			}
		} else {
			image = new BufferedImage(cellColumns * 2, cellRows * 2, BufferedImage.TYPE_INT_RGB);
			createRockAndWater();
			// fill in the image
			for (int col = 0; col < cellColumns; col++) {
				for (int row = 0; row < cellRows; row++) {
					int rgb = pixels[col][row] instanceof Water ? Color.WHITE.getRGB() : Rock.RGB_DEFAULT;
					setPixelRGB(col, row,  rgb);
				}
			}
		}
		// set two columns at the left and the right side as rock, to avoid index out of range later (computing meighbors)
		int lastCol = cellColumns - 1;
		for (int col = 0; col < 2; col++) {
			for (int row = 0; row < cellRows; row++) {
				pixels[col][row] = new Rock(col, row);
				pixels[lastCol - col][row] = new Rock(col, row);
			}
		}
		JSONObject jsonSimObj = Main.getData().getSimObject();
		// the amount of organic matter in the ocean should stay constant, initialize the reservoir
		if (jsonSimObj == null) {
			// create as a new simulation
			organicMatterReservoir = (cellColumns + cellRows * 2) * 50;
		} else {
			// create from an existing simulation (file)
			JSONObject jsonOcean = jsonSimObj.getJSONObject(Keys.OCEAN);
			organicMatterReservoir = jsonOcean.getInt(Keys.ORGANIC_MATTER_RESERVOIR);
		}
		sunshine = new Sunshine(this);
		smokers = new Smokers(this, 3);	// has to be created before OceanBorders, due to changes of the borders
		oceanBorders = new OceanBorders(this);	// this also creates rock pixels on the left side, the right side and the bottom
		diffusion = new Diffusion(this);
		organismMgr = new OrganismMgr(this);
		algaeProducer = new SurfaceAlgaeProducer(this);
		orgDisplayCtlr = Main.getOrgDisplayCtlr();
		initMatterValues();
	}

	/**
	 * Adds (or subtracts) organic matter to the reservoir of the ocean.
	 * 
	 * @param amount		the amount of organic matter to add (or subtract, if negative)
	 */
	public void addToOrganicMatterReservoir(int amount) {
		
		organicMatterReservoir += amount;
	}

	/**
	 * Create the surrounding rocky environment and the water of the ocean.
	 */
	private void createRockAndWater() {

		int maxWidening = 3;
		int maxThickness = 15;
		int minThickness = 5;
		// left and right rock thickness
		int left = FastRandom.nextIntStat(7) + 1;						// left and right rock thickness
		int right = FastRandom.nextIntStat(7) + 1;
		// from top (surface) to the bottom
		for (int row = 0; row < cellRows; row++) {
			// left side (rock)
			for (int col = 0; col < left; col++) {
				pixels[col][row] = new Rock(col, row);
			}
			// middle (water)
			for (int col = left; col < cellColumns - right; col++) {
				pixels[col][row] = new Water(col, row);
			}
			// right side (rock)
			for (int col = cellColumns - right; col < cellColumns; col++) {
				pixels[col][row] = new Rock(col, row);
			}
			// vary the rock thickness left and right
			left = nextThickness(left, maxWidening, maxThickness, minThickness);
			right = nextThickness(right, maxWidening, maxThickness, minThickness);
		}
		// now the bottom
		maxThickness = 20;
		minThickness = 8;
		int bottom = FastRandom.nextIntStat(7) + 1;						// bottom rock thickness
		for (int col = 0; col < cellColumns; col++) {
			for (int row = 2; row < bottom; row++) {
				pixels[col][cellRows - row] = new Rock(col, row);
			}
			bottom = nextThickness(bottom, maxWidening, maxThickness, minThickness);
		}
	}

	/**
	 * @return the organicMatterReservoir
	 */
	public int getOrganicMatterReservoir() {
		
		return organicMatterReservoir;
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		
		return image;
	}
	/**
	 * @return the oceanBorders
	 */
	public OceanBorders getOceanBorders() {
		
		return oceanBorders;
	}

	/**
	 * @return the organism manager
	 */
	public OrganismMgr getOrganismMgr() {
		
		return organismMgr;
	}

	/**
	 * @return the pixels
	 */
	public Pixel[][] getPixels() {
		
		return pixels;
	}

	/**
	 * @return the smokers
	 */
	public Smokers getSmokers() {

		return smokers;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		
		return step;
	}
	
	/**
	 * @return the sunshine
	 */
	public Sunshine getSunshine() {
		
		return sunshine;
	}

	/**
	 * Initialize the values of dissolved substances for all water pixels.
	 */
	public void initMatterValues() {
		
		for (int col = 0; col < cellColumns; col++) {
			for (int row = 0; row < cellRows; row++) {
				Pixel pixel = pixels[col][row];
				if (pixel instanceof Water) {
					((Water) pixel).initMatterValues();
				}
			}
		}
	}

	/**
	 * Return true if the pixel is Water and not cell of an organism, false otherwise.
	 * 
	 * @param col
	 * @param row
	 * @return true if the pixel is Water and not cell of an organism, false otherwise
	 */
	public boolean isWater(int col, int row) {

		if (!(pixels[col][row] instanceof Water)) {
			return false;
		}
		// a cell in any organism?
		return !organismMgr.hasCellOn(col, row);
	}

	/**
	 * A mouse released event happened within the oceans frame.
	 * 
	 * @param event			the event
	 */
	public void mouseReleased(MouseEvent event) {
		
		try {
			int x = event.getPoint().x;
			int y = event.getPoint().y;
			Pixel pixel = pixels[x / 2][y / 2];
			if (SwingUtilities.isLeftMouseButton(event)) {
				Main.instance().getMainView().setStatusText("Step: " + step + "     " + pixel.toString());
				statusLineChangeStopCounter = 5;
			} else if (SwingUtilities.isRightMouseButton(event)) {
				organismMgr.findAndDisplayNearestOrganism(pixel);
			}
		} catch (Exception e) {
			// intentionally do nothing
		}
	}

	/**
	 * Create the next thickness of rock.
	 * 
	 * @param value
	 * @param maxWidening
	 * @param maxThickness
	 * @param minThickness
	 * @return
	 */
	private int nextThickness(int value, int maxWidening, int maxThickness, int minThickness) {
		
		int widening = FastRandom.nextIntStat(maxWidening);
		value += (FastRandom.nextIntStat() & 1) == 0 ? widening : -widening;
		if (value <= minThickness) return minThickness;
		else if (value > maxThickness) return --value;
		return value;
	}

	/**
	 * Sets the RGB value of the buffered image (only, this does not change the ocean's pixel array).
	 * 
	 * @param column
	 * @param row
	 * @param rgb			the RGB value of the image pixel
	 */
    public void setPixelRGB(int column, int row, int rgb) {
    	
		int imgCol = column * 2;
		int imgRow = row * 2;
		image.setRGB(imgCol, imgRow, rgb);
		image.setRGB(imgCol + 1, imgRow, rgb);
		image.setRGB(imgCol, imgRow + 1, rgb);
		image.setRGB(imgCol + 1, imgRow + 1, rgb);
    }

	/**
	 * Start the simulation ON a SwingWorker thread.
	 */
	private void start() {
		
		stopSwingWorker = false;
		long lastTimeRepainted = System.currentTimeMillis();
		long lastTimeOrgDisplayUpdated = lastTimeRepainted;
		oceanPanel.repaint();
		for (step = 1; ; step++) {
			long time = System.currentTimeMillis();
			if (step % 50 == 0) {
				if (statusLineChangeStopCounter > 0) {
					statusLineChangeStopCounter--;
				} else {
					Main.getMainView().setStatusText("Step: " + step);
					
//					Runtime rt = Runtime.getRuntime();
//					System.out.println("Mem: " + (rt.totalMemory() - rt.freeMemory()));
					
				}
			}
			sunshine.next(cellColumns, pixels, time);
			smokers.smoke(time);
			algaeProducer.plungeAlgae(time);
			organismMgr.moveOrganisms(time);
			organismMgr.organismsOneStepOfLife(time);
			organismMgr.slowUpdate(time);
			diffusion.nextOceanDiffusionStep(step);
			if (step % 3 == 0) {
				// be polite to the others
				Util.sleep(1);
			}
			if (time - lastTimeRepainted > 100) {
				oceanPanel.repaint();
				lastTimeRepainted = time;
			}
			if (time - lastTimeOrgDisplayUpdated > 400) {
				orgDisplayCtlr.displayAndRepaint();
				lastTimeOrgDisplayUpdated = time;
			}
			if (stopSwingWorker) {
				return;
			}
		}
	}

	/**
	 * Start a SwingWorker to perform all ocean simulation steps.
	 * 
	 * @param oceanPanel
	 */
	@SuppressWarnings("unchecked")
	public void startSwingWorker(OceanPanel oceanPanel) {
		
		this.oceanPanel = oceanPanel;
		orgDisplayCtlr = Main.getOrgDisplayCtlr();		// due to late instantiation
		oceanPanel.set(this, smokers, organismMgr, orgDisplayCtlr);
		oceanSimSwingWorker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				try {
					start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		oceanSimSwingWorker.execute();
	}
	
	/**
	 * Stops the ocean simulation SwingWorker.
	 */
	public void stopSwingWorker() {
		
		stopSwingWorker = true;
		for (int i = 0; i < 5000; i++) {
			if (oceanSimSwingWorker.isDone()) {
				Util.verbose(Main.APP_NAME + " - simulation stopped, saving results ...");
				return;
			}
			Util.sleep(1);
		}
		throw new RuntimeException("Ocean: could not stop SwingWorker!");
	}

	/**
	 * Creates a JSONObject from this object.
	 * 
	 * @return the JSONObject containing the data of this object
	 */
	public JSONObject toJSONObject() {
		
		JSONObject jsonOcean = new JSONObject();
		jsonOcean.put(Keys.ORGANIC_MATTER_RESERVOIR, organicMatterReservoir);
		jsonOcean.put(Keys.SMOKERS, smokers.toJSONArray());
		JSONArray jsonOrganisms = organismMgr.toJSONArray();
		jsonOcean.put(Keys.ORGANISMS, jsonOrganisms);
		return jsonOcean;
	}
}
