/**
 * 
 */
package com.ovpnals.extensions.itemactions;

import com.ovpnals.extensions.ExtensionBundle;
import com.ovpnals.extensions.ExtensionBundleItem;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.AvailableTableItemAction;
import com.ovpnals.table.TableItemAction;

public final class RemoveExtensionAction extends TableItemAction {
    public RemoveExtensionAction() {
        super("removeExtension", "extensions", 200, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return !item.getBundle().isDevExtension() && ( item.getBundle().getType() == ExtensionBundle.TYPE_INSTALLED || item.getBundle().getType() == ExtensionBundle.TYPE_UPDATEABLE ) && !item.getSubFormName().equals("updateableExtensionsForm");
    }

    public String getPath(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "/removeExtension.do?id=" + item.getBundle().getId() + "&subForm=" + item.getSubFormName();
    }
}