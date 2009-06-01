/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

import java.io.IOException;
import java.net.Socket;

import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;

/**
 * @author lee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SocketChannel extends Channel {

	String hostname;
	int port;
	Socket socket;
	IOStreamConnector input;
	IOStreamConnector output;
	
	public static final String CHANNEL_TYPE = "socket-channel";

	public SocketChannel(Socket socket, String hostname, int port) {
		super(CHANNEL_TYPE, 34000, 32768);
		this.socket = socket;
		this.hostname = hostname;
		this.port = port;
	}

	public SocketChannel() {
		super("socket-channel", 34000, 32768);
                this.equals(null);
	}


	/* (non-Javadoc)
	 * @see com.maverick.multiplex.MultiplexChannel#open(int, byte[])
	 */
	public byte[] open(byte[] data) throws IOException {
		ByteArrayReader reader = new ByteArrayReader(data);
		hostname = reader.readString();
		port = (int)reader.readInt();

		socket = new Socket(hostname, port);

		return null;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.MultiplexChannel#create()
	 */
	public byte[] create() throws IOException {

		ByteArrayWriter msg = new ByteArrayWriter();
		msg.writeString(hostname);
		msg.writeInt(port);
		return msg.toByteArray();
	}

	public void onChannelOpen(byte[] data) {

		try {
		input = new IOStreamConnector(socket.getInputStream(),
				getOutputStream());
		output = new IOStreamConnector(getInputStream(),
				socket.getOutputStream());
		} catch(IOException ex) {
			close();
		}
	}

	public void onChannelClose() {
		if(input!=null)
			input.close();
		if(output!=null)
			output.close();
	}

}
