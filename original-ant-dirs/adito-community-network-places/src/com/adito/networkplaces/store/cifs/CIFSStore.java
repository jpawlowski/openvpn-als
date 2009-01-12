
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
			
package com.adito.networkplaces.store.cifs;

import jcifs.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.SystemProperties;
import com.adito.networkplaces.AbstractNetworkPlaceMount;
import com.adito.networkplaces.AbstractNetworkPlaceStore;
import com.adito.policyframework.LaunchSession;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.vfs.utils.URI;
import com.adito.vfs.utils.URI.MalformedURIException;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * CIFS implementation of a {@link AbstractNetworkPlaceStore},handles URIs with
 * schemes of <i>smb</i> or <i>cifs</i>.
 */
public class CIFSStore extends AbstractNetworkPlaceStore {
    final static Log log = LogFactory.getLog(CIFSStore.class);
	/**
	 * CIFS scheme name
	 */
	public final static String CIFS_SCHEME = "cifs";
	/**
	 * SMB scheme name
	 */
	public final static String SMB_SCHEME = "smb";

    /**
     * Constructor.
     */
    public CIFSStore() {
        super("cifs", SystemProperties.get("jcifs.encoding", "cp860"));
        try {
            setIfNotEmpty("jcifs.netbios.wins");
            setIfNotEmpty("jcifs.netbios.baddr");
            setIfNotEmpty("jcifs.netbios.scope");
            setIfNotEmpty("jcifs.smb.client.laddr");
            setIfNotEmpty("jcifs.netbios.laddr");
            setIfNotEmpty("jcifs.netbios.lmhosts");
            setIfNotEmpty("jcifs.smb.client.disablePlainTextPasswords");
            setIfNotEmpty("jcifs.netbios.hostname");
            setIfNotEmpty("jcifs.netbios.soTimeout");
            setIfNotEmpty("jcifs.netbios.retryCount");
            setIfNotEmpty("jcifs.netbios.retryTimeout");
            setIfNotEmpty("jcifs.resolveOrder");
            setIfNotEmpty("jcifs.smb.client.responseTimeout");
            setIfNotEmpty("jcifs.smb.client.soTimeout");

            String guestUser = Property.getProperty(new SystemConfigKey("cifs.guestUser")).replace('\\', ';').replace('/', ';');
            String guestPassword = Property.getProperty(new SystemConfigKey("cifs.guestPassword"));
            if(!guestUser.equals("")) {
                Config.setProperty("cifs.guestUser", guestUser);                
            }
            if(!guestPassword.equals("")) {
                Config.setProperty("cifs.guestUser", guestPassword);                
            }
        } catch (Exception e) {
            log.error("Failed to configure JCIFS. CIFS browsing may not act as expected.", e);
        }
    }

    /**
     * @param name
     */
    static void setIfNotEmpty(String name) {
        String val =Property.getProperty(new SystemConfigKey(name)).trim();
        if (!val.equals("")) {
            Config.setProperty(name, val);
        }

    }

    /* (non-Javadoc)
     * @see com.adito.vfs.AbstractNetworkPlaceStore#createMount(com.adito.policyframework.LaunchSession)
     */
    protected AbstractNetworkPlaceMount createMount(LaunchSession launchSession) throws Exception {
        return new CIFSMount(launchSession, this);
    }
    
    /* (non-Javadoc)
     * @see com.adito.vfs.webdav.AbstractNetworkPlaceStore#getGuestUsername(com.adito.vfs.webdav.DAVTransaction)
     */
    public String getGuestUsername() {
        return Property.getProperty(new SystemConfigKey("cifs.guestUser"));
    }
    
    /* (non-Javadoc)
     * @see com.adito.vfs.webdav.AbstractNetworkPlaceStore#getGuestPassword(com.adito.vfs.webdav.DAVTransaction)
     */
    public char[] getGuestPassword() {
        return Property.getProperty(new SystemConfigKey("cifs.guestPassword")).toCharArray();
    }
}