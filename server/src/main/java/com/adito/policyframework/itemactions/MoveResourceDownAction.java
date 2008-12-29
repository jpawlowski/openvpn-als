
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
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
public class MoveResourceDownAction extends AbstractPathAction {
    /**
     */
    public static final String TABLE_ITEM_ACTION_ID = "moveDown";

    /**
     * Constructor
     * @param navigationContext
     * @param messageResourcesKey
     */
    public MoveResourceDownAction(int navigationContext, String messageResourcesKey) {
        this(navigationContext, messageResourcesKey, "{2}.do?actionTarget=moveDown&selectedResource={0}");
    }

    /**
     * @param navigationContext
     * @param messageResourcesKey
     * @param requiredPath
     */
    public MoveResourceDownAction(int navigationContext, String messageResourcesKey, String requiredPath) {
        super(TABLE_ITEM_ACTION_ID, messageResourcesKey, 185, false, navigationContext, requiredPath);
    }

    /*
     * (non-Javadoc)
     * @see com.adito.table.TableItemAction#isEnabled(com.adito.table.AvailableTableItemAction)
     */
    public boolean isEnabled(AvailableTableItemAction availableItem) {
        try {
            ResourceItem item = (ResourceItem) availableItem.getRowItem();
            Permission[] permissions = new Permission[] { PolicyConstants.PERM_EDIT_AND_ASSIGN };
            ResourceUtil.checkResourceManagementRights(item.getResource(), availableItem.getSessionInfo(), permissions);
            return true;
        } catch (NoPermissionException e) {
            return false;
        }
    }
}