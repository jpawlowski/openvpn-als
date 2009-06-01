package com.maverick.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocketWithLayeredTransportFactory  {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SocketWithLayeredTransportFactory.class);
    // #endif	
    
	static SocketWithLayeredTransportFactory instance = null;
	static Class socketFactoryImpl = SocketWithLayeredTransportFactory.class;
	
	public SocketWithLayeredTransport createSocket(String arg0, int arg1) throws IOException,
			UnknownHostException {
		return new SocketWithLayeredTransport(arg0, arg1);
	}

	public SocketWithLayeredTransport createSocket(InetAddress arg0, int arg1) throws IOException {
		return new SocketWithLayeredTransport(arg0, arg1);
	}
	
	
	public static SocketWithLayeredTransportFactory getDefault() {
		
		try {
			return instance==null ? instance = (SocketWithLayeredTransportFactory) socketFactoryImpl.newInstance() : instance;
		} catch (Exception e) {
			//#ifdef DEBUG
			log.error("Failed to create instance of socket factory" + socketFactoryImpl.getName(), e);
			//#endif
			return instance = new SocketWithLayeredTransportFactory();
		}
	}
	
	public static void setFactoryImpl(Class socketFactoryImpl) {
		SocketWithLayeredTransportFactory.socketFactoryImpl = socketFactoryImpl;
	}
}
