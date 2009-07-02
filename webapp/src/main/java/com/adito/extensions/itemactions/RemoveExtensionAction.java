/**
 * 
 */
package com.adito.extensions.itemactions;

import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionBundleItem;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;
import com.adito.table.TableItemAction;

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