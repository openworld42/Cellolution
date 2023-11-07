
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
package cellolution.ui;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * A generic JFileChooser dialog.
 */
@SuppressWarnings("serial")
public class FileChooserDlg extends JFileChooser {

	/**
	 * Create the dialog and show it. If the path was set to null, cancel was clicked.
	 * 
	 * @param titel				the dialog title
	 * @param selectionMode		one of JFileChooser.FILES_ONLY, JFileChooser.DIRECTORIES_ONLY, 
	 * 							JFileChooser.FILES_AND_DIRECTORIES
	 * @param directoryToStart	the directory, where the dialog starts to display files
	 * @param filter			e.g. new FileNameExtensionFilter("JPG and GIF", "jpg", "gif") or null if none
	 */
	public FileChooserDlg(String titel, int selectionMode, 
			String directoryToStart, FileNameExtensionFilter filter) {

		initUI();
		// set the text in JFileChooser
//		UIManager.put("FileChooser.openButtonText", openBtnText);
//		UIManager.put("FileChooser.openButtonToolTipText", openBtnToolTip);
		SwingUtilities.updateComponentTreeUI(this);			// update UI
	    setDialogTitle(titel);
	    if (filter != null) {
	    	setFileFilter(filter);
		}
	    setFileSelectionMode(selectionMode);
	    if (directoryToStart != null) {
	    	setCurrentDirectory(new File(directoryToStart));
		}
	    setPreferredSize(new Dimension(600, 400));
	}

	/**
	 * Initialize the user interface.
	 */
	protected void initUI() {
		
		UIManager.put("FileChooser.lookInLabelText", "Look in");
//		UIManager.put("FileChooser.cancelButtonText", "Cancel");
//		UIManager.put("FileChooser.cancelButtonToolTipText", "Cancel");
		UIManager.put("FileChooser.fileNameHeaderText","File name");
		UIManager.put("FileChooser.fileNameLabelText", "File name");
		UIManager.put("FileChooser.filesOfTypeLabelText", "File types");
		UIManager.put("FileChooser.filterLabelText", "File types");
		UIManager.put("FileChooser.homeFolderToolTipText","Home");
		UIManager.put("FileChooser.upFolderToolTipText", "Up one level");
		UIManager.put("FileChooser.newFolderButtonText","Create new folder");
		UIManager.put("FileChooser.newFolderToolTipText","Create new folder");
		UIManager.put("FileChooser.listViewButtonToolTipText","List");
		UIManager.put("FileChooser.renameFileButtonText", "Rename file");
		UIManager.put("FileChooser.deleteFileButtonText", "Delete file");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Details");
		UIManager.put("FileChooser.fileSizeHeaderText","Size");
		UIManager.put("FileChooser.fileDateHeaderText", "Date modified");
	}
}
