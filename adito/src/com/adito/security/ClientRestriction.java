/*
 */
package com.adito.security;

/**
 * @author brett
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ClientRestriction {
  public abstract boolean getAllow();
  public abstract void setAllow(boolean allow);
  public abstract String getExceptions();
  public abstract void setExceptions(String exceptions);
  public abstract String getOsName();
  public abstract String getPropertyName();
}