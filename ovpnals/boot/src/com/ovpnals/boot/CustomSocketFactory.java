package com.ovpnals.boot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomSocketFactory extends SocketFactory {

    static Log log = LogFactory.getLog(CustomSSLSocketFactory.class);

    static SocketFactory instance;
    static Class socketFactoryImpl = CustomSocketFactory.class;
    
	public Socket createSocket(String arg0, int arg1) throws IOException,
			UnknownHostException {
		return new Socket(arg0, arg1);
	}

	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		return new Socket(arg0, arg1);
	}

	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
			throws IOException, UnknownHostException {
		return new Socket(arg0, arg1, arg2, arg3);
	}

	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
			int arg3) throws IOException {
		return new Socket(arg0, arg1, arg2, arg3);
	}

    public static SocketFactory getDefault()  {
        try {
			return instance == null ? instance = (SocketFactory) socketFactoryImpl.newInstance() : instance;
		} catch (Exception e) {
			log.error("Could not create instance of class " + socketFactoryImpl.getCanonicalName(), e);
			return instance == null ? instance = new CustomSocketFactory() : instance;
		}
    }
    
    public static void setFactoryImpl(Class socketFactoryImpl) {
    	CustomSocketFactory.socketFactoryImpl = socketFactoryImpl;
        instance = null;
    }	
}
