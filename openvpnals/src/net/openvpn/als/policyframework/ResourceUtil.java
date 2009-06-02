
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
			
package net.openvpn.als.policyframework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import net.openvpn.als.security.AuthenticationScheme;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.SystemDatabaseFactory;
import net.openvpn.als.security.User;

/**
 * A set of utilities for dealing with <i>Resources</i>
 */
public class ResourceUtil {
    
    final static Log log = LogFactory.getLog(ResourceUtil.class);

    /*
     * Private constructor to prevent instantiation
     */
    private ResourceUtil() {
    }

    /**
     * Filter a {@link List} of {@link Resource} objects, looking for either
     * resources owned by the supplied username, or global resources that have
     * the correct policy.
     * 
     * @param user user
     * @param resources list of owned resources
     * @param includeSuperUser include super user permitted resources
     * @return list of filtered owned resources
     * @throws Exception on any error
     */
    public static List filterResources(User user, List resources, boolean includeSuperUser) throws Exception {
        List validResources = new ArrayList();
        for (Iterator i = resources.iterator(); i.hasNext();) {
            Resource p = (Resource) i.next();
            // Include the resource if the current user created it
            if (p instanceof OwnedResource && ((OwnedResource) p).getOwnerUsername() != null
                            && !((OwnedResource) p).getOwnerUsername().equals("")) {
                if (((OwnedResource) p).getOwnerUsername().equals(user.getPrincipalName())) {
                    validResources.add(p);
                }
            } else {
                if (PolicyDatabaseFactory.getInstance().isPrincipalAllowed(user, p, includeSuperUser)) {
                    validResources.add(p);
                }
            }
        }
        return validResources;

    }

    private static void addResources(User user, boolean includeSuperUser, List validResources, Resource p) throws Exception {
        // Include the resource if the current user created it
        if (p instanceof OwnedResource && ((OwnedResource) p).getOwnerUsername() != null
                        && !((OwnedResource) p).getOwnerUsername().equals("")) {
            if (((OwnedResource) p).getOwnerUsername().equals(user.getPrincipalName())) {
                validResources.add(p);
            }
        } else {
            if (PolicyDatabaseFactory.getInstance().isPrincipalAllowed(user, p, includeSuperUser)) {
                validResources.add(p);
            }
        }
    }

    /**
     * Set the current list of available profiles for this session as a session
     * attribute.
     * 
     * @param session session
     * @return the available profiles
     * @throws Exception
     */
    public static List setAvailableProfiles(SessionInfo session) throws Exception {
        User user = LogonControllerFactory.getInstance().getUser(session.getHttpSession(), null);
        List profiles = filterResources(user, ProfilesFactory.getInstance().getPropertyProfiles(
                        user.getPrincipalName(), true, session.getUser().getRealm().getResourceId()), false);
        session.getHttpSession().setAttribute(Constants.PROFILES, profiles);
        return profiles;
    }

    /**
     * Create a {@link List} or {@link org.apache.struts.util.LabelValueBean}
     * objects from a {@link List} of {@link Resource} objects.
     * 
     * @param resourceList resource list
     * @return list of objects suitable for struts list components
     */
    public static List resourceListAsLabelValueBeanList(List resourceList) {
        List l = new ArrayList();
        Resource r;
        for (Iterator i = resourceList.iterator(); i.hasNext();) {
            r = (Resource) i.next();
            l.add(new LabelValueBean(r.getResourceName(), String.valueOf(r.getResourceId())));
        }
        return l;
    }

    /**
     * Filter a list of {@link OwnedResource} obects for those that do
     * <strong>not</strong> have an owner.
     * 
     * @param resources resources
     * @return filtered resources
     */
    public static List filterOwned(List resources) {
        List l = new ArrayList();
        for (Iterator i = resources.iterator(); i.hasNext();) {
            Resource resource = (Resource) i.next();
            if (resource instanceof OwnedResource && ((OwnedResource) resource).getOwnerUsername() == null) {
                l.add(resource);
            }
        }
        return l;
    }

