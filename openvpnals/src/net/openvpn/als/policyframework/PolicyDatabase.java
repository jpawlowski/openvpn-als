package net.openvpn.als.policyframework;

import java.util.List;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.Database;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;

/**
 * Implementations of this interface are responsible for all of the persistence
 * of policy related data as well as performing the logic in checking whether or
 * not a principal has access to a policy
 */
public interface PolicyDatabase extends Database {
    /**
     * Register a new resource type.
     * 
     * @param resourceType resource type to register
     * @throws Exception on any error
     */
    public void registerResourceType(ResourceType resourceType) throws Exception ;
    
    /**
     * De-register an existing resource type
     * 
     * @param resourceType resource type to de-register
     * @throws Exception on any error
     */
    public void deregisterResourceType(ResourceType resourceType) throws Exception ;

    /**
     * Get the number of resource types that have been registered.
     * 
     * @return number of registered resource types
     */
    public int getResourceTypeCount();
    

    /**
     * Get a {@link List} of all registered {@link ResourceType}s.
     * 
     * @param permissionClass permission class or <code>null</code> for any
     * @return list of resource types
     * @throws Exception
     */
    public List<ResourceType> getResourceTypes(String permissionClass) throws Exception;
    
    /**
     * Get a {@link Policy} given its ID.
     * 
     * @param id id of policy
     * @return policy
     * @throws Exception on any error
     */
    public Policy getPolicy(int id) throws Exception;
    
    /**
     * Update a policy. 
     * 
     * @param policy policy to update
     * @throws Exception on any error
     */
    public void updatePolicy(Policy policy) throws Exception ;
    
    /**
     * Create a new policy. The returned {@link Policy} object will contain the
     * newly assigned policy ID
     * 
     * @param name policy name
     * @param description policy description
     * @param type policy type
     * @param realmID 
     * @return Policy created policy object
     * @throws Exception on any error
     */
    public Policy createPolicy(String name, String description, int type, int realmID) throws Exception;
    
    /**
     * Delete a policy. If this policy is a parent of other policies then all
     * child policies will also be deleted.
     * 
     * @param id policy to delete
     * @return deleted policy
     * @throws Exception on any error
     */
    public Policy deletePolicy(int id) throws Exception;

    /**
     * Return a list of all policies for a given realm
     * @param realm 
     * 
     * @return list of top level policies
     * @throws Exception on any error
     */
    public List<Policy> getPolicies(Realm realm) throws Exception;

    /**
     * Return a list of all policies for a given realm
     * excluding the personal policies
     * @param realm 
     * 
     * @return list of top level policies
     * @throws Exception on any error
     */
    public List<Policy> getPoliciesExcludePersonal(Realm realm) throws Exception;
    
    /**
     * Return a list of all policies
     * 
     * @return list of top level policies
     * @throws Exception on any error
     */
    public List<Policy> getPolicies() throws Exception;
 
    /**
     * Grant a policy to a principal, giving it access to any resources that are
     * attached to the policy
     * 
     * @param policy policy to grant
     * @param principal principal to grant to
     * @throws Exception on any error
     */
    public void grantPolicyToPrincipal(Policy policy, Principal principal) throws Exception;

    /**
     * Revoke a policy from a principal, removing any access it may have to the
     * resources that are attached to the policy.
     * 
     * @param policy policy to revoke
     * @param principal principal to revoke policy from
     * @throws Exception on any error
     */
    public void revokePolicyFromPrincipal(Policy policy, Principal principal) throws Exception;

    /**
     * Revoke all policies from a specified principal
     *  
     * @param principal principal to revoke policies from
     * @throws Exception on any error
     */
    public void revokeAllPoliciesFromPrincipal(Principal principal)  throws Exception ;
    
    /**
     * Revoke all policies from principals within the supplied realm
     * 
     * @param realm
     * @throws Exception
     */
    public void revokeAllPoliciesFromPrincipals(Realm realm) throws Exception;
    
    
    /**
     * Attach a resource to a policy, giving access to any principals that are
     * attached the policy.
     * 
     * @param resource resource to attach to policy
     * @param policy policy to attach resource to
     * @param sequence sequence
     * @param realm 
     * @throws Exception on any error
     */
    public void attachResourceToPolicy(Resource resource, Policy policy, int sequence, Realm realm) throws Exception;
    
    /**
     * Detach a resource from a policy, removing access from any principals that
     * are attached the policy.
     * 
     * @param resource resource to detach from the policy
     * @param policy policy to detach resource from
     * @param realm the realm the user is signon to
     * @throws Exception on any error
     */
    public void detachResourceFromPolicy(Resource resource, Policy policy, Realm realm) throws Exception;

