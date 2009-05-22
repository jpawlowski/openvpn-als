/**
 * 
 */
package com.ovpnals.navigation.itemactions;

import java.text.MessageFormat;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.navigation.AbstractFavoriteItem;
import com.ovpnals.navigation.WrappedFavoriteItem;
import com.ovpnals.policyframework.itemactions.AbstractPathAction;
import com.ovpnals.security.Constants;
import com.ovpnals.table.AvailableTableItemAction;

public final class RemoveFavoriteAction extends AbstractPathAction {
	public RemoveFavoriteAction() {
		super("remove", "navigation", 100, true, "{2}.do?actionTarget=confirmRemove&selectedItem={0}");
	}

	public boolean isEnabled(AvailableTableItemAction availableItem) {
		WrappedFavoriteItem item = (WrappedFavoriteItem) availableItem.getRowItem();
		return !item.getFavoriteType().equals(AbstractFavoriteItem.GLOBAL_FAVORITE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.table.TableItemAction#getPath(com.ovpnals.table.AvailableTableItemAction)
	 */
	public String getPath(AvailableTableItemAction availableItem) {
		WrappedFavoriteItem item = (WrappedFavoriteItem) availableItem.getRowItem();
		return MessageFormat.format(requiredPath, new Object[] { item.getFavoriteItem()
						.getResource()
						.getResourceType()
						.getResourceTypeId() + "_"
			+ item.getFavoriteItem().getResource().getResourceId(),
			((ActionMapping) availableItem.getRequest().getAttribute(Constants.REQ_ATTR_ACTION_MAPPING)).getName(),
			((ActionMapping) availableItem.getRequest().getAttribute(Constants.REQ_ATTR_ACTION_MAPPING)).getPath() });
	}
}