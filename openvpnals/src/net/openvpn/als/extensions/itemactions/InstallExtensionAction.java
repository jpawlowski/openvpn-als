/**
 * 
 */
package net.openvpn.als.extensions.itemactions;

import net.openvpn.als.extensions.ExtensionBundle;
import net.openvpn.als.extensions.ExtensionBundleItem;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;
import net.openvpn.als.tasks.TaskUtil;

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