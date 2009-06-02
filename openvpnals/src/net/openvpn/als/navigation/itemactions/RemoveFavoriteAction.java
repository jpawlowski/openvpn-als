/**
 * 
 */
package net.openvpn.als.navigation.itemactions;

import java.text.MessageFormat;

import org.apache.struts.action.ActionMapping;

import net.openvpn.als.navigation.AbstractFavoriteItem;
import net.openvpn.als.navigation.WrappedFavoriteItem;
import net.openvpn.als.policyframework.itemactions.AbstractPathAction;
import net.openvpn.als.security.Constants;
import net.openvpn.als.table.AvailableTableItemAction;

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
	 * @see net.openvpn.als.table.TableItemAction#getPath(net.openvpn.als.table.AvailableTableItemAction)
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