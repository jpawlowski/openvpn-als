
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
public interface SshPrivateKeyFormat {
    /**
     *
     *
     * @param formattedKey
     *
     * @return
     */
    public boolean isPassphraseProtected(byte[] formattedKey);

    /**
     *
     *
     * @param formattedKey
     *
     * @return
     */
    public boolean isFormatted(byte[] formattedKey);

    /**
     *
     *
     * @param formattedKey
     * @param passphrase
     *
     * @return byte[]
     *
     * @throws InvalidKeyException
     */
    public byte[] decryptKeyblob(byte[] formattedKey, String passphrase)
        throws InvalidKeyException;

    /**
     *
     *
     * @param keyblob
     * @param passphrase
     *
     * @return byte[]
     *
     * @throws InvalidKeyException
     */
    public byte[] encryptKeyblob(byte[] keyblob, String passphrase)
        throws InvalidKeyException;

    /**
     *
     *
     * @param algorithm
     *
     * @return boolean
     */
    public boolean supportsAlgorithm(String algorithm);

    /**
     *
     *
     * @return String
     */
    public String getFormatType();
}
