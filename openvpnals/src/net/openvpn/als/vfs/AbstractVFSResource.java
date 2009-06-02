
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.vfs;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.vfs.FileObject;

import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.vfs.webdav.DAVAuthenticationRequiredException;
import net.openvpn.als.vfs.webdav.DAVMultiStatus;
import net.openvpn.als.vfs.webdav.DAVUtilities;
import net.openvpn.als.vfs.webdav.methods.GET;

/**
 * Abstract implementation of a {@link VFSResource}.
 */
public abstract class AbstractVFSResource implements VFSResource {
    
	//	Private instance variables
    private boolean collection;
    private String name;
    private VFSResource parent;
    private URI relativeUri;
    private LaunchSession launchSession;
    private VFSRepository repository;
    

    /**
     * Constructor.
     *
     * @param launchSession
     * @param relativeUri
     * @param collection
     * @param name
     * @param parent
     * @param repository
     */
    public AbstractVFSResource(LaunchSession launchSession, URI relativeUri, boolean collection, String name, VFSResource parent, VFSRepository repository) {
        super();
        this.launchSession = launchSession;
        this.collection = collection;     
        this.name = name;
        this.parent = parent;
        this.relativeUri = relativeUri;
        this.repository = repository;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getLaunchSession()
     */
    public LaunchSession getLaunchSession() {
    	return launchSession;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#isMount()
     */
    public boolean isMount() {
    	return true;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#verifyAccess()
     */
    public void verifyAccess() {
    		
    }
    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getChildren()
     */
    public Iterator<VFSResource> getChildren() throws IOException, DAVAuthenticationRequiredException {
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getWebFolderPath()
     */
    public String getWebFolderPath() {
    	return parent == null ? "/fs/" : ( parent.getWebFolderPath() + relativeUri.getPath() + "/" );
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.vfs.VFSResource#getFile()
	 */
	public FileObject getFile() throws IOException{
		return null;
	}

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object object) {
        return toString().compareTo(object.toString());
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#isCollection()
     */
    public boolean isCollection() {
        return collection;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#isResource()
     */
    public boolean isResource() {
        return !isCollection();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getMount()
     */
    public VFSMount getMount() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.betaversion.webdav.DAVResource#getDisplayName()
     */
    public String getDisplayName() {
        String name = this.name;
        if (isCollection()) return (name + "/");
        return name;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getRelativePath()
     */
    public String getRelativePath() {
        return getRelativeURI().getPath();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getRelativeURI()
     */
    public URI getRelativeURI() {
        return relativeUri;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getParent()
     */
    public VFSResource getParent() {
        try {
            if(parent == null) {
                String parentPath = DAVUtilities.stripLeadingSlash(DAVUtilities.getParentPath(getFullPath()));
                if(parentPath == null || parentPath.equals("/")) {
                    return null;
                }
                else {
                    return repository.getResource(getLaunchSession(), parentPath, null);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return parent;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getContentType()
     */
    public String getContentType() {
        if (this.isNull()) return null;
        if (this.isCollection()) return GET.COLLECTION_MIME_TYPE;
        String mime = CoreServlet.getServlet().getServletContext().getMimeType(this.getDisplayName());
        return mime == null ? "application/octet-stream" : mime;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getContentLength()
     */
    public Long getContentLength() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getLastModified()
     */
    public Date getLastModified() {
        return new Date();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getEntityTag()
     */
    public String getEntityTag() {
        if (this.isNull()) return null;
        String path = this.getRelativePath();
        return DAVUtilities.getETAG(path, this.getLastModified());
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#delete()
     */
    public void delete() throws DAVMultiStatus {
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#copy(net.openvpn.als.vfs.VFSResource, boolean, boolean)
     */
    public void copy(VFSResource dest, boolean overwrite, boolean recursive) throws DAVMultiStatus {
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#move(net.openvpn.als.vfs.VFSResource, boolean)
     */
    public void move(VFSResource dest, boolean overwrite) throws DAVMultiStatus {
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#makeCollection()
     */
    public void makeCollection() {
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getInputStream()
     */
    public VFSInputStream getInputStream() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getOutputStream()
     */
    public VFSOutputStream getOutputStream() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#isNull()
     */
    public boolean isNull() {
        return false;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getBasename()
     */
    public String getBasename() {
        return name;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getFullURI()
     */
    public URI getFullURI() {
        VFSMount mount = getMount(); 
        URI uri = URI.create( mount == null ? ( "/" + getRelativeURI() ) : ( "/" + DAVUtilities.encodePath(mount.getMountString(), true) + "/" + DAVUtilities.encodePath(getRelativePath(), true) ) );
        return uri;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Rel. URI = " + getRelativeURI() + ", Rel. Path = " + getRelativePath() + ", Name = " + name + " Mount = " + getMount().getMountString();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.VFSResource#getFullPath()
     */
    public String getFullPath() {
        VFSMount mount = getMount(); 
        return mount == null ? ( "/" + getRelativeURI() ) : ( "/" + mount.getMountString() + "/" + getRelativePath());
    }
}
