
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
			
package com.adito.security.pki;

/**
 *
 *
 * @author $author$
 */
public abstract class SshKeyPair {
    private SshPrivateKey prv;
    private SshPublicKey pub;

    /**
     * Creates a new SshKeyPair object.
     */
    public SshKeyPair() {
    }

    /**
     *
     *
     * @param bits
     */
    public abstract void generate(int bits);

    /**
     *
     *
     * @param key
     */
    public void setPrivateKey(SshPrivateKey key) {
        this.prv = key;
        this.pub = key.getPublicKey();
    }

    /**
     *
     *
     * @param encoded
     *
     * @return SshPrivateKey
     *
     * @throws InvalidKeyException
     */
    public SshPrivateKey setPrivateKey(byte[] encoded)
        throws InvalidKeyException {
        setPrivateKey(decodePrivateKey(encoded));

        return this.prv;
    }

    /**
     *
     *
     * @return
     */
    public SshPrivateKey getPrivateKey() {
        return prv;
    }

    /**
     *
     *
     * @param encoded
     *
     * @return SshPublicKey
     *
     * @throws InvalidKeyException
     */
    public SshPublicKey setPublicKey(byte[] encoded)
        throws InvalidKeyException {
        this.pub = decodePublicKey(encoded);
        this.prv = null;

        return this.pub;
    }

    /**
     *
     *
     * @return SshPublicKey
     */
    public SshPublicKey getPublicKey() {
        return pub;
    }

    /**
     *
     *
     * @param encoded
     *
     * @return SshPrivateKey
     *
     * @throws InvalidKeyException
     */
    public abstract SshPrivateKey decodePrivateKey(byte[] encoded)
        throws InvalidKeyException;

    /**
     *
     *
     * @param encoded
     *
     * @return SshPublicKey
     *
     * @throws InvalidKeyException
     */
    public abstract SshPublicKey decodePublicKey(byte[] encoded)
        throws InvalidKeyException;
}
