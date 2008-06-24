
				/*
 *  Adito
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
			
package com.adito.vfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.policyframework.LaunchSession;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVStatus;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * Abstract {@link VFSStore} implementation.
 */
public abstract class AbstractStore implements VFSStore {
    final static Log log = LogFactory.getLog(AbstractStore.class);

    //	Privaet instance variables
    
    private VFSRepository repository;
    private VFSProvider provider;
    private String name;
    private String charset;


    /**
     * Constructor.
     *
     * @param name name 
     * @param charset encoding
     */
    public AbstractStore(String name, String charset) {
        super();
        this.name = name;
        this.charset = charset;
    }

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSStore#getMountPath(java.lang.String)
	 */
	public String getMountPath(String mountName) {
		return getName() + "/" + mountName;
	}

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getEncoding()
     */
    public String getEncoding() {
        return charset;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getGuestUsername()
     */
    public String getGuestUsername() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getGuestPassword()
     */
    public char[] getGuestPassword() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#init(com.adito.vfs.VFSRepository, com.adito.vfs.VFSProvider)
     */
    public void init(VFSRepository repository, VFSProvider provider) {
        this.repository = repository;
        this.provider = provider;
    }
    
    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getProvider()
     */
    public VFSProvider getProvider() {
    	return provider;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getRepository()
     */
    public VFSRepository getRepository() {
        return repository;
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getStoreResource()
     */
    public VFSResource getStoreResource() throws DAVException {
        try {
            return new AbstractStoreResource(getName(), repository);
        } catch (URISyntaxException e) {
            throw new DAVException(DAVStatus.SC_INTERNAL_SERVER_ERROR, "Failed to create store resource.", e);
        }
    }

    class AbstractStoreResource extends AbstractVFSResource {

        AbstractStoreResource(String name, VFSRepository repository) throws URISyntaxException {
            super(new LaunchSession(getRepository().getSession()), new URI(name), true, name, repository.getRepositoryResource(), AbstractStore.this.repository);
        }

        public Iterator getChildren() throws IOException, DAVAuthenticationRequiredException {
            List<VFSResource> l = new ArrayList<VFSResource>();
            try {
                for (String mountName : getMountNames()) {
                    l.add(new MountVFSResource(mountName, this));
                }
            } catch (DAVAuthenticationRequiredException dare) {
            } catch (Exception e) {
                log.error("Failed to get store resources.", e);
            }
            return l.iterator();
        }

		public boolean isBrowsable() throws IOException {
			return false;
		}
        

    }
    
    class MountVFSResource extends AbstractVFSResource {

		MountVFSResource(String mountName, VFSResource parent) throws URISyntaxException {
			super(new LaunchSession(getRepository().getSession()), new URI(DAVUtilities.encodePath(mountName)), true, mountName, parent, AbstractStore.this.repository);
		}

		public boolean isBrowsable() throws IOException {
			return true;
		}

    	
    }
}