
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
			
package com.ovpnals.security.authwizard.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.input.MultiSelectSelectionModel;
import com.ovpnals.security.AuthenticationModuleDefinition;
import com.ovpnals.security.AuthenticationModuleManager;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.ModulesDataSource;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

public class AthenticationSchemeSelectionForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(AthenticationSchemeSelectionForm.class);

    public static final String ATTR_SELECTED_MODULES = "selectedModules";

    // TODO sampleAttributes attributes should be defined here.
    protected MultiSelectSelectionModel moduleModel;
    protected PropertyList selectedModules;

    /**
     * Constructor
     */
    public AthenticationSchemeSelectionForm() {
        super(true, true, "/WEB-INF/jsp/content/security/authwizard/athenticationSchemeSelection.jspf", "resourceName", true,
                        false, "athenticationSchemeSelection", "security", "authwizard.athenticationSchemeSelection", 2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        this.selectedModules = ((PropertyList) sequence.getAttribute(ATTR_SELECTED_MODULES, new PropertyList()));
        MultiSelectDataSource modules = new ModulesDataSource("security");
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        this.moduleModel = new MultiSelectSelectionModel(session, modules, selectedModules);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_SELECTED_MODULES, this.selectedModules);
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

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = new ActionErrors();
        if (this.isCommiting()) {
            PropertyList l = this.getSelectedModulesList();
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
        }
        return errs;
    }

}
