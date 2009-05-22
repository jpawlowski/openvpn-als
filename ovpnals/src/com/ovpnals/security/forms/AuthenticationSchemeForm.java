
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
			
package com.ovpnals.security.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.input.MultiSelectSelectionModel;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.forms.AbstractResourceForm;
import com.ovpnals.security.AuthenticationModuleDefinition;
import com.ovpnals.security.AuthenticationModuleManager;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.DefaultAuthenticationScheme;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;

/**
 * Implementation of a
 * {@link com.ovpnals.policyframework.forms.AbstractResourceForm} that
 * allows an administrator to edit an <i>Authentication Scheme</i>.
 * 
 * @see com.ovpnals.security.AuthenticationScheme
 */
public class AuthenticationSchemeForm extends AbstractResourceForm {
    protected String selectedTab = "details";
    protected MultiSelectSelectionModel moduleModel;
    protected PropertyList selectedModules;
    protected boolean isSystem = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            PropertyList l = getSelectedModulesList();
            if (l.size() < 1) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("editAuthenticationScheme.error.noModulesSelected"));
            } else {
                AuthenticationModuleDefinition def = AuthenticationModuleManager.getInstance().getModuleDefinition(
                    l.get(0).toString());
                if (l.size() == 1){
                    if (!def.getPrimary()) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editAuthenticationScheme.error.firstModuleNotPrimary"));
                    }
                }
                else{
                    if (!def.getPrimary() && !def.getPrimaryIfSecondardExists()) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("editAuthenticationScheme.error.firstModuleNotPrimary"));
                    }
                }
            }

            try {
                List granted = ResourceUtil.getGrantedResource(LogonControllerFactory.getInstance().getSessionInfo(request),
                    PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
                // does the user have any other signonable authentication
                // schemes?
                boolean found = false;
                SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
                for (Iterator iter = granted.iterator(); iter.hasNext();) {
                    AuthenticationScheme element = (DefaultAuthenticationScheme) iter.next();
                    if (element.getEnabled() && !element.isSystemScheme() && element.getResourceId() != this.getResourceId()) {

                        for (Iterator iterator = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(element,
                            info.getUser().getRealm()).iterator(); iterator.hasNext();) {
                            Policy policy = (Policy) iterator.next();
                            if (PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy,
                                LogonControllerFactory.getInstance().getSessionInfo(request).getUser())) {
                                found = true;
                            }
                        }
                    }
                }
                // if no other schemes autherised, then ensure that this one is.
                if (!found) {
                    for (Iterator iter2 = this.getSelectedPoliciesList().iterator(); iter2.hasNext();) {
                        String id = (String) iter2.next();
                        if (PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(
                            PolicyDatabaseFactory.getInstance().getPolicy(Integer.parseInt(id)),
                            LogonControllerFactory.getInstance().getSessionInfo(request).getUser())) {
                            found = true;
                        }
                    }
                }

                if (!found) {
                    errs
                                    .add(Globals.ERROR_KEY, new ActionMessage(
                                                    "authenticationSchemes.error.mustHavePolicySuperUserAssociation"));
                }
            } catch (Exception e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage(
                                "authenticationSchemes.error.failedToValidateSuperUserAuthSchemeConnection"));
            }

        }
        return errs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.forms.AbstractResourceForm#applyToResource()
     */
    public void applyToResource() throws Exception {
        AuthenticationScheme seq = (DefaultAuthenticationScheme) getResource();
        seq.clearModules();
        for (Iterator i = getSelectedModulesList().iterator(); i.hasNext();) {
            seq.addModule((String) i.next());
        }
    }
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        recordSelectedModulesStatus(request);
    }

    private void recordSelectedModulesStatus(HttpServletRequest request) {
        String requestSelectedModules = request.getParameter("selectedModules");
        if (null != requestSelectedModules && !"".equals(requestSelectedModules)) {
            selectedModules.setAsTextFieldText(requestSelectedModules);
        } else if (null != selectedModules) {
            selectedModules.clear();
        }
        if (null != moduleModel) {
            moduleModel.getSelectedValues().clear();
            List<LabelValueBean> availableValues = moduleModel.getAvailableValues();
            for (LabelValueBean labelValueBean : availableValues) {
                if (selectedModules.contains(labelValueBean.getValue())) {
                    moduleModel.getSelectedValues().add(labelValueBean);
                } else {
                    moduleModel.getSelectedValues().remove(labelValueBean);
                }
            }
            moduleModel.rebuild(LogonControllerFactory.getInstance().getSessionInfo(request));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        return 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getTabName1(int)
     */
    public String getTabName(int idx) {
        switch (idx) {
            case 0:
                return "details";
            case 1:
                return "modules";
            default:
                return "policies";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int i) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.forms.AbstractResourceForm#getResourceByName(java.lang.String,
     *      com.ovpnals.security.SessionInfo)
     */
    public Resource getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE.getResourceByName(resourceName, session);
    }

    /**
     * Get the module model
     * 
     * @return the module model.
     */
    public MultiSelectSelectionModel getModuleModel() {
        return moduleModel;
    }

    /**
     * Set the module model
     * 
     * @param moduleModel model to set.
     */
    public void setModuleModel(MultiSelectSelectionModel moduleModel) {
        this.moduleModel = moduleModel;
    }

    /**
     * Get the selected modules as a list
     * 
     * @return selected modules list
     */
    public PropertyList getSelectedModulesList() {
        return selectedModules;
    }

    /**
     * Get the selected modules as a string suitable for the multi select
     * components
     * 
     * @return selected modules as string
     */
    public String getSelectedModules() {
        return selectedModules.getAsTextFieldText();
    }

    /**
     * Set the selected modules as a string from the multi select components
     * 
     * @param selectedModules selected modules as string
     */
    public void setSelectedModules(String selectedModules) {
        this.selectedModules.setAsTextFieldText(selectedModules);
    }

    /**
     * Set the selected modules list
     * 
     * @param selectedModules selected modules list
     */
    public void setSelectedModulesList(PropertyList selectedModules) {
        this.selectedModules = selectedModules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.forms.AbstractResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      com.ovpnals.policyframework.Resource, boolean,
     *      com.ovpnals.input.MultiSelectSelectionModel,
     *      com.ovpnals.boot.PropertyList, com.ovpnals.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        this.isSystem = (((DefaultAuthenticationScheme) resource).isSystemScheme());
    }

    /**
     * @return boolean
     */
    public boolean isSystem() {
        return isSystem;
    }
}