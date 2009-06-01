package com.maverick.multiplex.channels;

import java.io.IOException;
import java.net.Socket;

import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.IOStreamConnector;
import com.maverick.multiplex.MultiplexedSocketFactory;

public class LocalForwardingChannel extends Channel {

	public static final String CHANNEL_TYPE = "direct-tcpip";
	
	// Protected instance variables
	
	protected String hostname = null;
	protected int port;	
	protected Socket socket = null;
	
	//
	IOStreamConnector input;
	IOStreamConnector output;
	
	public LocalForwardingChannel(String channelType, String hostname, int port) {
		super(channelType, 32768, 35000);
		this.hostname = hostname;
		this.port = port;
	}
	
	public LocalForwardingChannel(String hostname, int port) {
		super(CHANNEL_TYPE, 32768, 35000);
		this.hostname = hostname;
		this.port = port;
	}
	
	public LocalForwardingChannel(String channelType) {
		super(channelType, 32768, 35000);
	}
	
	public LocalForwardingChannel() {
		this(CHANNEL_TYPE);
	}
	
	public byte[] create() throws IOException {
		ByteArrayWriter msg = new ByteArrayWriter();
		msg.writeString(hostname);
		msg.writeInt(port);
		
		return msg.toByteArray();
	}

	public void onChannelClose() {
		if(input!=null)
			input.close();
		if(output!=null)
			output.close();
		try {
			if(socket!=null)
				socket.close();
		} catch (IOException e) {
		}
	}

	public void onChannelOpen(byte[] data) {
		
		if(socket!=null) {
			try {
				input = new IOStreamConnector(socket.getInputStream(),
						getOutputStream());
				output = new IOStreamConnector(getInputStream(),
						socket.getOutputStream());
			} catch(IOException ex) {
				close();
			}
	    }
	}

	public byte[] open(byte[] data) throws IOException, ChannelOpenException {
		ByteArrayReader msg = new ByteArrayReader(data);
		this.hostname = msg.readString();
		this.port = (int) msg.readInt();
        this.socket = MultiplexedSocketFactory.getDefault().createSocket(hostname, port);
		return null;
	}
}