
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
			
package com.adito.applications;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.navigation.RequiresSessionPasswordAbstractFavoriteItem;

/**
 * Wrapper for {@link com.adito.applications.ApplicationShortcut} instances.
 */

public class ApplicationShortcutItem extends RequiresSessionPasswordAbstractFavoriteItem {

    // Private instance variables
    private ExtensionDescriptor application;
    private int navigationContext;

    /**
     * Constructor
     * 
     * @param application application extension descriptor
     * @param resource application shortcut resource
     * @param policies policies attached to resource
     * @param navigationContext navigation context
     * @param requiresSessionPassword  requires session password
     */
    public ApplicationShortcutItem(ExtensionDescriptor application,ApplicationShortcut resource, List policies, int navigationContext, boolean requiresSessionPassword) {
        super(resource, policies, requiresSessionPassword);
        this.application = application;
        this.navigationContext = navigationContext;
    }

    /**
     * Get the extension descriptor of the application this shortcut requires
     * 
     * @return extension description
     */
    public ExtensionDescriptor getExtensionDescriptor() {
        return application;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteItem#getLink(java.lang.String)
     */
    public String getLink(int policy, String referer, HttpServletRequest request) {
        return "/launchApplication.do?resourceId=" + getResource().getResourceId() + "&policy=" + policy +
        "&returnTo=" + Util.urlEncode(Util.isNullOrTrimmedBlank(referer) ? CoreUtil.getRealRequestURI(request) : referer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.AbstractFavoriteItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        if (application.getSmallIcon() == null) {
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runApplication.gif";
        } else {
            return "/fs/apps/" + application.getApplicationBundle().getId() + "/" + application.getSmallIcon();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.AbstractFavoriteItem#getLargeIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconPath(HttpServletRequest request) {
        if (application.getSmallIcon() == null) {
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runApplicationLarge.gif";
        } else {
            return "/fs/apps/" + application.getApplicationBundle().getId() + "/" + application.getLargeIcon();
        }
    }

    /**
     * Get all of the parameters as encoded URL parameter string
     * 
     * @return parameters as encoded URL parameter string
     */
    public String getParameterString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = ((ApplicationShortcut) getResource()).getParameters().entrySet().iterator(); i.hasNext();) {
            if (buf.length() > 0) {
                buf.append("&");
            }
            Map.Entry entry = (Map.Entry) i.next();
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(Util.urlEncode(String.valueOf(entry.getValue())));
        }
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteItem#getName()
     */
    public String getFavoriteName() {
        return getResource().getResourceName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.AbstractFavoriteItem#getFavoriteSubType()
     */
    public String getFavoriteSubType() {
        return getExtensionDescriptor().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteItem#getTarget()
     */
    public String getTarget() {
        return "_self";
    }
    
	/* (non-Javadoc)
	 * @see com.adito.navigation.AbstractFavoriteItem#getOnClick(int, javax.servlet.http.HttpServletRequest)
	 */
	public String getOnClick(int policy, HttpServletRequest request) {
		return "";
	}
}