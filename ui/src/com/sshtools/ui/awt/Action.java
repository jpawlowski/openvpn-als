package com.sshtools.ui.awt;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * An extended ActionListener for AWT.
 * 
 * @author $Author: lee $
 */
public interface Action extends ActionListener {

  //  We use the sm

  /**
   * Key used for storing the action name
   */
  public static final String NAME = "Name"; //$NON-NLS-1$
  /**
   * Key for a short description for the action, used for tooltip text.
   */
  public static final String SHORT_DESCRIPTION = "ShortDescription"; //$NON-NLS-1$

  /**
   * Key for storing a long description for the action.
   */
  public static final String LONG_DESCRIPTION = "LongDescription"; //$NON-NLS-1$

  /**
   *  
   */
  public static final String ACTION_COMMAND_KEY = "ActionCommandKey"; //$NON-NLS-1$

  /**
   * Key used for storing a <code>KeyStroke</code> for the accelerator for the
   * action.
   */
  public static final String ACCELERATOR_KEY = "AcceleratorKey"; //$NON-NLS-1$

  /**
   * Key for storing an <code>Integer</code> object to be used as the mnemonic
   * for the action.
   */
  public static final String MNEMONIC_KEY = "MnemonicKey"; //$NON-NLS-1$

  /**
   * Key for a <code>String</code> value to specify the resource name for the
   * small icon
   */
  public final static String SMALL_IMAGE_PATH = "SmallImagePath"; //$NON-NLS-1$

  /**
   * Key for a <code>String</code> value to specify the resource name for the
   * icon
   */
  public final static String IMAGE_PATH = "ImagePath"; //$NON-NLS-1$

  /**
   * <code>Boolean</code> value to specify if text should be shown when this
   * action is used to build components for an <code>ActionBar</code>
   */
  public final static String HIDE_TOOLBAR_TEXT = "HideToolBarText"; //$NON-NLS-1$

  /**
   * Actions can have any number of attributes, each referenced by a key (a
   * string). <code>null</code> will be returned if no such attribute exists
   * 
   * @param key
   *          key
   * @return action attribute value
   */
  public Object getValue(String key);

  /**
   * Actions can have any number of attributes, each referenced by a key (a
   * string).
   * 
   * @param key
   *          key
   * @param value
   *          value
   */
  public void putValue(String key, Object value);

  /**
   * Return the name of the action. Same as doing {@link AppAction.getValue}
   * using a key of {@link AppAction.NAME}.
   * 
   * @return action name
   */
  public String getName();

  /**
   * Get if the component(s) that were built from this action should be 
   * enabled or not.
   * 
   * @return enabled
   */
  public boolean isEnabled();

  /**
   * Set if the component(s) that were built from this action should be 
   * enabled or not. 
   * 
   * @param enabled action enaqbled
   */
  public void setEnabled(boolean enabled);

  /**
   * Add a <code>PropertyChangeListener</code> that will be notified when
   * either a value changes or the enabled state changes 
   * 
   * @param l listener to add
   */
  public void addPropertyChangeListener(PropertyChangeListener l);

  /**
   * Remove a <code>PropertyChangeListener</code> from the list 
   * that will be notified when either a value changes or the enabled state changes 
   * 
   * @param l listener to remove
   */
  public void removePropertyChangeListener(PropertyChangeListener l);

}