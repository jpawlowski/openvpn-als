
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
			
package com.adito.networkplaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.Util;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.OwnedResource;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabase;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.ResourceUtil;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.WebDAVAuthenticationModule;
import com.adito.vfs.AbstractStore;
import com.adito.vfs.VFSMount;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSResource;
import com.adito.vfs.VFSStore;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVStatus;

/**
 * An abstract implementation of a {@link VFSStore} that provides mounts based
 * upon a configured <i>Network Place</i>.
 * <p>
 * The mounts themselves are created from the <i>Network Place</i> resources
 * the user has access to that have a scheme supported by this mount as part of
 * the URI.
 */
public abstract class AbstractNetworkPlaceStore extends AbstractStore {

	// Protected instance variables
	protected Map mounts;
	protected VFSResource storeResource;
	protected boolean manageableOnly;

	/**
	 * Constructor.
	 * 
	 * @param name charset
	 * @param charset charset
	 */
	public AbstractNetworkPlaceStore(String name, String charset) {
		super(name, charset);
		mounts = new HashMap();
	}

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#isFireEvents()
     */
    public boolean isFireEvents() {
        return true;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vfs.VFSStore#getMountPath(java.lang.String)
	 */
	public String getMountPath(String mountName) {
		return getName() + "/" + mountName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vfs.webdav.AbstractStore#getMountFromString(java.lang.String,
	 *      com.adito.security.SessionInfo)
	 */
	public VFSMount getMountFromString(String mountName, LaunchSession launchSession) throws DAVException, DAVAuthenticationRequiredException {
	    
	    // Network place mounts will always require authentication
	    if(launchSession.getSession() == null) {
	        throw new DAVAuthenticationRequiredException(WebDAVAuthenticationModule.DEFAULT_REALM);
	    } 
	    
		try {		    
			NetworkPlace resource = (NetworkPlace) NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE.getResourceByName(mountName,
				getRepository().getSession());
			
			if (resource == null) {
				throw new Exception("No network place resource named " + mountName);
			}
			ResourceType resourceType = resource.getResourceType();
			boolean readOnly = false;
			
			if (!getProvider().willHandle(resource.getScheme())) {
				throw new Exception("Network place has scheme " + resource.getScheme() + ", this store doesn't support it.");
			}
			
			/**
			 * Update the launch sessions resource
			 */
			launchSession.setResource(resource);
			
			/* First check if the launch session is using a policy that is 
			 * valid for this resource
			 */
			if(isSuperUser(launchSession) || isLaunchSessionUsingValidPolicy(launchSession, resource)) {
				// It is, we can continue to use this launch session
            } else {
				Policy grantingPolicy = null;
				
				/* If the launch session already has policy, determine if this is a tracked session. If it is then don't allow
				 * policy to change
				 */ 
				
				if(launchSession.hasPolicy()) {
					if(launchSession.isTracked()) {
						// Launch session is tracked, do not allow policy to change but allow super user to browser readonly
						if (!ResourceUtil.isManageableResource(resource, getRepository().getSession().getUser(), null)) {
							throw new NoPermissionException("You do not have permission to access this network place resource under this policy.",
								getRepository().getSession().getUser(),
											resourceType);
						}
						readOnly = true;
					}
					else {
						// Not a tracked launch session so policy changing is allowed
						launchSession.takePolicy();
					}
				}
				
				/* If the mount has not already been set as ready only (because its tracked)
				 * There check access to the mount is allowed and change the granting policy
				 */
				if(!readOnly) {
					try {
						if (!(resource instanceof OwnedResource) || (resource instanceof OwnedResource && ((OwnedResource) resource).getOwnerUsername() == null)) {
							try {
								grantingPolicy = PolicyDatabaseFactory.getInstance().getGrantingPolicyForUser(launchSession.getSession().getUser(), resource);
								if(grantingPolicy == null) {
									throw new NoPermissionException("You may not access this network place resource here.",
										getRepository().getSession().getUser(),
													resourceType);
								} 
							} catch (NoPermissionException npe2) {
								throw npe2;
							} catch (Exception e) {
								throw new NoPermissionException("Failed to determine if network place resource is accessable.",
									getRepository().getSession().getUser(),
												resourceType);
							}
						} else {
							if (!(getRepository().getSession().getUser().getPrincipalName().equals(((OwnedResource) resource).getOwnerUsername()))) {
								throw new NoPermissionException("You do not have permission to access this network place resource.",
									getRepository().getSession().getUser(),
												resourceType);
							}
						}
					} catch (NoPermissionException npe) {
						if (!ResourceUtil.isManageableResource(resource, getRepository().getSession().getUser(), PolicyConstants.PERM_USE )) {
							throw new NoPermissionException("You do not have permission to access this network place resource.",
								getRepository().getSession().getUser(),
											resourceType);
						}
						readOnly = true;
					} catch (Exception e) {
						throw new Exception("Failed to determine if network place resource is accessable.");
					}			
				}
				
				
				if(grantingPolicy != null) {
					launchSession.givePolicy(grantingPolicy);
				}
			}

			AbstractNetworkPlaceMount mount = createMount(launchSession);
			if (readOnly) {
				mount.setReadOnly(true);
			}
			return mount;
		} catch (NoPermissionException npe) {
			throw new DAVException(DAVStatus.SC_FORBIDDEN, "Policy does not allow you access to this resource.", npe);
		} catch (Exception e) {
			throw new DAVException(DAVStatus.SC_INTERNAL_SERVER_ERROR, "Failed to create mount.", e);
		}
	}
    
    private boolean isLaunchSessionUsingValidPolicy(LaunchSession launchSession, NetworkPlace resource) throws Exception {
        PolicyDatabase policyDatabase = PolicyDatabaseFactory.getInstance();
        boolean hasPolicy = launchSession.hasPolicy();
        if(!hasPolicy) {
        	return false;
        }
        boolean resourceAttachedToPolicy = policyDatabase.isResourceAttachedToPolicy(resource, launchSession.getPolicy(), launchSession.getSession().getRealm());
        boolean policyGrantedToUser = policyDatabase.isPolicyGrantedToUser(launchSession.getPolicy(), launchSession.getSession().getUser());
        return resourceAttachedToPolicy && policyGrantedToUser;
    }
    
    private boolean isSuperUser(LaunchSession launchSession) {
        SessionInfo sessionInfo = launchSession.getSession();
        return LogonControllerFactory.getInstance().isAdministrator(sessionInfo.getUser());
    }

	/**
	 * Create the an appropriate mount instance given a launch session. If the
	 * user does not have access to the mount an exception should be thrown.
	 * 
	 * @param launchSession session
	 * @return mount
	 * @throws Exception on any error
	 */
	protected abstract AbstractNetworkPlaceMount createMount(LaunchSession launchSession) throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vfs.VFSStore#getMountNames()
	 */
	public Collection<String> getMountNames() throws Exception {
		List<String> l = new ArrayList<String>();
		List granted = NetworkPlaceDatabaseFactory.getInstance().getNetworkPlaces();
		for (Iterator i = granted.iterator(); i.hasNext();) {
			NetworkPlace np = (NetworkPlace) i.next();
			try {
				if (getProvider().willHandle(np.getScheme())) {
					l.add(np.getResourceName());
				}
			} catch (Exception e) {
			}
		}
		return l;
	}

	/**
	 * Valid the provide URI elements are correct for URIs for the concrete
	 * store type.
	 * 
	 * @param scheme scheme
	 * @param path path
	 * @param host host
	 * @param port port
	 * @param username username
	 * @param password password
	 * @param errs errors
	 * @return errors
	 * @throws IllegalArgumentException
	 */
	public ActionErrors validateUserEntries(String scheme, String path, String host, int port, String username, String password,
											ActionErrors errs) throws IllegalArgumentException {
		try {
			if (getProvider().getHostRequirement() == VFSProvider.ELEMENT_REQUIRED && Util.isNullOrTrimmedBlank(host)) {
				errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noHost"));
			}

			if (getProvider().getUserInfoRequirement()== VFSProvider.ELEMENT_REQUIRED && Util.isNullOrTrimmedBlank(username)) {
				errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noUserInfo"));
			}
			return errs;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

}
