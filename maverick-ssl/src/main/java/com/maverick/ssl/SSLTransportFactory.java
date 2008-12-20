package com.maverick.ssl;

public class SSLTransportFactory {
	
	static Class impl;
	
	static {
		
		/**
		 * We now default to using SSLEngine based implementation if
		 * Java version is 1.5 or greater. 
		 */
		try {
			Class.forName("javax.net.ssl.SSLEngine");
			impl = Class.forName("com.maverick.ssl.SSLTransportJCE");
		} catch(Throwable t) {
			impl = SSLTransportImpl.class;
		}
	}
	
	
	//#ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SSLTransportFactory.class);	
    //#endif
    
	public static void setTransportImpl(Class impl) {
		SSLTransportFactory.impl = impl;
	}
	
	
	public static SSLTransport newInstance() {
		try {
			return (SSLTransport) impl.newInstance();
		} catch(Throwable t) {
			//#ifdef DEBUG 
			log.error("Failed to create SSLTransport instance", t);
			//#endif
			return new SSLTransportImpl();
		}
	}

}
