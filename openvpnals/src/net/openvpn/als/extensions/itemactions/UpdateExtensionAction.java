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
import net.openvpn.als.tasks.TaskUtil;

public final class UpdateExtensionAction extends TableItemAction {
    
    public UpdateExtensionAction() {
        super("updateExtension", "extensions", 300, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[]{ PolicyConstants.PERM_CHANGE});
    }

    public boolean isEnabled(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return !item.getBundle().isDevExtension() && item.getBundle().isUpdateable() && item.getSubFormName().equals("updateableExtensionsForm");
    }

    @Override
    public String getOnClick(AvailableTableItemAction availableItem) {
        return TaskUtil.getTaskPathOnClick(getPath(availableItem), "extensions", "updateExtension", availableItem.getRequest().getSession(), 440, 100);
    }

    public String getPath(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "/showExtensionStore.do?actionTarget=update&id=" + item.getBundle().getId()+"&version="+ item.getVersion().toString() + "&subForm=" + item.getSubFormName();
    }
}