/*
 * Copyright 2002-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs.provider.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.GenericFileName;

import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A wrapper to the FTPClient to allow automatic reconnect on connection loss.<br />
 * I decided to not to use eg. noop() to determine the state of the connection to avoid unnecesary server round-trips.
 * 
 * Modifications by 3SP <a href="http://3sp.com">3SP</a>
 * 
 * Added a <code>lastAccessed</code> attribute that is used
 * to determine if this client can be shutdown when it
 * hasn't been used for a bit
 *  
 */
class FTPClientWrapper implements FtpClient
{
    private final GenericFileName root;
    private final FileSystemOptions fileSystemOptions;

    private FTPClient ftpClient = null;
    private String baseDir = null;
    private long lastAccessed = -1;
    private long idleTimeout;

    private final static List wrappers = new ArrayList();
    
    /*
     * Monitor clients for idle timeout
     */
    static {
        Thread reaperThread = new Thread("FTPReaper") {
            public void run() {
                FTPClientWrapper wrapper;
                try {
                    while(true) {
                        for(Iterator i = new ArrayList(wrappers).iterator(); i.hasNext(); ) {
                            wrapper = (FTPClientWrapper)i.next();
                            if(wrapper.isConnected()) {
                                if(wrapper.idleTimeout != -1 &&
                                                System.currentTimeMillis() > ( wrapper.lastAccessed + wrapper.idleTimeout ) ) {
                                    wrapper.disconnect();
                                }
                            }
                            else {
                                wrappers.remove(wrapper);
                            }
                        }
                        Thread.sleep(10000);
                    }
                }
                catch(Exception e) {                    
                }
            }
        };
        reaperThread.setDaemon(true);
        reaperThread.start();
    }

    FTPClientWrapper(final GenericFileName root, final FileSystemOptions fileSystemOptions) throws FileSystemException
    {
        this.root = root;
        this.fileSystemOptions = fileSystemOptions;
        getFtpClient(); // fail-fast
    }

    public GenericFileName getRoot()
    {
        return root;
    }

    public FileSystemOptions getFileSystemOptions()
    {
        return fileSystemOptions;
    }

    private FTPClient createClient() throws FileSystemException
    {
        final GenericFileName rootName = getRoot();

        try {
        return FtpClientFactory.createConnection(rootName.getHostName(),
            rootName.getPort(),
            rootName.getUserName(),
            rootName.getPassword(),
            rootName.getPath(),
            getFileSystemOptions());
        }
        finally {
            updateLastAccessed();
        }
    }

    private FTPClient getFtpClient() throws FileSystemException
    {
        if (ftpClient == null)
        {
            ftpClient = createClient();
            synchronized(wrappers) 
            {
                Integer idleTimeout = FtpFileSystemConfigBuilder.getInstance().getIdleTimeout(fileSystemOptions);
                if(idleTimeout != null && idleTimeout.intValue() > 0) {
                    this.idleTimeout = idleTimeout.longValue();
                }
                wrappers.add(this);
            }
        }

        return ftpClient;
    }

    public boolean isConnected() throws FileSystemException
    {
        return getFtpClient().isConnected();
    }

    public void disconnect() throws IOException
    {
        try
        {
            getFtpClient().disconnect();
        }
        finally
        {
            ftpClient = null;
            lastAccessed = -1;
            wrappers.remove(this);
        }
    }

    public FTPFile[] listFiles(String key, String relPath) throws IOException
    {
        try
        {
            updateLastAccessed();
        	FTPClient client = getFtpClient();
        	String dir = client.printWorkingDirectory();
        	client.changeWorkingDirectory(relPath);
        	FTPFile[] list = client.listFiles(key,null);
        	client.changeWorkingDirectory(dir);
            return list;
        }
        catch (IOException e)
        {
            disconnect();
        	FTPClient client = getFtpClient();
        	String dir = client.printWorkingDirectory();
        	client.changeWorkingDirectory(relPath);
        	FTPFile[] list = client.listFiles(key,null);
        	client.changeWorkingDirectory(dir);
            return list;
        }
        finally 
        {
            updateLastAccessed();
        }
    }
    
    

