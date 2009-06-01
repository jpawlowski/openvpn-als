
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
			
package com.maverick.ssl;

import com.maverick.crypto.engines.RC4Engine;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
class SSL_RSA_WITH_RC4_128_MD5 extends SSLCipherSuiteWithMD5MAC {

    RC4Engine encrypt;
    RC4Engine decrypt;

    public SSL_RSA_WITH_RC4_128_MD5() {
    }

    public void init(byte[] encryptKey, byte[] encryptIV, byte[] decryptKey, byte[] decryptIV) {

        encrypt = new RC4Engine();
        encrypt.init(true, encryptKey);

        decrypt = new RC4Engine();
        decrypt.init(false, decryptKey);
    }

    public int getKeyLength() {
        return 16;
    }

    public int getIVLength() {
        return 0;
    }

    public void encrypt(byte[] b, int offset, int len) {
        encrypt.processBytes(b, offset, len, b, offset);
    }

    public void decrypt(byte[] b, int offset, int len) {
        decrypt.processBytes(b, offset, len, b, offset);
    }

}