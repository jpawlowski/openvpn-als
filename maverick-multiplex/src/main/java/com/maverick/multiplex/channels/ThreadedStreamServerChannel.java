package com.maverick.multiplex.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.IOUtil;

/**
 * Channel implementation used for download files required by the <i>Agent</i>
 * to launch an <i>Application Shortcut</i>.
 * 
 * @author Lee David Painter <a href="mailto: lee@3sp.com">&lt;lee@3sp.com&gt;</a>
 */
public class ThreadedStreamServerChannel extends Channel implements Runnable, StreamServerChannel {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ThreadedStreamServerChannel.class);
    // #endif

    /**
     * Channel type identifier
     */
    public static final String CHANNEL_TYPE = "stream";
    
    /**
     * Default timeout when waiting for initiator
     */
    public static int DEFAULT_INITIATOR_WAIT_TIMEOUT = 20000;

    StreamManager streamManager;
    ThreadedStreamServerChannel joinedChannel;
    String id;
    boolean initiator;
    Object joinLock = new Object();
    boolean closed = false;
    int initiatorWaitTimeout;

    
    /**
     * Constructor.
     * 
     * @param service service
     */
    public ThreadedStreamServerChannel(StreamManager service) {
    	this(service, DEFAULT_INITIATOR_WAIT_TIMEOUT);
    }
    
    /**
     * Constructor.
     * 
     * @param service service
     * @param initiatorWaitTimeout
     */
    public ThreadedStreamServerChannel(StreamManager service, int initiatorWaitTimeout) {
        super(CHANNEL_TYPE, 32768, 32768);
        // #ifdef DEBUG
        log.debug("Creating channel");
        // #endif
        this.streamManager = service;
        this.initiatorWaitTimeout = initiatorWaitTimeout;
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
        // #ifdef DEBUG
        log.debug("Opening. Id is '" + id + "', initiator is '" + initiator + "'");
        // #endif

        if (initiator) {
            if (streamManager.containsChannel(id, true)) {
                throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED,
                                "Cannot create channel (as initiator) when channel with same ID already exists.");
            }
            streamManager.putChannel(this);
        } else {
            // #ifdef DEBUG
            log.debug("Waiting for initiator with ID '" + id + "'");
            // #endif
            try {
                streamManager.waitForInitiator(id, initiatorWaitTimeout);
            } catch (IllegalStateException e) {
                throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, "Timeout waiting for initiator");
            } catch (InterruptedException e) {
                throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, "Interrupted waiting for initiator");
            }
            // #ifdef DEBUG
            log.debug("Got initiator with ID '" + id + "'");
            // #endif
            streamManager.putChannel(this);
            ThreadedStreamServerChannel channelToJoin = (ThreadedStreamServerChannel) streamManager.getChannel(id, true);
            joinedChannel = channelToJoin;
            channelToJoin.join(this);
        }
        // #ifdef DEBUG
        log.debug("Opened channel '" + id + "'");
        // #endif
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
        if (initiator) {
            // #ifdef DEBUG
            log.debug("Stream '" + id + "' is initiator so joining streams");
            // #endif
            Thread t = new Thread(this, "Stream" + id + "-" + (initiator ? "Initiator" : "Recipient"));
            t.start();
        } else {
            // #ifdef DEBUG
            log.debug("Stream '" + id + "' is NOT initiator so doing nothing");
            // #endif
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#onChannelClose()
     */
    public synchronized void onChannelClose() {
        // #ifdef DEBUG
        log.debug("Stream channel " + getId() + " closed");
        closed = true;
        synchronized (joinLock) {
            joinLock.notifyAll();
        }
        // #endif
    }

    public void run() {
        try {
            waitForJoin();
            if (closed) {
                return;
            }

            Thread t = new Thread(Thread.currentThread().getName() + "-In-Out") {
                public void run() {
                    InputStream in = joinedChannel.getInputStream();
                    OutputStream out = getOutputStream();
                    try {
                        IOUtil.copy(in, out);
                    } catch (IOException e) {
                    } finally {
                        if (!ThreadedStreamServerChannel.this.isClosed()) {
                            ThreadedStreamServerChannel.this.close();
                        }
                        if (!joinedChannel.isClosed()) {
                            ThreadedStreamServerChannel.this.close();
                        }
                        // // #ifdef DEBUG
                        // log.debug("Closing input stream");
                        // // #endif
                        // IOUtil.closeStream(in);
                        // // #ifdef DEBUG
                        // log.debug("Closing out stream");
                        // // #endif
                        // IOUtil.closeStream(out);
                        // // #ifdef DEBUG
                        // log.debug("Closed streams");
                        // // #endif
                    }
                }
            };
            t.start();
            InputStream in = getInputStream();
            OutputStream out = joinedChannel.getOutputStream();
            IOUtil.copy(in, out);
        } catch (IOException e) {
            // #ifdef DEBUG
            log.error("Failed to join streams.", e);
            // #endif
        } finally {
            if (!ThreadedStreamServerChannel.this.isClosed()) {
                ThreadedStreamServerChannel.this.close();
            }
            if (joinedChannel != null && !joinedChannel.isClosed()) {
                ThreadedStreamServerChannel.this.close();
            }
            // // #ifdef DEBUG
            // log.debug("Closing input stream");
            // // #endif
            // IOUtil.closeStream(in);
            // // #ifdef DEBUG
            // log.debug("Closing out stream");
            // // #endif
            // IOUtil.closeStream(out);
            // // #ifdef DEBUG
            // log.debug("Closed streams");
            // // #endif
            if (streamManager.containsChannel(this.getId(), initiator)) {
                // #ifdef DEBUG
                log.info("Removing stream channel " + getId() + "/" + initiator);
                streamManager.removeChannel(this);
                // #endif
            }
        }
    }

    public synchronized void join(ThreadedStreamServerChannel joinedChannel) {
        if (initiator == joinedChannel.initiator) {
            throw new IllegalArgumentException("Cannot both be initiators");
        }

        // #ifdef DEBUG
        log.debug("Joining this channel ('" + id + "') to '" + joinedChannel.getId());
        // #endif
        if (this.joinedChannel != null) {
            throw new IllegalStateException("Already joined.");
        }
        this.joinedChannel = joinedChannel;
        synchronized (joinLock) {
            // #ifdef DEBUG
            log.debug("Notifying channel '" + id + "' joined");
            // #endif
            joinLock.notifyAll();
            // #ifdef DEBUG
            log.debug("Notified channel '" + id + "' joined");
            // #endif
        }
    }

    public void waitForJoin() {
        synchronized (joinLock) {
            // #ifdef DEBUG
            log.debug("Channel '" + id + "' waiting to be joined");
            // #endif
            while (joinedChannel == null && !closed) {
                try {
                    joinLock.wait(1000);
                } catch (InterruptedException e) {
                }
            }
            // #ifdef DEBUG
            if(joinedChannel == null) {
                log.warn("Channel '" + id + "' closed before joined.");
            }
            else {
                log.debug("Channel '" + id + "' now joined to " + joinedChannel.getId());
            }
            // #endif
        }

    }

    public boolean isInitiator() {
        return initiator;
    }
}
