
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
			
package com.adito.security.itemactions;

import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.AuthenticationSchemeSequenceItem;
import com.adito.security.SessionInfo;
import com.adito.table.AvailableTableItemAction;
import com.adito.table.TableItemAction;

/**
 */
public class MoveAuthenticationSchemeUpAction extends TableItemAction {

    /**
     * Default constructor
     */
    public MoveAuthenticationSchemeUpAction() {
        super("moveUp", "security", 500, "", true, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_EDIT_AND_ASSIGN });
    }
    
    @Override
    public String getPath(AvailableTableItemAction availableItem) {
        AuthenticationSchemeSequenceItem item = (AuthenticationSchemeSequenceItem)availableItem.getRowItem();
        return "/showAuthenticationSchemes.do?actionTarget=moveUp&selectedResource=" + item.getResource().getResourceId();
    }

    @Override
    public boolean isEnabled(AvailableTableItemAction availableItem) {
        AuthenticationSchemeSequenceItem item = (AuthenticationSchemeSequenceItem)availableItem.getRowItem();
        return item.isCanMoveUp();
    }
}