    /**
     * Determine if the specified resource is attached to the specified policy
     * 
     * @param resource resource
     * @param policy policy
     * @param realm the ream the user is signed on to.
     * @return attached
     * @throws Exception
     */
    public boolean isResourceAttachedToPolicy(Resource resource, Policy policy, Realm realm) throws Exception;

    /**
     * Get if the provided {@link net.openvpn.als.policyframework.Principal} is
     * allowed to access the specified
     * {@link net.openvpn.als.policyframework.Resource}. If
     * <code>null</code> is provided as the resource, <code>true</code> will
     * be returned if the principal is allowed access to <strong>any</strong>
     * resource.
     * 
     * @param principal principal to test
     * @param resource resource to test. <code>null</code> will test for any
     *        resource.
     * @param includeSuperUser include the super user in the test for allowed
     * @return allowed
     * @throws Exception on any error
     */
    public boolean isPrincipalAllowed(Principal principal, Resource resource, boolean includeSuperUser) throws Exception;

    /**
     * Get if the principal is granted access via its policies to any resources
     * of the given resource type. Supply <code>null</code> as the resource
     * type to test if the principal is allowed access to any resources of any
     * type.
     * <p>
     * Note that by default the super user will not be granted resources of
     * the specified type, its up to the caller to treat super user as a 
     * special case.
     * 
     * @param principal principal (user / role) to test against
     * @param resourceType resource type to match or <code>null</code> for any resource type
     * @param resourceTypesToExclude list of {@link ResourceType}s to exclude or null to exclude none
     * @return allowed
     * @throws Exception on any error
     */
    public boolean isPrincipalGrantedResourcesOfType(Principal principal, ResourceType resourceType, List resourceTypesToExclude) throws Exception;

    /**
     * Get the resources a principal is granted access via its policies
     * 
     * @param principal principal
     * @param resourceType resource type
     * @return List of {@link Integer} objects containing the ids of the
     *         resources
     * @throws Exception on any error
     */
    public List<Integer> getGrantedResourcesOfType(Principal principal, ResourceType resourceType) throws Exception;
   

    /**
     * Get a list of {@link Policy} that are attached to a resource
     * 
     * @param resource
     * @param realm the realm of the user.
     * @return list of policies
     * @throws Exception on any error
     */
    public List<Policy> getPoliciesAttachedToResource(Resource resource, Realm realm) throws Exception;

    /**
     * Get a list of all {@link Principal}s granted the provided {@link Policy}
     * 
     * @param policy
     * @param realm 
     * @return list of principals
     * @throws Exception
     */
    public List<Principal> getPrincipalsGrantedPolicy(Policy policy, Realm realm) throws Exception;

    /**
     * Revoke all policy from all principals
     * 
     * @param policy
     * @param realm 
     * @throws Exception on any error
     */
    public void revokePolicyFromAllPrincipals(Policy policy, Realm realm) throws Exception;

    /**
     * Get a resource type given its id.
     * 
     * @param resourceTypeId
     * @return resource type
     */
    public ResourceType getResourceType(int resourceTypeId);
    
    /**
     * Get a policy given its name.
     * 
     * @param name policy name
     * @param realmID 
     * @return policy object
     * @throws Exception on any error
     */
    public Policy getPolicyByName(String name, int realmID) throws Exception;

    
    /**
     * Create a resource permission. The ID will be set upon success. The same
     * object instance will be returned
     * 
     * @param accessRights access rights
     * @return resource
     * @throws Exception
     */
    public AccessRights createAccessRights(AccessRights accessRights)  throws Exception;

    /**
     * Get the complete list of {@link AccessRights} objects
     * 
     * @return list of resource permissions
     * @throws Exception on any error
     */
    public List<AccessRights> getAccessRights() throws Exception;

    /**
     * Get the complete list of {@link AccessRights} objects
     * @param realmID
     * @return list of resource permissions
     * @throws Exception on any error
     */
    public List<AccessRights> getAccessRights(int realmID) throws Exception;
    
    /**
     * Get a resource permission given its name.
     * 
     * @param name resource permission
     * @param realmID 
     * @return delegation resource object
     * @throws Exception on any error
     */
    public AccessRights getAccessRightsByName(String name, int realmID) throws Exception;

    /**
     * Get a resource permission given its id
     * 
     * @param id
     * @return resource permission
     * @throws Exception on any error
     */
    public AccessRights getAccessRight(int id) throws Exception;

    /**
     * Determine whether an action can be performed by checking the resource
     * permission tree.
     * 
     * @param resourceType resource type to check
     * @param permissions permissions required
     * @param user user to check
     * @param all all permissions are check rather than any if true
     * @return resource permission is allowed
     * @throws Exception on any error
     */
    public boolean isPermitted(ResourceType resourceType, Permission[] permissions, User user, boolean all)
        throws Exception;

