/*
 */
package com.sshtools.util;

import java.util.Vector;


/**
 *
 */
public class Util {

  private Util() {
    super();
  }

  public static void main(String[] args) {
  }

  public static String[] splitString(String str, char delim) {
    return splitString(str, delim, (char) -1, (char) -1);
  }

  public static String[] splitString(String str, char delim, char quote,
      char escape) {
    Vector v = new Vector();
    StringBuffer str1 = new StringBuffer();
    char ch = ' ';
    boolean inQuote = false;
    boolean escaped = false;

    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);

      if ((escape != -1) && (ch == escape) && !escaped) {
        escaped = true;
      } else {
        if ((quote != -1) && (ch == quote) && !escaped) {
          inQuote = !inQuote;
        } else if (!inQuote && (ch == delim && !escaped)) {
          v.addElement(str1.toString());
          str1.setLength(0);
        } else {
          str1.append(ch);
        }
        if(escaped) {
          escaped = false;
        }
      }
    }

    if (str.length() > 0) {
      v.addElement(str1.toString());

    }
    String[] array;
    array = new String[v.size()];
    v.copyInto(array);

    return array;
  }

  public static void sort(Vector v, SortComparator comparator) {
    //  TODO more efficient sort algorithm
    int i, j, c;
    boolean s;
    for (i = v.size(); --i >= 0;) {
      s = false;
      for (j = 0; j < i; j++) {
        Object o1 = v.elementAt(j);
        Object o2 = v.elementAt(j + 1);
        c = comparator.sortCompare(o1, o2);
        if (c > 0) {
          v.setElementAt(o2, j);
          v.setElementAt(o1, j + 1);
          s = true;
        }
      }
      if (!s) {
        return;
      }
    }
  }
}
