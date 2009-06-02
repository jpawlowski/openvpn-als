
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.table.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.table.forms.AbstractPagerForm;

/**
 * Abstraact class to allow filtering and paging of resources.
 */
public abstract class AbstractPagerAction extends AuthenticatedDispatchAction {

    
    /**
     * Construtor
     */
    public AbstractPagerAction() {
        super();
    }

    /**
     * Construtor
     * 
     * @param resourceType
     * @param permissions
     */
    public AbstractPagerAction(ResourceType resourceType, Permission[] permissions) {
        super(resourceType, permissions);
    }

    /**
     * Constructor
     * 
     * @param resourceType
     * @param permissions
     * @param requiresResources
     */
    public AbstractPagerAction(ResourceType resourceType, Permission[] permissions, ResourceType requiresResources) {
        super(resourceType, permissions, requiresResources);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward filter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        
        if(mapping.getScope().equalsIgnoreCase("request")) {
            // Request scoped actions should just re-filter as if they were loading initially
            // The start row should be reset to 0 incase the filtered item(s) is on a page that is not currently displayed
            ((AbstractPagerForm)form).setStartRow(0);
            return unspecified(mapping, form, request, response);
        }
        else {
            // Where as session scopes just need to rebuild the pager
            // The start row should be reset to 0 incase the filtered item(s) is on a page that is not currently displayed
            ((AbstractPagerForm)form).getPager().firstPage();
            
            ((AbstractPagerForm)form).getPager().rebuild(((AbstractPagerForm)form).getFilterText());
            return mapping.findForward("display");
        }
    }

}
