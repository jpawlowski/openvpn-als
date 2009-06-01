package com.maverick.multiplex;

import java.io.IOException;

import com.maverick.util.ByteArrayWriter;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
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
