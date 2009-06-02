/**
 * 
 */
package net.openvpn.als.properties.itemactions;

import net.openvpn.als.policyframework.ResourceItem;
import net.openvpn.als.policyframework.itemactions.RemoveResourceAction;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;

public final class RemoveProfileAction extends RemoveResourceAction {
	public RemoveProfileAction() {
		super(SessionInfo.ALL_CONTEXTS, "properties");
	}

	public boolean isEnabled(AvailableTableItemAction availableItem) {
		return super.isEnabled(availableItem)
				&& ((ResourceItem) availableItem.getRowItem()).getResource()
						.getResourceId() != 0;
	}
}