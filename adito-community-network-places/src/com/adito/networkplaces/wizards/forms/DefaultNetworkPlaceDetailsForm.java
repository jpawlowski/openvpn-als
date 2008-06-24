
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
			
package com.adito.networkplaces.wizards.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.core.BundleActionMessage;
import com.adito.core.forms.AbstractFavoriteResourceDetailsWizardForm;
import com.adito.navigation.FavoriteResourceType;
import com.adito.networkplaces.NetworkPlacePlugin;


/**
 * Implementation of a {@link com.adito.core.forms.AbstractFavoriteResourceDetailsWizardForm}
 * that allows an administrator to enter the details of a new <i>Network Place</i>
 */
public class DefaultNetworkPlaceDetailsForm extends AbstractFavoriteResourceDetailsWizardForm {

    final static Log log = LogFactory.getLog(DefaultNetworkPlaceDetailsForm.class);

    /**
     * Constructor
     */
    public DefaultNetworkPlaceDetailsForm() {
        super(true, false, "/WEB-INF/jsp/content/vfs/networkingWizard/defaultNetworkPlaceDetails.jspf", "resourceName", true,
                        false, "defaultNetworkPlaceDetails", NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, "defaultNetworkPlaceWizard.defaultNetworkPlaceDetails", 1,
                        (FavoriteResourceType)NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = super.validate(mapping, request);
        if (isCommiting()) {
            if (this.getResourceName().contains("\\")) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, "defaultNetworkPlaceWizard.defaultNetworkPlaceDetails.error.invalid.char", "\\"));
            }
        }
        return errs;
    }
}
