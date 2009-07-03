
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
			
package com.adito.networkplaces.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.boot.Util;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.input.validators.HostnameOrIPAddressWithReplacementsValidator;
import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.NetworkPlaceUtil;
import com.adito.policyframework.Resource;
import com.adito.policyframework.forms.AbstractFavoriteResourceForm;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSProviderManager;

public class NetworkPlaceForm extends AbstractFavoriteResourceForm {
    private static final long serialVersionUID = 3658484546574513284L;
    private static final Log LOG = LogFactory.getLog(NetworkPlaceForm.class);
    private String selectedTab = "details";
    private String host;
    private String path;
    private int port;
    private String username;
    private String password;
    private String scheme;
    private String queryString;
    private boolean networkPlaceReadOnly;
    private boolean showHidden;
    private boolean allowRecursive;
    private boolean noDelete;
    private boolean autoStart;
    private VFSProvider provider;

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            try {
                /* Validate all of the elements */
                if (provider.getHostRequirement() == VFSProvider.ELEMENT_REQUIRED) {
                    String name = Util.trimmedOrBlank(host);
                    if ("".equals(name)) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noHost"));
                    } else {
                        // /* Officialy, the only supported characters in a
                        // hostname
                        // * are [a-z], [0-9], '.' and '-'. In practice though
                        // other
                        // * characters such as '_' are commonly used. Lets just
                        // catch
                        // * the obvious ones for now
                        // * JB Have added a central check using regEX
                        // */
                        //                        
                        if (!HostnameOrIPAddressWithReplacementsValidator.isValidAsHostOrIp(name)) {
                            errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.invalidHost"));
                        }
                    }
                }

                if (provider.getPortRequirement() == VFSProvider.ELEMENT_REQUIRED && port == 0) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noPort"));
                }

                if (provider.getUserInfoRequirement() == VFSProvider.ELEMENT_REQUIRED && Util.isNullOrTrimmedBlank(username)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noUserInfo"));
                }

                if (provider.getPathRequirement() == VFSProvider.ELEMENT_REQUIRED && Util.isNullOrTrimmedBlank(path)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("createNetworkPlace.error.noPath"));
                }
            } catch (Exception ex) {
                LOG.error("Unexpect error.", ex);
                errs.add(Globals.ERROR_KEY, new ActionMessage("vfs.unexpected.error", ex.getMessage()));
            }
        }
        return errs;
    }

    public int getTabCount() {
        return getTabCountWithoutAttribute() + (getCategoryIds().size());
    }
    
    private  int getTabCountWithoutAttribute() {
        return (getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 3 : 2);
    }

	public String getTabName(int idx) {
		if (getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
			switch (idx) {
				case 0:
					return "details";
				case 1:
					return "other";
				case 2:
					return "policies";
    	        default:
        	        return (String) getCategoryIds().get(idx - getTabCountWithoutAttribute());
			}
		} else {
			switch (idx) {
				case 0:
					return "details";
				case 1:
					return "other";
	            default:
    	            return (String) getCategoryIds().get(idx - getTabCountWithoutAttribute());
			}
		}
	}

	public String getTabTitle(int idx) {
		if (getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
			switch (idx) {
				case 0:
					return null;
				case 1:
					return null;
				case 2:
					return null;
    	        default:
        	        return (String) getCategoryTitles().get(idx - getTabCountWithoutAttribute());
			}
		} else {
			switch (idx) {
				case 0:
					return null;
				case 1:
					return null;
	            default:
    	            return (String) getCategoryTitles().get(idx - getTabCountWithoutAttribute());
			}
		}
	}
	
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        NetworkPlace networkPlace = (NetworkPlace) resource;
        provider = VFSProviderManager.getInstance().getProvider(networkPlace.getScheme());

        if (provider == null) {
            networkPlace = NetworkPlaceUtil.createNetworkPlaceForPath(networkPlace.getPath());
            provider = VFSProviderManager.getInstance().getProvider(networkPlace.getScheme());
        }
        this.scheme = networkPlace.getScheme();
        this.host = networkPlace.getHost();
        this.path = networkPlace.getPath();
        this.port = networkPlace.getPort();
        this.username = networkPlace.getUsername();
        this.password = networkPlace.getPassword();
        this.queryString = "";
        this.networkPlaceReadOnly = networkPlace.isReadOnly();
        this.showHidden = networkPlace.isShowHidden();
        this.allowRecursive = networkPlace.isAllowRecursive();
        this.noDelete = networkPlace.isNoDelete();
        this.autoStart = networkPlace.isAutoStart();
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void applyToResource() throws Exception {
        NetworkPlace np = (NetworkPlace) getResource();
        np.setScheme(scheme);
        np.setHost(getHost());
        np.setPath(getPath());
        np.setPort(getPort());
        np.setUsername(getUsername());
        np.setPassword(getPassword());
        np.setScheme(getScheme());
        np.setReadOnly(isNetworkPlaceReadOnly());
        np.setAllowResursive(isAllowRecursive());
        np.setNoDelete(isNoDelete());
        np.setShowHidden(isShowHidden());
        np.setAutoStart(isAutoStart());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAllowRecursive() {
        return allowRecursive;
    }

    public void setAllowRecursive(boolean allowRecursive) {
        this.allowRecursive = allowRecursive;
    }

    public boolean isNoDelete() {
        return noDelete;
    }

    public void setNoDelete(boolean noDelete) {
        this.noDelete = noDelete;
    }

    public boolean isNetworkPlaceReadOnly() {
        return networkPlaceReadOnly;
    }

    public void setNetworkPlaceReadOnly(boolean readOnly) {
        this.networkPlaceReadOnly = readOnly;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public VFSProvider getProvider() {
        return provider;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.showHidden = false;
        this.networkPlaceReadOnly = false;
        this.allowRecursive = false;
        this.noDelete = false;
        this.autoStart = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }

    @Override
    public boolean isResourcePropertyDefinition(Resource resource, PropertyDefinition d) {
        return d.getName().indexOf(((NetworkPlace) resource).getScheme()) != -1;
    }
    
    @Override
    public String getSubCategoryString(Resource resource) {
        return ((NetworkPlace) resource).getScheme();
    }

}