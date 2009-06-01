/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lee
 * 
 * This class allows multiple channels to operate over a single stream.
 */
public class MultiplexedConnection implements RequestHandler {

	int nextChannelId = 0;
	Hashtable channelsById = new Hashtable();
    DataInputStream in;
    DataOutputStream out;
    boolean running = false;
    ChannelFactory factory;
    int totalChannels;
    int maxChannels = 0;
    Thread thread;
    Vector activeChannels = new Vector();
    Vector listeners = new Vector();
    MessageStore globalMessages = new MessageStore(null, null);
    Hashtable requestHandlers = new Hashtable();
    Object sendLock = new Object();
    Object startLock = new Object();
    Request lastRequest;
    TimeoutCallback timeoutCallback = null;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
    	.getLog(MultiplexedConnection.class);
    // #endif

    public static final int MSG_CHANNEL_OPEN = 1;
    public static final int MSG_CHANNEL_OPEN_CONFIRMATION = 2;
    public static final int MSG_CHANNEL_OPEN_FAILURE = 3;
    public static final int MSG_CHANNEL_DATA = 4;
    public static final int MSG_CHANNEL_WINDOW_ADJUST = 5;
    public static final int MSG_CHANNEL_CLOSE = 6;
    public static final int MSG_DISCONNECT = 7;
    public static final int MSG_REQUEST = 8;
    public static final int MSG_REQUEST_SUCCESS = 9;
    public static final int MSG_REQUEST_FAILURE = 10;
    
    public static final int MSG_CHANNEL_REQUEST = 11;
    public static final int MSG_CHANNEL_REQUEST_SUCCESS = 12;
    public static final int MSG_CHANNEL_REQUEST_FAILURE = 13;
    