    /**
     * Get if a single resource may be managed by the specified user. For a
     * resource to be manageable, a user must either be the super user or the
     * parent resource permission of the resource must be attached to a policy
     * that the specified user. A resource will also be manageable if one of its
     * parents is manageable.
     * <p>
     * If a permission is provided, any resource permission that matches must
     * contain the permission
     * 
     * @param resource resource to test
     * @param user user
     * @param permission permission
     * @return <code>true</code> if the resource is manageable
     * @throws Exception on any error
     */
    public static boolean isManageableResource(Resource resource, User user, Permission permission) throws Exception {
        boolean b = false;
        if (LogonControllerFactory.getInstance().isAdministrator(user)){
                return true;
                }
        else{
            b = PolicyDatabaseFactory.getInstance().isPermitted(resource.getResourceType(), new Permission[] {permission}, user, false);
        }

        return b;
    }
    
    /**
     * Get if a single personal resource may be managed by the specified user. For a
     * resource to be manageable, a user must either be the super user or the
     * parent personal resource permission of the resource must be attached to the policy
     * of that user. The resource must be attached to the personal policy of
     * the specified user and only to this personal policy.
     * A resource will also be manageable if one of its
     * parents is manageable.
     * 
     * @param resource resource to test
     * @param user user
     * @param permission permission
     * @return <code>true</code> if the resource is manageable
     * @throws Exception on any error
     */
    public static boolean isManageablePersonalResource(Resource resource, User user, Permission permission) throws Exception {
        boolean b = false;
        if (LogonControllerFactory.getInstance().isAdministrator(user)){
            return true;
        }
        else{
            b = PolicyDatabaseFactory.getInstance().isPersonalPermitted(resource, new Permission[] {permission}, user);
        }
        
        return b;
    }

