package com.maverick.multiplex.channels;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.util.ByteArrayWriter;

public class StreamClientChannel extends Channel  {
    final static Log log = LogFactory.getLog(StreamClientChannel.class);

    /**
     * Channel type identifier
     */
    public static final String CHANNEL_TYPE = "stream";

    String id;
    boolean initiator;

    /**
     * Constructor.
     * 
     * @param service service
     * 
     */
    public StreamClientChannel(String id, boolean initiator) {
        this(id, initiator, CHANNEL_TYPE);
    }

    public StreamClientChannel(String id, boolean initiator, String channelType) {
        super(channelType, 32768, 32768);
        this.id = id;
        this.initiator = initiator;
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
        ByteArrayWriter baw = new ByteArrayWriter();
        baw.writeBoolean(initiator);
        baw.writeString(id);
        return baw.toByteArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.maverick.multiplex.Channel#onChannelClose()
     */
    public void onChannelClose() {
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void onChannelOpen(byte[] data) {        
    }
}
