
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
			
package net.openvpn.als.server;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509KeyManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.ContextKey;
import net.openvpn.als.boot.KeyStoreManager;
import net.openvpn.als.boot.PropertyClass;


/**
 * Implementation of an {@link javax.net.ssl.X509KeyManager} that uses
 * the OpenVPNALS keystore and the <b>Active Certifice Name</b>
 * configured in the property database to determine the alias to load as the
 * SSL Certificate.
 */
public class CustomKeyManager implements X509KeyManager {

    final static Log log = LogFactory.getLog(CustomKeyManager.class);
    private String keyPassword;
    private PropertyClass contextConfig;
    /**
     * Constructor
     * 
     * @param keyPassword key password
     */
    public CustomKeyManager(String keyPassword) {
        this.keyPassword = keyPassword;
        contextConfig = ContextHolder.getContext().getConfig();
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[], java.security.Principal[], java.net.Socket)
     */
    public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket socket) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String, java.security.Principal[], java.net.Socket)
     */
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        String alias = ContextHolder.getContext().getConfig().retrieveProperty(new ContextKey("webServer.alias"));
        return alias;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
     */
    public X509Certificate[] getCertificateChain(String certname) {
        try {
            Certificate[] f = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getCertificateChain(certname);
            List l = new ArrayList();
            for(int i = 0 ; i < f.length ; i++) {
                if(f[i] instanceof X509Certificate) {
                    l.add(f[i]);
                }
            }
            return (X509Certificate[])l.toArray(new X509Certificate[l.size()]); 
        } catch (Exception e) {
            Main.log.error(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        String str[] = { "" };
        return str;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
     */
    public PrivateKey getPrivateKey(String alias) {
        try {
            return (PrivateKey) KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getPrivateKey(contextConfig.retrieveProperty(new ContextKey("webServer.alias")), contextConfig.retrieveProperty(new ContextKey("webServer.keystore.sslCertificate.password")).toCharArray());
        } catch (Exception e) {
            Main.log.error(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        String str[] = { contextConfig.retrieveProperty(new ContextKey("webServer.alias")) };
        return str;
    }

}