package com.maverick.multiplex;

/**
 * @author lee
 *
 */
public interface ChannelFactory {
	Channel createChannel(MultiplexedConnection connection, String type) throws ChannelOpenException;
}
