package com.adito.agent;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.MultiplexedConnection;

/** This interface is implemented by all classes providing services
  * that use the Agent, for example TunnelingService and
  * NetworkPlaceService.
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
