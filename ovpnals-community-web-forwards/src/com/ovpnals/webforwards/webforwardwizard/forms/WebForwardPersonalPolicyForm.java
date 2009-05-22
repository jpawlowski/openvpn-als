
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
			
package com.ovpnals.webforwards.webforwardwizard.forms;

import com.ovpnals.policyframework.forms.AbstractWizardPersonalResourcePolicyForm;
import com.ovpnals.webforwards.WebForwardPlugin;

/**
 * Implementation of a
 * {@link com.ovpnals.policyframework.forms.AbstractWizardPersonalResourcePolicyForm}
 * that allows an administrator to assign policies to new <i>Network Places</i>.
 */
public class WebForwardPersonalPolicyForm  extends AbstractWizardPersonalResourcePolicyForm {
    
    /**
     * Constructor.
     */
    public WebForwardPersonalPolicyForm() {
        super(true, true, "/WEB-INF/jsp/content/webforward/webforwardwizard/webForwardPersonalPolicy.jspf",
                        "webForwardPersonalPolicy", WebForwardPlugin.MESSAGE_RESOURCES_KEY, "webForwardWizard.webForwardPersonalPolicy", 5,
                        WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
    }
}
