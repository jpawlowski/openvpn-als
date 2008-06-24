package com.maverick.multiplex;

public class ChannelOpenException extends Exception {

	int reason;
	
	public static final int COMMUNICATION_TIMEOUT = 1;
	public static final int CHANNEL_REFUSED = 2;
	public static final int CHANNEL_LIMIT_EXCEEDED = 3;
    public static final int CONNECT_FAILED = 4;	
	
	public ChannelOpenException(int reason, String msg) {
		super(msg);
		this.reason = reason;
	}
	
	public int getReason() {
		return reason;
	}
}
