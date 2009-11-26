package com.maverick.multiplex;

import java.io.IOException;

import com.maverick.util.ByteArrayWriter;

/**
 * This class is used for encapsulating messages sent by the Agent to the server.  
 *
 */
public class Packet extends ByteArrayWriter {

    public Packet() throws IOException {
        writeInt(0);
    }

    public void prepare() {
       encodeInt(buf, 0, size() - 4);
    }
    
    public void reset() {
    	super.reset();
    	try {
			writeInt(0);
		} catch (IOException e) {
		}
    }
}
