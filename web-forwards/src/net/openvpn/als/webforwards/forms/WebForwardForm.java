
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
			
package net.openvpn.als.webforwards.forms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.input.MultiSelectSelectionModel;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.forms.AbstractFavoriteResourceForm;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.vfs.webdav.DAVUtilities;
import net.openvpn.als.webforwards.AbstractWebForward;
import net.openvpn.als.webforwards.ReplacementProxyWebForward;
import net.openvpn.als.webforwards.ReverseProxyWebForward;
import net.openvpn.als.webforwards.WebForward;
import net.openvpn.als.webforwards.WebForwardTypes;

/**
 * Form for providing the attributes to be edited and validated.
 */
public class WebForwardForm extends AbstractFavoriteResourceForm {
    public static final String ATTR_NO_AUTHENTICATION = "none";
    public static final String ATTR_FORM_BASED_AUTHENTICATION = "form";
    public static final String ATTR_HTTP_BASED_AUTHENTICATION = "http";
    private String selectedTab = "details";
    private int type;
    private String destinationURL;
    private String category;
    private boolean autoStart;

    // Replacement proxy attributes
    private PropertyList restrictToHosts;
    private String encoding;

    private String authenticationType;

    // Authenticating web forward
    private String authenticationUsername;
    private String authenticationPassword;
    private String preferredAuthenticationScheme;

    // Form based authentication
    private String formType;
    private String formParameters;

