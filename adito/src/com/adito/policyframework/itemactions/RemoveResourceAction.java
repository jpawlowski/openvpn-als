/**
 * 
 */
package com.adito.policyframework.itemactions;

import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.ResourceItem;
import com.adito.policyframework.ResourceUtil;
import com.adito.table.AvailableTableItemAction;

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
     * @see com.adito.table.TableItemAction#isEnabled(com.adito.table.AvailableTableItemAction)
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