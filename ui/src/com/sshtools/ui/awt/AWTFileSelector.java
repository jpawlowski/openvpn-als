
				/*
 *  OpenVPNALS
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
			
package com.sshtools.ui.awt;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.ui.FileFilter;
import com.sshtools.ui.FileSelector;
import com.sshtools.ui.StockIcons;
import com.sshtools.ui.awt.options.Option;
import com.sshtools.ui.awt.options.OptionDialog;
import com.sshtools.util.StringComparator;
import com.sshtools.util.Util;

/**
 *  
 */
public class AWTFileSelector extends Panel implements FileSelector, ActionListener, ItemListener {


    private java.awt.List files;
    private File cwd;
    private TextField path;
    //  private Label cwdLabel;
    private Choice lookIn;
    private ImageButton go;
    private ImageButton remove;
    private ImageButton newFolder;
    private ImageButton home;
    private ImageButton parent;
    private int type;
    private Checkbox showHiddenFiles;
    private Choice filterSelect;
    private Vector filters;
    
    private FileFilter acceptAllFilter = new FileFilter() {

        public String getDescription() {
            return Messages.getString("AWTFileSelector.allFiles"); //$NON-NLS-1$
        }

        public boolean accept(File f) {
            return true;
        }
        
    };
    
    public AWTFileSelector() {
        super(new BorderLayout());
    }

