
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
			
package net.openvpn.als.policyframework.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.input.MultiSelectDataSource;
import net.openvpn.als.input.MultiSelectPoliciesSelectionModel;
import net.openvpn.als.input.MultiSelectSelectionModel;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.forms.DefaultWizardForm;

/**
 * Abstract implementation of a {@link DefaultWizardForm}.
 */
public abstract class AbstractWizardPolicySelectionForm extends DefaultWizardForm {
    

    private MultiSelectSelectionModel policyModel;
    private PropertyList selectedPolicies;
    private boolean showPersonalPolicies;
    
    /**
     * Statics for sequence attributes
     */
    public final static String ATTR_SELECTED_POLICIES = "selectedPolicies";

    /**
     * @param nextAvailable
     * @param previousAvailable
     * @param page
     * @param focussedField
     * @param autoComplete
     * @param finishAvailable
     * @param pageName
     * @param resourceBundle
     * @param resourcePrefix
     * @param stepIndex
     */
    public AbstractWizardPolicySelectionForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField, boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle, String resourcePrefix, int stepIndex) {
        super(nextAvailable, previousAvailable, page, focussedField, autoComplete, finishAvailable, pageName, resourceBundle,
            resourcePrefix, stepIndex);
    }   

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.DefaultWizardForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting()) {
            if(selectedPolicies.size() == 0) {
                ActionErrors errs = new ActionErrors();
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(getResourceBundle(), getResourcePrefix() + ".error.noPoliciesSelected"));
                return errs;
            }
            else {
                /* Make sure the selected policies are all currently available, this prevents
                 * anyone fiddling the polices they are allowed to configure
                 */
                for(Iterator i = selectedPolicies.iterator(); i.hasNext(); ) {
                    String pol = (String)i.next();
                    if(!policyModel.contains(pol)) {
                        throw new Error("User doesn't have permission to select the policy '" + pol + "', this shouldn't happen.");
                    }
                }
            }
        }
        return super.validate(mapping, request);
    }


    /**
     * @return Returns the selectedPolicies.
     */
    public String getSelectedPolicies() {
        return selectedPolicies.getAsTextFieldText();
    }

    /**
     * @param selectedPolicies The selectedPolicies to set.
     */
    public void setSelectedPolicies(String selectedPolicies) {
        this.selectedPolicies.setAsTextFieldText(selectedPolicies);
    }
    
    /**
     * @param selectedPolicies The selectedPolicies to set.
     */
    public void setSelectedPolicies(PropertyList selectedPolicies) {
        this.selectedPolicies = selectedPolicies;
    }

    /**
     * @return Returns the policyModel.
     */
    public MultiSelectSelectionModel getPolicyModel() {
        return policyModel;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.DefaultWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(getAttributeKey(), selectedPolicies);
    }

    /**
     * @param policyModel The policyModel to set.
     */
    public void setPolicyModel(MultiSelectSelectionModel policyModel) {
        this.policyModel = policyModel;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        
        String requestShowPersonalPolicies = request.getParameter("showPersonalPolicies");
        if (!Util.isNullOrTrimmedBlank(requestShowPersonalPolicies))
            setShowPersonalPolicies(Boolean.parseBoolean(requestShowPersonalPolicies));
        
        AbstractWizardSequence seq = getWizardSequence(request);
        
        selectedPolicies = (PropertyList)seq.getAttribute(getAttributeKey(), new PropertyList());
        
        policyModel = initSelectModel(mapping, request, selectedPolicies, isShowPersonalPolicies());
        
        setSelectedPolicies(selectedPolicies);
        
        MultiSelectDataSource policies = createDatasource(mapping, request);
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        //policyModel = new MultiSelectSelectionModel(session, policies, selectedPolicies);
    }

    protected MultiSelectPoliciesSelectionModel initSelectModel(ActionMapping mapping, HttpServletRequest request, PropertyList selectedPolicies, boolean isShowPersonalPolicies) {
        
        MultiSelectDataSource policies = createDatasource(mapping, request);
        MultiSelectDataSource personalPolicies = null;
        if (!isShowPersonalPolicies) {
            personalPolicies = policies;
            policies = createDatasourceExcludePersonal(mapping, request);
        }
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        MultiSelectPoliciesSelectionModel policyModel = new MultiSelectPoliciesSelectionModel(session, policies, personalPolicies, selectedPolicies);
        return policyModel;
    }
    
    /**
     * @param mapping
     * @param request
     * @return MultiSelectDataSource
     */
    public abstract MultiSelectDataSource createDatasource(ActionMapping mapping, HttpServletRequest request);
    
    public abstract MultiSelectDataSource createDatasourceExcludePersonal(ActionMapping mapping, HttpServletRequest request);
    
    protected String getAttributeKey() {
        return ATTR_SELECTED_POLICIES;
    }

    /**
     * @return showPersonalPolicies.
     */
    public boolean isShowPersonalPolicies() {
        return showPersonalPolicies;
    }

    /**
     * @param showPersonalPolicies
     */
    public void setShowPersonalPolicies(boolean showPersonalPolicies) {
        this.showPersonalPolicies = showPersonalPolicies;
    }
    
}
