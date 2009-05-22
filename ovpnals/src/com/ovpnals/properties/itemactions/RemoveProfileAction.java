/**
 * 
 */
package com.ovpnals.properties.itemactions;

import com.ovpnals.policyframework.ResourceItem;
import com.ovpnals.policyframework.itemactions.RemoveResourceAction;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.AvailableTableItemAction;

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