
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
			
package com.adito.properties.wizards.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.CoreServlet;
import com.adito.core.forms.AbstractResourceDetailsWizardForm;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.ResourceUtil;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.wizards.actions.ProfileDetailsAction;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.User;
import com.adito.wizard.AbstractWizardSequence;

public class ProfileDetailsForm extends AbstractResourceDetailsWizardForm {
    private List availableProfiles;
    private int baseOn;

    final static Log log = LogFactory.getLog(ProfileDetailsForm.class);

    // Statics for sequence attributes
    public final static String ATTR_BASE_ON = "baseOn";

    public ProfileDetailsForm() {
        super(true, false, "/WEB-INF/jsp/content/properties/profileWizard/profileDetails.jspf", "resourceName", true, false,
                        "profileDetails", "properties", "profileWizard.profileDetails", 1, PolicyConstants.PROFILE_RESOURCE_TYPE);
        availableProfiles = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.DefaultWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        baseOn = ((Integer) sequence.getAttribute(ATTR_BASE_ON, new Integer(0))).intValue();
        availableProfiles.clear();
        try {
            String scope = (String) sequence.getAttribute(ProfileDetailsAction.ATTR_PROFILE_SCOPE, null);
            User user = LogonControllerFactory.getInstance().getUser(request);
            availableProfiles = ResourceUtil.filterResources(user, ProfilesFactory.getInstance()
                            .getPropertyProfiles(Constants.SCOPE_PERSONAL.equals(scope) ? user.getPrincipalName() : "", true,
                            user.getRealm().getResourceId()), Constants.SCOPE_PERSONAL.equals(scope) ? false : true);
        } catch (Exception e) {
            log.error("Could not get available profiles.", e);
        }
    }

    /**
     * @return Returns the baseOn.
     */
    public int getBaseOn() {
        return baseOn;
    }

    /**
     * @param baseOn The baseOn to set.
     */
    public void setBaseOn(int baseOn) {
        this.baseOn = baseOn;
    }

    /**
     * @return Returns the availableProfiles.
     */
    public List getAvailableProfiles() {
        return availableProfiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.forms.AbstractResourceWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_BASE_ON, new Integer(baseOn));
    }
    }

