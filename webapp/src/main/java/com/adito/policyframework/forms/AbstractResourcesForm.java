
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
			
package com.adito.policyframework.forms;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.CoreUtil;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceItem;
import com.adito.policyframework.ResourceItemModel;
import com.adito.properties.Property;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.table.TableItemModel;
import com.adito.table.forms.AbstractPagerForm;

/**
 */
public class AbstractResourcesForm<T extends ResourceItem> extends AbstractPagerForm {

    /**
     * Constant used for the <i>Icons</i> view of resources
     */
    public final static String ICONS_VIEW = "icons";

    /**
     * Constant used for the <i>List</i> view of resources
     */
    public final static String LIST_VIEW = "list";

    protected int selectedResource;
    protected int launchedPolicy;
    protected String selectedView;

    static Log log = LogFactory.getLog(AbstractResourcesForm.class);

    public AbstractResourcesForm(String id) {
        super(new ResourceItemModel(id));
    }

    public AbstractResourcesForm(TableItemModel model) {
        super(model);
    }

    public void setSelectedResource(int selectedResource) {
        this.selectedResource = selectedResource;
    }

    public int getSelectedResource() {
        return selectedResource;
    }

    public void setLaunchedPolicy(int launchedPolicy) {
        this.launchedPolicy = launchedPolicy;
    }

    public int getLaunchedPolicy() {
        return launchedPolicy;
    }

    public void initialize(List resources, Class resourceClass, Class resourceItemClass, HttpSession session, String defaultSortColumnId) {
        super.initialize(session, defaultSortColumnId);
        launchedPolicy = -1;
        try {
            for (Iterator i = resources.iterator(); i.hasNext();) {
                Resource dr = (Resource) i.next();
                Constructor c = resourceItemClass.getConstructor(new Class[] { resourceClass, List.class });
                List policies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(dr,
                                LogonControllerFactory.getInstance().getSessionInfo(session).getUser().getRealm());
                T item = (T) c.newInstance(new Object[] { dr, policies });
                getModel().addItem(item);
            }
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            log.error("Failed to initialise resources form.", t);
        }
    }
    
    public void checkSelectedView(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = (SessionInfo)request.getSession().getAttribute(Constants.SESSION_INFO);
        int realmID = sessionInfo.getRealm().getRealmID();
        int navigationContext = sessionInfo.getNavigationContext();
        String defaultView =  navigationContext == 
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT   
                    ? Property.getProperty(new SystemConfigKey("ui.defaultManagementConsoleViewType")) 
                    : Property.getProperty(new ProfilePropertyKey("ui.defaultUserConsoleViewType", sessionInfo));
        if(navigationContext == SessionInfo.USER_CONSOLE_CONTEXT) {
            selectedView = CoreUtil.getCookieValue("ui_view_" + getModel().getId() + "_" + navigationContext, request, defaultView);
            
            if (selectedView == null){
                selectedView = Property.getProperty(new ProfilePropertyKey(CoreUtil.getCurrentPropertyProfileId(request.getSession()), 
                                sessionInfo.getUser().getPrincipalName(), 
                                "ui.defaultUserConsoleViewType", realmID));
                
                CoreUtil.storeUIState("ui_view_" + getModel().getId() + "_" + navigationContext, defaultView, request, response);
            }

        }
        else{
            selectedView = defaultView;
        }
    }

    /**
     * Get the selected view. This will be one of {@link #ICONS_VIEW} or
     * {@link #LIST_VIEW}.
     * 
     * @return selected view
     */
    public String getSelectedView() {
        return selectedView;
    }

    /**
     * Set the selected view. This will be one of {@link #ICONS_VIEW} or
     * {@link #LIST_VIEW}.
     * 
     * @param selectedView selected view
     */
    public void setSelectedView(String selectedView) {
        this.selectedView = selectedView;
    }

}