    MessageObserver channelOpenMessages = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MSG_CHANNEL_OPEN_CONFIRMATION:
                case MSG_CHANNEL_OPEN_FAILURE:
                    return true;
                default:
                    return false;
            }
        }
    };

    MessageObserver requestMessages = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MSG_REQUEST_SUCCESS:
                case MSG_REQUEST_FAILURE:
                case MSG_DISCONNECT:
                    return true;
                default:
                    return false;
            }
        }
    };    

    public MultiplexedConnection(ChannelFactory factory) {
        this.factory = factory;
    }
    
    public void setChannelFactory(ChannelFactory factory) {
    	this.factory = factory;
    }

    public void startProtocol(InputStream _in, OutputStream _out, boolean threaded) {
        setStreams(_in, _out);
        running = true;
        if(threaded) {
	        this.thread = new Thread(new Runner(), "MultiplexProtocolThread");
	        thread.start(); 
        }
        else {
        	this.thread = Thread.currentThread();
        	runProtocol();
        }
    }
    
    public void setMaxChannels(int maxChannels) {
    	this.maxChannels = maxChannels;
    }
    
    public int getMaxChannels() {
    	return maxChannels;
    }
    
    public int getTotalChannelCount() {
        return totalChannels;
    }

	/**
	 * Get the thread the protocol is running on.
	 * 
	 * @return protocol thread
	 */
	public Thread getThread() {
		return thread;
	}

    public int getActiveChannelCount() {
        return activeChannels.size();
    }

    public synchronized Channel[] getActiveChannels() {
        Channel[] tmp = new Channel[activeChannels.size()];
        activeChannels.copyInto(tmp);
        return tmp;
    }

    public void addListener(MultiplexedConnectionListener listener) {
        if (listener != null)
            listeners.addElement(listener);
    }

    public void stop() {
        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Shutting down multiplexed connection thread");
        // #endif
        running = false;
        if (thread != null && !Thread.currentThread().equals(thread))
            thread.interrupt();
    }
    
    /**
     * Close the streams. This will a 
     */
    public void close() {
        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Closing multiplexed connection streams");
        // #endif
    	running = false;
        try {
        	in.close();
        }
        catch(IOException ioe) {        	
        }
        try {
        	out.close();
        }
        catch(IOException ioe) {        	
        }        
    }

    protected void onChannelOpen(Message msg) throws IOException {
        String type = msg.readString();
        int remoteid = (int) msg.readInt();

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Open channel '" + type + "' [" + remoteid + "]");
        // #endif
        
        int remotepacket = (int) msg.readInt();
        int remotewindow = (int) msg.readInt();

        byte[] data = null;
        
        if(msg.available() > 0) { 
        	data = new byte[msg.available()];
        	msg.read(data);
        }

        if (factory == null) {
            sendChannelOpenFailure(remoteid, "This connection does not support the opening of channels");
            return;
        }

        Channel channel = null;
        try {
        	channel = factory.createChannel(this, type);
	        if (channel == null) {
	            sendChannelOpenFailure(remoteid, "Failed to create channel of type " + type);
	            return;
	        }  	
        }
        catch(ChannelOpenException coe) {
            sendChannelOpenFailure(remoteid, coe.getMessage() == null ? ( "Failed to create channel of type " + type + ". Reason " + coe.getReason()) : coe.getMessage() );
            return;        	
        }

        channel.init(this, remoteid, remotepacket, remotewindow);

        if (allocateChannel(channel) == -1) {
            sendChannelOpenFailure(remoteid, "Too many channels already open");

        } else {
        	try {
        		data = channel.open(data);
                sendChannelOpenConfirmation(channel, data);
        	}
        	catch(ChannelOpenException coe) {
                sendChannelOpenFailure(remoteid, coe.getMessage() == null ? ( "Failed to open channel. Reason " + coe.getReason()) : coe.getMessage() );
                return;        	
        	}
        }

        channel.fireChannelOpen(data);

    }

    protected void onChannelMessage(Message msg) throws IOException {

    	
        Integer channelid = new Integer((int) msg.readInt());
        Channel channel = (Channel) channelsById.get(channelid);

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Channel message [" + channelid + "]");
        // #endif
        
        if(channel != null) {
//        	if(channel.isCompressionEnabled() && msg.getMessageId() == MSG_CHANNEL_DATA) {
//        	
//	        	ByteArrayOutputStream out = new ByteArrayOutputStream();
//	        	byte[] tmp = new byte[65535];
//	        	
//	        	out.write(msg.array(), 0, msg.getPosition());
//	        	
//	        	ZStream compress = channel.getCompressionIn();
//	        	
//	        	compress.next_in = msg.array();
//	        	compress.next_in_index = msg.getPosition()+4;
//	        	compress.avail_in = msg.available()-4;
//	        	
//	        	int status;
//	        	
//	        	do {
//	        		compress.next_out = tmp;
//	        		compress.next_out_index = 0;
//	        		compress.avail_out = tmp.length;
//	        		
//	        		status = compress.deflate(JZlib.Z_PARTIAL_FLUSH);
//	        		switch(status) {
//	        		case JZlib.Z_OK:
//	        			out.write(tmp, 0, tmp.length - compress.avail_out);
//	        			break;
//	        		default:
//	        			throw new IOException("Compression Failure: deflate returned " + status);
//	        		}
//	        	} while(channel.getCompressionIn().avail_out==0);
//	        	
//	        	msg = new Message(out.toByteArray());
//	        	msg.skip(5); // Id and length fields 
//        	}
        
        	handleChannelMessage(msg, channel);        
        } else {
        	// #ifdef DEBUG
            log.warn("Message received for non-existent channel id " + channelid);
            // #endif

        }
    }

    protected void handleChannelMessage(Message msg, Channel channel) throws IOException {
        if (channel.processChannelMessage(msg)) {
        	channel.messageStore.addMessage(msg);
        }
    }
    
    protected void onChannelRequest(Message msg) throws IOException {
        Integer channelid = new Integer((int) msg.readInt());
        Channel channel = (Channel) channelsById.get(channelid);

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Channel request [" + channelid + "]");
        // #endif
        
        if (channel != null) {
        	String requestName = msg.readString();
        	boolean wantReply = msg.readBoolean();
        	byte[] data = msg.readBinaryString();
        	
        	Request request = new Request(requestName, data);
				
            if(channel.onChannelRequest(request)) {
                // #ifdef DEBUG
            	if(log.isDebugEnabled())
            		log.debug("Channel request success [" + channelid + "]");
                // #endif
            	if(wantReply) {
            		Packet reply = new Packet();
            		reply.write(MSG_CHANNEL_REQUEST_SUCCESS);
            		reply.writeInt(channelid.intValue());
            		reply.writeString(request.getRequestName()); 
            		reply.writeBinaryString(request.getRequestData());
                    sendMessage(reply);
            	}
            } else {
                // #ifdef DEBUG
            	if(log.isDebugEnabled())
            		log.debug("Channel request failure [" + channelid + "]");
                // #endif
            	if(wantReply) {
            		Packet reply = new Packet();
            		reply.write(MSG_CHANNEL_REQUEST_FAILURE);
            		reply.writeInt(channelid.intValue());
            		reply.writeString(request.getRequestName()); 
            		reply.writeBinaryString(request.getRequestData());
                    sendMessage(reply);
            	}
            	
            }
            
        } else {
        	// #ifdef DEBUG
            log.warn("Message received for non-existent channel id " + channelid);
            // #endif
        }    	
    }
    
    protected void onRequestSuccessOrFailure(Message msg) throws IOException {
        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Request success or failure");
        // #endif
    	globalMessages.addMessage(msg);
    }
    
    public void registerRequestHandler(String requestName, RequestHandler handler) {
    	requestHandlers.put(requestName, handler);
    }
    
    public void unregisterRequestHandler(String requestName) {
    	requestHandlers.remove(requestName);
    }
    
    protected void onRequest(Message msg) throws IOException {
    	String requestName = msg.readString();
    	boolean wantReply = msg.readBoolean();

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Request '" + requestName + "' wantsReply=" + wantReply);
        // #endif
    	
    	byte[] data = null;
    	
    	if(msg.available() > 0)
    		data = msg.readBinaryString();
    	
    	handleRequest(wantReply, new Request(requestName, data));
    }

    protected void handleRequest(boolean wantReply, Request request) throws IOException {
        boolean success;
        if(requestHandlers.containsKey(request.getRequestName())) {
    		success = ((RequestHandler)requestHandlers.get(request.getRequestName())).processRequest(request, this); 
    	} else
    		success = processRequest(request, null);
    	
    	if(wantReply) {
    		
    		Packet p = new Packet();
    		p.write(success ? MSG_REQUEST_SUCCESS : MSG_REQUEST_FAILURE);
    		if(request.getRequestData()!=null)
    			p.writeBinaryString(request.getRequestData());
    		
    		sendMessage(p);
    	}

   	
    	if(requestHandlers.containsKey(request.getRequestName())) {
    		((RequestHandler)requestHandlers.get(request.getRequestName())).postReply(this); 
    	} else
    		postReply(this);
    }

    protected void onDisconnect(Message msg) throws IOException {
        int reason = (int) msg.readInt();
        String desc = msg.readString();

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Remote disconnected:" + desc + " (" + reason + ")");
        // #endif
        
        stop();
    }

    private int allocateChannel(Channel channel) {
    	synchronized (channelsById) {
	        
	    	if(maxChannels > 0 && channelsById.size() >= maxChannels)
	    		return -1;
	    	
	    	Integer channelid = null;
	    	do {
	    		// This should only ever really happen once but just in case
	    		channelid = new Integer(nextChannelId++);
	    	} while(channelsById.containsKey(channelid));
	    	
	    	channelsById.put(channelid, channel);
	    	channel.channelid = channelid.intValue();
	        activeChannels.addElement(channel);
	        totalChannels++;
	        return channelid.intValue();
    	}
    }

    void freeChannel(Channel channel) {
    	synchronized (channelsById) {
	        channelsById.remove(new Integer(channel.channelid));
	        activeChannels.removeElement(channel);			
		}
    }
    
    public  boolean sendRequest(Request request, boolean wantReply) throws IOException {
    	return sendRequest(request, wantReply, 0);
    }
    
    public boolean sendRequest(Request request, boolean wantReply, int timeoutMs) throws IOException {

    	//#ifdef DEBUG
    	log.info("Sending request " + request.getRequestName() + " timeout=" + timeoutMs + " wantReply=" + wantReply);
    	//#endif
    	synchronized(sendLock) {
	    	Packet msg = new Packet();
	        msg.write(MSG_REQUEST);
	        msg.writeString(request.getRequestName());
	        msg.writeBoolean(wantReply);
	        if(request.getRequestData()!=null) {
	        	msg.writeBinaryString(request.getRequestData());
	        }
	        
	        sendMessage(msg);
	        
	        if(wantReply) {

	        	if(Thread.currentThread() == thread) {
	        		throw new IOException("You cannot send requests that require replies on the protocol thread.");
	        	}
	        	
	        	Message reply = globalMessages.nextMessage(requestMessages, timeoutMs);
	
	            switch (reply.getMessageId()) {
	                case MSG_REQUEST_SUCCESS:
	                case MSG_REQUEST_FAILURE:
	                	
	                	byte[] data = null;
	                	if(reply.available() > 0)
	                		data = reply.readBinaryString();
	                	
	                	request.setRequestData(data);
	                	
	                	boolean success = reply.getMessageId()==MSG_REQUEST_SUCCESS;
	                	
	                	//#ifdef DEBUG
	                	log.info("Remote responded to request " + request.getRequestName() + " with success=" + success);
	                	//#endif
	                	return success;
	                case MSG_DISCONNECT:
	                	throw new EOFException("Connection closed before request reply");
	                default:
	                    throw new IOException("Unexpected reply in channel open procedure");
	            }
	        } else
	        	return true;
    	}
    }

    public boolean processRequest(Request request, MultiplexedConnection connection) {
		return false;
	}

	public void postReply(MultiplexedConnection connection) {		
	}
    
    public void openChannel(Channel channel)throws IOException, ChannelOpenException {
    	openChannel(channel, 0);
    }
    
    public void openChannel(Channel channel, int timeout) throws IOException, ChannelOpenException {

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Open channel '" + channel.getType() + "' timeout=" + timeout);
        // #endif

    	synchronized (sendLock) {
            
	        byte[] data = channel.create();
	
	        if (allocateChannel(channel) == -1) {
	            throw new ChannelOpenException(ChannelOpenException.CHANNEL_LIMIT_EXCEEDED, 
	            		"Failed to allocate channel: too many active channels");
	        }
	
	        Packet msg = new Packet();
	        msg.write(MSG_CHANNEL_OPEN);
	        msg.writeString(channel.getType());
	        msg.writeInt(channel.channelid);
	        msg.writeInt(channel.getLocalPacket());
	        msg.writeInt(channel.getLocalWindow());
	        if (data != null)
	            msg.write(data);
	
	        sendMessage(msg);
	
	        try {
		        Message reply = channel.messageStore.nextMessage(channelOpenMessages, timeout);
		
		        switch (reply.getMessageId()) {
		            case MSG_CHANNEL_OPEN_CONFIRMATION:
		                int remoteid = (int) reply.readInt();
		                int remotepacket = (int) reply.readInt();
		                int remotewindow = (int) reply.readInt();
		                
		                data = null;
		                if(reply.available() > 0) {
		                	data = new byte[reply.available()];
		                	reply.read(data);
		                }
		                channel.init(this, remoteid, remotepacket, remotewindow);
		                channel.fireChannelOpen(data);
		                break;
		            case MSG_CHANNEL_OPEN_FAILURE:
		                String desc = reply.readString();
		                freeChannel(channel);
		                throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, desc);
		            default:
		                throw new IOException("Unexpected reply in channel open procedure");
		        }
	        } catch(InterruptedIOException ex) {
	        	throw new ChannelOpenException(ChannelOpenException.COMMUNICATION_TIMEOUT, "Timeout limit exceeded");
	        }
    	}

    }

    public void sendChannelData(Channel channel, byte[] data, int off, int len) throws IOException {
        Packet msg = new Packet();
        msg.write(MSG_CHANNEL_DATA);
        
//        if(channel.isCompressionEnabled()) {
//        	
//        	ByteArrayOutputStream out = new ByteArrayOutputStream();
//        	byte[] tmp = new byte[65535];
//        	
//        	ZStream compress = channel.getCompressionOut();
//        	
//        	compress.next_in = data;
//        	compress.next_in_index = off;
//        	compress.avail_in = len;
//        	
//        	int status;
//        	
//        	do {
//        		compress.next_out = tmp;
//        		compress.next_out_index = 0;
//        		compress.avail_out = tmp.length;
//        		
//        		status = compress.inflate(JZlib.Z_PARTIAL_FLUSH);
//        		switch(status) {
//        		case JZlib.Z_OK:
//        			out.write(tmp, 0, tmp.length - compress.avail_out);
//        			break;
//        		default:
//        			throw new IOException("Compression Failure: inflate returned " + status);
//        		}
//        	} while(channel.getCompressionIn().avail_out==0);
//        	
//        	data = out.toByteArray();
//        	off = 0;
//        	len = data.length;
//        }
        
        msg.writeInt(channel.remoteid);
        msg.writeBinaryString(data, off, len);

        sendMessage(msg);
    }

    private void sendChannelOpenFailure(int channelid, String desc) throws IOException {
        Packet msg = new Packet();
        msg.write(MSG_CHANNEL_OPEN_FAILURE);
        msg.writeInt(channelid);
        msg.writeString(desc);

        sendMessage(msg);
    }

    private void sendChannelOpenConfirmation(Channel channel, byte[] data) throws IOException {
        Packet msg = new Packet();
        msg.write(MSG_CHANNEL_OPEN_CONFIRMATION);
        msg.writeInt(channel.remoteid);
        msg.writeInt(channel.channelid);
        msg.writeInt(channel.getLocalPacket());
        msg.writeInt(channel.getLocalWindow());
        if (data != null)
            msg.write(data);

        sendMessage(msg);
    }

    public void sendWindowAdjust(Channel channel, int increment) throws IOException {
        Packet msg = new Packet();
        msg.write(MSG_CHANNEL_WINDOW_ADJUST);
        msg.writeInt(channel.remoteid);
        msg.writeInt(increment);

        sendMessage(msg);
    }

    public void closeChannel(Channel channel) throws IOException {
        Packet msg = new Packet();
        msg.write(MSG_CHANNEL_CLOSE);
        msg.writeInt(channel.remoteid);

        sendMessage(msg);
    }

    public void closeAllChannels() {

    	Channel channel;
    	for(Enumeration e = channelsById.elements(); e.hasMoreElements();) {
    		channel = (Channel) e.nextElement();
    		try {
    			channel.close();
    		} catch(Throwable t) { }
    	}
    	
    	channelsById.clear();
    }

    public void disconnect(String desc) {

    	// #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Disconnecting multiplexed connection");
        // #endif
        
        running = false;
        
        closeAllChannels();

        try {
            Packet msg = new Packet();
            msg.write(MSG_DISCONNECT);
            msg.writeInt(0);
            msg.writeString(desc);

            sendMessage(msg);

        } catch (IOException ex) {
        	// #ifdef DEBUG
        	if(log.isDebugEnabled())
        		log.debug("Error on disconnect", ex);
            // #endif
        } finally {
            try {
                in.close();
            } catch (Throwable t) {
            }
            try {
                out.close();
            } catch (Throwable t) {
            }
            
            
        }
    }

    protected void sendMessage(Packet msg) throws IOException {
        
        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Sending message of " + msg.size() + " bytes");
        // #endif
    	
        if(!isRunning()) {
            throw new IOException("Not connected.");
        }

        msg.prepare();
        
        // log.info("Writing " + msg.size() + " bytes of socket data");
        out.write(msg.array(), 0, msg.size());
        out.flush();

        // #ifdef DEBUG
    	if(log.isDebugEnabled())
    		log.debug("Sent message of " + msg.size() + " bytes");
        // #endif
    }
    
    public void waitForProtocolStart(long timeout) throws InterruptedException {
    	synchronized(startLock) {
    		if(!isRunning()) {
    			startLock.wait(timeout);
    		}    		
    	}
    }
    
    public boolean isRunning() {
    	return running;
    }
    
    private void setStreams(InputStream _in, OutputStream _out) {
    	this.in = new DataInputStream(_in);
    	this.out = new DataOutputStream(_out);
    }
    
    
    private void runProtocol() {
    	
    	
    	try {
	    	synchronized (startLock) {
		    	startLock.notifyAll();				
			}
	
	        for (Enumeration it = listeners.elements(); it.hasMoreElements();) {
	            ((MultiplexedConnectionListener) it.nextElement()).onConnectionOpen();
	        }  
	        
	        while (running) {
	
	            try {

	                int msglength = in.readInt();
	
	                if (msglength <= 0) {
	                    // #ifdef DEBUG
	                    log.error("Invalid message length of " + msglength + " bytes");
	                    // #endif
	                    stop();
	                } else {
	
	                    byte[] tmp = new byte[msglength];
	                    in.readFully(tmp);
	
	                    Message msg = new Message(tmp);
	
	                    switch (msg.getMessageId()) {
	                        case MSG_CHANNEL_OPEN:
	                            onChannelOpen(msg);
	                            break;
	                        case MSG_CHANNEL_OPEN_CONFIRMATION:
	                            onChannelMessage(msg);
	                            break;
	                        case MSG_CHANNEL_OPEN_FAILURE:
	                            onChannelMessage(msg);
	                            break;
	                        case MSG_CHANNEL_DATA:
	                            onChannelMessage(msg);
	                            break;
	                        case MSG_CHANNEL_REQUEST:
	                        	onChannelRequest(msg);
	                        	break;
	                        case MSG_CHANNEL_WINDOW_ADJUST:
	                            onChannelMessage(msg);
	                            break;
	                        case MSG_CHANNEL_CLOSE:
	                            onChannelMessage(msg);
	                            break;
	                        case MSG_CHANNEL_REQUEST_SUCCESS:
	                        case MSG_CHANNEL_REQUEST_FAILURE:
	                        	onChannelMessage(msg);
	                        	break;
	                        case MSG_DISCONNECT:
	                            onDisconnect(msg);
	                            break;
	                        case MSG_REQUEST_SUCCESS:
	                        case MSG_REQUEST_FAILURE:
	                        	onRequestSuccessOrFailure(msg);
	                            break;
	                        case MSG_REQUEST:
	                            onRequest(msg);
	                            break;
	                        default:
	                            throw new IOException("Unexpected message id " + msg.getMessageId());
	
	                    }
	
	                } 
	            } catch(InterruptedIOException ex) {
            		if(timeoutCallback!=null && timeoutCallback.isAlive(MultiplexedConnection.this)) {
            			continue;
            		}
	            	if(running) {
	            		// #ifdef DEBUG
	            		log.error("Multiplexed connection timed out", ex);
	            		// #endif
	            		stop();
	            	}
	            } catch (IOException ex) {
	            	if(running) {
	            		if(!(ex instanceof EOFException)) {
	            			// #ifdef DEBUG
	            			log.error("Multiplexed connection thread failed", ex);
	            			// #endif
	            		}
		                stop();
	            	}
	            }
	
	        }

    	} finally {
    	    running = false;
	        for (Enumeration it = listeners.elements(); it.hasMoreElements();) {
	            ((MultiplexedConnectionListener) it.nextElement()).onConnectionClose();
	        }    	
    	}
    }
    
    class Runner implements Runnable {

    	public Runner() {
    	}
        /**
         * Perform the multiplexed connection protocol
         */
        public void run() {
            try {
                runProtocol();
            }
            catch(Throwable t) {
            	// #ifdef DEBUG
                log.error("Protocol thread failed.", t);
                // #endif
            }
        }
    }

	public void setTimeoutCallback(TimeoutCallback timeoutCallback) {
		this.timeoutCallback = timeoutCallback;
	}
}
