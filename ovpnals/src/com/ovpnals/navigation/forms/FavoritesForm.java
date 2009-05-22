
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
			
package com.ovpnals.navigation.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.navigation.AbstractFavoriteItem;
import com.ovpnals.navigation.Favorite;
import com.ovpnals.navigation.FavoriteResourceType;
import com.ovpnals.navigation.WrappedFavoriteItem;
import com.ovpnals.policyframework.NoPermissionException;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.security.User;
import com.ovpnals.table.AbstractTableItemTableModel;
import com.ovpnals.table.forms.AbstractPagerForm;
import com.ovpnals.util.TicketGenerator;

/**
 * Implementation of a {@link com.ovpnals.table.actions.AbstractPagerAction}
 * that provides a paged list of all the users configured favorites including
 * both <i>User Favorites</i> and <i>Policy Favorites</i>.
 */
public class FavoritesForm extends AbstractPagerForm {

    final static Log log = LogFactory.getLog(FavoritesForm.class);

    private String selectedView;

    /**
     * Constructor
     */
    public FavoritesForm() {
        super(new FavoritesModel());
    }

    /**
     * Initialise the form by loading all the favorites.
     * 
     * @param request request
     * @param defaultSortColumnId default sort column
     */
    public void initialize(HttpServletRequest request, String defaultSortColumnId) {
        super.initialize(request.getSession(), defaultSortColumnId);
        SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
        getModel().clear();
        try {
            User user = LogonControllerFactory.getInstance().getUser(request);
            for (Iterator i = PolicyDatabaseFactory.getInstance().getResourceTypes(null).iterator(); i.hasNext();) {
                ResourceType rt = (ResourceType) i.next();
                if (rt instanceof FavoriteResourceType) {
                    FavoriteResourceType frt = (FavoriteResourceType) rt;
                    try {
                        List fl = SystemDatabaseFactory.getInstance().getFavorites(frt.getResourceTypeId(), user);
                        for (Iterator j = fl.iterator(); j.hasNext();) {
                            Favorite f = (Favorite) j.next();
                            try {
                                WrappedFavoriteItem wfi = frt.createWrappedFavoriteItem(f.getFavoriteKey(), request,
                                                AbstractFavoriteItem.USER_FAVORITE);
                                if (wfi == null) {
                                    SystemDatabaseFactory.getInstance().removeFavorite(f.getType(), f.getFavoriteKey(),
                                                    f.getUsername());
                                }
                                try {
                                    ResourceUtil.checkResourceAccessRights(wfi.getFavoriteItem().getResource(), info);
                                    getModel().addItem(wfi);
                                } catch (NoPermissionException npe) {
                                    // Skip
                                }
                            } catch (Exception e) {
                                log.error("Failed to add user favorite " + f.getFavoriteKey() + ".", e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Failed to create user favorites for resource type " + frt.getResourceTypeId());
                    }
                    try {
                        for (Iterator iter = ResourceUtil.filterResourceIdsForGlobalFavorites(
                                        PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(user, frt), rt).iterator(); iter
                                        .hasNext();) {
                            Integer element = (Integer) iter.next();
                            try {
                                WrappedFavoriteItem wfi = frt.createWrappedFavoriteItem(element.intValue(), request,
                                                AbstractFavoriteItem.GLOBAL_FAVORITE);
                                if (wfi == null) {
                                    // TODO ? sahould we do soemthoing
                                    // here?????????
                                } else {
                                    if (!getModel().contains(wfi)) {
                                        try {
                                            ResourceUtil.checkResourceAccessRights(wfi.getFavoriteItem().getResource(), info);
                                            getModel().addItem(wfi);
                                        } catch (NoPermissionException npe) {
                                            // Skip
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Failed to add policy favorite for resource " + element.intValue() + ".", e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Failed to create policy favorites for resource type " + frt.getResourceTypeId());
                    }

                }
            }
        } catch (Exception e) {
            log.error("Failed to create favorite items.", e);
        }
        checkSort();
        getPager().rebuild(getFilterText());
        /*
         * Store a ticket for use with launching a web folder without
         * authenticating. This ticket will only be available until it is used
         */
        if (request.getSession().getAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET) == null) {
            request.getSession().setAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET,
                            TicketGenerator.getInstance().generateUniqueTicket("W", 6));
        }

    }

    /**
     * Check the selected view
     * 
     * @param request
     * @param response
     */
    public void checkSelectedView(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = (SessionInfo) request.getSession().getAttribute(Constants.SESSION_INFO);
        String defaultView = CoreUtil.getUsersProfileProperty(request.getSession(), "ui.defaultUserConsoleViewType", sessionInfo
                        .getUser());
        selectedView = CoreUtil.getCookieValue("ui_view_" + getModel().getId() + "_"
                        + ((SessionInfo) request.getSession().getAttribute(Constants.SESSION_INFO)).getNavigationContext(),
                        request, defaultView);
    }

    /**
     * Get the selected view. This will be one of
     * {@link com.ovpnals.policyframework.forms.AbstractResourcesForm#ICONS_VIEW}
     * or
     * {@link com.ovpnals.policyframework.forms.AbstractResourcesForm#LIST_VIEW}.
     * 
     * @return selected view
     */
    public String getSelectedView() {
        return selectedView;
    }

    /**
     * Set the selected view. This will be one of
     * {@link com.ovpnals.policyframework.forms.AbstractResourcesForm#ICONS_VIEW}
     * or
     * {@link com.ovpnals.policyframework.forms.AbstractResourcesForm#LIST_VIEW}.
     * 
     * @param selectedView selected view
     */
    public void setSelectedView(String selectedView) {
        this.selectedView = selectedView;
    }

    // Supporting classes

    static class FavoritesModel extends AbstractTableItemTableModel {

        public int getColumnWidth(int col) {
            return 0;
        }

        public String getId() {
            return "favorites";
        }

        public int getColumnCount() {
            return 1;
        }

        public String getColumnName(int col) {
            return "name";
        }

        public Class getColumnClass(int col) {
            return String.class;
        }

    }

}