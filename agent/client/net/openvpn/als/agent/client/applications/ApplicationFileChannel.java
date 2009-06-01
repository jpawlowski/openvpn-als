package net.openvpn.als.agent.client.applications;

import java.io.IOException;

import com.maverick.multiplex.Channel;
import com.maverick.util.ByteArrayWriter;

public class ApplicationFileChannel extends Channel {

	
	public static final String CHANNEL_TYPE = "applicationFile";
	
	String name;
	String ticket;
	String filename;
	
	
	public ApplicationFileChannel(String name, String ticket, String filename) {
		super(CHANNEL_TYPE, 32768, 35000);
		this.name = name;
		this.ticket = ticket;
		this.filename = filename;
	}
	
	public byte[] create() throws IOException {
	
		try {
			ByteArrayWriter msg = new ByteArrayWriter();
			msg.writeString(name);
			msg.writeString(ticket);
			msg.writeString(filename.replace('\\', '/'));
			
			return msg.toByteArray();
		} catch (IOException e) {
			throw new IOException("Failed to create application descriptor channel open request message");
		}

	}

	public void onChannelClose() {
		// TODO Auto-generated method stub

	}

	public void onChannelOpen(byte[] data) {
		// TODO Auto-generated method stub

	}

	public byte[] open(byte[] data) throws IOException {
		throw new IOException("Application file channel cannot be opened on the client");
	}

}