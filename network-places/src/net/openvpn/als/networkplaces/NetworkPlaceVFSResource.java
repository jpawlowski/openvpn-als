
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
			
package net.openvpn.als.networkplaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.security.PasswordCredentials;
import net.openvpn.als.vfs.FileObjectVFSResource;
import net.openvpn.als.vfs.VFSMount;
import net.openvpn.als.vfs.VFSRepository;
import net.openvpn.als.vfs.VFSResource;
import net.openvpn.als.vfs.webdav.DAVException;
import net.openvpn.als.vfs.webdav.DAVMultiStatus;
import net.openvpn.als.vfs.webdav.DAVUtilities;

/**
 * Extension of {@link FileObjectVFSResource} for use with resources 
 * controlled by a <i>Network Place</i>
 */
public class NetworkPlaceVFSResource extends FileObjectVFSResource {

	final static Log log = LogFactory.getLog(NetworkPlaceVFSResource.class);

	/**
	 * Constructor.
	 *
	 * @param launchSession
	 * @param mount
	 * @param parent
	 * @param relativePath
	 * @param repository
	 * @param requestCredentials
	 * @throws IOException
	 */
	public NetworkPlaceVFSResource(LaunchSession launchSession, VFSMount mount, VFSResource parent, String relativePath, VFSRepository repository, PasswordCredentials requestCredentials) throws IOException {
		super(launchSession, mount, parent, relativePath, repository, requestCredentials);
	}

    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.FileObjectVFSResource#delete()
     */
    public void delete() throws DAVMultiStatus, IOException {
        if (((AbstractNetworkPlaceMount) getMount()).getNetworkPlace().isNoDelete()) {
            throw new DAVException(500, "This resource cannot be deleted because the system policy does not allow deletion.");
        }
        super.delete();	    	
    }
	
    /* (non-Javadoc)
     * @see net.openvpn.als.vfs.FileObjectVFSResource#getChildren()
     */
    public Iterator<VFSResource> getChildren() throws IOException {
        if (!this.isCollection())
            return null;

        // this forces VFS to re cashe.
        this.getFile().close();

        FileObject children[] = this.getFile().getChildren();
        List<VFSResource> resources = new ArrayList<VFSResource>(children.length);

        for (int x = 0; x < children.length; x++) {
            String fileName = children[x].getName().getBaseName();
            if (fileName.startsWith(PREFIX) && fileName.endsWith(SUFFIX)) {
                continue;
            }
            if (fileName.equalsIgnoreCase(PAGEFILE_SYS)) {
                continue;
            }
            
            //  Test if a file is hidden, but do not fail if this test fails, just exclude the file
            try {
	            if (!((AbstractNetworkPlaceMount)getMount()).getNetworkPlace().isShowHidden() && children[x].isHidden()) {
	                continue;
	            }
            }
            catch(FileSystemException fse) {
            	log.warn("Could not determine if file " + children[x].getName() + " is hidden.");
                continue;            	
            }
            
            if (!isCollection() && !isResource()) {
                continue;
            }
            try {
                /*
                 * TODO BPS - I think we have a problem here.
                 * 
                 * When getting children that require further authentication we
                 * get a {@link DAVAuthenticationRequiredException} exception.
                 * We do not want to throw an exception at this point but we do
                 * want add the child. Its only when children of the child are
                 * accessed that we want to throw the exception. Because
                 * DAVMount.getResource() is the one that throws this, a
                 * resource object can never be created.
                 * 
                 * This will happen for example when listomg /fs/[store] and a
                 * network place that requires auth. is hit.
                 */

                VFSResource r = getMount().getResource(DAVUtilities.concatenatePaths(relativePath, fileName), requestCredentials/*, transaction*/);
                if (!((AbstractNetworkPlaceMount)getMount()).getNetworkPlace().isAllowRecursive() && r.isCollection())
                    continue;
                resources.add(r);
            } catch (Exception e) {
                /*
                 * NOTE - BPS - We cannot log this exception as it may have user
                 * information in the URI.
                 */
            }
        }
        return resources.iterator();
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.vfs.FileObjectVFSResource#getDisplayName()
	 */
	public String getDisplayName() {
		if (isMount()) {
			return ((AbstractNetworkPlaceMount)getMount()).getNetworkPlace().getResourceName() + "/";
		}
		return super.getDisplayName();
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.vfs.FileObjectVFSResource#isMount()
	 */
	public boolean isMount() {
		return getRelativePath().equals("");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Rel. URI = " + getRelativeURI() + ", Rel. Path = " + getRelativePath();
	}
}