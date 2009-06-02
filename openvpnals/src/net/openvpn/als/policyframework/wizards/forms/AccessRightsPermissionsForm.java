
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
			
package net.openvpn.als.policyframework.wizards.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.input.MultiSelectSelectionModel;
import net.openvpn.als.policyframework.AccessRightsMultiSelectDataSource;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.forms.DefaultWizardForm;

/**
 * Implementation of a {@link DefaultWizardForm}. Provides setters and getters
 * for attributes for the access rights page.
 */
public class AccessRightsPermissionsForm extends DefaultWizardForm {

    public final static String ATTR_SELECTED_ACCESS_RIGHTS = "selectedAccessRights";

    private MultiSelectSelectionModel accessRightsModel;
    private PropertyList selectedAccessRights;

    /**
     * 
     */
    public AccessRightsPermissionsForm() {
        super(true, true, "/WEB-INF/jsp/content/policyframework/accessRightsWizard/accessRightsPermissions.jspf", "resourceType",
                        true, false, "accessRightsPermissions", "policyframework", "accessRightsWizard.permissions", 2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.forms.AbstractResourceWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(getAttributeKey(), selectedAccessRights);
    }

    public MultiSelectSelectionModel getAccessRightsModel() {
        return accessRightsModel;
    }

    public void setAccessRightsModel(MultiSelectSelectionModel accessRightsModel) {
        this.accessRightsModel = accessRightsModel;
    }

    public PropertyList getSelectedAccessRightsList() {
        return selectedAccessRights;
    }

    public String getSelectedAccessRights() {
        return selectedAccessRights.getAsTextFieldText();
    }
    
    public void setSelectedAccessRights(String selectedAccessRights) {
        this.selectedAccessRights.setAsTextFieldText(selectedAccessRights);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.DefaultWizardForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting()) {
            if(selectedAccessRights.size() == 0) {
                ActionErrors errs = new ActionErrors();
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(getResourceBundle(), getResourcePrefix() + ".error.noAccessRightsSelected"));
                return errs;
            }
            else {
                /* Make sure the selected access rights are all currently available.
                 */
                for(Iterator i = selectedAccessRights.iterator(); i.hasNext(); ) {
                    String accessRight = (String)i.next();
                    if(!accessRightsModel.contains(accessRight)) {
                        throw new Error("User doesn't have permission to select the Access Right '" + accessRight + "', this shouldn't happen.");
                    }
                }
            }
        }
        return super.validate(mapping, request);
    }

    protected String getAttributeKey() {
        return ATTR_SELECTED_ACCESS_RIGHTS;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        AbstractWizardSequence seq = getWizardSequence(request);
        selectedAccessRights = (PropertyList)seq.getAttribute(getAttributeKey(), new PropertyList());  
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        String permissionClass = (String) seq.getAttribute(AccessRightsDetailsForm.ATTR_RESOURCE_PERMISSION_CLASS,
                        PolicyConstants.DELEGATION_CLASS);
        AccessRightsMultiSelectDataSource accessRightsMultiSelectDataSource = new AccessRightsMultiSelectDataSource(permissionClass);
        accessRightsModel = new MultiSelectSelectionModel(session, accessRightsMultiSelectDataSource, selectedAccessRights);
    }
}