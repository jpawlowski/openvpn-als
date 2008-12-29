package com.adito.agent;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.MultiplexedConnection;

/**
 */
public interface AgentService {

	/**
	 * @param tunnel
	 */
	void initializeTunnel(AgentTunnel tunnel);
	
	/**
	 * @param tunnel
	 */
	void performStartup(AgentTunnel tunnel);
	
	/**
	 * @param connection
	 * @param type
	 * @return Channel
	 * @throws ChannelOpenException 
	 */
	Channel createChannel(MultiplexedConnection connection, String type) throws ChannelOpenException;
}