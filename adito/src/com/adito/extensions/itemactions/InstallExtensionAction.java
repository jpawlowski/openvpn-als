/**
 * 
 */
package com.adito.extensions.itemactions;

import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionBundleItem;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;
import com.adito.table.TableItemAction;
import com.adito.tasks.TaskUtil;

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