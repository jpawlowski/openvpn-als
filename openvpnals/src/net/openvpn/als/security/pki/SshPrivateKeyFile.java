
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
			
package net.openvpn.als.security.pki;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.ByteArrayReader;


/**
 *
 *
 * @author $author$
 */
public class SshPrivateKeyFile {
    private static Log log = LogFactory.getLog(SshPrivateKeyFile.class);
    private SshPrivateKeyFormat format;
    private byte[] keyblob;

    /**
     * Creates a new SshPrivateKeyFile object.
     *
     * @param keyblob
     * @param format
     */
    protected SshPrivateKeyFile(byte[] keyblob, SshPrivateKeyFormat format) {
        this.keyblob = keyblob;
        this.format = format;
    }

    /**
     *
     *
     * @return
     */
    public byte[] getBytes() {
        return keyblob;
    }

    /**
     *
     *
     * @param passphrase
     *
     * @return byte[]
     *
     * @throws InvalidKeyException
     */
    public byte[] getKeyBlob(String passphrase) throws InvalidKeyException {
        return format.decryptKeyblob(keyblob, passphrase);
    }

    /**
     *
     *
     * @param oldPassphrase
     * @param newPassphrase
     *
     * @throws InvalidKeyException
     */
    public void changePassphrase(String oldPassphrase, String newPassphrase)
        throws InvalidKeyException {
        byte[] raw = format.decryptKeyblob(keyblob, oldPassphrase);
        keyblob = format.encryptKeyblob(raw, newPassphrase);
    }

    /**
     *
     *
     * @param formattedKey
     *
     * @return
     *
     * @throws InvalidKeyException
     */
    public static SshPrivateKeyFile parse(byte[] formattedKey)
        throws InvalidKeyException {
        if (formattedKey == null) {
            throw new InvalidKeyException("Key data is null");
        }

        if (log.isInfoEnabled())
        	log.info("Parsing private key file");

        // Try the default private key format
        SshPrivateKeyFormat format;
        format = SshPrivateKeyFormatFactory.newInstance(SshPrivateKeyFormatFactory.getDefaultFormatType());

        boolean valid = format.isFormatted(formattedKey);

        if (valid) {
            return new SshPrivateKeyFile(formattedKey, format);
        } else {
            throw new InvalidKeyException(
                "The key format is not a supported format");
        }
    }

    /**
     *
     *
     * @param keyfile
     *
     * @return SshPrivateKeyFile
     *
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static SshPrivateKeyFile parse(File keyfile)
        throws InvalidKeyException, IOException {

    	return parse(new FileInputStream(keyfile));
    }
    
    /**
     * @param in
     * @return SshPrivateKeyFile
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static SshPrivateKeyFile parse(InputStream in)
        throws InvalidKeyException, IOException {
    
        byte[] data = null;

        try {
            data = new byte[in.available()];
            in.read(data);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }

        return parse(data);
    }

    /**
     *
     *
     * @return
     */
    public boolean isPassphraseProtected() {
        return format.isPassphraseProtected(keyblob);
    }

    /*public void changePassphrase(String oldPassphrase, String newPassphrase)
     throws InvalidKeyException {
     keyblob = format.changePassphrase(keyblob, oldPassphrase, newPassphrase);
      }*/
    /**
     * @param key
     * @param passphrase
     * @param format
     * @return SshPrivateKeyFile
     * @throws InvalidKeyException
     */
    public static SshPrivateKeyFile create(SshPrivateKey key,
        String passphrase, SshPrivateKeyFormat format)
        throws InvalidKeyException {
        byte[] keyblob = format.encryptKeyblob(key.getEncoded(), passphrase);

        return new SshPrivateKeyFile(keyblob, format);
    }

    /**
     *
     *
     * @param newFormat
     * @param passphrase
     *
     * @throws InvalidKeyException
     */
    public void setFormat(SshPrivateKeyFormat newFormat, String passphrase)
        throws InvalidKeyException {
        byte[] raw = this.format.decryptKeyblob(keyblob, passphrase);
        format = newFormat;
        keyblob = format.encryptKeyblob(raw, passphrase);
    }

    /**
     *
     *
     * @return SshPrivateKeyFormat
     */
    public SshPrivateKeyFormat getFormat() {
        return format;
    }

    /**
     *
     *
     * @param passphrase
     *
     * @return SshPrivateKey
     *
     * @throws InvalidKeyException
     */
    public SshPrivateKey toPrivateKey(String passphrase)
        throws InvalidKeyException {
            byte[] raw = format.decryptKeyblob(keyblob, passphrase);
            SshKeyPair pair = SshKeyPairFactory.newInstance(getAlgorithm(raw));

            return pair.decodePrivateKey(raw);

    }

    /**
     *
     *
     * @return String
     */
    public String toString() {
        return new String(keyblob);
    }

    /**
     * @param raw
     * @return String
     */
    private String getAlgorithm(byte[] raw) {
        try {
			ByteArrayReader r = new ByteArrayReader(raw);
			return r.readString();
		} catch (IOException e) {
			return null;
		}
    }
}
