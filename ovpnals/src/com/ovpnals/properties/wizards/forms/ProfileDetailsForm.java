
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
			
package com.ovpnals.properties.wizards.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.forms.AbstractResourceDetailsWizardForm;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.properties.ProfilesFactory;
import com.ovpnals.properties.wizards.actions.ProfileDetailsAction;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.User;
import com.ovpnals.wizard.AbstractWizardSequence;

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
     * @see com.ovpnals.wizard.forms.DefaultWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence)
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
     * @see com.ovpnals.core.forms.AbstractResourceWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_BASE_ON, new Integer(baseOn));
    }
    }

