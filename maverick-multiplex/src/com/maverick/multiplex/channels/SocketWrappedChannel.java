package com.maverick.multiplex.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.maverick.multiplex.Channel;

/**
 * Use this class in places where an application is passed a Socket. This
 * class simply wraps a forwarding channel in a Socket implementation.
 * @author lee
 *
 */
public class SocketWrappedChannel extends Socket {
	Channel channel;
	
	public SocketWrappedChannel(Channel channel) {
		this.channel = channel;
	}
	
	public InputStream getInputStream() {
		return channel.getInputStream();
	}
	
	public OutputStream getOutputStream() {
		return channel.getOutputStream();
	}
	
	public void close() throws IOException {
		channel.close();
	}
}