package com.sshtools.ui.awt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Abstract implementation of an {@link Action}.
 */
public abstract class AbstractAction
    implements Action {
  
  //  Private instance variables
  private Hashtable attributes = new Hashtable();
  private boolean enabled;
  private Vector listeners;

  /**
   * Construct a new action with a name.
   *
   * @param name
   */
  public AbstractAction(String name) {
    super();
    listeners = new Vector();
    putValue(NAME, name);
    enabled = true;
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.Action#isEnabled()
   */
  public boolean isEnabled() {
    return enabled;
  }
  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.Action#setEnabled(boolean)
   */
  public void setEnabled(boolean enabled) {
    if(this.enabled != enabled) {
      boolean oldVal = this.enabled;
      this.enabled = enabled;
      firePropertyChanged(this, "enabled", oldVal ? Boolean.TRUE : Boolean.FALSE, enabled ? Boolean.TRUE : Boolean.FALSE); //$NON-NLS-1$
    }
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.Action#getName()
   */
  public String getName() {
    return (String) getValue(NAME);
  }

  /* (non-Javadoc)
       * @see com.sshtools.ui.awt.Action#putValue(java.lang.String, java.lang.Object)
   */
  public void putValue(String key, Object value) {
    Object oldVal = attributes.put(key, value);
    firePropertyChanged(this, key, oldVal, value);
  }


  /**
   * Get the value for an attribute. <code>null</code> will be returned
   * if no such attribute can be found.
   *
   * @param key attribute key
   * @return attribute value
   */
  public Object getValue(String key) {
    return attributes.get(key);
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.Action#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    listeners.addElement(l);
  }
  
  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.Action#removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    listeners.removeElement(l);
  }
  
  /**
   * @param event
   */
  private void firePropertyChanged(Object source, String key, Object oldVal, Object newVal) {
    PropertyChangeEvent evt = null;
    synchronized(listeners) {
      for(int i = listeners.size() - 1; i >= 0 ; i-- ) {
        if(evt == null) {
          evt = new PropertyChangeEvent(source, key, oldVal, newVal);          
        }
        ((PropertyChangeListener)listeners.elementAt(i)).propertyChange(evt);
      }
    }    
  }
}
