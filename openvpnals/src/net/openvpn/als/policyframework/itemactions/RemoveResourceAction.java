/**
 * 
 */
package net.openvpn.als.policyframework.itemactions;

import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.ResourceItem;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.table.AvailableTableItemAction;

/**
 */
public class RemoveResourceAction extends AbstractPathAction {
    /**
     */
    public static final String TABLE_ITEM_ACTION_ID = "remove";

    /**
     * @param navigationContext
     * @param messageResourcesKey
     */
    public RemoveResourceAction(int navigationContext, String messageResourcesKey) {
        this(navigationContext, messageResourcesKey, "{2}.do?actionTarget=confirmRemove&selectedResource={0}");
    }

    /**
     * @param navigationContext
     * @param messageResourcesKey
     * @param requiredPath
     */
    public RemoveResourceAction(int navigationContext, String messageResourcesKey, String requiredPath) {
        super(TABLE_ITEM_ACTION_ID, messageResourcesKey, 200, true, navigationContext, requiredPath);
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.table.TableItemAction#isEnabled(net.openvpn.als.table.AvailableTableItemAction)
     */
    public boolean isEnabled(AvailableTableItemAction availableItem) {
        try {
            ResourceItem item = (ResourceItem) availableItem.getRowItem();
            Permission[] permissions = new Permission[] { PolicyConstants.PERM_DELETE, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE };
            ResourceUtil.checkResourceManagementRights(item.getResource(), availableItem.getSessionInfo(), permissions);
            return true;
        } catch (NoPermissionException e) {
            return false;
        }
    }
}