
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
			
package com.maverick.crypto.encoders;

/**
 * Converters for going from hex to binary and back. Note: this class assumes ASCII processing.
 */
public class HexTranslator
{
    private static final byte[]   hexTable =
        {
            (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6', (byte)'7',
            (byte)'8', (byte)'9', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f'
        };

    /**
     * size of the output block on encoding produced by getDecodedBlockSize()
     * bytes.
     */
    public int getEncodedBlockSize()
    {
        return 2;
    }

    public int encode(
        byte[]  in,
        int     inOff,
        int     length,
        byte[]  out,
        int     outOff)
    {
        for (int i = 0, j = 0; i < length; i++, j += 2)
        {
            out[outOff + j] = hexTable[(in[inOff] >> 4) & 0x0f];
            out[outOff + j + 1] = hexTable[in[inOff] & 0x0f];

            inOff++;
        }

        return length * 2;
    }

    /**
     * size of the output block on decoding produced by getEncodedBlockSize()
     * bytes.
     */
    public int getDecodedBlockSize()
    {
        return 1;
    }

    public int decode(
        byte[]  in,
        int     inOff,
        int     length,
        byte[]  out,
        int     outOff)
    {
		int halfLength = length / 2;
		byte left, right;
        for (int i = 0; i < halfLength; i++)
        {
			left  = in[inOff + i * 2];
			right = in[inOff + i * 2 + 1];

            if (left < (byte)'a')
            {
                out[outOff] = (byte)((left - '0') << 4);
            }
            else
            {
                out[outOff] = (byte)((left - 'a' + 10) << 4);
            }
            if (right < (byte)'a')
            {
                out[outOff] += (byte)(right - '0');
            }
            else
            {
                out[outOff] += (byte)(right - 'a' + 10);
            }

            outOff++;
        }

        return halfLength;
    }
}
