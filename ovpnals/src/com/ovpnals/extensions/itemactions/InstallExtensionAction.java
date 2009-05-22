/**
 * 
 */
package com.ovpnals.extensions.itemactions;

import com.ovpnals.extensions.ExtensionBundle;
import com.ovpnals.extensions.ExtensionBundleItem;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.AvailableTableItemAction;
import com.ovpnals.table.TableItemAction;
import com.ovpnals.tasks.TaskUtil;

public final class InstallExtensionAction extends TableItemAction {

    public InstallExtensionAction() {
        super("installExtension", "extensions", 100, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[]{ PolicyConstants.PERM_CHANGE});
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return item.getBundle().getType() == ExtensionBundle.TYPE_INSTALLABLE && !item.getSubFormName().equals("updateableExtensionsForm");
    }

    @Override
    public String getOnClick(AvailableTableItemAction availableItem) {
        return TaskUtil.getTaskPathOnClick(getPath(availableItem), "extensions", "installExtensions", availableItem.getRequest().getSession(), 440, 100);
    }

    public String getPath(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "/showExtensionStore.do?actionTarget=install&id=" + item.getBundle().getId()+"&version="+item.getBundle().getVersion().toString() + "&subForm=" + item.getSubFormName();
    }
}