    public void init(int type, File cwd, boolean showButtons, boolean showHiddenFilesSwitch, boolean showButtonImages, boolean showButtonText) {

        // Initialise
        this.cwd = cwd;
        this.type = type;
        filters = new Vector();
        filters.addElement(acceptAllFilter);
        if (cwd == null) {
            cwd = new File(System.getProperty("user.dir")); //$NON-NLS-1$
        }

        // Create the
        files = new SelectList() {

            public Dimension getPreferredSize() {
                return new Dimension(400, 260);
            }

            public void selected() {
                String item = files.getSelectedItem();
                if (item != null) {
                    path.setText(item);
                    selectFile();
                }
            }

        };
        files.addItemListener(this);

        // Create the 'Look In' component
        lookIn = new Choice();
        lookIn.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                AWTFileSelector.this.cwd = new File(lookIn.getSelectedItem());
                refresh();                
            }
            
        });
        rebuildLookIn();

        // Create the tool bar
        Panel z = new Panel(new FlowLayout());
        if (showButtons) {
            home = new ImageButton(showButtonImages ? UIUtil.getStockImage(StockIcons.STOCK_HOME, AWTFileSelector.class) : null, showButtonText ? Messages.getString("AWTFileSelector.home") : null, "home"); //$NON-NLS-1$ //$NON-NLS-2$
            home.setHoverButton(true);
            home.addActionListener(this);
            home.setToolTipText(Messages.getString("AWTFileSelector.navigateToYourHomeDirectory")); //$NON-NLS-1$
            z.add(home);
            parent = new ImageButton(showButtonImages ? UIUtil.getStockImage(StockIcons.STOCK_UP_FOLDER, AWTFileSelector.class) : null, showButtonText ? Messages.getString("AWTFileSelector.home") : null, "home"); //$NON-NLS-1$ //$NON-NLS-2$
            parent.setHoverButton(true);
            parent.addActionListener(this);
            parent.setToolTipText(Messages.getString("AWTFileSelector.navigateToParent")); //$NON-NLS-1$
            z.add(parent);
            newFolder = new ImageButton(showButtonImages ? UIUtil.getStockImage(StockIcons.STOCK_NEW_FOLDER, AWTFileSelector.class) : null, showButtonText ? Messages.getString("AWTFileSelector.new") : null, "newFolder"); //$NON-NLS-1$ //$NON-NLS-2$
            newFolder.setHoverButton(true);
            newFolder.addActionListener(this);
            newFolder.setToolTipText(Messages.getString("AWTFileSelector.createFolder")); //$NON-NLS-1$
            z.add(newFolder);
            remove = new ImageButton(showButtonImages ? UIUtil.getStockImage(StockIcons.STOCK_DELETE, AWTFileSelector.class) : null, showButtonText ? Messages.getString("AWTFileSelector.delete") : null, "delete"); //$NON-NLS-1$ //$NON-NLS-2$
            remove.setHoverButton(true);
            remove.addActionListener(this);
            remove.setToolTipText(Messages.getString("AWTFileSelector.removeSelected")); //$NON-NLS-1$
            z.add(remove);
        }
        if (showHiddenFilesSwitch) {
            showHiddenFiles = new Checkbox(Messages.getString("AWTFileSelector.hiddentFiles"));             //$NON-NLS-1$
            showHiddenFiles.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    refresh();
                }
            });
        }

        // Create the top bar
        Panel top = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        UIUtil.gridBagAdd(top, lookIn, gbc, GridBagConstraints.RELATIVE);
        gbc.weightx = 0.0;
        UIUtil.gridBagAdd(top, z, gbc, GridBagConstraints.REMAINDER);
        
        // Create the path panel
        Panel pathPanel = new Panel(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        path = new TextField(""); //$NON-NLS-1$
        path.addActionListener(this);
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.weightx = 0.0;
        gbc1.insets = new Insets(2, 2, 2, 2);
        UIUtil.gridBagAdd(pathPanel, new Label(Messages.getString("AWTFileSelector.fileName")), gbc1, showButtons ? 1 : GridBagConstraints.RELATIVE); //$NON-NLS-1$
        gbc1.weightx = 1.0;
        UIUtil.gridBagAdd(pathPanel, path, gbc1, showButtons ? GridBagConstraints.RELATIVE : GridBagConstraints.REMAINDER);
        gbc1.weightx = 0.0;
        if(showButtons) {
            go = new ImageButton(null, Messages.getString("AWTFileSelector.go"), "go"); //$NON-NLS-1$ //$NON-NLS-2$
            go.setHoverButton(true);
            go.addActionListener(this);
            go.setToolTipText(Messages.getString("AWTFileSelector.navigateToSelectedFolder")); //$NON-NLS-1$
            UIUtil.gridBagAdd(pathPanel, go, gbc1, GridBagConstraints.REMAINDER);
        }
        UIUtil.gridBagAdd(pathPanel, new Label(Messages.getString("AWTFileSelector.filesOfType")), gbc1, showButtons ? 1 : GridBagConstraints.RELATIVE); //$NON-NLS-1$
        gbc1.weightx = 1.0;
        filterSelect = new Choice();
        rebuildFilterSelect();
        filterSelect.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        });
        UIUtil.gridBagAdd(pathPanel, filterSelect, gbc1, showButtons ? GridBagConstraints.RELATIVE : GridBagConstraints.REMAINDER);
        gbc1.weightx = 0.0;
        if(showButtons) {
            UIUtil.gridBagAdd(pathPanel, new Label(), gbc1, GridBagConstraints.REMAINDER);            
        }

        // Build the main component
        add(top, "North"); //$NON-NLS-1$
        add(files, "Center"); //$NON-NLS-1$
        add(pathPanel, "South"); //$NON-NLS-1$
        refresh();
    }
    
    public void setUseAcceptAllFilter(boolean useAcceptAllFilter) {
        if(useAcceptAllFilter && !filters.contains(acceptAllFilter)) {
            filters.insertElementAt(acceptAllFilter, 0);
        }
        else if(!useAcceptAllFilter && filters.contains(acceptAllFilter)) {
            filters.removeElement(acceptAllFilter);
        }
        rebuildFilterSelect();
    }
    
    public void addFileFilter(FileFilter filter) {
        filters.addElement(filter);
        rebuildFilterSelect();
    }
    
    private void rebuildFilterSelect() {
        filterSelect.removeAll();
        for(Enumeration e = filters.elements(); e.hasMoreElements(); ) {
            FileFilter f = (FileFilter)e.nextElement();
            filterSelect.add(f.getDescription());
        }
    }

    private void rebuildLookIn() {
        File dir = cwd;
        String lastParentPath = null;
        lookIn.removeAll();
        while (dir != null && dir.exists()) {            
            lookIn.add(dir.getAbsolutePath());
            String parentPath = dir.getParent();
            System.out.println("Parent = " + parentPath); //$NON-NLS-1$
            dir = parentPath == null ? null : new File(parentPath);
        }
    }
    
    private void gotoParent() {

        String newPath = cwd.getAbsolutePath();
        if (newPath.endsWith(File.separator)) {
            newPath = newPath.substring(0, newPath.length() - 1);
        }
        int idx = newPath.lastIndexOf(File.separator);
        if (idx != -1) {
            newPath = newPath.substring(0, idx + 1);
            cwd = new File(newPath);
            path.setText(""); //$NON-NLS-1$
            refresh();
        }
    }

    private void selectFile() {
        if (path.getText().equals("..")) { //$NON-NLS-1$
            gotoParent();
        } else {
            File f = new File(path.getText());
            if (!f.isAbsolute())
                f = new File(cwd, path.getText());
            if (f.exists()) {
                if (f.isFile()) {
                    cwd = new File(f.getParent());
                    path.setText(f.getName());
                } else {
                    cwd = f;
                    path.setText(""); //$NON-NLS-1$
                }
                refresh();
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public File[] getSelectedFiles() {
        int[] sel = files.getSelectedIndexes();
        File[] f = new File[sel.length];
        for (int i = 0; i < sel.length; i++) {
            f[i] = new File(cwd, files.getItem(sel[i]).toString());
        }
        return f;
    }

    public File getSelectedFile() {
        File f = new File(path.getText());
        if (f.isAbsolute())
            if (type == 1 && !f.isDirectory())
                return cwd;
            else
                return f;
        if (type == 1 && !f.isDirectory())
            return cwd;
        else
            return new File(cwd, path.getText());
    }

    public void refresh() {
        String l[] = cwd.list();
        rebuildLookIn();
        files.removeAll();
        Vector v = new Vector();
        if (cwd.getParent() != null)
            files.add(".."); //$NON-NLS-1$
        for (int i = 0; l != null && i < l.length; i++) {
            if (showHiddenFiles == null || showHiddenFiles.getState() && l[i].startsWith(".") || !l[i].startsWith(".")) { //$NON-NLS-1$ //$NON-NLS-2$
                if(filters.size() == 0) {
                    v.addElement(l[i]);
                }
                else {
                    for(Enumeration e = filters.elements(); e.hasMoreElements(); ) {
                        FileFilter filter = (FileFilter)e.nextElement();
                        if(filter.getDescription().equals(filterSelect.getSelectedItem())) {
                            File f = new File(cwd, l[i]);
                            if(f.isDirectory() || filter.accept(f)) {
                                v.addElement(l[i]);
                                break;
                            }                            
                        }
                    }
                }
            }
        }
        Util.sort(v, StringComparator.getDefaultInstance());
        for (Enumeration e = v.elements(); e.hasMoreElements();) {
            files.addItem(e.nextElement().toString());
        }
        files.deselect(files.getSelectedIndex());
        doLayout();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == home) {
            cwd = new File(System.getProperty("user.home")); //$NON-NLS-1$
            refresh();
        }
        else if (e.getSource() == parent) {
            gotoParent();
        }
        else if (e.getSource() == remove) {
            File f = getSelectedFile();
            Option choice = OptionDialog.prompt(this, OptionDialog.WARNING, Messages.getString("AWTFileSelector.confirmRemove"), MessageFormat.format(Messages.getString("AWTFileSelector.confirmRemoveText"), //$NON-NLS-1$ //$NON-NLS-2$
                            new Object[] { f.getPath() } ), OptionDialog.CHOICES_YES_NO);
            if (choice == OptionDialog.CHOICE_YES)
                if (!f.delete()) {
                    OptionDialog.error(this, Messages.getString("AWTFileSelector.error"), Messages.getString("AWTFileSelector.failedToRemove")); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    refresh();
                    path.setText(""); //$NON-NLS-1$
                }
        } else if (e.getSource() == newFolder)
            newFolder();
        else
            selectFile();
    }

    private void newFolder() {
        String name = OptionDialog.promptForText(this, Messages.getString("AWTFileSelector.newFolder"), "", null, ' ', Messages.getString("AWTFileSelector.name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (name != null) {
            File f = new File(cwd, name);
            try {
                if (!f.mkdir())
                    throw new IOException(Messages.getString("AWTFileSelector.couldNotCreateDirectory")); //$NON-NLS-1$
                refresh();
            } catch (IOException ioe) {
                OptionDialog.error(this, Messages.getString("AWTFileSelector.error"), ioe.getMessage()); //$NON-NLS-1$
            }
        }
    }

    public void setAllowMultipleSelection(boolean allowMultipleSelection) {
        files.setMultipleMode(allowMultipleSelection);
    }

    public void itemStateChanged(ItemEvent e) {
        String sel = files.getSelectedItem();
        if (sel != null && sel.equals("..")) { //$NON-NLS-1$
            String parent = getWorkingDirectory().getParent();
            path.setText(parent);
        }
        path.setText(sel != null ? sel : ""); //$NON-NLS-1$
    }

    public Option showDialog(Component parent, String title) {
        Option choice = OptionDialog.prompt(parent, OptionDialog.UNCATEGORISED, title, this, OptionDialog.CHOICES_OK_CANCEL, null,  showHiddenFiles);
        return choice;
    }

    public File getWorkingDirectory() {
        return cwd;
    }

    /**
     * @param filter
     */
    public void setSelectedFileFilter(FileFilter filter) {
        int idx = filters.indexOf(filter);
        if(idx != -1) {
            filterSelect.select(idx);
        }
    }

    /**
     * @param cwd2
     */
    public void setWorkingDirectory(File cwd) {
        this.cwd = cwd;
        refresh();        
    }

    public static void main(String[] args) {
    }
}