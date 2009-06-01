
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.applications.wizards.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.applications.ApplicationShortcut;
import net.openvpn.als.applications.ApplicationShortcutChangeEvent;
import net.openvpn.als.applications.ApplicationShortcutDatabaseFactory;
import net.openvpn.als.applications.ApplicationShortcutEventConstants;
import net.openvpn.als.applications.wizards.forms.ApplicationShortcutWizardAdditionalDetailsForm;
import net.openvpn.als.applications.wizards.forms.ApplicationShortcutWizardApplicationForm;
import net.openvpn.als.applications.wizards.forms.ApplicationShortcutWizardDetailsForm;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.extensions.ShortcutParameterItem;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.actions.AbstractFinishWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardFinishForm;

/**
 * Implementation of
 * {@link net.openvpn.als.wizard.actions.AbstractFinishWizardAction} that allows
 * an adminstrator to finish the <i>Application Shortcut</i> creation wizard.
 */
public class ApplicationShortcutWizardFinishAction extends AbstractFinishWizardAction {
    final static Log log = LogFactory.getLog(ApplicationShortcutWizardFinishAction.class);

    /**
     * Constructor.
     */
    public ApplicationShortcutWizardFinishAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        List actionStatus = new ArrayList();
        AbstractWizardSequence seq = getWizardSequence(request);
        String application = (String) seq.getAttribute(ApplicationShortcutWizardApplicationForm.ATTR_SELECTED_APPLICATION, null);
        String name = (String) seq.getAttribute(ApplicationShortcutWizardDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(ApplicationShortcutWizardDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        boolean favorite = ((Boolean) seq.getAttribute(ApplicationShortcutWizardDetailsForm.ATTR_FAVORITE, Boolean.FALSE))
                        .booleanValue();
        List parameterItems = ((List) seq.getAttribute(ApplicationShortcutWizardAdditionalDetailsForm.ATTR_PARAMETERS, null));
        Map parameterMap = new HashMap();
        for (Iterator i = parameterItems.iterator(); i.hasNext();) {
            ShortcutParameterItem pi = (ShortcutParameterItem) i.next();
            parameterMap.put(pi.getName(), pi.getPropertyValue().toString());
        }
        boolean autoStart = false; // TODO hook this in
        ApplicationShortcut shortcut = null;
        try {
            int shortcutId = ApplicationShortcutDatabaseFactory.getInstance().createApplicationShortcut(application, name, description,
                            parameterMap, autoStart, getSessionInfo(request).getRealmId());
            shortcut = ApplicationShortcutDatabaseFactory.getInstance().getShortcut(shortcutId);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "applicationShortcutWizard.applicationShortcutFinish.status.applicationShortcutCreated"));
            CoreServlet.getServlet().fireCoreEvent(
                new ApplicationShortcutChangeEvent(this, ApplicationShortcutEventConstants.CREATE_APPLICATION_SHORTCUT, shortcut,
                                getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL));
        } catch (Exception e) {
            log.error("Failed to create application shortcut.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "applicationShortcutWizard.applicationShortcutFinish.status.failedToCreateApplicationShortcut", e
                                            .getMessage()));
            CoreServlet.getServlet().fireCoreEvent(
                new ApplicationShortcutChangeEvent(this, ApplicationShortcutEventConstants.CREATE_APPLICATION_SHORTCUT, getSessionInfo(request), e));
        }
        if (shortcut != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("applicationShortcutWizard.applicationShortcutFinish", seq,
                shortcut, favorite, request));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractFinishWizardAction#exit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        throw new Exception("Cannot create sequence on this page.");
    }

}
