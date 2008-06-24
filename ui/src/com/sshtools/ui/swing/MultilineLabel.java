/* HEDAER */

package com.sshtools.ui.swing;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Swing component that takes a string, splits it up into lines based on the
 * newline character and displays each line.
 * 
 * @author $Author: brett $
 */

public class MultilineLabel extends JPanel {

    // Private instance variables

    private GridBagConstraints constraints;

    private String text;

    /**
     * Creates a new MultilineLabel object.
     */

    public MultilineLabel() {

        this(""); //$NON-NLS-1$

    }

    /**
     * Creates a new MultilineLabel object.
     * 
     * @param text
     */

    public MultilineLabel(String text) {

        super(new GridBagLayout());

        constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.fill = GridBagConstraints.NONE;

        setText(text);

    }

    /**
     * Set the font
     * 
     * @param f font
     */

    public void setFont(Font f) {

        super.setFont(f);

        for (int i = 0; i < getComponentCount(); i++) {

            getComponent(i).setFont(f);

        }

    }

    /**
     * Set the font
     * 
     * @param text
     */

    public void setText(String text) {

        this.text = text;

        removeAll();

        StringTokenizer tok = new StringTokenizer(text, "\n"); //$NON-NLS-1$

        constraints.weighty = 0.0;

        constraints.weightx = 1.0;

        while (tok.hasMoreTokens()) {

            String t = tok.nextToken();

            if (!tok.hasMoreTokens()) {

                constraints.weighty = 1.0;

            }

            UIUtil.jGridBagAdd(this, new JLabel(t), constraints,

            GridBagConstraints.REMAINDER);

        }

        revalidate();

        repaint();

    }

    /**
     * Get the text
     * 
     * @return text
     */

    public String getText() {

        return text;

    }

}
