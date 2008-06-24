package com.adito.vfs;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.vfs.FileObject;

import com.adito.policyframework.LaunchSession;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVMultiStatus;

public interface VFSResource extends Comparable{

    public static final String PREFIX = ".dav_";

    public static final String SUFFIX = ".temp";
    
    public static final String PAGEFILE_SYS = "pagefile.sys";

    public void verifyAccess() throws DAVAuthenticationRequiredException, Exception;
    
    public boolean isNull() throws IOException ;

    public boolean isCollection() throws IOException;

    public boolean isBrowsable() throws IOException;
    
    public boolean isResource() throws IOException;
    
    public boolean isMount();

    public VFSMount getMount();

    public String getDisplayName();
    
    public String getWebFolderPath();

    public String getBasename();

    /**
     * Get the path of this resource relative to its its <i>Mount</i>. This
     * should be an empty string if the resource is the root of the mount.
     * 
     * @return relative path
     */
    public String getRelativePath();

    public URI getRelativeURI();

    public VFSResource getParent();

    public Iterator<VFSResource> getChildren() throws IOException, DAVAuthenticationRequiredException;

    public String getContentType() throws IOException;

    public Long getContentLength() throws IOException;

    public Date getLastModified() throws IOException;

    public String getEntityTag() throws IOException;

    public void delete() throws DAVMultiStatus, IOException;

    public void copy(VFSResource dest, boolean overwrite, boolean recursive) throws DAVMultiStatus, IOException;

    public void move(VFSResource dest, boolean overwrite) throws DAVMultiStatus, IOException;

    public void makeCollection() throws IOException;

    public VFSInputStream getInputStream() throws IOException;

    public VFSOutputStream getOutputStream() throws IOException;

    public FileObject getFile() throws IOException;
    
    /**
     * Return the full URI. This will be relative to the root of the VFS, i.e.
     * <i>/[store]/[mount]/[path]</i>. The URI should encoded invalid
     * path characters.
     * 
     * @return full URI relative to the server root
     */
    public URI getFullURI() ;

    /**
     * Return the full URI. This will be relative to the root of the VFS, i.e.
     * <i>/[store]/[mount]/[path]</i> and invalid path characters will not
     * be encoded.
     * 
     * @return full path relative to the server root
     */
    public String getFullPath();
    
    public LaunchSession getLaunchSession();
}
