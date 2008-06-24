/* ========================================================================== *
 * Copyright (C) 2004-2005 Pier Fumagalli <http://www.betaversion.org/~pier/> *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package com.adito.vfs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVListener;


/**
 * <p>A specialized {@link OutputStream} to write to {@link VFSResource}s.</p>
 * 
 * <p>When writing to this {@link OutputStream} the data will be written to
 * a temporary file. This temporary file will be moved to its final destination
 * (the original file identifying the resource) when the {@link #close()}
 * method is called.</p>
 *
 * <p>This specialized {@link OutputStream} never throws {@link IOException}s,
 * but rather relies on the unchecked {@link DAVException} to notify the
 * framework of the correct DAV errors.</p>
 *
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class VFSOutputStream extends OutputStream {

    final static Log log = LogFactory.getLog(VFSOutputStream.class);
    
    /** <p>The index of the temporary file.</p> */
    private static int tmpno = (int)( Math.random() * 100000 );
    /** <p>The original resource {@link File}.</p> */
    private FileObject temporary = null;
    /** <p>The {@link OutputStream} of the temporary {@link File}. </p> */
    private OutputStream output = null;
    /** <p>The {@link VFSResource} associated with this instance. </p> */
    private VFSResource resource = null;

    /**
     * <p>Create a new {@link VFSOutputStream} instance.</p>
     */
    protected VFSOutputStream(VFSResource resource) {
        if (resource == null) throw new NullPointerException();
        this.resource = resource;

        try {
            if(resource instanceof FileObjectVFSResource) {
            	// LDP - changed to get the parent using the FileObject rather than
            	// the DAVResource beause we may have a null parent if the resource
            	// is located at the root of a mount.
	            this.temporary = resource.getFile().getParent();
	            this.temporary = this.temporary.resolveFile(
	                            VFSResource.PREFIX + ( tmpno++) + VFSResource.SUFFIX);
	            this.output = this.temporary.getContent().getOutputStream();
            }
            else {
                throw new IOException("DAV resource is not a true file.");
            }
        } catch (IOException e) {
            String message = VfsUtils.maskSensitiveArguments("Unable to create temporary file. " + e.getMessage());
            throw new DAVException(507, message, resource);
        }
    }

    /**
     * <p>Rename the temporary {@link File} to the original one.</p>
     */
    protected void rename(FileObject temporary, FileObject original)
    throws IOException {
        if ((original.exists()) && (!original.delete())) {
            throw new IOException("Unable to delete original file");
        }
        temporary.moveTo(original);
    }

    /**
     * <p>Abort any data written to the temporary file and delete it.</p>
     */
    public void abort() {
        try {
            if (this.temporary.exists()) 
            	this.temporary.delete();
        } catch (FileSystemException e) {
            log.error(e);            
        }
        if (this.output != null) try {
            this.output.close();
        } catch (IOException exception) {
            // Swallow the IOException on close
        } finally {
            this.output = null;
        }
    }

    /**
     * <p>Close this {@link OutputStream} {@link #rename(File,File) renaming}
     * the temporary file to the {@link VFSResource#getFile() original} one.</p>
     */
    public void close() {
        if (this.output == null) return;
        try {
            /* What kind of event should this invocation trigger? */
            int event = ((FileObjectVFSResource)this.resource).getFile().exists() ?
                        DAVListener.RESOURCE_MODIFIED:
                        DAVListener.RESOURCE_CREATED;

            /* Make sure that everything is closed and named properly */
            this.output.close();
            this.output = null;
            this.rename(this.temporary, ((FileObjectVFSResource)this.resource).getFile());

            /* Send notifications to all listeners of the repository */
            this.resource.getMount().getStore().getRepository().notify(this.resource, event);

        } catch (IOException e) {
            String message = "Error processing temporary file";
            throw new DAVException(507, message, e, this.resource);
        } finally {
            this.abort();
        }
    }

    /**
     * <p>Flush any unwritten data to the disk.</p>
     */
    public void flush() {
        if (this.output == null) throw new IllegalStateException("Closed");
        try {
            this.output.flush();
        } catch (IOException e) {
            this.abort();
            String message = "Unable to flush buffers";
            throw new DAVException(507, message, e, this.resource);
        }
    }

    /**
     * <p>Write data to this {@link OutputStream}.</p>
     */
    public void write(int b) {
        if (this.output == null) throw new IllegalStateException("Closed");
        try {
            this.output.write(b);
        } catch (IOException e) {
            this.abort();
            String message = "Unable to write data";
            throw new DAVException(507, message, e, this.resource);
        }
    }
    
    /**
     * <p>Write data to this {@link OutputStream}.</p>
     */
    public void write(byte b[]) {
        if (this.output == null) throw new IllegalStateException("Closed");
        try {
            this.output.write(b);
        } catch (IOException e) {
            this.abort();
            String message = "Unable to write data";
            throw new DAVException(507, message, e, this.resource);
        }
    }
    
    /**
     * <p>Write data to this {@link OutputStream}.</p>
     */
    public void write(byte b[], int o, int l) {
        if (this.output == null) throw new IllegalStateException("Closed");
        try {
            this.output.write(b, o, l);
        } catch (IOException e) {
            this.abort();
            String message = "Unable to write data";
            throw new DAVException(507, message, e, this.resource);
        }
    }
    
    /**
     * <p>Finalize this {@link VFSOutputStream} instance.</p>
     */
    public void finalize() {
        this.abort();
    }
}
