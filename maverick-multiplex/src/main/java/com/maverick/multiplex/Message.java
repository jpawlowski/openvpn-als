package com.maverick.multiplex;

import com.maverick.util.ByteArrayReader;


/**
 * 
 * 
 * @author lee
 */
public class Message extends ByteArrayReader {
	
	int messageid;
	Message next;
	Message previous;
	
	public Message() {
		super(new byte[] {} );
	}
	
	public Message(byte[] msg) {
		super(msg);
		messageid = read();
	}
	
	public int getMessageId() {
		return messageid;
	}

}
