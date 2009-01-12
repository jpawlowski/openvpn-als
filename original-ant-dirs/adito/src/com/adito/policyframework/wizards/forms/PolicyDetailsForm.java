
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
			
package com.adito.policyframework.wizards.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.forms.AbstractResourceDetailsWizardForm;
import com.adito.policyframework.PolicyConstants;

/**
 * Implementation of a {@link com.adito.core.forms.AbstractResourceDetailsWizardForm}
 * that allows an administrator using the <i>Create Policy</i> wizard to 
 * enter the basic policy details such as resource name and description. 
 */
public class PolicyDetailsForm extends AbstractResourceDetailsWizardForm {    
    final static Log log =  LogFactory.getLog(PolicyDetailsForm.class); 

    /**
     * Constructor
     */
    public PolicyDetailsForm() {
        super(true, false, "/WEB-INF/jsp/content/policyframework/policyWizard/policyDetails.jspf", 
            "resourceName", true, false, "policyDetails", "policyframework", "policyWizard.policyDetails", 1,
            PolicyConstants.POLICY_RESOURCE_TYPE);
    }
}

