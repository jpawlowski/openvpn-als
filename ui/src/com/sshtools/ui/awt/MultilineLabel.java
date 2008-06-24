/* HEDAER */
package com.sshtools.ui.awt;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.util.StringTokenizer;

/**
 * Swing component that takes a string, splits it up into lines based on the
 * newline character and displays each line.
 * 
 * @author $Author: lee $
 */
public class MultilineLabel extends Panel {
  //  Private instance variables
  private GridBagConstraints constraints;

  private String text;
  private int alignment;

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
    invalidate();
    removeAll();
    StringTokenizer tok = new StringTokenizer(text, "\n"); //$NON-NLS-1$
    constraints.weighty = 0.0;
    constraints.weightx = 1.0;
    while (tok.hasMoreTokens()) {
      String t = tok.nextToken();
//      if (!tok.hasMoreTokens()) {
//        constraints.weighty = 1.0;
//      }
      Label l = new Label(t);
      UIUtil.gridBagAdd(this, l, constraints,
          GridBagConstraints.REMAINDER);
    }
    validate();
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
  
  /**
   * Set the alignment. Uses <code>GridBagConstraints.anchor</code>
   * 
   * @param alignment alignment
   */
  public void setAlignment(int alignment) {
    constraints.anchor = alignment;
    setText(text);
  }
}