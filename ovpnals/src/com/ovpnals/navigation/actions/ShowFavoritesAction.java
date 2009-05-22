
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

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.navigation.FavoriteResourceType;
import com.ovpnals.navigation.forms.FavoritesForm;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.forms.AbstractResourcesForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.table.TableItemModel;
import com.ovpnals.table.actions.AbstractPagerAction;

/**
 * Implenetation of an
 * {@link com.ovpnals.core.actions.AuthenticatedDispatchAction} that lists
 * the users current favorites.
 */
public class ShowFavoritesAction extends AbstractPagerAction {

    /**
     * Constructor
     */
    public ShowFavoritesAction() {
        super();
    }

    /**
     * Default dispatch method.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        Util.noCache(response);
        ((FavoritesForm) form).initialize(request, "name");
        ((FavoritesForm) form).checkSelectedView(request, response);
        return mapping.findForward("display");
    }

    /**
     * Confirm removal of favorite
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return mapping.findForward("confirmRemove");
    }

    /**
     * Remove selected favorite
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = ((FavoritesForm) form).getSelectedItem();
        int idx = id.indexOf('_');
        int type = Integer.parseInt(id.substring(0, idx));
        int key = Integer.parseInt(id.substring(idx + 1));
        ResourceType ty = PolicyDatabaseFactory.getInstance().getResourceType(type);
        if(ty != null && ty instanceof FavoriteResourceType) {
            FavoriteResourceType frt = (FavoriteResourceType)ty;
            Resource r = frt.getResourceById(key);            
            if(ResourceUtil.isResourceGlobalFavorite(r)) {
                throw new Exception("Cannot remove policy favorites here.");
            }
            else {
                ResourceUtil.checkResourceAccessRights(r, getSessionInfo(request));
            }
            SystemDatabaseFactory.getInstance().removeFavorite(type, key, getSessionInfo(request).getUser().getPrincipalName());
        }
        return mapping.findForward("refresh");
    }

    /**
     * Show information about the resource
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward information(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = ((FavoritesForm) form).getSelectedItem();
        int idx = id.indexOf('_');
        int type = Integer.parseInt(id.substring(0, idx));
        int key = Integer.parseInt(id.substring(idx + 1));
        ResourceType ty = PolicyDatabaseFactory.getInstance().getResourceType(type);
        if(ty != null && ty instanceof FavoriteResourceType) {
            FavoriteResourceType frt = (FavoriteResourceType)ty;
            Resource r = frt.getResourceById(key);
            request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, r);
        }
        return mapping.findForward("resourceInformation");
    }

    /**
     * Change the selected view to
     * {@link AbstractResourcesForm#ICONS_VIEW}.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward viewIcons(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ((FavoritesForm) form).setSelectedView(AbstractResourcesForm.ICONS_VIEW);
        TableItemModel model = ((FavoritesForm) form).getModel();
        CoreUtil.storeUIState("ui_view_" + model.getId() + "_" + getSessionInfo(request).getNavigationContext(), AbstractResourcesForm.ICONS_VIEW, request, response);
        return mapping.findForward("display");
    }

    /**
     * Change the selected view to
     * {@link AbstractResourcesForm#LIST_VIEW}.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward viewList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ((FavoritesForm) form).setSelectedView(AbstractResourcesForm.LIST_VIEW);
        TableItemModel model = ((FavoritesForm) form).getModel();
        CoreUtil.storeUIState("ui_view_" + model.getId() + "_" + getSessionInfo(request).getNavigationContext(), AbstractResourcesForm.LIST_VIEW, request, response);
        return mapping.findForward("display");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT;
    }
}
