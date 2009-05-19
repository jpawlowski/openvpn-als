package com.adito.agent.client.tunneling;

import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.IOStreamConnector;
import com.maverick.util.ByteArrayReader;
import com.adito.agent.client.Agent;
import com.adito.agent.client.AgentClientGUI;
import com.adito.agent.client.util.TunnelConfiguration;

public class RemoteTunnelChannel extends Channel {

	public static final String CHANNEL_TYPE = "remote-tunnel";

	private Socket socket = null;
	private IOStreamConnector input;
	private IOStreamConnector output;
	private TunnelConfiguration configuration;
	private long lastData;
	private Agent agent;

	public RemoteTunnelChannel(Agent agent) {
		super(CHANNEL_TYPE, 32768, 35000);
		this.agent = agent;
	}

	public TunnelConfiguration getConfiguration() {
		return configuration;
	}

	public byte[] create() throws IOException {
		return null;
	}

    public void onChannelData(byte[] buf, int off, int len) {
    	super.onChannelData(buf, off, len);
    	lastData = System.currentTimeMillis();
    };

	public void onChannelClose() {
		if (input != null)
			input.close();
		if (output != null)
			output.close();
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
		}
	}

	public void onChannelOpen(byte[] data) {
		lastData = System.currentTimeMillis();
		if (socket != null) {
			try {
				input = new IOStreamConnector(socket.getInputStream(), getOutputStream());
				output = new IOStreamConnector(getInputStream(), socket.getOutputStream());
			} catch (IOException ex) {
				close();
			}
		}
	}

	public byte[] open(byte[] data) throws IOException, ChannelOpenException {
		ByteArrayReader reply = new ByteArrayReader(data);

		String launchId = reply.readString();
		int id = (int) reply.readInt();
		String name = reply.readString();
		int type = (int) reply.readInt();
		String transport = reply.readString();
		String sourceInterface = reply.readString();
		int sourcePort = (int) reply.readInt();
		int destinationPort = (int) reply.readInt();
		String destinationHost = reply.readString();
		

		if(agent.getConfiguration().isRemoteTunnelsRequireConfirmation()) {
			if(!agent.getGUI().confirm(AgentClientGUI.WARNING, Messages.getString("RemoteForwardingChannelListener.confirmRemoteTunnel"),  //$NON-NLS-1$$ 
				Messages.getString("RemoteForwardingChannelListener.cancelRemoteTunnel"),    //$NON-NLS-1$$
				Messages.getString("RemoteForwardingChannelListener.incoming.title"),   //$NON-NLS-1$$ 
				MessageFormat.format(Messages.getString("RemoteForwardingChannelListener.incoming.text"), new Object[] { destinationHost + ":" + destinationPort } ))) {  //$NON-NLS-1$$
				throw new ChannelOpenException(ChannelOpenException.CHANNEL_REFUSED, "Rejected by user.");
			}
		}		

		configuration = new DefaultTunnel(id,
						type,
						transport,
						sourceInterface,
						sourcePort,
						destinationPort,
						destinationHost,
						true,
						false,
						name,
						launchId);

        try {
            this.socket = new Socket(destinationHost, destinationPort);
        } catch ( IOException ioe) {
            throw new ChannelOpenException(ChannelOpenException.CONNECT_FAILED, "Failed to open socket.");
        }
		return null;
	}

	public long getDataLastTransferredTime() {
		return lastData;
	}
}
