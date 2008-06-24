package com.sshtools.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

/**
 * Swing component that provides a horziontal separator that is preceeded by some text.
 *
 * @author $Author: brett $
 */
public class TitledSeparator
    extends JPanel {

  // Private instance variables

  /**
   * Construct a titled separtor with some text
   *
   * @param text text
   */
  public TitledSeparator(String text) {
    super(new GridBagLayout());
    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.weightx = 0.0;
    gbc2.insets = new Insets(4, 0, 2, 2);
    JLabel l = new JLabel(text);
    l.setFont(UIManager.getFont("ToolTip.font"));
    UIUtil.jGridBagAdd(this, l, gbc2,
                       GridBagConstraints.RELATIVE);
    gbc2.weightx = 1.0;
    UIUtil.jGridBagAdd(this, new JSeparator(JSeparator.HORIZONTAL), gbc2,
                       GridBagConstraints.REMAINDER);
  }
}