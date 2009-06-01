package com.sshtools.ui.awt;

import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A utility class that extends {@link ImageButton} to create a button from
 * an {@link Action}.
 *
 * @author $Author: lee $
 * @author $Revision: 1.11 $
 */

public class ActionButton
    extends ImageButton implements PropertyChangeListener {

  public final static String LARGE_ICONS = "large"; //$NON-NLS-1$
  public final static String NO_ICONS = "none"; //$NON-NLS-1$
  public final static String SMALL_ICONS = "small"; //$NON-NLS-1$

  public final static String SHOW_TEXT = "show"; //$NON-NLS-1$
  public final static String NO_TEXT = "none"; //$NON-NLS-1$
  public final static String SELECTIVE_TEXT = "selective"; //$NON-NLS-1$

  //	Private statics
  private static final Insets DEFAULT_MARGIN = new Insets(3, 3, 3, 3);

  //	Private instance variables

  private Action action;
  private String iconDisplay;
  private String textDisplay;

  /**
   * Construct a new button from an action
   *
   * @param action action
   */
  public ActionButton(Action action) {
    this(action, SMALL_ICONS, SHOW_TEXT);
  }

  /**
   * Construct a new button from an action
   *
   * @param action action
   * @param iconDisplay icon display type
   * @parma textDisplay text display text
   */
  public ActionButton(Action action, String iconDisplay, String textDisplay) {
    super();
    this.iconDisplay = iconDisplay;
    this.textDisplay = textDisplay;
    setMargin(DEFAULT_MARGIN);
    setAction(action);
    setHoverButton(true);
  }

  /**
   * Get the action used to build this component.
   *
   * @return action
   */
  public Action getAction() {
    return action;
  }

  /**
       * Set the image, text, action listener etc from the action. Any previous action
   * will be deregistered.
   *
   * @param action action
   */
  public void setAction(final Action action) {
    if (this.action != null) {
      removeActionListener(this.action);
      action.removePropertyChangeListener(this);
    }
    this.action = action;
    String imgName = LARGE_ICONS.equals(iconDisplay) ? (String) action.getValue(Action.IMAGE_PATH) :
      	( SMALL_ICONS.equals(iconDisplay) ? (String) action.getValue(Action.SMALL_IMAGE_PATH) : null);
    Image img = null;
    if (imgName != null) {
      img = UIUtil.loadImage(action.getClass(), imgName);
      if (img != null) {
        setImage(UIUtil.waitFor(img, this));
      }
    }
    Boolean hide = (Boolean) action.getValue(Action.HIDE_TOOLBAR_TEXT);
    setToolTipText((String)action.getValue(Action.LONG_DESCRIPTION));
    setText( (String) action.getValue(Action.NAME));
    //	Also show text if icons were requested but this one is not available - prevents empty buttons
    setTextVisible( img == null || SHOW_TEXT.equals(textDisplay) || ( SELECTIVE_TEXT.equals(textDisplay) && ( hide == null || !hide.booleanValue() ) ));
    addActionListener(action);
    setEnabled(action.isEnabled());
    action.addPropertyChangeListener(this);
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if("enabled".equals(evt.getPropertyName())) { //$NON-NLS-1$
      setEnabled(((Boolean)evt.getNewValue()).booleanValue());
    }
    repaint();
  }
}