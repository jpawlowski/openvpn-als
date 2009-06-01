
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

import com.maverick.crypto.digests.MD5Digest;

/**
 * An abstract {@link SSLCipherSuite} that uses an MD5 message digest.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public abstract class SSLCipherSuiteWithMD5MAC implements SSLCipherSuite {

    MD5Digest generateDigest = new MD5Digest();
    MD5Digest verifyDigest = new MD5Digest();

    byte[] encryptMAC;
    byte[] decryptMAC;

    byte[] padding1 = new byte[] { 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
        0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36,
        0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36 };

    byte[] padding2 = new byte[] { 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c,
        0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c,
        0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c, 0x5c };

    public SSLCipherSuiteWithMD5MAC() {

    }

    public abstract int getKeyLength();

    public abstract int getIVLength();

    public abstract void encrypt(byte[] b, int offset, int len);

    public abstract void decrypt(byte[] b, int offset, int len);

    public final void init(byte[] encryptKey, byte[] encryptIV, byte[] encryptMAC, byte[] decryptKey, byte[] decryptIV,
                           byte[] decryptMAC) {
        this.encryptMAC = encryptMAC;
        this.decryptMAC = decryptMAC;

        init(encryptKey, encryptIV, decryptKey, decryptIV);
    }

    protected abstract void init(byte[] encryptKey, byte[] encryptIV, byte[] decryptKey, byte[] decryptIV);

    public final int getMACLength() {
        return generateDigest.getDigestSize();
    }

    public byte[] generateMAC(byte[] b, int offset, int len, int type, long sequenceNo) {
        return calculateMAC(generateDigest, b, offset, len, type, sequenceNo, encryptMAC);
    }

    public boolean verifyMAC(byte[] b, int offset, int len, int type, long sequenceNo, byte[] mac, int macoff, int maclen) {
        byte[] gen = calculateMAC(verifyDigest, b, offset, len, type, sequenceNo, decryptMAC);
        for (int i = 0; i < gen.length; i++) {
            if (gen[i] != mac[i + macoff]) {
                return false;
            }
        }
        return true;
    }

    private byte[] calculateMAC(MD5Digest digest, byte[] b, int off, int len, int type, long sequenceNo, byte[] key) {
        digest.reset();

        digest.update(key, 0, key.length);
        digest.update(padding1, 0, padding1.length);
        digest.update((byte) ((sequenceNo >> 56) & 0xFF));
        digest.update((byte) ((sequenceNo >> 48) & 0xFF));
        digest.update((byte) ((sequenceNo >> 40) & 0xFF));
        digest.update((byte) ((sequenceNo >> 32) & 0xFF));
        digest.update((byte) ((sequenceNo >> 24) & 0xFF));
        digest.update((byte) ((sequenceNo >> 16) & 0xFF));
        digest.update((byte) ((sequenceNo >> 8) & 0xFF));
        digest.update((byte) ((sequenceNo >> 0) & 0xFF));

        digest.update((byte) type);
        digest.update((byte) ((len >> 8) & 0xFF));
        digest.update((byte) (len & 0xFF));

        digest.update(b, off, len);

        byte[] temp = new byte[digest.getDigestSize()];
        digest.doFinal(temp, 0);

        digest.reset();

        digest.update(key, 0, key.length);
        digest.update(padding2, 0, padding2.length);
        digest.update(temp, 0, temp.length);

        digest.doFinal(temp, 0);

        return temp;

    }

}