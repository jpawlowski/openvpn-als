
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
			
package net.openvpn.als.properties.actions;

import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.actions.AbstractResourcesDispatchActionTest;
import net.openvpn.als.properties.DefaultPropertyProfile;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.properties.forms.ProfilesForm;
import net.openvpn.als.properties.forms.PropertyProfileItem;
import net.openvpn.als.services.ResourceServiceAdapter;

/**
 */
public class ShowProfilesDispatchActionTest extends AbstractResourcesDispatchActionTest<PropertyProfile, PropertyProfileItem> {
    
    /**
     * @throws Exception
     */
    public ShowProfilesDispatchActionTest() throws Exception {
        super("", "");
    }

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        setRequestPath("/showGlobalProfiles");
        setForwardPath(".site.GlobalProfiles");
        setEditPath("/editGlobalPropertyProfile");
        setConfirmDeletePath("/confirmRemoveGlobalPropertyProfile");
        setRemovedMessage("message.profileDeleted");
        setActionFormClass(ProfilesForm.class);

        setResourceService(new ResourceServiceAdapter<PropertyProfile>(PolicyConstants.PROFILE_RESOURCE_TYPE));
    }

    @Override
    protected PropertyProfile getDefaultResource(int selectedRealmId) {
        return new DefaultPropertyProfile(selectedRealmId, null, "resourceName", "resourceDescription");
    }

    @Override
    protected int getInitialResourceCount() {
        return 1;
    }
}
