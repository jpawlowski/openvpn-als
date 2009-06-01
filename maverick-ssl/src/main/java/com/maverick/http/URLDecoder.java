
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
			
package com.maverick.http;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class URLDecoder {

    public static String decode(String s) {
        return decode(s, true);
    }

    public static String decode(String s, boolean decodePlus) {

        boolean needToChange = false;
        StringBuffer sb = new StringBuffer();
        int numChars = s.length();
        int i = 0;

        while (i < numChars) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    if (decodePlus) {
                        sb.append(' ');
                        i++;
                        needToChange = true;
                    } else {
                        sb.append(c);
                        i++;
                    }
                    break;
                case '%':

                    try {

                        byte[] bytes = new byte[(numChars - i) / 3];
                        int pos = 0;

                        while (((i + 2) < numChars) && (c == '%')) {
                            bytes[pos++] = (byte) Integer.parseInt(s.substring(i + 1, i + 3), 16);
                            i += 3;
                            if (i < numChars)
                                c = s.charAt(i);
                        }

                        if ((i < numChars) && (c == '%'))
                            throw new IllegalArgumentException(Messages.getString("URLDecoder.incompleteTrailingEscape")); //$NON-NLS-1$

                        sb.append(new String(bytes, 0, pos));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(Messages.getString("URLDecoder.illegalHexCharacter") //$NON-NLS-1$
                            + e.getMessage());
                    }
                    needToChange = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (needToChange ? sb.toString() : s);
    }

}
