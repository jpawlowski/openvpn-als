package net.openvpn.als.agent.client.tunneling;

import java.io.IOException;

import com.maverick.multiplex.MultiplexedSocketFactory;
import com.maverick.multiplex.channels.LocalForwardingChannel;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;

public class TunnelChannel extends LocalForwardingChannel {

	public static final String CHANNEL_TYPE = "tunnel-tcpip";
	
	private String launchId;
	
	public TunnelChannel(String hostname, int port, String launchId) {
		super(CHANNEL_TYPE, hostname, port);
		this.launchId = launchId;
	}
	
	public TunnelChannel() {
		super(CHANNEL_TYPE);
		launchId = "";
	}
	
	public byte[] create() throws IOException {
		ByteArrayWriter msg = new ByteArrayWriter();
		msg.writeString(hostname);
		msg.writeInt(port);
		msg.writeString(launchId);
		return msg.toByteArray();
	}

	public byte[] open(byte[] data) throws IOException {
		ByteArrayReader msg = new ByteArrayReader(data);
		this.hostname = msg.readString();
		this.port = (int) msg.readInt();
		this.launchId = msg.readString();
        this.socket = MultiplexedSocketFactory.getDefault().createSocket(hostname, port);
		return null;
	}
}