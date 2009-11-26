package com.adito.agent.client;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelFactory;
import com.maverick.multiplex.MultiplexedConnection;
import com.adito.agent.client.tunneling.RemoteTunnelChannel;
import com.adito.agent.client.tunneling.RemoteTunnelChannelListener;

/** This class is currently used when creating remote tunnels, but it is extendable
  * to other types of uses.
  */
public class AgentChannelFactory implements ChannelFactory {
	
	private Agent agent;
		
    /** Create a new channel */
	public AgentChannelFactory(Agent agent) {
		this.agent = agent;
	}

    /** Create a new RemoteTunnelChannel and add a listener to it. As the name
      * implies, this is for remote tunnels only.
      *
      * @param  connection  the Agent<->Server multiplexed connection to bind this channel to
      * @param  type    type of channel - only "remote-tunnel" is supported currently
      * @return 
      */
	public Channel createChannel(MultiplexedConnection connection, String type) {
		if(type.equals(RemoteTunnelChannel.CHANNEL_TYPE)) {
			RemoteTunnelChannel channel = new RemoteTunnelChannel(agent);
			channel.addListener(new RemoteTunnelChannelListener(agent));
			return channel;
		}
		return null;
	}
}
