
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
			
package com.ovpnals.policyframework.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.wizard.AbstractWizardSequence;

/**
 * Abstract implementation of a {@link com.ovpnals.policyframework.forms.AbstractWizardPolicySelectionForm}
 * that should be used for wizards that create delegatable resources. 
 */
public class AbstractWizardPersonalResourcePolicyForm extends AbstractWizardResourcePolicySelectionForm {

    private String personalPolicyName;

    /**
     * Constructor.
     *
     * @param nextAvailable next button available
     * @param previousAvailable previous button available
     * @param page page 
     * @param pageName page name
     * @param resourceBundle resource bundle
     * @param resourcePrefix resource prefix
     * @param stepIndex step
     * @param resourceType resource type
     */
    public AbstractWizardPersonalResourcePolicyForm(boolean nextAvailable, boolean previousAvailable, String page, String pageName, String resourceBundle, String resourcePrefix, int stepIndex, ResourceType resourceType) {
        super(nextAvailable, previousAvailable, page, pageName, resourceBundle,
                        resourcePrefix, stepIndex, resourceType);
    }
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return null;
    }
    
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        PropertyList selectedPolicies = new PropertyList();
        selectedPolicies.add(getPersonalPolicyName());
        sequence.putAttribute(AbstractWizardResourcePolicySelectionForm.ATTR_SELECTED_POLICIES, selectedPolicies);
    }

    public String getPersonalPolicyName() {
        return personalPolicyName;
    }

    public void setPersonalPolicyName(String personalPolicyName) {
        this.personalPolicyName = personalPolicyName;
    }
}
