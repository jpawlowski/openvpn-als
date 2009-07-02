package com.adito.agent;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelFactory;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.MultiplexedConnection;

public class DefaultAgentChannelFactory implements ChannelFactory {

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.ChannelFactory#createChannel(java.lang.String)
	 */
	public Channel createChannel(MultiplexedConnection connection, String type) throws ChannelOpenException {
		Channel channel = null;
		for(AgentService service : DefaultAgentManager.getInstance().getServices()) {
			channel = service.createChannel(connection, type);
			if(channel!=null)
				break;
		}
		return channel;
	}
}
