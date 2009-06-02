/**
 * 
 */
package net.openvpn.als.extensions.itemactions;

import net.openvpn.als.extensions.ExtensionBundle;
import net.openvpn.als.extensions.ExtensionBundleItem;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;

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