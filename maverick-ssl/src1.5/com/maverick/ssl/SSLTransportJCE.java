package com.maverick.ssl;


import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class SSLTransportJCE implements SSLTransport {

	private static SSLContext sslContext;
	private SSLSession session;
    private SSLEngine engine;
    private SSLEngineResult.HandshakeStatus hsStatus;
    private boolean initialHandshake;

    SSLEngineResult res;
    
    // #ifdef DEBUG
    static Log log = LogFactory.getLog(SSLTransportJCE.class);
    // #endif
    
    private InputStream rawIn;
    private OutputStream rawOut;
    
    byte[] bufferIn;
    byte[] bufferOut;

	ByteBuffer in_outputData;
	ByteBuffer in_inputData;
	ByteBuffer out_outputData;
	
    InputStream sslIn = new SSLInputStream();
    OutputStream sslOut = new SSLOutputStream();
    

    public InputStream getInputStream() {
    	return sslIn;
    }
    
    public OutputStream getOutputStream() {
    	return sslOut;
    }
    
    public void initialize(InputStream in, OutputStream out) throws IOException {
    	this.rawIn = in;
    	this.rawOut = out;
    	
//    	 Initialize SSL
        try {
            // Create an SSLEngine to use
            engine = getSSLContext().createSSLEngine();

            // Duh! we're the server
            engine.setUseClientMode(true);

            if(System.getProperty("com.maverick.ssl.preferredCiphers")!=null 
            		&& !"".equals(System.getProperty("com.maverick.ssl.preferredCiphers"))) {
            	StringTokenizer tokens = new StringTokenizer(System.getProperty("com.maverick.ssl.preferredCiphers"), ",");
            	Vector tmp = new Vector();
            	
            	while(tokens.hasMoreTokens()) {
            		tmp.addElement(tokens.nextToken());
            	}

            	//#ifdef DEBUG
            	log.info("Setting preferred ciphers to " + System.getProperty("com.maverick.ssl.preferredCiphers"));
            	//#endif
            	
            	String[] ciphers = engine.getEnabledCipherSuites();
            	
            	for(int i=0;i<ciphers.length;i++) {
            		if(!tmp.contains(ciphers[i]))
            			tmp.add(ciphers[i]);
            	}
            	
            	ciphers = new String[tmp.size()];
            	tmp.copyInto(ciphers);
            	engine.setEnabledCipherSuites(ciphers);
            }
            
            
            // Get the session and begin the handshake
            session = engine.getSession();
            engine.beginHandshake();
            hsStatus = engine.getHandshakeStatus();
            initialHandshake = true;

            // This is a dummy byte buffer which is used during initial
            // handshake negotiation
            bufferIn = new byte[session.getPacketBufferSize()];
            bufferOut = new byte[session.getPacketBufferSize()];
            
        	performInitialHandshake();

        	//#ifdef DEBUG
        	log.info("SSL handshake complete using protocol " + engine.getSession().getProtocol() + " with cipher " + engine.getSession().getCipherSuite());
        	//#endif
        	
        	in_outputData = ByteBuffer.allocateDirect(session.getPacketBufferSize());
        	in_inputData = ByteBuffer.allocateDirect(session.getPacketBufferSize()); 
        	out_outputData = ByteBuffer.allocateDirect(session.getPacketBufferSize());
        	in_outputData.flip(); // Force an empty buffer state
        } catch(Exception ex) {
            throw new IOException(ex.getMessage());
        }    	
    }
    
    private void performInitialHandshake() throws IOException {
    	
	        /**
	         * Perform the initial handshake operation
	         */
        	ByteBuffer dummy = ByteBuffer.allocate(0);
        	ByteBuffer outputBuffer = ByteBuffer.allocateDirect(session.getPacketBufferSize());
        	ByteBuffer inputBuffer = ByteBuffer.allocateDirect(session.getPacketBufferSize());
        
	        while(initialHandshake) {
	
	        switch (hsStatus) {
	        case FINISHED:
	            /**
	             * We have completed the handshake so lets start doing the real stuff
	             */
	            // #ifdef DEBUG
	        	if(log.isDebugEnabled())
	        		log.debug("SSL Handshake finished");
	        	// #endif
	            initialHandshake = false;
	            return;
	
	        case NEED_TASK:
	
	            /**
	             * The SSL engine needs to perform a task... do it!
	             */
	        	// #ifdef DEBUG
	            if(log.isDebugEnabled())
	            	log.debug("Performing SSLEngine task");
	            // #endif
	            // Execute the tasks
	            Runnable task;
	            while ((task = engine.getDelegatedTask()) != null) {
	                task.run();
	            }
	            hsStatus = engine.getHandshakeStatus();
	            break;
	
	        case NEED_UNWRAP:
	
	            /**
	             * SSL engine wants more data to make sure we're reading from
	             * the socket
	             */
	        	// #ifdef DEBUG
	            if(log.isDebugEnabled())
	            	log.debug("Reading SSL data from raw InputStream");
	            // #endif
	            boolean forceRead = false;
	            
	            do {
		            inputBuffer.flip();
		            
		            if(!inputBuffer.hasRemaining() || forceRead) {
		            	
		            	inputBuffer.compact();
		            	int read = rawIn.read(bufferIn);
		
		            	if(read==-1)
		            		throw new EOFException("Unexpected EOF whilst waiting for SSL unwrap");
		           	
		            	inputBuffer.put(bufferIn, 0, read);
		            	inputBuffer.flip();
		            }
		
		           	res = engine.unwrap(inputBuffer, outputBuffer);
		
	           		inputBuffer.compact();

	           		forceRead = res.getStatus()==SSLEngineResult.Status.BUFFER_UNDERFLOW;
	           		
	            } while(forceRead);
	            
	           	if(res.getStatus()!=SSLEngineResult.Status.OK) {
	           		throw new IOException(res.getStatus().toString());
	           	}
	           	
	           	hsStatus = res.getHandshakeStatus();
	           	
	           	sendOutput(outputBuffer);
	           	break;
	
	        case NEED_WRAP:
	
	            /**
	             * SSL engine wants to write data to the socket so make sure
	             * we can write to it
	             */
	            // #ifdef DEBUG
	        	if(log.isDebugEnabled())
	        		log.debug("Writing SSL data to raw OutputStream");
	        	// #endif
	        	res = engine.wrap(dummy, outputBuffer);
	        	
	           	if(res.getStatus()!=SSLEngineResult.Status.OK) {
	           		throw new IOException(res.getStatus().toString());
	           	}
	           	
	           	hsStatus = res.getHandshakeStatus();

	        	sendOutput(outputBuffer);
	        	
	            break;
	
	        case NOT_HANDSHAKING:
	            /**
	             * This state should never be caught here
	             */
	        	// #ifdef DEBUG
	            log.error("doHandshake has caught a NOT_HANDSHAKING state.. This is impossible!");
	            // #endif
	        }
	    }    	
    }
    
    private void sendOutput(ByteBuffer outputBuffer) throws IOException {
    	outputBuffer.flip();
    	
    	int remaining = outputBuffer.remaining();
    	while(remaining > 0) {
    		outputBuffer.get(bufferOut, 0, Math.min(remaining, bufferOut.length));
    		rawOut.write(bufferOut, 0, remaining - outputBuffer.remaining());
    		remaining = outputBuffer.remaining();
    	}
    	
    	outputBuffer.compact();
    }
    
    /**
     * Get the SSL context. If SSL isn't initialized when this is called we
     * initialize it.
     * @return SSLContext
     * @throws IOException
     */
    private synchronized static SSLContext getSSLContext() throws IOException {
        if (sslContext != null) {
            return sslContext;
        }
        initializeSSL();
        return sslContext;
    }

    /**
     * Initialize SSL. If at all possible this should be called before any
     * connection attempts are made.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void initializeSSL() throws FileNotFoundException, IOException {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { new SSLTransportTrustManager() }, null);
        } catch (Exception ex) {
            throw new IOException("SSL initialization failed: " + ex.getMessage());
        }
    }
    
    /**
     * Initializes the SSLContext with the value supplied.
     * @param context
     */
    public synchronized static void setSSLContext(SSLContext context) {
        sslContext = context;
    }
    
    class SSLInputStream extends InputStream {
    	
    	SSLInputStream() {
    		
    	}
    	
    	public int read() throws IOException {
    		byte[] b = new byte[1];
    		if(readData(b,0,1)==1)
    			return (int) b[0];
    		else
    			return -1;
    	}
    	
    	public int read(byte[] buf, int off, int len) throws IOException {
    		return readData(buf, off, len);
    	}
    	
        /**
         * @param buf
         * @param off
         * @param len
         * @return int
         * @throws IOException
         */
        public int readData(byte[] buf, int off, int len) throws IOException {
        	
        	try {
				while(true) {

					if(in_outputData.hasRemaining()) {
						int actualLen = Math.min(len, in_outputData.remaining());
						in_outputData.get(buf, off, actualLen);
						return actualLen;
					}
					
					int read = -1;
					
					do {
						
						in_inputData.flip();
						
						if(!in_inputData.hasRemaining() || res.getStatus()==SSLEngineResult.Status.BUFFER_UNDERFLOW) {
							
							in_inputData.compact();
							read = rawIn.read(bufferIn, 0, Math.min(in_inputData.remaining(), bufferIn.length));
				        	
				       		if(read==-1)
				       			return -1;
				       		
				       		if(read==0)
				       			return 0;

				   			in_inputData.put(bufferIn,0,read);
				   			in_inputData.flip();
						}
						
			   			in_outputData.compact();
			   			res = engine.unwrap(in_inputData, in_outputData);
			   			in_outputData.flip();
			   			in_inputData.compact();
					
					} while(res.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW);
				    
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
        	
        }    	
    }
    
    
    class SSLOutputStream extends OutputStream {
    	
    	public void write(int b) throws IOException {
    		writeData(new byte[] { (byte)b }, 0, 1);
    	}
    	
    	public void write(byte[] buf, int off, int len) throws IOException {
    		writeData(buf, off, len);
    	}
    	
        void writeData(byte[] buf, int off, int len) throws IOException {
        	
        	ByteBuffer source = ByteBuffer.wrap(buf, off, len);
        	
        	while(source.remaining() > 0) {
    	    	res = engine.wrap(source, out_outputData);
    	    	
    	    	out_outputData.flip();
    	    	
    	    	int remaining = out_outputData.remaining();
    	    	while(remaining > 0) {
    	    		out_outputData.get(bufferOut, 0, Math.min(remaining, bufferOut.length));
    	    		rawOut.write(bufferOut, 0, remaining - out_outputData.remaining());
    	    		remaining = out_outputData.remaining();
    	    	}
    	    	out_outputData.compact();
        	}
        }
    }
    
	public void close() throws SSLException {
	
		try {

			if(!engine.isOutboundDone())
				engine.closeOutbound();
			
			if(engine.isInboundDone())
				engine.closeInbound();
			
		} catch (Exception e) {
		} finally {
			try {
				rawIn.close();
			} catch(Throwable t) { }
			try {
				rawOut.close();
			} catch(Throwable t) { }
		}
		
	}
}