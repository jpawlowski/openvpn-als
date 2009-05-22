
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.navigation.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreMenuTree;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.DefaultAction;
import com.ovpnals.navigation.NavigationManager;
import com.ovpnals.navigation.forms.HelpForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

/**
 * Action to switch to the user console. All users are allowed to do this.
 */

public class HelpAction extends DefaultAction {
    
    /**
     * Constructor
     */
    public HelpAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.security.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        String source = request.getParameter("source");
        HelpForm helpForm = (HelpForm)form;
        if(source == null) {
            throw new Exception("Help action requires a source parameter.");
        }
        if(source.equalsIgnoreCase("property")) {
            String name = request.getParameter("name");
            if(name == null) {
                throw new Exception("Help for property source requires a name parameter.");
            }
            String propertyClassName = request.getParameter("propertyClass");
            if(propertyClassName == null) {
                throw new Exception("Help for property source requires a propertyClass parameter.");
            }
            PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(propertyClassName);
            if(propertyClass == null) {
                throw new Exception("Invalid property class.");
            } 
            PropertyDefinition def = propertyClass.getDefinition(name);
            if(def == null) {
                throw new Exception("No property definition with name of " + name);
            }
            else{
                // set the label and description from the messages resources if they have any.
                // If there are Application resources for the definition then set the label and description
                String label = CoreUtil.getMessage(request, def.getMessageResourcesKey(), def.getNameMessageResourceKey());
                if (label != null){
                    def.setLabel(label);
                }
//                String description = messageResources == null ? null : messageResources.getMessage(def.getDescriptionMessageResourceKey());
                String description = CoreUtil.getMessage(request, def.getMessageResourcesKey(), def.getDescriptionMessageResourceKey());
                if (description != null){
                    def.setDescription(description);
                }
            }
            request.setAttribute(Constants.REQ_ATTR_PROPERTY_DEFINITION, def);
            return mapping.findForward("property");
        }
        else if(source.equalsIgnoreCase("help")) {
            // load the documentation context menu
            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
            helpForm.setMenu(NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE).rebuildMenus(SessionInfo.HELP_CONTEXT, 
                info,
                request,
                Util.getOriginalRequest(request)));
            return mapping.findForward("help");
        }
        throw new Exception("No source of type " + source);

    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.HELP_CONTEXT;
    }

}