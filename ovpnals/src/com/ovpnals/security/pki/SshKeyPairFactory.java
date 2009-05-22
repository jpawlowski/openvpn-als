
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.security.pki;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.ByteArrayReader;
import com.ovpnals.security.pki.dsa.SshDssKeyPair;
import com.ovpnals.security.pki.rsa.SshRsaKeyPair;


/**
 *
 *
 * @author $author$
 */
public class SshKeyPairFactory {
    private static Map pks;
    private static String defaultAlgorithm;
    private static Log log = LogFactory.getLog(SshKeyPairFactory.class);

    static {
        pks = new HashMap();
        if (log.isInfoEnabled())
        	log.info("Loading public key algorithms");

        pks.put("ssh-rsa", SshRsaKeyPair.class);
        pks.put("ssh-dss", SshDssKeyPair.class);
        
        if ((defaultAlgorithm == null) || !pks.containsKey(defaultAlgorithm)) {
            Iterator it = pks.keySet().iterator();
            defaultAlgorithm = (String) it.next();
        }
    }

    /**
     * Creates a new SshKeyPairFactory object.
     */
    protected SshKeyPairFactory() {
    }

    /**
     *
     */
    public static void initialize() {
    }

    /**
     *
     *
     * @return
     */
    public static String getDefaultPublicKey() {
        return defaultAlgorithm;
    }

    /**
     *
     *
     * @return
     */
    public static List getSupportedKeys() {
        // Get the list of pks
        return new ArrayList(pks.keySet());
    }

    /**
     *
     *
     * @param methodName
     *
     * @return SshKeyPair
     *
     */
    public static SshKeyPair newInstance(String methodName) {
        try {
            return (SshKeyPair) ((Class) pks.get(methodName)).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     *
     * @param algorithm
     *
     * @return
     */
    public static boolean supportsKey(String algorithm) {
        return pks.containsKey(algorithm);
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
    public static SshPrivateKey decodePrivateKey(byte[] encoded)
        throws InvalidKeyException {
        try {
            ByteArrayReader bar = new ByteArrayReader(encoded);
            String algorithm = bar.readString();

            if (supportsKey(algorithm)) {
                SshKeyPair pair = newInstance(algorithm);

                return pair.decodePrivateKey(encoded);
            } else {
                return null;
            }
        } catch (IOException ioe) {
            return null;
        }
    }
    
    public static SshPrivateKey decodePrivateKey(InputStream in)
    throws InvalidKeyException, IOException {
 	
 	
 	ByteArrayOutputStream out = new ByteArrayOutputStream();
 	byte[] buf = new byte[4096];
 	int read;
 	
 	while((read = in.read(buf)) > -1) {
 		out.write(buf, 0, read);
 	}
 	
 	return decodePrivateKey(out.toByteArray());
 	
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
    public static SshPublicKey decodePublicKey(byte[] encoded)
        throws InvalidKeyException {
        try {
            ByteArrayReader bar = new ByteArrayReader(encoded);
            String algorithm = bar.readString();

            if (supportsKey(algorithm)) {
                SshKeyPair pair = newInstance(algorithm);

                return pair.decodePublicKey(encoded);
            } else {
                return null;
            }
        } catch (IOException ioe) {
            return null;
        }
    }
    
    public static SshPublicKey decodePublicKey(InputStream in)
       throws InvalidKeyException, IOException {
    	
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] buf = new byte[4096];
    	int read;
    	
    	while((read = in.read(buf)) > -1) {
    		out.write(buf, 0, read);
    	}
    	
    	return decodePublicKey(out.toByteArray());
    	
    }
}

