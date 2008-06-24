
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
			
package com.maverick.ssl;

/**
 * Defines a cipher suite for use in the SSL protocol.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public interface SSLCipherSuite {

    public int getKeyLength();

    public int getMACLength();

    public int getIVLength();

    public void init(byte[] encryptKey, byte[] encryptIV, byte[] encryptMAC, byte[] decryptKey, byte[] decryptIV, byte[] decryptMAC);

    public void encrypt(byte[] b, int offset, int len);

    public void decrypt(byte[] b, int offset, int len);

    public byte[] generateMAC(byte[] b, int offset, int len, int type, long sequenceNo);

    public boolean verifyMAC(byte[] b, int offset, int len, int type, long sequenceNo, byte[] mac, int macoff, int maclen);

}