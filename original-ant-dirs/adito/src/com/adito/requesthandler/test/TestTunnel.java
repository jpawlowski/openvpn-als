package com.adito.requesthandler.test;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.RequestHandlerTunnel;

public class TestTunnel implements RequestHandlerTunnel {

    public TestTunnel() {
        super();
        // TODO Auto-generated constructor stub
    }


    private static Log log= LogFactory.getLog(TestTunnel.class);

    private Thread _thread;
    private int _timeoutMs;
    private InputStream _in;
    private OutputStream _out;


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

        Copy copy= new Copy();
        _in= in;
        _out= out;
        try
        {
            _thread= Thread.currentThread();
            copy.start();

            copydata(null, _out);
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                _in.close();

            }
            catch (Exception e)
            {
                if (log.isDebugEnabled())
                    log.debug("Failed to close tunnel.", e);
            }
            copy.interrupt();

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
                byte[] buf = new byte[32768];
                int read;
                
                if(in!=null) {
                    while((read = in.read(buf)) > -1) {
                        // Do nothing?!?!?!
                    }
                }
                else if(out!=null) {
                    Random rnd = new Random();
                    while(true) {
                        rnd.nextBytes(buf);
                        out.write(buf);
                    }
                }
                timestamp= 0;
                return;
            }
            catch (InterruptedIOException e)
            {
                if (timestamp == 0)
                    timestamp= System.currentTimeMillis();
                else if (_timeoutMs > 0 && (System.currentTimeMillis() - timestamp) > _timeoutMs)
                    throw e;
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
                copydata(_in, null);
            }
            catch (Exception e)
            {
                if (log.isDebugEnabled())
                    log.debug("Failed to copy data." , e);
            }
            finally
            {
                try
                {
                    _out.close();
                }
                catch (Exception e)
                {
                }
                _thread.interrupt();
            }
        }
    }

    public void close() {

    }
}
