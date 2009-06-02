
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
			
package net.openvpn.als.policyframework.policies.actions;

import java.util.Calendar;

import net.openvpn.als.policyframework.DefaultPolicy;
import net.openvpn.als.policyframework.Policy;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.policyframework.actions.AbstractRealmAwareResourceDispatchActionTest;
import net.openvpn.als.policyframework.forms.AbstractResourceForm;
import net.openvpn.als.policyframework.forms.PolicyForm;
import net.openvpn.als.services.ResourceServiceAdapter;

/**
 */
public class ShowPolicyDispatchActionTest extends AbstractRealmAwareResourceDispatchActionTest<Policy> {
    private final ResourceType<Policy> resourceType = PolicyConstants.POLICY_RESOURCE_TYPE;

    /**
     */
    public ShowPolicyDispatchActionTest() {
        super("", "");
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        setInitialRequestPath("/policies");
        setRequestPath("/editPolicy");
        setForwardPath(".site.EditPolicy");
        setSavedMessage("editPolicy.message.saved");
        setActionFormClass(PolicyForm.class);
        setResourceService(new ResourceServiceAdapter<Policy>(resourceType));
    }
    
    /**
     * This is implemented to ignore this test method.
     */
    @Override
    public void testCreateResourceNotImplemented() {
    }
    
    @Override
    protected void updateResourceProperties(AbstractResourceForm<Policy> resourceForm) throws Exception {
//        PolicyForm policyForm = (PolicyForm) resourceForm;
//        PropertyList selectedAccountsList = policyForm.getSelectedAccountsList();
//        PropertyList selectedGroupsList = policyForm.getSelectedRolesList();
        
    }

    @Override
    protected void assertRealmAwareResourceEquals(Policy original, Policy updated) {
        assertEquals("Users are correct", original.getAttachedUsers(), updated.getAttachedUsers());
        assertEquals("Groups are correct", original.getAttachedGroups(), updated.getAttachedGroups());
    }

    @Override
    protected Policy getDefaultResource(int selectedRealmId) {
        return new DefaultPolicy(-1, "MyNewPolicy", "A test policy.", Policy.TYPE_NORMAL, Calendar.getInstance(), Calendar
                        .getInstance(), selectedRealmId);
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
//        AccessRights resource = executeEditRedirect();
//
//        try {
//            AccessRightsForm resourceForm = (AccessRightsForm) getActionForm();
//            updateInvalidResourceProperties(resourceForm);
//
//            setRequestPathInfo(getRequestPath());
//            addRequestParameter("actionTarget", "commit");
//            actionPerform();
//            verifyNoActionErrors();
//            verifyActionMessages(new String[] { getSavedMessage() });
//            assertEquals("Forward should match", getInitialRequestPath(), toStrippedUrl(getActualForward()));
//
//            AccessRights byId = getResourceById(resource.getResourceId());
//            assertEquals(resourceForm.getResource(), byId);
//            assertNotSame("Invalid permission added should not match", resource.getAccessRights(), byId.getAccessRights());
//        } finally {
//            deleteResource(resource);
//        }
    }
}