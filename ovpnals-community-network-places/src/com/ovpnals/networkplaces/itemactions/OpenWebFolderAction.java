
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
			
package com.ovpnals.networkplaces.itemactions;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.core.BrowserChecker;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.networkplaces.NetworkPlaceItem;
import com.ovpnals.networkplaces.NetworkPlacePlugin;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceItem;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabase;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.security.WebDAVAuthenticationModule;
import com.ovpnals.table.AvailableTableItemAction;
import com.ovpnals.table.TableItemAction;

public class OpenWebFolderAction extends TableItemAction {

    public static final String TABLE_ITEM_ACTION_ID = "addToFavorites";

    public OpenWebFolderAction(String messageResourcesKey) {
		super("openWebFolder", messageResourcesKey, 100, "_self", false);
	}
	
	public ResourceItem getResourceItem(AvailableTableItemAction availableItem) {
		return (ResourceItem)availableItem.getRowItem();
	}
	

	public boolean isEnabled(AvailableTableItemAction availableItem) {
		ResourceItem item = getResourceItem(availableItem);
		HttpServletRequest request = availableItem.getRequest();
		SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
		try {
		    // this is an extra check to ensure that the webDav authentication scheme is accessible to allow web folders.
		    AuthenticationScheme authenticationSchemeSequence = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence("WebDAV", sessionInfo.getRealm().getRealmID());
		    boolean principalAllowed = PolicyDatabaseFactory.getInstance().isPrincipalAllowed(sessionInfo.getUser(), authenticationSchemeSequence, true);
		    if (principalAllowed){
	            BrowserChecker checker = new BrowserChecker(request.getHeader("user-agent"));
	            if (item.getResource().getResourceType().equals(
	                    NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE)
	                    && CoreUtil
	                            .isAuthenticationModuleInUse(WebDAVAuthenticationModule.MODULE_NAME) 
	                    && ( checker.isBrowserVersionExpression(BrowserChecker.BROWSER_IE, "+=5") || 
	                        ( Property.getPropertyBoolean(
	                            new SystemConfigKey("ui.allowOpenWebFolderInFirefox")) && checker.isBrowserVersion(BrowserChecker.BROWSER_FIREFOX, -1) ) ) ) {
	                return true;
	            }
		    }
		} catch (Exception e) {
		}
		return false;
	}

	public String getPath(AvailableTableItemAction availableItem) {
		NetworkPlaceItem npi = (NetworkPlaceItem)getResourceItem(availableItem);
		return npi.getWebFolderPath(-1, availableItem.getRequest());
	}
	
	public String getAdditionalAttributeName() {
		return "folder";
	}
	
	public String getAdditionalAttributeValue(AvailableTableItemAction availableItem) {
		//return getPath(availableItem) + "?ticket=" + availableItem.getRequest().getSession().getAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET);
        return getPath(availableItem);
	}
}