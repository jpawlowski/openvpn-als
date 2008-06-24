
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
			
package com.adito.policyframework.policies.actions;

import java.util.Calendar;

import com.adito.policyframework.DefaultPolicy;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyItem;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.actions.AbstractResourcesDispatchActionTest;
import com.adito.policyframework.forms.PoliciesForm;
import com.adito.services.ResourceServiceAdapter;

/**
 */
public class PoliciesDispatchActionTest extends AbstractResourcesDispatchActionTest<Policy, PolicyItem> {
    private final ResourceType<Policy> resourceType = PolicyConstants.POLICY_RESOURCE_TYPE;

    /**
     * @throws Exception
     */
    public PoliciesDispatchActionTest() throws Exception {
        super("", "");
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        setRequestPath("/policies");
        setForwardPath(".site.Policies");
        setEditPath("/editPolicy");
        setConfirmDeletePath("/removePolicy");
        setRemovedMessage("policy.deleted.message");
        setActionFormClass(PoliciesForm.class);

        setResourceService(new ResourceServiceAdapter<Policy>(resourceType));
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
}