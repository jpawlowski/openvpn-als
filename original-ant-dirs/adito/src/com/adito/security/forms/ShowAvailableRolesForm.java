
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
			
package com.adito.security.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.adito.security.Role;
import com.adito.security.RoleItem;
import com.adito.security.RoleItemModel;
import com.adito.table.forms.AbstractPagerForm;

/**
 * Implementation of {@link AbstractPagerForm} that is used to display a list of
 * configured <i>Groups</i> (previously known as <i>Roles</i>) and to allow
 * the administrator to create, edit and view (if the underlying user database
 * supports it).
 */
public class ShowAvailableRolesForm extends AbstractPagerForm<RoleItem> {
    /**
     * Constructor.
     */
    public ShowAvailableRolesForm() {
        super(new RoleItemModel());
    }

    /**
     * Initialise the form.
     * 
     * @param roles
     * @param session session
     * @throws Exception on any error
     */
    public void initialize(Role[] roles, HttpSession session) throws Exception {
        initialize(toRoleItems(roles), session);
    }
    
    private Collection<RoleItem> toRoleItems(Role[] roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        Collection<RoleItem> items = new ArrayList<RoleItem>();
        for (Role role : roles) {
            items.add(new RoleItem(role));
        }
        return items;
    }
    
    /**
     * @param session
     * @throws Exception 
     */
    public void reInitialize(HttpSession session) throws Exception {
        List<RoleItem> items = getModel().getItems();
        initialize(items, session);
    }
    
    private void initialize(Collection<RoleItem> roles, HttpSession session) throws Exception {
        super.initialize(session, "role");
        for (RoleItem roleItem : roles) {
            getModel().addItem(roleItem);
        }
        getPager().rebuild(getFilterText());
    }
}