
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
			
package com.adito.applications.wizards.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.applications.ApplicationsPlugin;
import com.adito.core.forms.AbstractFavoriteResourceDetailsWizardForm;
import com.adito.navigation.FavoriteResourceType;

/**
 * Extension of a {@link com.adito.core.forms.AbstractResourceDetailsWizardForm}
 * that allows an administrator to enter the additional details for 
 * a new application shortcut.
 */
public class ApplicationShortcutWizardDetailsForm extends AbstractFavoriteResourceDetailsWizardForm  {

    final static Log log = LogFactory.getLog(ApplicationShortcutWizardDetailsForm.class);

    /**
     * Constructor
     */
    public ApplicationShortcutWizardDetailsForm() {
        super(true, true, "/WEB-INF/jsp/content/applications/applicationShortcutWizard/details.jspf", "resourceName", true, false,
            "applicationShortcutDetails", ApplicationsPlugin.MESSAGE_RESOURCES_KEY, "applicationShortcutWizard.applicationShortcutDetails", 2, (FavoriteResourceType)ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE);
    }
}
