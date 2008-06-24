package com.maverick.multiplex;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 */
public interface ChannelListener {

    public void onChannelOpen(Channel channel);

    public void onChannelData(Channel channel, byte[] buf, int off, int len);
    
    public void onChannelClose(Channel channel);

}
