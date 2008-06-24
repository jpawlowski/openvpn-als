
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
			
package com.adito.extensions.itemactions;

import com.adito.extensions.ExtensionBundleItem;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;
import com.adito.table.TableItemAction;

/**
 */
public class StopExtensionAction extends TableItemAction {

    /**
     * Default constructor
     */
    public StopExtensionAction() {
        super("stop", "extensions", 200, "", false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }
    
    @Override
    public String getPath(AvailableTableItemAction availableItem) {
        ExtensionBundleItem item = (ExtensionBundleItem)availableItem.getRowItem();
        return "/showExtensionStore.do?actionTarget=stop&id=" + item.getBundle().getId() + "&subForm=" + item.getSubFormName();
    }

    @Override
    public boolean isEnabled(AvailableTableItemAction availableItem) {
    	// TODO Not yet 
//        ExtensionBundleItem item = (ExtensionBundleItem) availableItem.getRowItem();
//        return item.getBundle().canStop();
    	return false;
    }
}
