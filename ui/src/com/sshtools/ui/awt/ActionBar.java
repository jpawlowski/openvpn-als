package com.sshtools.ui.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Panel;
import java.awt.SystemColor;

/**
 * A toolbar implementation that creates buttons from a set of {@link Action}.
 * 
 * @author $Autho$
 */
public class ActionBar extends Panel {

    private Color baseBackground, baseForeground;

    public ActionBar() {
        super();
        Separator separator = new Separator(Separator.HORIZONTAL);
        setLayout(new ToolLayout(separator));
        setBackground(SystemColor.control);
        setForeground(SystemColor.controlText);
        add(separator);
    }
    
    public void add(ActionButton button) {
        if (baseBackground != null) {
            button.setBaseBackground(baseBackground);
        }
        if (baseForeground != null) {
            button.setBaseForeground(baseForeground);
        }
        super.add(button);
    }

    public void addAction(Action action) {
        add(new ActionButton(action));
    }

    public void addSeparator() {
        add(new Separator(Separator.VERTICAL));
    }

    public void setBaseBackground(Color baseBackground) {
        setBackground(baseBackground == null ? SystemColor.control : baseBackground);
        this.baseBackground = baseBackground;
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof ActionButton) {
                ((ActionButton) c).setBaseBackground(baseBackground);
            }
        }
    }

    public void setBaseForeground(Color baseForeground) {
        setForeground(baseForeground == null ? SystemColor.controlText : baseForeground);
        this.baseForeground = baseForeground;
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof ActionButton) {
                ((ActionButton) c).setBaseForeground(baseForeground);
            }
        }
    }

}