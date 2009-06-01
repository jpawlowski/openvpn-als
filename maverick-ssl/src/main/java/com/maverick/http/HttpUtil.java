
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

import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpUtil {

    private static final String ACCEPTABLE = "ABCDEFGHIJLKMNOPQRSTUVWXYZ" + // ALPHA //$NON-NLS-1$
        // (UPPER)
        "abcdefghijklmnopqrstuvwxyz" + // ALPHA (LOWER) //$NON-NLS-1$
        "0123456789" + // DIGIT //$NON-NLS-1$
        "_-!.~'()*" + // UNRESERVED //$NON-NLS-1$
        ",;:$+=" + // PUNCT //$NON-NLS-1$
        "?/@"; // RESERVED //$NON-NLS-1$

    /**
     * Encode a path suitable for use in a URI.
     * 
     * @param path path
     * @param encodeSlash encode forward slashes (/)
     * @return encoded path
     */
    public static String encodePath(String path, boolean encodeSlash, String charset) {
        /* Encode the string */
        StringBuffer buffer = new StringBuffer();
        byte encoded[];
        try {
            if (charset == null)
                encoded = path.getBytes();
            else
                encoded = path.getBytes(charset);
            for (int x = 0; x < encoded.length; x++) {
                if (((int) encoded[x] == '%' && encodeSlash) || ACCEPTABLE.indexOf((int) encoded[x]) < 0) {
                    buffer.append('%');
                    buffer.append(toHexString(encoded[x]));
                    continue;
                }
                buffer.append((char) encoded[x]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return path;
        }

        return buffer.toString();
    }

    /**
     * <p>
     * Return the HEX representation of an array of bytes.
     * </p>
     * 
     * @param buffer the array of bytes to convert in a HEX {@link String}.
     * @return a <b>non-null</b> {@link String} instance.
     */
    public static String toHexString(byte buffer[]) {
        char output[] = new char[buffer.length * 2];
        int position = 0;
        for (int x = 0; x < buffer.length; x++) {
            output[position++] = toHexDigit(buffer[x] >> 4);
            output[position++] = toHexDigit(buffer[x]);
        }
        return new String(output);
    }

    /**
     * <p>
     * Return the HEX representation of a byte.
     * </p>
     * 
     * @param number the byte to convert in a HEX {@link String}.
     * @return a <b>non-null</b> 2-characters {@link String} instance.
     */
    public static String toHexString(byte number) {
        char output[] = new char[2];
        output[0] = toHexDigit((int) (number >> 4));
        output[1] = toHexDigit((int) (number));
        return new String(output);
    }

    /**
     * <p>
     * Return the single digit character representing the HEX encoding of the
     * lower four bits of a given integer.
     * </p>
     * 
     * @param number number to conver
     * @return hex character
     */
    private static char toHexDigit(int number) {
        switch (number & 0x0F) {
            case 0x00:
                return '0';
            case 0x01:
                return '1';
            case 0x02:
                return '2';
            case 0x03:
                return '3';
            case 0x04:
                return '4';
            case 0x05:
                return '5';
            case 0x06:
                return '6';
            case 0x07:
                return '7';
            case 0x08:
                return '8';
            case 0x09:
                return '9';
            case 0x0A:
                return 'A';
            case 0x0B:
                return 'B';
            case 0x0C:
                return 'C';
            case 0x0D:
                return 'D';
            case 0x0E:
                return 'E';
            case 0x0F:
                return 'F';
        }
        String message = "Invalid HEX digit " + Integer.toHexString(number); //$NON-NLS-1$
        throw new IllegalArgumentException(message);
    }

}
