/*
 * Created on 08-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maverick.multiplex;

import com.maverick.util.ByteArrayReader;


/**
 * @author lee
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
