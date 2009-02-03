
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
			
package com.adito.applications.actions;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.applications.ApplicationShortcut;
import com.adito.applications.ApplicationShortcutDatabaseFactory;
import com.adito.applications.ApplicationsPlugin;
import com.adito.applications.forms.ApplicationShortcutForm;
import com.adito.policyframework.Resource;
import com.adito.policyframework.actions.AbstractResourceDispatchAction;
import com.adito.policyframework.forms.AbstractResourceForm;
import com.adito.security.SessionInfo;

/**
 * Extension of
 * {@link com.adito.policyframework.actions.AbstractResourceDispatchAction}
 * that allows an administrator to edit the details of an application shortcut.
 * <p>
 * The type of application cannot be changed, only the usual resources details
 * and the short specific values of the application extensions parameters.
 */
public class EditApplicationShortcutDispatchAction extends AbstractResourceDispatchAction {

    static Log log = LogFactory.getLog(EditApplicationShortcutDispatchAction.class);

    /**
     * Constructor.
     */
    public EditApplicationShortcutDispatchAction() {
        super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }


    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#edit(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((ApplicationShortcutForm) form).setLocale((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY));
        return super.edit(mapping, form, request, response);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#createResource(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        // TODO only needed if your wanting to create the resouce without the
        // wizard.
        return null;
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#commit(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("application.message.saved"));
        this.addMessages(request, msgs);
        return super.commit(mapping, form, request, response);
    }

    /* (non-Javadoc)
	 * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#commitCreatedResource(org.apache.struts.action.ActionMapping, com.adito.policyframework.forms.AbstractResourceForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected Resource commitCreatedResource(ActionMapping mapping, AbstractResourceForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationShortcutForm asf = (ApplicationShortcutForm)form;
		ApplicationShortcut as = (ApplicationShortcut)asf.getResource();
		int resourceId = ApplicationShortcutDatabaseFactory.getInstance().createApplicationShortcut(
			as.getApplication(), asf.getResourceName(), asf.getResourceDescription(), 
			as.getParameters(), as.isAutoStart(), getSessionInfo(request).getRealmId());
		return as.getResourceType().getResourceById(resourceId);
		
	}

}