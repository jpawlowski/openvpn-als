
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.policyframework.accessrights.actions;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.ovpnals.policyframework.AccessRight;
import com.ovpnals.policyframework.AccessRights;
import com.ovpnals.policyframework.DefaultAccessRights;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.actions.AbstractRealmAwareResourceDispatchActionTest;
import com.ovpnals.policyframework.forms.AbstractResourceForm;
import com.ovpnals.policyframework.forms.AccessRightsForm;
import com.ovpnals.services.ResourceServiceAdapter;
import com.ovpnals.testcontainer.StrutsExecutionStep;

/**
 */
public class AccessRightsDispatchActionTest extends AbstractRealmAwareResourceDispatchActionTest<AccessRights> {
    private final ResourceType<AccessRights> resourceType = PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE;

    /**
     */
    public AccessRightsDispatchActionTest() {
        super("", "");
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        setInitialRequestPath("/accessRightsList");
        setRequestPath("/editAccessRights");
        setForwardPath(".site.EditAccessRights");
        setSavedMessage("editAccessRights.message.saved");
        setActionFormClass(AccessRightsForm.class);
        setResourceService(new ResourceServiceAdapter<AccessRights>(resourceType));
    }
    
    /**
     * This is implemented to ignore this test method.
     */
    @Override
    public void testCreateResourceNotImplemented() {
    }
    
    @Override
    protected void updateResourceProperties(AbstractResourceForm<AccessRights> resourceForm) throws Exception {
        AccessRightsForm accessRightsForm = (AccessRightsForm) resourceForm;
        AccessRights resource = accessRightsForm.getResource();
        resource.addAccessRight(new AccessRight(resourceType, new Permission(PolicyConstants.PERM_EDIT_AND_ASSIGN_ID, "policyframework")));
        resource.addAccessRight(new AccessRight(resourceType, new Permission(PolicyConstants.PERM_DELETE_ID, "policyframework")));
    }

    protected void updateInvalidResourceProperties(AbstractResourceForm<AccessRights> resourceForm) throws Exception {
        AccessRightsForm accessRightsForm = (AccessRightsForm) resourceForm;
        AccessRights resource = accessRightsForm.getResource();
        resource.addAccessRight(new AccessRight(resourceType, new Permission(PolicyConstants.PERM_EDIT_ID, "policyframework")));
    }

    @Override
    protected void assertRealmAwareResourceEquals(AccessRights original, AccessRights updated) {
        assertEquals("Permissions are correct", original.getAccessRights(), updated.getAccessRights());
    }

    @Override
    protected AccessRights getDefaultResource(int selectedRealmId) {
        AccessRight accessRight = new AccessRight(resourceType, new Permission(PolicyConstants.PERM_CREATE_AND_ASSIGN_ID, "policyframework"));
        List<AccessRight> accessRightsList = Collections.singletonList(accessRight);
        return new DefaultAccessRights(selectedRealmId, -1, "MyNewAccessRight", "A test access right.", accessRightsList,
                        PolicyConstants.PERSONAL_CLASS, Calendar.getInstance(), Calendar.getInstance());
    }

    @Override
    protected int getInitialResourceCount() {
        return 1;
    }
    
    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testEditCommitResourceInvalidPermission() throws Exception {
        AccessRights resource = executeEditRedirect();

        try {
            AccessRightsForm resourceForm = (AccessRightsForm) getActionForm();
            updateInvalidResourceProperties(resourceForm);

            StrutsExecutionStep executionStep = new StrutsExecutionStep(getRequestPath(), getInitialRequestPath());
            executionStep.addRequestParameter("actionTarget", "commit");
            executionStep.addMessage(getSavedMessage());
            executeStep(executionStep);
            
            AccessRights byId = getResourceById(resource.getResourceId());
            assertEquals(resourceForm.getResource(), byId);
            assertNotSame("Invalid permission added should not match", resource.getAccessRights(), byId.getAccessRights());
        } finally {
            deleteResource(resource);
        }
    }
}