    /**
     * Get if the list of {@link ResourceItem} objects contains any obects that
     * wrap the specified {@link Resource}
     * 
     * @param items items to search
     * @param resource resource to search for
     * @return resource found
     */
    public static boolean resourceItemListContainsResource(List items, Resource resource) {
        ResourceItem ri;
        for (Iterator i = items.iterator(); i.hasNext();) {
            ri = (ResourceItem) i.next();
            if (ri.getResource().equals(resource)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current resource may be managed (i.e. edited, removed etc)
     * taking into account the current navigation context, whether the resource
     * is owned and if rights to manage it have been delegated to the current
     * user
     * 
     * @param resource resource
     * @param session session
     * @param permissions permissions required for management. if any of these
     *        are assigned the the resource may be managed
     * @throws NoPermissionException if not allowed
     */
    public static void checkResourceManagementRights(Resource resource, SessionInfo session, Permission[] permissions)
                    throws NoPermissionException {
        for (int i = 0; i < permissions.length; i++) {
            try {
                ResourceType resourceType = resource.getResourceType();
                // If in the management console, this resource must be
                // manageable
                if (session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
                    try {
                        if (!ResourceUtil.isManageableResource(resource, session.getUser(), permissions[i])) {
                            throw new NoPermissionException("You do not have permission to manage this resource.", session
                                            .getUser(), resourceType);
                        }
                    } catch (NoPermissionException npe) {
                        throw npe;
                    } catch (Exception e) {
                        throw new NoPermissionException("Failed to determine if resource is manangeable.", session.getUser(),
                                        resourceType);
                    }
                }
                // If in the user console the resource must be owned
                else if (session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
                    try {
                    if (!(resource instanceof OwnedResource)) {
                        if (!ResourceUtil.isManageablePersonalResource(resource, session.getUser(), permissions[i])) {
                            throw new NoPermissionException("You may not managed this resource here.", session.getUser(), resourceType);
                        }
                    } else {
                        if (!(session.getUser().getPrincipalName().equals(((OwnedResource) resource).getOwnerUsername()))) {
                            throw new NoPermissionException("You do not have permission to manage this resource.", session
                                            .getUser(), resourceType);
                        }
                    }
                    } catch (Exception e) {
                        throw new NoPermissionException("Failed to determine if resource is manangeable.", session.getUser(),
                                        resourceType);
                    }
                    
                } else {
                    throw new NoPermissionException("You may not manage this resource here.", session.getUser(), resourceType);
                }
                break;
            } catch (NoPermissionException npe) {
                if (i == (permissions.length - 1)) {
                    throw npe;
                }
            }
        }

    }

    /**
     * Check if the current resource may be accessed taking into account the
     * current navigation context, whether the resource is owned and if rights
     * to access it have been assigned to the current user
     * 
     * @param resource resource
     * @param session session
     * @throws NoPermissionException if not allowed
     */
    public static void checkResourceAccessRights(Resource resource, SessionInfo session) throws NoPermissionException {
        ResourceType resourceType = resource.getResourceType();
        // If in the management console, this resource must be manageable
        if (session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
            try {
                if (!ResourceUtil.isManageableResource(resource, session.getUser(), null)) {
                    throw new NoPermissionException("You do not have permission to access this resource.", session.getUser(),
                                    resourceType);
                }
            } catch (NoPermissionException npe) {
                throw npe;
            } catch (Exception e) {
                throw new NoPermissionException("Failed to determine if resource is accessable.", session.getUser(), resourceType);
            }
        }
        // If in the user console the resource must be assigned or owned
        else if (session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
            if (!(resource instanceof OwnedResource)
                            || (resource instanceof OwnedResource && ((OwnedResource) resource).getOwnerUsername() == null)) {
                try {
                    // assigned
                    if (!PolicyDatabaseFactory.getInstance().isPrincipalAllowed(session.getUser(), resource, false)) {
                        throw new NoPermissionException("You may not access this resource here.", session.getUser(), resourceType);
                    }
                } catch (NoPermissionException npe) {
                    throw npe;
                } catch (Exception e) {
                    throw new NoPermissionException("Failed to determine if resource is accessable.", session.getUser(),
                                    resourceType);
                }
            } else {
                // or owned
                if (!(session.getUser().getPrincipalName().equals(((OwnedResource) resource).getOwnerUsername()))) {
                    throw new NoPermissionException("You do not have permission to access this resource.", session.getUser(),
                                    resourceType);
                }
            }
        } else {
            throw new NoPermissionException("You may not access this resource here.", session.getUser(), resourceType);
        }
    }

    /**
     * Check if  {@link AccessRights} may be viewed, edited or removed.
     * If the <code>actionTarget</code> supplied is <strong>view</strong>
     * then a check is made to see if the resource permission is one that
     * permits the current user to perform actions. If <strong>edit</strong>,
     * <strong>remove</strong> or <strong>confirmRemove</strong> is supplied
     * then a check if made if the resource has a parent that the current user
     * has access to.
     * 
     * @param resource resource to check
     * @param session session of current user
     * @param actionTarget action target
     * @throws NoPermissionException no permission excepion
     */
    public static void checkAccessRightsValid(AccessRights resource, SessionInfo session, String actionTarget)
                    throws NoPermissionException {

        if (actionTarget.equals("edit") || actionTarget.equals("remove") || actionTarget.equals("confirmRemove")) {
            ResourceUtil.checkResourceManagementRights(resource, session, new Permission[]{});
        } else if (actionTarget.equals("view")) {
            try {
                List l = LogonControllerFactory.getInstance().isAdministrator(session.getUser()) ? new ArrayList()
                                : PolicyDatabaseFactory.getInstance().getPermittingAccessRights(null, null, null,
                                                session.getUser());
                if (!l.contains(resource)) {
                    throw new NoPermissionException("Permission denied.", session.getUser(),
                                    PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE);
                }
            } catch (NoPermissionException npe) {
                throw npe;
            } catch (Exception e) {
                throw new NoPermissionException("Failed to determine management rights.", session.getUser(),
                                PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE);
            }
        } else {
            throw new Error("checkValid() only supports edit, remove or view here, not '" + actionTarget + "'.");
        }

    }

    /**
     * @param user
     * @return List
     * @throws Exception
     */
    public static List getSignonAuthenticationSchemeIDs(User user) throws Exception {
        List<Integer> resourceIds = PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(user,
                        PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
        List<Integer> filteredResourceIDs = new ArrayList<Integer>();
        for (Integer integer : resourceIds) {
            AuthenticationScheme authenticationScheme = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(integer);
            // Need to check for null because a plugin may have been removed that provided a previously valid scheme
            if (authenticationScheme != null && !authenticationScheme.isSystemScheme())
                filteredResourceIDs.add(authenticationScheme.getResourceId());
        }
        return filteredResourceIDs;
    }

    /**
     * Obtain a list of all resources assigned to a user.
     * @param session
     * @return
     * @throws Exception
     */
    public static List getGrantedResources(SessionInfo session) throws Exception {
    	
    	List allResources = new ArrayList();
    	List types = PolicyDatabaseFactory.getInstance().getResourceTypes(null);
    	
    	for(Iterator it = types.iterator(); it.hasNext();) {
    		ResourceType type = (ResourceType) it.next();
    		
    		allResources.addAll(ResourceUtil.getGrantedResource(session, type));
    	}
    	
    	return allResources;
    }

    /**
     * Gets a list of {@link Resource} granted for use for the specified
     * session.
     * 
     * @param session session
     * @param resourceType resource type
     * @return list of resources
     * @throws Exception
     */
    public static List getGrantedResource(SessionInfo session, ResourceType resourceType) throws Exception {
        List l = new ArrayList();
        List granted = PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(session.getUser(), resourceType);
        for (Iterator i = granted.iterator(); i.hasNext();) {
            Integer r = (Integer) i.next();
            Resource resource = resourceType.getResourceById(r.intValue());
            if(resource == null) {
                log.warn("Could not locate resource with ID of " + r.intValue() + " for type " + resourceType.getResourceTypeId());
            }
            else {
                if (isPolicyResourceTypeEnforceable(resourceType)
                                && Property.getPropertyBoolean(new SystemConfigKey("security.enforce.policy.resource.access"))) {
                    for (Iterator iter = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(resource,
                                    session.getUser().getRealm()).iterator(); iter.hasNext();) {
                        Policy element = (Policy) iter.next();
                        List authSchemePolicies = (List) session.getHttpSession().getAttribute("auth.scheme.policies");
                        if (authSchemePolicies != null && (authSchemePolicies).contains(element)) {
                            l.add(resource);
                        }
                    }
                } else {
                    l.add(resource);
                }
            }
        }
        return l;
    }

    public static boolean isPolicyResourceTypeEnforceable(ResourceType rt) {
        if (rt.equals(PolicyConstants.PROFILE_RESOURCE_TYPE) || rt.equals(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE)
                        || rt.equals(PolicyConstants.POLICY_RESOURCE_TYPE)
                        || rt.equals(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE))
            return false;
        else
            return true;
    }


    /**
     * Filter a list of {@link Integer} objects containing resource ids for
     * those that have a global favorite.
     * 
     * @param resources resources
     * @param resourceType resource type
     * @return filtered list of resources that have favorites
     * @throws Exception on any error
     */
    public static List filterResourceIdsForGlobalFavorites(List resources, ResourceType resourceType) throws Exception {
        List l = new ArrayList();
        for (Iterator i = resources.iterator(); i.hasNext();) {
            Integer r = (Integer) i.next();
            if (SystemDatabaseFactory.getInstance().getFavorite(resourceType.getResourceTypeId(), null, r.intValue()) != null) {
                l.add(r);
            }
        }
        return l;
    }

    /**
     * Set whether a resource is a global favorite (available to all have 
     * who policy to use it).
     * 
     * @param resource resource 
     * @param addToFavorites add to favorites
     * @throws Exception on any error
     */    
    public static void setResourceGlobalFavorite(Resource resource, boolean addToFavorites) throws Exception {
        if(addToFavorites != isResourceGlobalFavorite(resource)) {
            if(addToFavorites) {
                SystemDatabaseFactory.getInstance().addFavorite(resource.getResourceType().getResourceTypeId(), resource.getResourceId(), null);
            }
            else {
                SystemDatabaseFactory.getInstance().removeFavorite(resource.getResourceType().getResourceTypeId(), resource.getResourceId(), null);            
            }
        }
    }

    /**
     * Get if a resource is added as a global favorite (available to all have 
     * who policy to use it).
     * 
     * @param resource resource
     * @return added as a favorite
     * @throws Exception
     */
    public static boolean isResourceGlobalFavorite(Resource resource) throws Exception {
        return SystemDatabaseFactory.getInstance().getFavorite(resource.getResourceType().getResourceTypeId(), null, resource.getResourceId()) != null;
    }

}
