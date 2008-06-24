package com.maverick.multiplex.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelListener;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.IOUtil;

/**
NOT YET USED - CONSIDER REMOVING ONCE CONVERSE IS RELEASED
 */
public class EventStreamServerChannel extends Channel implements Runnable, StreamServerChannel {
    final static Log log = LogFactory.getLog(EventStreamServerChannel.class);

    /**
     * Channel type identifier
     */
    public static final String CHANNEL_TYPE = "stream";

    StreamManager streamManager;
    EventStreamServerChannel joinedChannel;
    String id;
    boolean initiator;
    Object joinLock = new Object();

    /**
     * Constructor.
     * 
     * @param service service
     * 
     */
    public EventStreamServerChannel(StreamManager service) {
        super(CHANNEL_TYPE, 32768, 32768);
        System.out.println("Creating receiver channel");
        this.streamManager = service;
    }

    /**
     * Get the stream ID.
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#open(byte[])
     */
    public byte[] open(byte[] data) throws IOException, ChannelOpenException {

        ByteArrayReader reader = new ByteArrayReader(data);
        initiator = reader.readBoolean();
        id = reader.readString();

        if (initiator) {
            System.out.println("Opening intiator channel");
            if (streamManager.containsChannel(id, true)) {
                throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED,
                                "Cannot create channel (as initiator) when channel with same ID already exists.");
            }
            streamManager.putChannel(this);
            System.out.println("Opened intiator channel");
        } else {
            System.out.println("Recipient channel");
            streamManager.putChannel(this);            
            EventStreamServerChannel channelToJoin = (EventStreamServerChannel)streamManager.getChannel(id, true);
            joinedChannel = channelToJoin;
            channelToJoin.join(this);
            System.out.println("Opened recipient channel");
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#onChannelData(byte[], int, int)
     */
    public void onChannelData(byte[] buf, int off, int len) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#create()
     */
    public byte[] create() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#onChannelOpen(byte[])
     */
    public void onChannelOpen(byte[] data) {
        if(initiator) {
            System.out.println("Initiator so joining streams");
            Thread t = new Thread(this, "Stream" + id + "-" + (initiator ? "Initiator" : "Recipient"));
            t.start();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#onChannelClose()
     */
    public void onChannelClose() {
        streamManager.removeChannel(this);
    }

    public void run() {
        waitForJoin();
        
        Thread t = new Thread(Thread.currentThread().getName() + "-In-Out") {
            public void run() {
                InputStream in = joinedChannel.getInputStream();
                OutputStream out = getOutputStream();
                try {
                    IOUtil.copy(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    IOUtil.closeStream(in);
                    IOUtil.closeStream(out);
                }
            }
        };
        t.start();   
        InputStream in = getInputStream();
        OutputStream out = joinedChannel.getOutputStream();
        try {
            IOUtil.copy(in, out);
        } catch (IOException e) {
            log.error("Failed to join streams.", e);
        }
        finally {
            IOUtil.closeStream(in);
            IOUtil.closeStream(out);
        }
    }

    public synchronized void join(EventStreamServerChannel joinedChannel) {
        System.out.println("Joining recipients " + joinedChannel.getId() + " to this channel (" + getId() + ")");
        if (this.joinedChannel != null) {
            throw new IllegalStateException("Already joined.");
        }
        this.joinedChannel = joinedChannel;
        synchronized (joinLock) {
            joinLock.notifyAll();
        }
    }

    public void waitForJoin() {
        synchronized (joinLock) {
            System.out.println("Waiting for recipient to join");
            while (joinedChannel == null) {
                try {
                    joinLock.wait(1000);
                } catch (InterruptedException e) {
                }
            }
            System.out.println("Recipient joined");
        }

    }

    public boolean isInitiator() {
        return initiator;
    }
    
    
    class BridgeListener implements ChannelListener {
        
        EventStreamServerChannel bridgedChannel;
        
        BridgeListener(EventStreamServerChannel channel) {
            this.bridgedChannel = bridgedChannel;
        }
        
        public void onChannelClose(Channel channel) {                
        }

        public void onChannelData(Channel channel, byte[] buf, int off, int len) {
            
        }

        public void onChannelOpen(Channel channel) {                
        }            
    }
}
