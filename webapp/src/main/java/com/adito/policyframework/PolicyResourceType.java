
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

import java.util.Collection;

import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.security.SessionInfo;

/**
 * Implementation of a {@link com.adito.policyframework.ResourceType} for
 * <i>Policy</i> resources.
 */
public class PolicyResourceType extends DefaultResourceType<Policy> {

    /**
     * Constructor
     */
    public PolicyResourceType() {
        super(PolicyConstants.POLICY_RESOURCE_TYPE_ID, "policyframework", PolicyConstants.SYSTEM_CLASS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteResourceType#getResourceById(int)
     */
    public Policy getResourceById(int resourceId) throws Exception {
        return PolicyDatabaseFactory.getInstance().getPolicy(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.DefaultResourceType#getResourceByName(java.lang.String,
     *      com.adito.security.SessionInfo)
     */
    public Policy getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return PolicyDatabaseFactory.getInstance().getPolicyByName(resourceName, session.getUser().getRealm().getResourceId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#removeResource(int,
     *      com.adito.security.SessionInfo)
     */
    public Policy removeResource(int resourceId, SessionInfo session) throws Exception {
        try {
            if (resourceId == PolicyDatabaseFactory.getInstance().getEveryonePolicyIDForRealm(session.getUser().getRealm())) {
                throw new Exception("Cannot remove 'Everyone' policy.");
            }
            Policy resource = PolicyDatabaseFactory.getInstance().deletePolicy(resourceId);
            CoreServlet.getServlet().fireCoreEvent(
                addPolicyAttributes(new ResourceDeleteEvent(this, CoreEventConstants.DELETE_POLICY, resource, session,
                                CoreEvent.STATE_SUCCESSFUL), ((Policy) resource)));
            return resource;
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(new ResourceDeleteEvent(this, CoreEventConstants.DELETE_POLICY, session, e));
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#updateResource(com.adito.boot.policyframework.Resource,
     *      com.adito.security.SessionInfo)
     */
    public void updateResource(Policy resource, SessionInfo session) throws Exception {
        try {
            PolicyDatabaseFactory.getInstance().updatePolicy((Policy) resource);
            CoreEvent coreEvent = addPolicyAttributes(new ResourceChangeEvent(this, CoreEventConstants.UPDATE_POLICY, resource,
                            session, CoreEvent.STATE_SUCCESSFUL), ((Policy) resource));

            CoreServlet.getServlet().fireCoreEvent(coreEvent);

        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(new ResourceChangeEvent(this, CoreEventConstants.UPDATE_POLICY, session, e));
            throw e;
        }
    }

    CoreEvent addPolicyAttributes(CoreEvent evt, Policy shortcut) {
        return evt;
    }

    @Override
    public Policy createResource(Policy resource, SessionInfo session) throws Exception {
        Policy policy = (Policy) resource;
        return PolicyDatabaseFactory.getInstance().createPolicy(policy.getResourceName(), policy.getResourceDescription(),
            policy.getType(), policy.getRealmID());
    }

    @Override
    public Collection<Policy> getResources(SessionInfo session) throws Exception {
        return PolicyDatabaseFactory.getInstance().getPolicies(session.getRealm());
    }
}