    // reverse proxy attribute
    private String paths;
    private boolean activeDNS;
    private String hostHeader;
    private Map customHeaders;
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            if (getResourceName().equalsIgnoreCase("default")
                            && (!getEditing() || (getEditing() && !getResource().getResourceName().equalsIgnoreCase("default")))) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("error.createNetworkPlace.cantUseNameDefault"));
                setResourceName("");
            }
            try {
                if (this.getDestinationURL().indexOf("${") == -1) {
                    // only chek the format if there is no hash on the front as
                    // this indicates no validation.
                    new URL(this.getDestinationURL());
                }
            } catch (MalformedURLException e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage(
                                "webForwardWizard.webForwardSpecificDetails.error.malformedURLException"));
            }

            if (getCategory().trim().equals("")) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("editWebForward.error.noCategory"));
            }

            if (type == WebForward.TYPE_PATH_BASED_REVERSE_PROXY) {
                if (!this.activeDNS && this.hostHeader.equals("")) {
                    if (this.paths != null && this.paths.length() == 0) {
                        errs.add(Globals.ERROR_KEY,
                            new ActionMessage("webForwardWizard.webForwardSpecificDetails.error.needs.path"));
                    }
                }
                if (this.paths != null && this.paths.length() > 0) {
                    String paths = "";
                    StringTokenizer t = new StringTokenizer(this.paths, "\n\r");
                    while (t.hasMoreTokens()) {
                        String path = t.nextToken();
                        path = path.trim();
                        if (!path.startsWith("/"))
                            path = "/" + path;
                        if (path.endsWith("/"))
                            path = DAVUtilities.stripTrailingSlash(path);
                        if (!path.startsWith("/")) {
                            errs.add(Globals.ERROR_KEY, new ActionMessage(
                                            "webForwardWizard.webForwardSpecificDetails.error.invalidPath", path));
                        } else {
                            paths += path + "\n";
                        }
                    }
                    if (errs.size() == 0)
                        this.paths = paths;
                }
            } else if (type == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
                if (this.activeDNS && !this.hostHeader.equals("")) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage(
                                    "webForwardWizard.webForwardSpecificDetails.error.hostBased.bothSelected"));
                }
                if (!this.activeDNS && this.hostHeader.equals("")) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage(
                                    "webForwardWizard.webForwardSpecificDetails.error.hostBased.nonSelected"));
                }
            }
        }
        return errs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        if (type == WebForward.TYPE_TUNNELED_SITE){
            return getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 3 : 2;
        } else{
            return getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 4 : 3;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        if (type == WebForward.TYPE_TUNNELED_SITE) {
            switch (idx) {
                case 0:
                    return "details";
                case 1:
                    return "attributes";
                default:
                    return "policies";
            }
        } else {
            switch (idx) {
                case 0:
                    return "details";
                case 1:
                    return "attributes";
                case 2:
                    return "authentication";
                default:
                    return "policies";
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.forms.AbstractFavoriteResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      net.openvpn.als.policyframework.Resource, boolean,
     *      net.openvpn.als.input.MultiSelectSelectionModel,
     *      net.openvpn.als.boot.PropertyList, net.openvpn.als.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        WebForward webForward = (WebForward) resource;

        this.type = webForward.getType();
        this.destinationURL = webForward.getDestinationURL();
        this.category = webForward.getCategory();
        this.autoStart = webForward.isAutoStart();
        
        if (this.type == WebForward.TYPE_REPLACEMENT_PROXY) {
            ReplacementProxyWebForward spwf = (ReplacementProxyWebForward) webForward;
            this.restrictToHosts = spwf.getRestrictToHosts();
            this.encoding = spwf.getEncoding();
            this.authenticationUsername = spwf.getAuthenticationUsername();
            this.authenticationPassword = spwf.getAuthenticationPassword();
            this.preferredAuthenticationScheme = spwf.getPreferredAuthenticationScheme();
            this.formParameters = spwf.getFormParameters();
            this.formType = spwf.getFormType();
        } else if (this.type == WebForward.TYPE_PATH_BASED_REVERSE_PROXY || this.type == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
            ReverseProxyWebForward rpwf = (ReverseProxyWebForward) webForward;
            this.paths = rpwf.getPaths();
            this.activeDNS = rpwf.getActiveDNS();
            this.customHeaders = rpwf.getCustomHeaders();
            this.authenticationUsername = rpwf.getAuthenticationUsername();
            this.authenticationPassword = rpwf.getAuthenticationPassword();
            this.preferredAuthenticationScheme = rpwf.getPreferredAuthenticationScheme();
            this.hostHeader = rpwf.getHostHeader();
            this.formParameters = rpwf.getFormParameters();
            this.formType = rpwf.getFormType();
            this.encoding = rpwf.getCharset();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.forms.AbstractResourceForm#applyToResource()
     */
    public void applyToResource() throws Exception {
        ((AbstractWebForward) getResource()).setCategory(getCategory());
        ((AbstractWebForward) getResource()).setDestinationURL(getDestinationURL());
        ((AbstractWebForward) getResource()).setAutoStart(isAutoStart());
        if (getType() == WebForward.TYPE_TUNNELED_SITE) {
        } else if (getType() == WebForward.TYPE_REPLACEMENT_PROXY) {
            ((ReplacementProxyWebForward) resource).setAuthenticationUsername(getAuthenticationUsername());
            ((ReplacementProxyWebForward) resource).setAuthenticationPassword(getAuthenticationPassword());
            ((ReplacementProxyWebForward) getResource()).setPreferredAuthenticationScheme(getPreferredAuthenticationScheme());
            ((ReplacementProxyWebForward) getResource()).setEncoding(getEncoding());
            ((ReplacementProxyWebForward) getResource()).setRestrictToHosts(getRestrictToHostsList());
            ((ReplacementProxyWebForward) getResource()).setFormType(getFormType());
            ((ReplacementProxyWebForward) getResource()).setFormParameters(getFormParameters());
        } else if (getType() == WebForward.TYPE_PATH_BASED_REVERSE_PROXY || getType() == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
            ((ReverseProxyWebForward) resource).setAuthenticationUsername(getAuthenticationUsername());
            ((ReverseProxyWebForward) resource).setAuthenticationPassword(getAuthenticationPassword());
            ((ReverseProxyWebForward) resource).setPreferredAuthenticationScheme(getPreferredAuthenticationScheme());
            ((ReverseProxyWebForward) resource).setPaths(getPaths());
            ((ReverseProxyWebForward) resource).setHostHeader(getHostHeader());
            ((ReverseProxyWebForward) resource).setActiveDNS(isActiveDNS());
            ((ReverseProxyWebForward) resource).setFormType(getFormType());
            ((ReverseProxyWebForward) resource).setFormParameters(getFormParameters());
            ((ReverseProxyWebForward) resource).setCharset(getEncoding());
        }
    }

    /**
     * @return The Web Forward Category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The Web Forward Category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * @return The destination URL.
     */
    public String getDestinationURL() {
        return destinationURL;
    }

    /**
     * @param destinationURL The destination URL.
     */
    public void setDestinationURL(String destinationURL) {
        this.destinationURL = destinationURL;
    }

    public boolean isPathBased() {
        return WebForward.TYPE_PATH_BASED_REVERSE_PROXY == type;
    }

    public boolean isHostBased() {
        return WebForward.TYPE_HOST_BASED_REVERSE_PROXY == type;
    }

    /**
     * @return The type of web forward.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type The type of web forward.
     */
    public void setType(int type) {
        this.type = type;
    }

    public boolean isActiveDNS() {
        return activeDNS;
    }

    public void setActiveDNS(boolean activeDNS) {
        this.activeDNS = activeDNS;
    }

    public String getAuthenticationType() {
        if (authenticationType == null) {
            if (!isEmpty(authenticationUsername) || !isEmpty(authenticationPassword)) {
                return ATTR_HTTP_BASED_AUTHENTICATION;
            } else if (!isEmpty(formParameters)) {
                return ATTR_FORM_BASED_AUTHENTICATION;
            } else {
                return ATTR_NO_AUTHENTICATION;
            }
        }
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationPassword() {
        return authenticationPassword;
    }

    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }

    public String getAuthenticationUsername() {
        return authenticationUsername;
    }

    public void setAuthenticationUsername(String authenticationUsername) {
        this.authenticationUsername = authenticationUsername;
    }

    public Map getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map customHeaders) {
        this.customHeaders = customHeaders;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getPreferredAuthenticationScheme() {
        return preferredAuthenticationScheme;
    }

    public void setPreferredAuthenticationScheme(String preferredAuthenticationScheme) {
        this.preferredAuthenticationScheme = preferredAuthenticationScheme;
    }

    public String getRestrictToHosts() {
        return restrictToHosts.getAsTextFieldText();
    }

    public PropertyList getRestrictToHostsList() {
        return restrictToHosts;
    }

    public void setRestrictToHosts(String restrictToHosts) {
        this.restrictToHosts.setAsTextFieldText(restrictToHosts);
    }

    public List getPreferredAuthenticationSchemeList() {
        return WebForwardTypes.PREFERED_SCHEMES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int i) {
        return null;
    }

    public String getHostHeader() {
        return hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.activeDNS = false;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(String formParameters) {
        this.formParameters = formParameters;
    }

    public List getFormTypeList() {
        return WebForwardTypes.FORM_SUBMIT_TYPES;
    }

    public List getEncodeingTypeList() {
        return WebForwardTypes.ENCODING_TYPES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }
}