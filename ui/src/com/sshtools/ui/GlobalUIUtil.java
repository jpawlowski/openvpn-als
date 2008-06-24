
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			

package com.sshtools.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;

/**
 * General UI utitlies that may be used for both Swing and AWT.
 */
public class GlobalUIUtil {

  
  /**
   * Create a new Font given an existing font and a different font name. 
   * The base fonts size and style will be used. This
   * is provided because 1.1 doesnt have a Font.deriveFont() method.
   * 
   * @param font font to base new font on
   * @param name new font name
   * @return derived font
   */
  public static Font deriveFont(Font font, String name) {
    return new Font(name, font.getStyle(), font.getSize());
  }
  
  /**
   * Create a new Font given an existing font and a different font style. 
   * The base fonts name and size will be used. This
   * is provided because 1.1 doesnt have a Font.deriveFont() method.
   * 
   * @param font font to base new font on
   * @param style new font style
   * @return derived font
   */
  public static Font deriveFont(Font font, int style) {
    return new Font(font.getName(), style, font.getSize());
  }
  
  /**
   * Create a new Font given an existing font and a different font size. 
   * The base fonts name and style will be used. This
   * is provided because 1.1 doesnt have a Font.deriveFont() method.
   * 
   * @param font font to base new font on
   * @param size new font size
   * @return derived font
   */
  public static Font deriveFont(Font font, float size) {
    return new Font(font.getName(), font.getStyle(), (int)size);
  }
  /**
   * 
   *
   * @param number
   * @param defaultValue
   *
   * @return
   */
  public static int stringToInt(String number, int defaultValue) {
    try {
      return (number == null) ? defaultValue : Integer.parseInt(number);
    }
    catch (NumberFormatException nfe) {
      return defaultValue;
    }
  }

  /**
   *
   *
   * @param color
   *
   * @return
   */
  public static String colorToString(Color color) {
    StringBuffer buf = new StringBuffer();
    buf.append('#');
    buf.append(numberToPaddedHexString(color.getRed(), 2));
    buf.append(numberToPaddedHexString(color.getGreen(), 2));
    buf.append(numberToPaddedHexString(color.getBlue(), 2));

    return buf.toString();
  }

  /**
   *
   *
   * @param font
   *
   * @return
   */
  public static String fontToString(Font font) {
    StringBuffer b = new StringBuffer(font.getName());
    b.append(","); //$NON-NLS-1$
    b.append(font.getStyle());
    b.append(","); //$NON-NLS-1$
    b.append(font.getSize());

    return b.toString();
  }

  /**
   *
   *
   * @param fontString
   *
   * @return
   */
  public static Font stringToFont(String fontString) {
    StringTokenizer st = new StringTokenizer(fontString, ","); //$NON-NLS-1$

    try {
      return new Font(st.nextToken(), Integer.parseInt(st.nextToken()),
                      Integer.parseInt(st.nextToken()));
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   *
   *
   * @param s
   *
   * @return
   *
   * @throws IllegalArgumentException
   */
  public static Color stringToColor(String s) {
    try {
      return new Color(Integer.decode("0x" + s.substring(1, 3)).intValue(), //$NON-NLS-1$
                       Integer.decode("0x" + s.substring(3, 5)).intValue(), //$NON-NLS-1$
                       Integer.decode("0x" + s.substring(5, 7)).intValue()); //$NON-NLS-1$
    }
    catch (Exception e) {
      throw new IllegalArgumentException(
          Messages.getString("GlobalUIUtil.badColorStringFormat")); //$NON-NLS-1$
    }
  }

  /**
   *
   *
   * @param s
   *
   * @return
   *
   * @throws IllegalArgumentException
   */
  public static Color stringToColor(String s, Color defaultColor) {
      try {
          return new Color(Integer.decode("0x" + s.substring(1, 3)).intValue(), //$NON-NLS-1$
                  Integer.decode("0x" + s.substring(3, 5)).intValue(), //$NON-NLS-1$
                  Integer.decode("0x" + s.substring(5, 7)).intValue()); //$NON-NLS-1$
      }
      catch (Throwable t) {
          return defaultColor;
      }
  }

  /**
   *
   *
   * @param number
   * @param size
   *
   * @return
   *
   * @throws IllegalArgumentException
   */
  public static String numberToPaddedHexString(int number, int size) {
    String s = Integer.toHexString(number);

    if (s.length() > size) {
      throw new IllegalArgumentException(
          Messages.getString("GlobalUIUtil.numberTooBigForPaddedHexString")); //$NON-NLS-1$
    }

    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < (size - s.length()); i++) {
      buf.append('0');
    }

    buf.append(s);

    return buf.toString();
  }
}
