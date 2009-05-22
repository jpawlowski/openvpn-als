
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
			
package com.ovpnals.applications.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.applications.ApplicationShortcut;
import com.ovpnals.applications.ApplicationShortcutItem;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceItemModel;
import com.ovpnals.policyframework.forms.AbstractResourcesForm;
import com.ovpnals.security.SessionInfo;

/**
 * Extension of
 * {@link com.ovpnals.policyframework.forms.AbstractResourcesForm} that
 * provides the model for lists of <i>Application Shortcuts</i>.
 */
public class ApplicationShortcutsForm extends AbstractResourcesForm {

    static Log log = LogFactory.getLog(ApplicationShortcutsForm.class);

    // Private instance variables

    /**
     * Constructor
     */
    public ApplicationShortcutsForm() {
        super(new ResourceItemModel("applicationShortcuts"));
    }

    /**
     * Initialise the pager and the items and the ability to sort.
     * 
     * @param applicationShortcuts List of Samples to be added.
     * @param session The session information.
     * @param defaultSortColumnId default sort column
     * @param request request from action that initialises this form
     */
    public void initialise(List applicationShortcuts, SessionInfo session, String defaultSortColumnId, HttpServletRequest request) {
        super.initialize(session.getHttpSession(), defaultSortColumnId);
        if (selectedView == null) {
            selectedView = session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT ? ICONS_VIEW : LIST_VIEW;
        } else if (session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
            selectedView = LIST_VIEW;
        }
        try {
            for (Iterator i = applicationShortcuts.iterator(); i.hasNext();) {
                ApplicationShortcut applicationShortcut = (ApplicationShortcut) i.next();
                List policies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(applicationShortcut,
                                session.getUser().getRealm());
                ExtensionDescriptor ed = ExtensionStore.getInstance().getExtensionDescriptor(applicationShortcut.getApplication());
                if (ed == null) {
                    log.warn("Found shortcut with an application ID '" + applicationShortcut.getApplication()
                                    + "' that does not exist. An extension may have been removed.");
                } else {
                	if(ed.getApplicationBundle().getStatus() != ExtensionBundleStatus.ACTIVATED) {
                        log.warn("Found shortcut with an application ID '" + applicationShortcut.getApplication()
                            + "' that uses an application contained in an extension bundle that is not activated (its status is '" + ed.getApplicationBundle().getStatus().getName() + "'). Ignoring.");
                	}
                	else {
	                    ApplicationShortcutItem item = new ApplicationShortcutItem(ed, applicationShortcut, policies, session
	                                    .getNavigationContext(), applicationShortcut.sessionPasswordRequired(session));
	                    item.setFavoriteType(getFavoriteType(applicationShortcut.getResourceId()));
	                    getModel().addItem(item);                		
                	}
                }
            }
            checkSort();
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            log.error("Failed to initialise resources form.", t);
        }
    }
}
