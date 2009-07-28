/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.ui.swing.options;

/**
 * 
 * 
 * @author $author$
 */
public class Option {
    public static final Option YES = new Option("Yes", "Yes", 'y');
    public static final Option NO = new Option("No", "No", 'n');
    
    private String text;
    private String toolTipText;
    private int mnemonic;

    /**
     * Creates a new Option object.
     * 
     * @param text
     * @param toolTipText
     * @param mnemonic
     */
    public Option(String text, String toolTipText, int mnemonic) {
        this.text = text;
        this.toolTipText = toolTipText;
        this.mnemonic = mnemonic;
    }

    /**
     * 
     * 
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * 
     * @return
     */
    public int getMnemonic() {
        return mnemonic;
    }

    /**
     * 
     * 
     * @return
     */
    public String getToolTipText() {
        return toolTipText;
    }
}
