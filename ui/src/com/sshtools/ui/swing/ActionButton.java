
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

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

public class ActionButton extends JButton {
    // Private statics
    private final static Insets INSETS = new Insets(0, 0, 0, 0);
    // Private instance variables
    private boolean hideText;
    private boolean enablePlasticWorkaround;
    private Color hoverForeground, oldForeground;

    public ActionButton(AppAction action) {
        this(action, true);
    }

    public ActionButton(AppAction action, boolean useLargeIcon) {
        this(action, useLargeIcon ? AppAction.LARGE_ICON : AppAction.SMALL_ICON, true);
    }

    public ActionButton(AppAction action, boolean useLargeIcon, boolean showSelectiveText) {
        this(action, useLargeIcon ? AppAction.LARGE_ICON : AppAction.SMALL_ICON, showSelectiveText);
        
    }

    public ActionButton(AppAction action, String iconKey, boolean showSelectiveText) {
        super();
        init(action, iconKey, showSelectiveText);
    }

    private void init(AppAction a, final String iconKey, boolean showText) {
        enablePlasticWorkaround = UIManager.getLookAndFeel().getClass().getName().startsWith("com.jgoodies.looks.plastic.");
        setAction(a);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    if(hoverForeground != null) {
                        oldForeground = getForeground();
                        setForeground(hoverForeground);
                    }
                    setBorderPainted(true);
                    if (!enablePlasticWorkaround) {
                        setContentAreaFilled(true);
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                setBorderPainted(false);
                setContentAreaFilled(enablePlasticWorkaround);
                if(oldForeground != null) {
                    setForeground(oldForeground);
                    oldForeground = null;
                }
            }
        });
        a.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(iconKey)) {
                    Icon icon = (Icon) evt.getNewValue();
                    ActionButton.this.setIcon(icon);
                    ActionButton.this.invalidate();
                    ActionButton.this.repaint();
                }
            }
        });
        setBorderPainted(false);
        setContentAreaFilled(enablePlasticWorkaround);
        if (a != null && a.getValue(Action.ACCELERATOR_KEY) != null) {
            setMnemonic(0);
            registerKeyboardAction(a, (KeyStroke) a.getValue(Action.ACCELERATOR_KEY), JButton.WHEN_IN_FOCUSED_WINDOW);
        }
        setIcon((Icon) a.getValue(iconKey));
        if (Boolean.FALSE.equals(a.getValue(AppAction.TEXT_ON_TOOLBAR)) || !showText) {
            setHideText(true);
        } else {
            setHideText(false);
        }
        setVerticalTextPosition(JButton.BOTTOM);
        setHorizontalTextPosition(JButton.CENTER);
    } 

    public Insets getMargin() {
        return INSETS;
    }

    public boolean isRequestFocusEnabled() {
        return false;
    }

    public boolean isFocusTraversable() {
        return false;
    }

    public void setHideText(boolean hideText) {
        if (this.hideText != hideText) {
            firePropertyChange("hideText", this.hideText, hideText);
        }
        this.hideText = hideText;
        this.setHorizontalTextPosition(ActionButton.RIGHT);
        repaint();
    }

    public String getText() {
        return hideText ? null : super.getText();
    }

    public void setHoverForeground(Color hoverForeground) {
        this.hoverForeground = hoverForeground;        
    }
}