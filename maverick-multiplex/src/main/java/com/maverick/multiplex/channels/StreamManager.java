package com.maverick.multiplex.channels;

import java.util.Hashtable;

public class StreamManager {

    protected Hashtable channels;

    private Object initiatorWaitLock = new Object();

    public StreamManager() {
        super();
        channels = new Hashtable();
    }

    public StreamServerChannel getChannel(String id, boolean initiator) {
        return (StreamServerChannel) channels.get(getKey(id, initiator));
    }

    String getKey(String id, boolean initiator) {
        return id + (initiator ? "-initiator" : "-recipient");
    }

    public void putChannel(StreamServerChannel streamChannel) {
        synchronized (channels) {
            String key = getKey(streamChannel.getId(), streamChannel.isInitiator());
            if (channels.containsKey(key)) {
                throw new IllegalArgumentException("Channel already exists.");
            }
            channels.put(key, streamChannel);
            synchronized (initiatorWaitLock) {
                initiatorWaitLock.notifyAll();
            }
        }
    }

    public boolean containsChannel(String id, boolean initiator) {
        return channels.containsKey(getKey(id, initiator));
    }

    public void removeChannel(StreamServerChannel channel) {
        if (channels.remove(getKey(channel.getId(), channel.isInitiator())) == null) {
            throw new IllegalArgumentException("No such channel.");
        }
    }

    public void waitForInitiator(String id, int timeout) throws IllegalStateException, InterruptedException {
        long expire = timeout == -1 ? Long.MAX_VALUE : ( timeout + System.currentTimeMillis() );
        while (System.currentTimeMillis() < expire && !containsChannel(id, true)) {
            synchronized (initiatorWaitLock) {
                initiatorWaitLock.wait(1000);
            }
        }
        if(!containsChannel(id, true)) {
            throw new IllegalStateException("Timeout waiting for initiator.");
        }
    }

    public void interruptInitiatorWait(String id) {
        // TODO Auto-generated method stub
        
    }

}
