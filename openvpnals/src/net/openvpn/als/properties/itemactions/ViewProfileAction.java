/**
 * 
 */
package net.openvpn.als.properties.itemactions;

import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.ResourceItem;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;

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