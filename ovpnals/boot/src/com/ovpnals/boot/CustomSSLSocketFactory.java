
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
			
package com.ovpnals.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class CustomSSLSocketFactory extends SocketFactory {

    private static final Log log = LogFactory.getLog(CustomSSLSocketFactory.class);
    private static SocketFactory instance;
    private static Class socketFactoryImpl = CustomSSLSocketFactory.class;


    /**
     */
    public CustomSSLSocketFactory() {

    }

    public static SocketFactory getDefault() {
        try {
            return instance == null ? instance = (SocketFactory) socketFactoryImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + socketFactoryImpl.getCanonicalName(), e);
            return instance == null ? instance = new CustomSSLSocketFactory() : instance;
        }
    }

    /**
     * @param socketFactoryImpl
     */
    public static void setFactoryImpl(Class socketFactoryImpl) {
        CustomSSLSocketFactory.socketFactoryImpl = socketFactoryImpl;
    }
    
    public Socket createSocket() throws IOException {
        SSLSocket theSocket = (SSLSocket) getSocketFactory().createSocket();
        return theSocket;
    }

    public Socket createSocket(String hostname, int port) throws IOException, UnknownHostException {
        SSLSocket theSocket = (SSLSocket) getSocketFactory().createSocket(hostname, port);
        return theSocket;
    }

    public Socket createSocket(String hostname, int port, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
        SSLSocket theSocket = (SSLSocket) getSocketFactory().createSocket(hostname, port, arg2, arg3);
        return theSocket;
    }

    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        SSLSocket theSocket = (SSLSocket) getSocketFactory().createSocket(arg0, arg1);
        return theSocket;
    }

    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        SSLSocket theSocket = (SSLSocket) getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
        return theSocket;
    }

    private SSLSocketFactory getSocketFactory() throws IOException {
        try {
            SSLContext sslCtx = SSLContext.getInstance("SSL");
            KeyManager[] aKM = SSLKeyManager.getKeyManagerArray();
            TrustManager[] aTM = SSLTrustManager.getTrustManagerArray();
            sslCtx.init(aKM, aTM, null);
            SSLSocketFactory socketFactory = sslCtx.getSocketFactory();
            return socketFactory;
        } catch (KeyManagementException e) {
            log.error("Cannot create SSL socket", e);
            throw new IOException("Cannot create SSL socket: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot create SSL socket", e);
            throw new IOException("Cannot create SSL socket: " + e.getMessage());
        }
    }



    
}