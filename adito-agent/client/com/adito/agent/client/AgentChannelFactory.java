package com.adito.agent.client;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelFactory;
import com.maverick.multiplex.MultiplexedConnection;
import com.adito.agent.client.tunneling.RemoteTunnelChannel;
import com.adito.agent.client.tunneling.RemoteTunnelChannelListener;

public class AgentChannelFactory implements ChannelFactory {
	
	private Agent agent;
		
	public AgentChannelFactory(Agent agent) {
		this.agent = agent;
	}

	public Channel createChannel(MultiplexedConnection connection, String type) {
		if(type.equals(RemoteTunnelChannel.CHANNEL_TYPE)) {
			RemoteTunnelChannel channel = new RemoteTunnelChannel(agent);
			channel.addListener(new RemoteTunnelChannelListener(agent));
			return channel;
		}
		return null;
	}
}
