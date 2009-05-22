/**
 * 
 */
package com.ovpnals.properties.itemactions;

import com.ovpnals.policyframework.NoPermissionException;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.ResourceItem;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.properties.PropertyProfile;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.AvailableTableItemAction;
import com.ovpnals.table.TableItemAction;

public final class ViewProfileAction extends TableItemAction {
	public ViewProfileAction() {
		super("view", "properties", 50, false);
	}

	public boolean isEnabled(AvailableTableItemAction availableItem) {
		ResourceItem item = (ResourceItem) availableItem
				.getRowItem();
        try {
            ResourceUtil.checkResourceManagementRights(item.getResource(), availableItem.getSessionInfo(), new Permission[] {  PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,  PolicyConstants.PERM_EDIT_AND_ASSIGN });
            return true;
        }
        catch(NoPermissionException npe) {
        	try {
				ResourceUtil.checkResourceAccessRights(item.getResource(), availableItem.getSessionInfo());
	        	return true;
			} catch (NoPermissionException e) {
			}
        }
		return false;
	}

	public String getPath(AvailableTableItemAction availableItem) {
		ResourceItem item = (ResourceItem) availableItem
				.getRowItem();
		PropertyProfile p = (PropertyProfile)item.getResource();
		return p.getOwnerUsername() != null || ( p.getOwnerUsername() == null && availableItem.getSessionInfo().getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) ? "/showUserProperties.do?selectedPropertyProfile=" + item.getResource().getResourceId() : 
			"/showGlobalProperties.do?selectedPropertyProfile=" + item.getResource().getResourceId();
	}
}