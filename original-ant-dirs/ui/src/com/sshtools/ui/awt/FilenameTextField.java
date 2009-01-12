
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
			
package com.sshtools.ui.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.ui.FileFilter;
import com.sshtools.ui.FileSelect;
import com.sshtools.ui.awt.options.Option;
import com.sshtools.ui.awt.options.OptionDialog;

/**
 * @author brett
 */
public class FilenameTextField extends Panel  {

    private TextField textField;
    private ImageButton chooserButton;
    private BorderPanel borderPanel;
    private Vector listeners;
    private FileSelect chooser;

    public FilenameTextField(int columns) {
        this("", columns); //$NON-NLS-1$
    }

    public FilenameTextField(String text) {
        this(text, (text != null) ? text.length() : 0);
    }

    public FilenameTextField(String text, int columns) {
        this(text, columns, null);
    }

    public FilenameTextField(String text, int columns, Image chooserButtonImage) {
        this(text, columns, chooserButtonImage, Messages.getString("FilenameTextField.selectAFile")); //$NON-NLS-1$
    }

    public FilenameTextField(String text, int columns, Image chooserButtonImage, String chooserToolTipText) {
        super(new BorderLayout(4, 1));
        chooserButton = new ImageButton(chooserButtonImage, text, Messages.getString("FilenameTextField.browse")); //$NON-NLS-1$
        chooserButton.setToolTipText(chooserToolTipText);
        chooserButton.setHorizontalAlignment(ImageButton.CENTER_ALIGNMENT);
        chooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showChooser();
            }
        });
        textField = new TextField(text, columns) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, chooserButton.getPreferredSize().height);
            }

            public Dimension getMinimumSize() {
                return new Dimension(super.getMinimumSize().width, chooserButton.getMinimumSize().height);
            }
        };
        textField.setFont(new Font("Arial", Font.PLAIN, 12)); //$NON-NLS-1$

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireActionEvent(e);                
            }            
        });
        chooserButton.setHoverButton(true);
        add(textField, BorderLayout.CENTER);
        add(chooserButton, BorderLayout.EAST);
    }
    
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        chooserButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }
    
    public void addFileFilter(FileFilter filter) {
        createChooser();
        chooser.addFileFilter(filter);
    }
    
    private void fireActionEvent(ActionEvent evt) {
        for (Enumeration en = listeners.elements(); en.hasMoreElements();) {
            ActionListener l = (ActionListener)en.nextElement();
            l.actionPerformed(evt);
        }
    }
    
    private void createChooser() {
        if(chooser == null) {
            File cwd = getText() == null ? null : new File(getText());
            if(cwd == null || !cwd.exists()) {
                cwd = new File(System.getProperty("user.home")); //$NON-NLS-1$
            }
            chooser = new FileSelect(FileSelect.FILES_AND_DIRECTORIES, 
                            cwd, true, true);
        }
    }
    
    private void showChooser() {
        createChooser();
        File cwd = new File(getText());
        if(!cwd.exists()) {
            cwd = new File(System.getProperty("user.home")); //$NON-NLS-1$
        }
        chooser.setWorkingDirectory(cwd);
        Option option = chooser.showDialog(this, Messages.getString("FilenameTextField.selectAFile")); //$NON-NLS-1$
        if(option == OptionDialog.CHOICE_OK) {
            setText(chooser.getSelectedFile().getAbsolutePath());            
        }
        textField.requestFocus();
    }
    
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * @param l
     */
    public synchronized void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    /**
     * @return
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * @param textFieldBackgroundColor
     */
    public void setTextFieldBackground(Color textFieldBackgroundColor) {
        textField.setBackground(textFieldBackgroundColor);        
    }

    /**
     * @param filter
     */
    public void setSelectedFileFilter(FileFilter filter) {
        createChooser();
        chooser.setSelectedFileFilter(filter);
    }

}