    /**
     * Determine whether an action on a personal resource can be performed
     * by checking the resource permission tree and policies assigned to this resource.
     * 
     * @param resource resource to check
     * @param permissions permissions required
     * @param user user to check
     * @return resource permission is allowed
     * @throws Exception on any error
     */
    public boolean isPersonalPermitted(Resource resource, Permission[] permissions, User user)
        throws Exception;
    
    /**
     * Get a list of {@link AccessRights} objects that are valid for user
     * to view / edit.
     * 
     * @param resourceType resource type to check or null for any
     * @param permission permission required or null for any
     * @param permissionClass class of resource permission or null for any
     * @param user user to check (may not be null)
     * @return list of resource permission objects
     * @throws Exception on any error
     */
    public List<AccessRights> getAccessRights(ResourceType resourceType, Permission permission, String permissionClass, User user)
        throws Exception;

    /**
     * Get a list of {@link AccessRights} objects that are permit the the
     * specified user to perform an action.
     * 
     * @param resourceType resource type to check or null for any
     * @param permission permission required or null for any
     * @param permissionClass class of resource permission or null for any
     * @param user user to check (may not be null)
     * @return list of resource permission objects
     * @throws Exception on any error
     */
    public List<AccessRights> getPermittingAccessRights(ResourceType resourceType, Permission permission, String permissionClass, User user) throws Exception;

    /**
     * Determine whether the user can perform <strong>any</strong>
     * administrative actions using the delegation tree.
     * 
     * @param user user to check
     * @param delegation include deletation class
     * @param system include system class
     * @param personal include personal class
     * @return allowed
     * @throws Exception on any error
     */
    public boolean isAnyAccessRightAllowed(User user, boolean delegation, boolean system, boolean personal) throws Exception;

    /**
     * get <strong>any</strong> permission the user can perform
     * using the delegation tree.
     * 
     * @param user user to check
     * @param delegation include delegation class
     * @param system include system class
     * @param personal include personal class
     * @return List<AccessRight>
     * @throws Exception on any error
     */
    public List<AccessRight> getAnyAccessRightAllowed(User user, boolean delegation, boolean system, boolean personal)throws Exception;
     
    /**
     * Delete a resource permission given its id
     * 
     * @param id id to remove
     * @return deleted resource permission
     * @throws Exception on any error
     */
    public AccessRights deleteAccessRights(int id) throws Exception;
    
    /**
     * Update a resource permission
     * 
     * @param permission permission to update
     * @throws Exception on any error
     */
    public void updateAccessRights(AccessRights permission) throws Exception;

    /**
     * Load the access rights
     * 
     * @throws Exception on any error
     */
    public void initAccessRights() throws Exception;

    /**
     * Get a list of {@link Policy} objects that have been delegated ANY
     * permission to maintain a resource type. A permission class may also be
     * specified
     * 
     * @param resourceType resource type
     * @param permissionClass permission class
     * @param user user
     * @return list of policies
     * @throws Exception on any error
     */
    public List<Policy> getPoliciesOfDelegatedAccessRights(ResourceType resourceType, String permissionClass, User user)
                    throws Exception;
    
    /**
     * Get the Everyone policy for a given realm, there is only one Everyone policy per realm.
     * @param realm
     * @return int
     * @throws Exception
     */
    public int getEveryonePolicyIDForRealm(Realm realm)throws Exception;
    
    /**
     * Check to see if the resource is connected to a policy in the defined realm.
     * 
     * @param resource
     * @param realm
     * @return boolean
     * @throws Exception
     */
    public boolean isResourceInRealm(Resource resource, Realm realm) throws Exception;

    /**
     * Get the granting policy for the user or the attached roles.
     * 
     * @param user
     * @param resource
     * @return Policy
     * @throws Exception
     */
    public Policy getGrantingPolicyForUser(User user, Resource resource) throws Exception;

    /**
     * Method to verify whether a policy is granted to a user. This checks the policy for both the user and
     * their allocated groups.
     * 
     * @param policy
     * @param user
     * @return boolean
     * @throws Exception
     */
    public boolean isPolicyGrantedToUser(Policy policy, User user) throws Exception;

    /**
     * Detach a resource from ALL policies it is attached to
     * 
     * @param resource resource to detach
     * @param session originating session
     * @throws Exception on any error
     */
    public void detachResourceFromPolicyList(Resource resource, SessionInfo session) throws Exception;

    /**
     * Attach a resource to a list of policies
     * 
     * @param resource resource to attach
     * @param selectedPolicies policies to attach to
     * @param session originating session 
     * @throws Exception
     */
    public void attachResourceToPolicyList(Resource resource, PropertyList selectedPolicies, SessionInfo session) throws Exception;

}
