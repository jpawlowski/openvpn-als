package com.adito.tunnels.agent;

import java.io.IOException;

import com.maverick.multiplex.ChannelOpenException;
import com.maverick.util.ByteArrayReader;
import com.adito.boot.CustomSocketFactory;

/**
 * Channel implementation used for <i>Local SSL Tunnels</i>. The channel 
 * makes a socket connection to the provided hostname and port and joins
 * the channel streams to the socket's streams. 
 */
public class LocalForwardingChannel extends com.maverick.multiplex.channels.LocalForwardingChannel {
	
	/**
	 * Constructor.
	 *
	 */
	public LocalForwardingChannel() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.Channel#open(byte[])
	 */
	public byte[] open(byte[] data) throws IOException , ChannelOpenException{
		ByteArrayReader msg = new ByteArrayReader(data);
		this.hostname = msg.readString();
		this.port = (int) msg.readInt();
        try {
            this.socket = CustomSocketFactory.getDefault().createSocket(hostname, port);
        } catch (IOException ioe) {
            throw new ChannelOpenException(ChannelOpenException.CONNECT_FAILED, ioe.getMessage());
        }
        return null;
	}

}
