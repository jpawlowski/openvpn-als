/**
 * 
 */
package net.openvpn.als.extensions.itemactions;

import net.openvpn.als.extensions.ExtensionBundleItem;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;

public final class ExtensionInformationAction extends TableItemAction {

    public ExtensionInformationAction() {
        super("extensionInformation", "extensions", 400, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return item.getBundle().getInstructionsURL()!=null && !item.getBundle().getInstructionsURL().equals("") && !item.getSubFormName().equals("updateableExtensionsForm");
    }

    public String getOnClick(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "window.open('" + item.getBundle().getInstructionsURL() + "')";
    }
}