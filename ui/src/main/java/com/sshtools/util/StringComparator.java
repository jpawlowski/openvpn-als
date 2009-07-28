/*
 */
package com.sshtools.util;

/**
 * @author magicthize
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StringComparator implements SortComparator {
  
  private static SortComparator instance;

  /* (non-Javadoc)
   * @see com.sshtools.util.SortComparator#sortCompare(java.lang.Object, java.lang.Object)
   */
  public int sortCompare(Object o1, Object o2) {
    return ( o1 == null && o2 != null) ? -1 : ( o1 != null && o2 == null ? 1 : String.valueOf(o1).compareTo(String.valueOf(o2)));
  }

  /**
   * @return
   */
  public static SortComparator getDefaultInstance() {
    if(instance == null) {
      instance = new StringComparator();
    }
    return instance;
  }

}
