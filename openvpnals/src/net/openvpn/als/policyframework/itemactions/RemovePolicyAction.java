
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
			
package net.openvpn.als.policyframework.itemactions;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.Policy;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.ResourceItem;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.table.AvailableTableItemAction;

public class RemovePolicyAction extends RemoveResourceAction {

    /**
     * @param navigationContext
     * @param messageResourcesKey
     */
    public RemovePolicyAction(int navigationContext, String messageResourcesKey) {
        super(navigationContext, messageResourcesKey);
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        try {
            ResourceItem item = (ResourceItem) availableItem.getRowItem();
            try {
                Realm realm = UserDatabaseManager.getInstance().getRealm(item.getResource().getRealmID());
                if (item.getResource().getResourceId() == PolicyDatabaseFactory.getInstance().getEveryonePolicyIDForRealm(realm)){
                    // it is the Everyone Policy this cannot be deleted.
                    return false;
                }
            } catch (Exception e) {
                // there has been an error so do not display
                return false;
            }
            
            Permission[] permissions = new Permission[] { PolicyConstants.PERM_DELETE };
            ResourceUtil.checkResourceManagementRights(item.getResource(), availableItem.getSessionInfo(), permissions);
            return true;
        } catch (NoPermissionException e) {
            return false;
        }
    }

}
