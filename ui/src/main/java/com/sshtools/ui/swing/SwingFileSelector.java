
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.sshtools.ui.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import com.sshtools.ui.FileFilter;
import com.sshtools.ui.FileSelect;
import com.sshtools.ui.FileSelector;
import com.sshtools.ui.awt.options.Option;
import com.sshtools.ui.awt.options.OptionDialog;

/**
 * @author Brett Smith <brett@localhost>
 */
public class SwingFileSelector implements FileSelector {

    private JFileChooser chooser;
    private HashMap filters = new HashMap();
    
    public SwingFileSelector() {
        chooser = new JFileChooser();
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#init(int, java.io.File, boolean, boolean, boolean, boolean)
     */
    public void init(int type, File cwd, boolean showButtons, boolean showHiddenFilesSwitch, boolean showButtonImages,
                    boolean showButtonText) {
        chooser.setApproveButtonText("Ok"); // TODO FileSelect does not recognise dialog type (i.e. Open / Save)
        setWorkingDirectory(cwd);
        if(type == FileSelect.DIRECTORIES_ONLY) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        else {
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        if(showHiddenFilesSwitch) {
            final JCheckBox hiddenFiles = new JCheckBox("Show hidden files", false);
            hiddenFiles.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    chooser.setFileHidingEnabled(!hiddenFiles.isSelected());
                }
            });
            chooser.setAccessory(hiddenFiles);
        }
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#setUseAcceptAllFilter(boolean)
     */
    public void setUseAcceptAllFilter(boolean useAcceptAllFilter) {
        chooser.setAcceptAllFileFilterUsed(useAcceptAllFilter);
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#addFileFilter(com.sshtools.ui.FileFilter)
     */
    public void addFileFilter(final FileFilter filter) {
        javax.swing.filechooser.FileFilter swingFilter = 
            new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                return filter.accept(f);
            }

            public String getDescription() {
                return filter.getDescription();
            }
        };
        filters.put(filter, swingFilter);
        chooser.addChoosableFileFilter(swingFilter);
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#getSelectedFiles()
     */
    public File[] getSelectedFiles() {
        return chooser.getSelectedFiles();
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#getSelectedFile()
     */
    public File getSelectedFile() {
        return chooser.getSelectedFile();
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#refresh()
     */
    public void refresh() {
        // TODO refreshable?
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#setAllowMultipleSelection(boolean)
     */
    public void setAllowMultipleSelection(boolean allowMultipleSelection) {
        chooser.setMultiSelectionEnabled(allowMultipleSelection);
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#showDialog(java.awt.Component, java.lang.String)
     */
    public Option showDialog(Component parent, String title) {
        chooser.setDialogTitle(title);
        int opt = chooser.showDialog(parent, "Ok");
        if(opt == JFileChooser.APPROVE_OPTION) {
            return OptionDialog.CHOICE_OK;
        }
        else {
            return OptionDialog.CHOICE_CANCEL;
        }
        
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#getWorkingDirectory()
     */
    public File getWorkingDirectory() {
        return chooser.getCurrentDirectory();
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#setSelectedFileFilter(com.sshtools.ui.FileFilter)
     */
    public void setSelectedFileFilter(FileFilter filter) {
        javax.swing.filechooser.FileFilter swingFilter = 
            (javax.swing.filechooser.FileFilter)filters.get(filter);
        if(swingFilter != null) {
            chooser.setFileFilter(swingFilter);
        }
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.FileSelector#setWorkingDirectory(java.io.File)
     */
    public void setWorkingDirectory(File cwd) {
        chooser.setCurrentDirectory(cwd);
    }

}
