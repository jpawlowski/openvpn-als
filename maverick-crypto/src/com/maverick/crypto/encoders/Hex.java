
				/*
 *  OpenVPNALS
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
			
package com.maverick.crypto.encoders;

/**
 * Converters for going from hex to binary and back.
 * <p>
 * Note: this class assumes ASCII processing.
 */
public class Hex
{
    private static HexTranslator   encoder = new HexTranslator();

    public static byte[] encode(
        byte[]  array)
    {
        return encode(array, 0, array.length);
    }

    public static byte[] encode(
        byte[]  array,
        int     off,
        int     length)
    {
        byte[]      enc = new byte[length * 2];

        encoder.encode(array, off, length, enc, 0);

        return enc;
    }

    public static byte[] decode(
        String  string)
    {
        byte[]          bytes = new byte[string.length() / 2];
        String          buf = string.toLowerCase();

        for (int i = 0; i < buf.length(); i += 2)
        {
			char    left  = buf.charAt(i);
			char    right = buf.charAt(i+1);
            int     index = i / 2;

            if (left < 'a')
            {
                bytes[index] = (byte)((left - '0') << 4);
            }
            else
            {
                bytes[index] = (byte)((left - 'a' + 10) << 4);
            }
            if (right < 'a')
            {
                bytes[index] += (byte)(right - '0');
            }
            else
            {
                bytes[index] += (byte)(right - 'a' + 10);
            }
        }

        return bytes;
    }

    public static byte[] decode(
        byte[]  array)
    {
        byte[]          bytes = new byte[array.length / 2];

        encoder.decode(array, 0, array.length, bytes, 0);

        return bytes;
    }
}