    public boolean removeDirectory(String relPath) throws IOException
    {
        try 
        {
            updateLastAccessed();
            return getFtpClient().removeDirectory(relPath);
        }
        catch (IOException e)
        {
            disconnect();
            return getFtpClient().removeDirectory(relPath);
        }
    }

    public boolean deleteFile(String relPath) throws IOException
    {
        try
        {
            updateLastAccessed();
            return getFtpClient().deleteFile(relPath);
        }
        catch (IOException e)
        {
            disconnect();
            return getFtpClient().deleteFile(relPath);
        }
    }

    public boolean rename(String oldName, String newName) throws IOException
    {
        try
        {
            updateLastAccessed();
            return getFtpClient().rename(oldName, newName);
        }
        catch (IOException e)
        {
            disconnect();
            return getFtpClient().rename(oldName, newName);
        }
    }

    public boolean makeDirectory(String relPath) throws IOException
    {
        try
        {
            updateLastAccessed();
            return getFtpClient().makeDirectory(relPath);
        }
        catch (IOException e)
        {
            disconnect();
            return getFtpClient().makeDirectory(relPath);
        }
    }

    public boolean completePendingCommand() throws IOException
    {
        if (ftpClient != null)
        {
            updateLastAccessed();
            return getFtpClient().completePendingCommand();
        }

        return true;
    }

    public InputStream retrieveFileStream(String relPath) throws IOException
    {
        try
        {
            return new InFilter(getFtpClient().retrieveFileStream(relPath));
        }
        catch (IOException e)
        {
            disconnect();
            return new InFilter(getFtpClient().retrieveFileStream(relPath));
        }
    }

    public InputStream retrieveFileStream(String relPath, long restartOffset) throws IOException
    {
        try
        {
            FTPClient client = getFtpClient();
            client.setRestartOffset(restartOffset);
            return new InFilter(client.retrieveFileStream(relPath));
        }
        catch (IOException e)
        {
            disconnect();

            FTPClient client = getFtpClient();
            client.setRestartOffset(restartOffset);
            return new InFilter(client.retrieveFileStream(relPath));
        }
    }

    public OutputStream appendFileStream(String relPath) throws IOException
    {
        try
        {
            return new OutFilter(getFtpClient().appendFileStream(relPath));
        }
        catch (IOException e)
        {
            disconnect();
            return new OutFilter(getFtpClient().appendFileStream(relPath));
        }
    }

    public OutputStream storeFileStream(String relPath) throws IOException
    {
        try
        {
            return new OutFilter(getFtpClient().storeFileStream(relPath));
        }
        catch (IOException e)
        {
            disconnect();
            return new OutFilter(getFtpClient().storeFileStream(relPath));
        }
    }

    public boolean abort() throws IOException
    {
        try
        {
            // imario@apache.org: 2005-02-14
            // it should be better to really "abort" the transfer, but
            // currently I didnt manage to make it work - so lets "abort" the hard way.
            // return getFtpClient().abort();

            disconnect();
            return true;
        }
        catch (IOException e)
        {
            disconnect();
        }
        return true;
    }

    public String getReplyString() throws IOException
    {
        return getFtpClient().getReplyString();
    }
    
    public long getLastAccessed() {
        return lastAccessed;
    }
    
    void updateLastAccessed() {
        System.out.println("Something accessed client " + this);
        lastAccessed = System.currentTimeMillis();
        try {
            throw new Exception();
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    class InFilter extends FilterInputStream {
        protected InFilter(InputStream in) {
            super(in);
        }

        public int read() throws IOException {
            updateLastAccessed();
            return super.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            updateLastAccessed();
            try {
                return super.read(b, off, len);
            }
            finally {
                updateLastAccessed();
            }
        }
    }
    
    class OutFilter extends FilterOutputStream {

        public OutFilter(OutputStream out) {
            super(out);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            updateLastAccessed();
            try {
                super.write(b, off, len);
            }
            finally {
                updateLastAccessed();
            }
        }

        public void write(int b) throws IOException {
            updateLastAccessed();
            super.write(b);
        }
        
    }
}