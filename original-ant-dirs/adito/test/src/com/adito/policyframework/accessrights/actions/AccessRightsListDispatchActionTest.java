
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
			
package com.adito.policyframework.accessrights.actions;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.adito.policyframework.AccessRight;
import com.adito.policyframework.AccessRights;
import com.adito.policyframework.AccessRightsItem;
import com.adito.policyframework.DefaultAccessRights;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.actions.AbstractResourcesDispatchActionTest;
import com.adito.policyframework.forms.AccessRightsListForm;
import com.adito.services.ResourceServiceAdapter;

/**
 */
public class AccessRightsListDispatchActionTest extends AbstractResourcesDispatchActionTest<AccessRights, AccessRightsItem> {
    private final ResourceType<AccessRights> resourceType = PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE;

    /**
     * @throws Exception
     */
    public AccessRightsListDispatchActionTest() throws Exception {
        super("", "");
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        setRequestPath("/accessRightsList");
        setForwardPath(".site.AccessRightsList");
        setEditPath("/editAccessRights");
        setConfirmDeletePath("/confirmRemoveAccessRights");
        setRemovedMessage("access.rights.deleted.message");
        setActionFormClass(AccessRightsListForm.class);
        
        setResourceService(new ResourceServiceAdapter<AccessRights>(resourceType));
    }

    @Override
    protected AccessRights getDefaultResource(int selectedRealmId) {
        AccessRight accessRight = new AccessRight(resourceType, new Permission(1, "policyFramework"));
        List<AccessRight> accessRightsList = Collections.singletonList(accessRight);
        return new DefaultAccessRights(selectedRealmId, -1, "MyNewAccessRight", "A test access right.", accessRightsList,
                        PolicyConstants.PERSONAL_CLASS, Calendar.getInstance(), Calendar.getInstance());
    }

    @Override
    protected int getInitialResourceCount() {
        return 1;
    }

    @Override
    public String getViewPath() {
        return "/viewAccessRights";
    }
}