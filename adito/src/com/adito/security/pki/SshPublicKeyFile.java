
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
public class SshPublicKeyFile {
    private static Log log = LogFactory.getLog(SshPublicKeyFile.class);
    private SshPublicKeyFormat format;
    private byte[] keyblob;
    private String comment;

    /**
     * Creates a new SshPublicKeyFile object.
     *
     * @param keyblob
     * @param format
     */
    protected SshPublicKeyFile(byte[] keyblob, SshPublicKeyFormat format) {
        this.keyblob = keyblob;
        this.format = format;
    }

    /**
     *
     *
     * @return
     */
    public byte[] getBytes() {
        return format.formatKey(keyblob);
    }

    /**
     *
     *
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     *
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     *
     *
     * @return
     */
    public byte[] getKeyBlob() {
        return keyblob;
    }

    /**
     *
     *
     * @param key
     * @param format
     *
     * @return
     */
    public static SshPublicKeyFile create(SshPublicKey key,
        SshPublicKeyFormat format) {
        SshPublicKeyFile file = new SshPublicKeyFile(key.getEncoded(), format);
        file.setComment(format.getComment());

        return file;
    }

    /**
     *
     *
     * @param keyfile
     *
     * @return SshPublicKeyFile
     *
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static SshPublicKeyFile parse(File keyfile)
        throws InvalidKeyException, IOException {

    	return parse(new FileInputStream(keyfile));
    }
    
    /**
     * @param in
     * @return SshPublicKeyFile
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static SshPublicKeyFile parse(InputStream in)
        throws InvalidKeyException, IOException {    	

    	byte[] data = new byte[in.available()];
        in.read(data);
        in.close();

        return parse(data);
    }

    /**
     *
     *
     * @param formattedKey
     *
     * @return SshPublicKeyFile
     *
     * @throws InvalidKeyException
     */
    public static SshPublicKeyFile parse(byte[] formattedKey)
        throws InvalidKeyException {
    	if (log.isInfoEnabled())
    		log.info("Parsing public key file");

        // Try the default private key format
        SshPublicKeyFormat format;
        format = SshPublicKeyFormatFactory.newInstance(SshPublicKeyFormatFactory.getDefaultFormatType());

        boolean valid = format.isFormatted(formattedKey);


        if (valid) {
            SshPublicKeyFile file = new SshPublicKeyFile(format.getKeyBlob(
                        formattedKey), format);
            file.setComment(format.getComment());

            return file;
        } else {
            throw new InvalidKeyException(
                "The key format is not a supported format");
        }
    }

    /**
     *
     *
     * @return String
     */
    public String getAlgorithm() {
    	try {
			ByteArrayReader r = new ByteArrayReader(keyblob);
			return r.readString();
		} catch (IOException e) {
			return null;
		}
    }

    /**
     *
     *
     * @param newFormat
     *
     * @throws InvalidKeyException
     */
    public void setFormat(SshPublicKeyFormat newFormat)
        throws InvalidKeyException {
        if (newFormat.supportsAlgorithm(getAlgorithm())) {
            newFormat.setComment(format.getComment());
            this.format = newFormat;
        } else {
            throw new InvalidKeyException(
                "The format does not support the public key algorithm");
        }
    }

    /**
     *
     *
     * @return SshPublicKeyFormat
     */
    public SshPublicKeyFormat getFormat() {
        return format;
    }

    /**
     *
     *
     * @return SshPublicKey
     *
     * @throws IOException
     */
    public SshPublicKey toPublicKey() throws IOException, InvalidKeyException {
        ByteArrayReader bar = new ByteArrayReader(keyblob);
        String type = bar.readString();
        SshKeyPair pair = SshKeyPairFactory.newInstance(type);

        return pair.decodePublicKey(keyblob);
    }

    /**
     *
     *
     * @return
     */
    public String toString() {
        return new String(format.formatKey(keyblob));
    }
}
