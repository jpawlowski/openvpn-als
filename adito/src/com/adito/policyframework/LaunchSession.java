
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
			
package com.adito.policyframework;

import java.util.HashMap;
import java.util.Map;

import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;
import com.adito.security.User;

/**
 * Whenever a resource is launched, it is launched under a particular policy.
 * This object encapsulates everything about a launched resource.
 */
public class LaunchSession {

	/**
	 * Constant returned from {@link LaunchSession#checkAccessRights()} that
	 * indicates user access is allowed.
	 */
	public final static AccessRight USER_ACCESS = new AccessRight();

	/**
	 * Constant returned from {@link LaunchSession#checkAccessRights()} that
	 * indicates management access is allowed
	 */
	public final static AccessRight MANAGEMENT_ACCESS = new AccessRight();

	/**
	 * URL parameter used to identifiy launch session
	 */
	public static final String LAUNCH_ID = "launchId";

	/**
	 * Longer (more likely to be unique) URL parameter used to identifiy launch session
	 */
	public static final String LONG_LAUNCH_ID = "sslx_launchId";

	// Private instance variables

	private SessionInfo session;
	private Resource resource;
	private Policy policy;
	private String id;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * Constructor for a resource that does not need to be under any policy
	 * control
	 * 
	 * @param resource
	 */
	public LaunchSession(Resource resource) {
		this(null, null, resource, null);
	}

	/**
	 * Constructor for a temporary resource session that is not attached to any
	 * policy framework resource.
	 * 
	 * @param session session
	 */
	public LaunchSession(SessionInfo session) {
		this(null, session, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param id resource session id
	 * @param session session
	 * @param resource resource
	 * @param policy policy launched under
	 */
	public LaunchSession(String id, SessionInfo session, Resource resource, Policy policy) {
		super();
		
		this.id = id;
		this.session = session;
		this.resource = resource;
		this.policy = policy;
		if( ( resource == null && policy != null ) || ( policy == null && resource != null ) ) {
			throw new IllegalArgumentException("If either resource or policy is provided, resource and policy must be provided.");
		}
	}

	/**
	 * Get if this launch should check if permission is allowed. This will be
	 * true if the launch session has a resource attribute and a policy attribute.
	 * 
	 * @return should check access rights
	 */
	public boolean hasPolicy() {
		return resource != null && policy != null;
	}

	/**
	 * Check if access is granted to this resource. The return value indicates
	 * the access type allowed. Will return either {@link #USER_ACCESS} or
	 * {@link #MANAGEMENT_ACCESS}.
	 * 
	 * @param user if available, the requesting user. This is checked against the launchsession's user 
	 * @param sessionInfo if available, the requesting user. This is checked against the launchsession's user
	 * @return access right
	 * @throws NoPermissionException if permission is not allowed
	 * @throws PolicyException if permission cannot be determined for some reason
	 */
	public AccessRight checkAccessRights(User user, SessionInfo sessionInfo) throws NoPermissionException, PolicyException {
		if (resource == null) {
			throw new PolicyException(PolicyException.INTERNAL_ERROR, "This resource session is not attached to a resource.");
		}
		
		ResourceType resourceType = resource.getResourceType();
		if(sessionInfo != null && this.session != null && !sessionInfo.equals(this.session)) {
			throw new NoPermissionException("You do not own this session.", session.getUser(), resourceType);
		}
		if(user != null && this.session != null && !user.equals(this.session.getUser())) {
			throw new NoPermissionException("Your user does not own this session.", user, resourceType);
		}
		
		// Make sure the resource is a launch-able type
		if(resource.getLaunchRequirement() == LaunchRequirement.REQUIRES_WEB_SESSION) {
            if(this.session == null || this.session.getHttpSession() == null ) {
                throw new PolicyException(PolicyException.INTERNAL_ERROR, "This resource is launchable only when a web session is available.");
            }
        }
		else if(resource.getLaunchRequirement() != LaunchRequirement.LAUNCHABLE) {
            throw new PolicyException(PolicyException.INTERNAL_ERROR, "This resource is not launchable.");
        } 
		
		try {
			if (!(resource instanceof OwnedResource) || (resource instanceof OwnedResource && ((OwnedResource) resource).getOwnerUsername() == null)) {
				try {
					// assigned
					if (!PolicyDatabaseFactory.getInstance().isPrincipalAllowed(session.getUser(),
						resource,
						false)) {
						throw new NoPermissionException("You may not access this resource here.", session.getUser(), resourceType);
					}
				} catch (NoPermissionException npe2) {
					throw npe2;
				} catch (Exception e) {
					throw new NoPermissionException("Failed to determine if resource is accessable.",
									session.getUser(),
									resourceType);
				}
			} else {
				// or owned
				if (!(session.getUser().getPrincipalName().equals(((OwnedResource) resource).getOwnerUsername()))) {
					throw new NoPermissionException("You do not have permission to access this resource.",
									session.getUser(),
									resourceType);
				}
			}
			return USER_ACCESS;
		} catch (NoPermissionException npe) {
			ResourceUtil.checkResourceManagementRights(resource, session, new Permission[] {  PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,  PolicyConstants.PERM_EDIT_AND_ASSIGN });
			return MANAGEMENT_ACCESS;
		} catch (Exception e) {
			throw new NoPermissionException("Failed to determine if resource is accessable.", session.getUser(), resourceType);
		}
	}

	/**
	 * Get the policy this resource was launched under.
	 * 
	 * @return policy
	 */
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * Get the resource lauched
	 * 
	 * @return launch
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Set the resouce launched
	 * 
	 * @param resource resource
	 */
	public void setResource(Resource resource) {
		this.resource = resource;

	}

	/**
	 * Get the user session that launched the resource.
	 * 
	 * @return resource
	 */
	public SessionInfo getSession() {
		return session;
	}

	/**
	 * Get the unique ID for this launch
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Remove policy from this launch session 
	 */
	public void takePolicy() {
		policy = null;	
	}

	/**
	 * Give this launch session policy.
	 * 
	 * @param policy policy
	 */
	public void givePolicy(Policy policy) {
		this.policy = policy;		
	}

	/**
	 * Get if this launch session is tracked. It is tracked if it
	 * has an ID.
	 * 
	 * @return tracked
	 */
	public boolean isTracked() {
		return id != null;
	}
	
	/**
	 * Get an attribute value given its name. <code>null</code>
	 * will be returned if no such attribute exists.
	 * 
	 * @param name name of attribute
	 * @return value
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	/**
	 * Set an attribute
	 * 
	 * @param name name of attribute
	 * @param value value of attribute
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return o instanceof LaunchSession && getId() != null
			&& ((LaunchSession) o).getId() != null
			&& ((LaunchSession) o).getId().equals(getId())
			&& ((LaunchSession) o).getSession().getHttpSession().equals(getSession().getHttpSession());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getSession().getHttpSession().getId() + "_" + getId()).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (hasPolicy() ? ("PolicyProtected [policy=" + policy.getResourceId()
			+ ",resource="
			+ resource.getResourceName()
			+ "/"
			+ resource.getResourceType().getResourceTypeId() + ",") : "Unprotected [") + "session="
			+ session.getId()
			+ ",httpSession="
			+ session.getHttpSession().getId()
			+ "]";
	}

	/**
	 * Access right
	 */
	public static class AccessRight {
	}

    /**
     * Its possible to start tracking a launch session once an authorized session 
     * has been established.
     */
    public void startTracking() {
        if(getSession() != null) {
            throw new IllegalStateException("Cannot start tracking a launch session without session info.");
        }
        
        
        
    }
}
