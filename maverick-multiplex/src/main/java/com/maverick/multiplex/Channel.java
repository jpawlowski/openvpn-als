/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author lee
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Channel {

    MultiplexedConnection connection;
    int channelid;
    int remoteid;
    String type;
    int timeout;
    DataWindow remotewindow;
    DataWindow localwindow;
    Vector listeners = new Vector();
    ChannelInputStream in;
    ChannelOutputStream out;
    int windowSequence = 0;
    boolean isClosed;
    boolean autoConsumeInput = false;
    boolean compressionEnabled = false;
    int compressionLevel = 6;
//    private ZStream compressionIn;
//    private ZStream compressionOut;
    
    //#ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Channel.class);
    //#endif
    MessageObserver stickyMessages = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                    return true;
                default:
                    return false;
            }
        }
    };

    MessageObserver channelRequestMessages = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_REQUEST_SUCCESS:
                case MultiplexedConnection.MSG_CHANNEL_REQUEST_FAILURE:
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                    return true;
                default:
                    return false;
            }
        }
    };
    final MessageObserver WINDOW_ADJUST_MESSAGES = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_WINDOW_ADJUST:
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                    return true;
                default:
                    return false;
            }
        }
    };

    final MessageObserver CHANNEL_CLOSE_MESSAGES = new MessageObserver() {
        public boolean wantsNotification(Message msg) {
            switch (msg.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                    return true;
                default:
                    return false;
            }
        }
    };

    final MessageObserver CHANNEL_DATA_MESSAGES = new MessageObserver() {
        public boolean wantsNotification(Message msg) {

            // Access to this observer is synchronized by the ThreadSynchronizer
            // so we can flag our InputStream as blocking when the method is
            // called and released once we have found a message
            switch (msg.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_DATA:
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                    return true;
                default:
                    return false;
            }
        }
    };

    protected MessageStore messageStore = new MessageStore(this, stickyMessages);

    public Channel(String type, int localpacket, int localwindow) {
        this(type, localpacket, localwindow, 0, false, 0);
    }
    
    public Channel(String type, int localpacket, int localwindow, boolean compress, int compressionLevel) {
    	this(type, localpacket, localwindow, 0, compress, compressionLevel);
    }

    public Channel(String type, int localpacket, int localwindow, int timeout, boolean compress, int compressionLevel) {
        this.type = type;
        this.localwindow = new DataWindow(localpacket, localwindow);
        this.timeout = timeout;
        this.compressionEnabled = compress;
        this.compressionLevel = compressionLevel;
        in = new ChannelInputStream(CHANNEL_DATA_MESSAGES);
        out = new ChannelOutputStream();
        
//        compressionIn = new ZStream();
//        compressionOut = new ZStream();
//        
//        compressionIn.inflateInit();
//        compressionOut.deflateInit(compressionLevel);
        
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public MultiplexedConnection getConnection() {
        return connection;
    }

    public void init(MultiplexedConnection connection, int remoteid, int remotepacket, int remotewindow) {
        this.connection = connection;
        this.remoteid = remoteid;
        this.remotewindow = new DataWindow(remotewindow, remotepacket);
    }

    public synchronized boolean sendChannelRequest(Request request, boolean wantReply) throws IOException {
        return sendChannelRequest(request, wantReply, 0);
    }

    public synchronized boolean sendChannelRequest(Request request, boolean wantReply, int timeoutMs) throws IOException {

        Packet msg = new Packet();
        msg.write(MultiplexedConnection.MSG_CHANNEL_REQUEST);
        msg.writeInt(channelid);
        msg.writeString(request.getRequestName());
        msg.writeBoolean(wantReply);
        msg.writeBinaryString(request.getRequestData());

        connection.sendMessage(msg);

        if (wantReply) {
            Message reply = messageStore.nextMessage(channelRequestMessages, timeoutMs);

            switch (reply.getMessageId()) {
                case MultiplexedConnection.MSG_CHANNEL_REQUEST_SUCCESS:
                case MultiplexedConnection.MSG_CHANNEL_REQUEST_FAILURE:

                    byte[] data = null;
                    if (reply.available() > 0)
                        data = reply.readBinaryString();

                    request.setRequestData(data);

                    return reply.getMessageId() == MultiplexedConnection.MSG_CHANNEL_REQUEST_SUCCESS;
                case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                	checkCloseStatus(true);
                    throw new EOFException("Channel closed before request reply");
                default:
                    throw new IOException("Unexpected reply in channel open procedure");
            }
        } else
            return true;
    }

    boolean closing = false;

    public void close() {
    	if(connection == null) {
            // #ifdef DEBUG
            log.warn("Closing channel of type " + type +" before it was opened");
            // #endif
            return;    		
    	}
        // #ifdef DEBUG
        log.debug("Close channel '" + getType() + "'");
        // #endif

        boolean performClose = false;

        synchronized (this) {
            if (!closing)
                performClose = closing = true;
        }

        try {
        	
        	if (performClose) {

                // Close the ChannelOutputStream
                out.close();
                in.close();

                // Send our close message
                connection.closeChannel(this);

        	}
        } catch (EOFException eof) {
            // Ignore this is the message store informing of close/eof
        } catch (IOException ex) {
            // IO Error during close so the connection has dropped
            connection.disconnect("IOException during channel close: " + ex.getMessage());

        } finally {
        	isClosed = true;
        	checkCloseStatus(false);
        }
    }

    private void checkCloseStatus(boolean remoteClosed) {

        if(!isClosed) {
            close();
            if(!remoteClosed)
                remoteClosed = (messageStore.hasMessage(CHANNEL_CLOSE_MESSAGES)!=null);
        }

        if(remoteClosed) {
        		if (connection != null)
        			connection.freeChannel(this);    

                synchronized (listeners) {
                    
                    onChannelClose();
                    
                    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                        ((ChannelListener) e.nextElement()).onChannelClose(this);
                    }
                    
                }
            }

    }
    public abstract byte[] open(byte[] data) throws IOException, ChannelOpenException;

    public abstract byte[] create() throws IOException;

    void fireChannelOpen(byte[] data) {

        onChannelOpen(data);

        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            ((ChannelListener) e.nextElement()).onChannelOpen(this);
        }

    }

    public abstract void onChannelOpen(byte[] data);

    public void onChannelData(byte[] buf, int off, int len) {
    };

    public abstract void onChannelClose();

    public void addListener(ChannelListener listener) {
        if (listener != null)
            listeners.addElement(listener);
    }

    public boolean onChannelRequest(Request request) {
        return false;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public InputStream getInputStream() {
        return in;
    }

    public String getType() {
        return type;
    }

    public int getLocalWindow() {
        return localwindow.available();
    }

    public int getLocalPacket() {
        return localwindow.getPacketSize();
    }

    public boolean isClosed() {
        return messageStore.isClosed();
    }

    protected void adjustWindow(int increment) throws IOException {
        localwindow.adjust(increment);
        connection.sendWindowAdjust(this, increment);
    }
    
    private void uncompressMesasge(Message msg) {
    	
    }
    
    private void compressMessage(Message msg) {
    	
    }
    
    protected boolean processChannelMessage(Message msg) throws IOException {
    	
    	boolean addToMessageStore = true;

    	switch(msg.getMessageId()) {
    	case MultiplexedConnection.MSG_CHANNEL_CLOSE:
    		checkCloseStatus(true);
    		break;
        case MultiplexedConnection.MSG_CHANNEL_DATA:
        	
            if (autoConsumeInput) {
                localwindow.consume(msg.available()-4);
                if (localwindow.available() <= in.buffer.length / 2) {
                    adjustWindow(in.buffer.length - localwindow.available());
                }
                addToMessageStore = false;
            }
            
            if(compressionEnabled) {
            	uncompressMesasge(msg);
            } 
            
            onChannelData(msg.array(), msg.getPosition()+4, msg.available()-4);
            
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                ((ChannelListener) e.nextElement()).onChannelData(this, msg.array(), msg.getPosition()+4, msg.available()-4);
            }
            
            break;
    	default:
    			break;
    	}
    	
    	return addToMessageStore;
    }

    Message processMessages(MessageObserver messagefilter) throws IOException, EOFException {

        Message msg;

        /**
         * Collect the next channel message from the connection protocol
         */
        msg = messageStore.nextMessage(messagefilter, timeout);

        switch (msg.getMessageId()) {

            case MultiplexedConnection.MSG_CHANNEL_WINDOW_ADJUST:
                int i = (int) msg.readInt();
                remotewindow.adjust(i);
                windowSequence++;
                break;

            case MultiplexedConnection.MSG_CHANNEL_DATA:
                msg.skip(4); // Skip the length
                in.write(msg.array(), msg.getPosition(), msg.available());
                break;

            case MultiplexedConnection.MSG_CHANNEL_CLOSE:
                checkCloseStatus(true);
                throw new EOFException("The channel is closed");

            default:
                break;
        }

        return msg;
    }

    class ChannelOutputStream extends OutputStream {

        boolean isEOF = false;
        boolean closed = false;

        public void write(int b) throws java.io.IOException {
            write(new byte[] { (byte) b }, 0, 1);
        }

        public synchronized void write(byte[] buf, int offset, int len) throws IOException {

            int write;

            do {

                if (remotewindow.available() <= 0) {
                    Message msg = processMessages(WINDOW_ADJUST_MESSAGES);
                }

                if (closed) {
                    throw new IOException("The channel stream is closed!");
                }

                write = remotewindow.available() < remotewindow.getPacketSize() ? (remotewindow.available() < len ? remotewindow.available()
                    : len)
                    : (remotewindow.getPacketSize() < len ? remotewindow.getPacketSize() : len);

                if (write > 0) {                   
                	
                	connection.sendChannelData(Channel.this, buf, offset, write);
                    remotewindow.consume(write);
                    len -= write;
                    offset += write;
                }

            } while (len > 0);

        }

        public void close() throws IOException {
        	closed = true;
        	Channel.this.close();
        }

    }

    class ChannelInputStream extends InputStream {

        byte[] buffer;
        int unread = 0;
        int position = 0;
        int base = 0;
        MessageObserver messagefilter;
        long transfered = 0;
        boolean closed = false;

        ChannelInputStream(MessageObserver messagefilter) {
            buffer = new byte[localwindow.available()];
            this.messagefilter = messagefilter;
        }

        public synchronized int available() throws IOException {
        	if(closed && unread==0) {
        		return -1;
        	}

            try {
                if (unread == 0) {
                    if (messageStore.hasMessage(messagefilter) != null) {
                        processMessages(messagefilter);
                    }
                }
                return unread;
            } catch (EOFException ex) {
            	closed = true;
            	messageStore.close();
                return -1;
            }
        }

        public void close() {
        	Channel.this.close();
        }

        public int read() throws IOException {
            byte[] b = new byte[1];
            int ret = read(b, 0, 1);
            if (ret > 0) {
                return b[0] & 0xFF;
            } else {
                return -1;
            }
        }

        public long skip(long len) throws IOException {

            int count = unread < len ? unread : (int) len;

            try {
                if (count == 0 && isClosed())
                    throw new EOFException("The inputstream is closed");

                int index = base;
                base = (base + count) % buffer.length;
                unread -= count;

                if ((unread + localwindow.available()) < (buffer.length / 2)) {
                    adjustWindow(buffer.length - localwindow.available() - unread);
                }

            } finally {
                transfered += count;
            }
            return count;
        }

        public synchronized int read(byte[] buf, int offset, int len) throws IOException {

            try {

                if (available() == -1)
                    return -1;

                if (unread <= 0 && !isClosed()) {
                    processMessages(messagefilter);
                }

                int count = unread < len ? unread : len;

                if (count == 0 && isClosed())
                    return -1;

                int index = base;
                base = (base + count) % buffer.length;
                if (buffer.length - index > count) {
                    System.arraycopy(buffer, index, buf, offset, count);
                } else {
                    int remaining = buffer.length - index;
                    System.arraycopy(buffer, index, buf, offset, remaining);
                    System.arraycopy(buffer, 0, buf, offset + remaining, count - remaining);
                }

                unread -= count;

                if ((unread + localwindow.available()) < (buffer.length / 2)) {
                    adjustWindow(buffer.length - localwindow.available() - unread);
                }

                transfered += count;

                return count;
            } catch (EOFException ex) {
            	closed = true;
            	messageStore.close();
                return -1;
            }
        }

        void write(byte[] buf, int offset, int len) throws IOException {

            if (localwindow.available() < len) {
                connection.disconnect("Received data exceeding current window space");
                throw new IOException("Window space exceeded");
            }

            int i = 0;
            int index;
            int count;
            while (i < len) {
                // Copy data up to the end of the array and start back
                // at the beginning
                index = (base + unread) % buffer.length;
                count = ((buffer.length - index < len - i) ? buffer.length - index : len - i);
                System.arraycopy(buf, offset + i, buffer, index, count);
                unread += count;
                i += count;
            }

            localwindow.consume(len);

        }
    }

    class DataWindow {
        int windowsize;
        int packetsize;

        DataWindow(int windowsize, int packetsize) {
            this.windowsize = windowsize;
            this.packetsize = packetsize;
        }

        int getPacketSize() {
            return packetsize;
        }

        void adjust(int count) {
            windowsize += count;
        }

        void consume(int count) {
            windowsize -= count;
        }

        int available() {
            return windowsize;
        }
    }

	public boolean isAutoConsumeInput() {
		return autoConsumeInput;
	}

	public void setAutoConsumeInput(boolean autoConsumeInput) {
		this.autoConsumeInput = autoConsumeInput;
	}
	
//	public int getCompressionLevel() {
//		return compressionLevel;
//	}
//
//	public boolean isCompressionEnabled() {
//		return compressionEnabled;
//	}

//	ZStream getCompressionIn() {
//		return compressionIn;
//	}
//
//	ZStream getCompressionOut() {
//		return compressionOut;
//	}

}
