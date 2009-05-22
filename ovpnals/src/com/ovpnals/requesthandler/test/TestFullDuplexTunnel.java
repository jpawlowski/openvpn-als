package com.ovpnals.requesthandler.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.RequestHandlerTunnel;
import com.ovpnals.boot.Util;

public class TestFullDuplexTunnel implements RequestHandlerTunnel {

	
    private InputStream _in;
    private OutputStream _out;
    private Thread thread;
    
    static Log log = LogFactory.getLog(TestFullDuplexTunnel.class);
    
    public TestFullDuplexTunnel() {
    	
    }
    
    
	public void close() {
		

	}

	/* ------------------------------------------------------------ */
    /** handle method.
     * This method is called by the HttpConnection.handleNext() method if
     * this HttpTunnel has been set on that connection.
     * The default implementation of this method copies between the HTTP
     * socket and the socket passed in the constructor.
     * @param in
     * @param out
     */
    public void tunnel(InputStream in, OutputStream out)
    {
    	
    	log.info("Starting new full duplex test tunnel");
    	
        Copy copy= new Copy();

        try
        {
            thread= Thread.currentThread();
            _in = in;
            _out = out;
            copy.start();
            copydata(new RandomInputStream(), out);
        }
        catch (Exception e)
        {
        	log.error("Error from full duplex test thread", e);
        }
        finally
        {
           	Util.closeStream(_out);
           	Util.closeStream(_in);
           	copy.interrupt();
           	
           	log.info("Full duplex test therad is exiting");
        }
    }

    /* ------------------------------------------------------------ */
    private void copydata(InputStream in, OutputStream out) throws java.io.IOException
    {
        long timestamp= 0;
        while (true)
        {
            try
            {
                Util.copy(in, out);
                timestamp= 0;
                return;
            }
            catch (InterruptedIOException e)
            {

            }
        }
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /** Copy thread.
     * Helper thread to copy from the HTTP input to the sockets output
     */
    private class Copy extends Thread
    {
        public void run()
        {
            try
            {
                copydata(_in, new RandomOutputStream());
            }
            catch (Exception e)
            {
//            	log.error("Error from full duplex test thread (2nd Thread)", e);
            }
            finally
            {
                try
                {
                   	Util.closeStream(_out);
                   	Util.closeStream(_in);
                   	thread.interrupt();
                }
                catch (Exception e)
                {
                }
            }
            
            log.info("Exiting run method of Copy thread");
        }
    }

	class RandomInputStream extends InputStream {
		
		Random rnd = new Random();
		
		public int read() {
			return rnd.nextInt(255);
		}
		
		public int read(byte[] buf, int off, int len) throws IOException {
			
			rnd.nextBytes(buf);
			
			return len;
		}
	}
	
	
	class RandomOutputStream extends OutputStream {

		Random rnd = new Random();
		
		public void write(int b) {
			rnd.nextInt();
		}
		
		public void write(byte[] buf, int off, int len) {
			
			rnd.nextBytes(buf);
		}
	}
}
