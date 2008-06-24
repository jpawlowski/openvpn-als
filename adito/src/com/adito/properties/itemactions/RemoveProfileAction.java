/**
 * 
 */
package com.adito.properties.itemactions;

import com.adito.policyframework.ResourceItem;
import com.adito.policyframework.itemactions.RemoveResourceAction;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;

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