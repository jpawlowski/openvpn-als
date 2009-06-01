/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

/**
 * @author lee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ChannelFactory {
	Channel createChannel(MultiplexedConnection connection, String type) throws ChannelOpenException;
}
