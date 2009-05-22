
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
			
package com.ovpnals.security.actions;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.policyframework.ResourceStack;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.actions.AbstractResourcesDispatchAction;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.AuthenticationSchemeSequenceItem;
import com.ovpnals.security.DefaultAuthenticationScheme;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.security.forms.AuthenticationSchemesForm;

/**
 * Implementation of a
 * {@link com.ovpnals.core.actions.AuthenticatedDispatchAction} that allows
 * an administrator to view, create, edit, delete and set default
 * <i>Authentication Schemes</i>.
 */
public class ShowAuthenticationSchemesDispatchAction extends AbstractResourcesDispatchAction<AuthenticationScheme, AuthenticationSchemeSequenceItem> {
    /**
     * Constructor
     */
    public ShowAuthenticationSchemesDispatchAction() {
        super(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
    }

    private AuthenticationScheme getAuthenticationScheme(ActionForm form) throws Exception {
        AuthenticationSchemesForm schemesForm = (AuthenticationSchemesForm) form;
        int id = schemesForm.getSelectedResource();
        AuthenticationScheme scheme = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(id);
        if (scheme == null) {
            throw new Exception("No scheme with Id of " + id + ".");
        }
        return scheme;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ovpnals.policyframework.actions.AbstractResourcesDispatchAction#confirmRemove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_DELETE, request);
        AuthenticationSchemesForm schemesForm = (AuthenticationSchemesForm) form;
        int id = schemesForm.getSelectedResource();
        AuthenticationScheme scheme = getAuthenticationScheme(form);
        if (scheme.isSystemScheme()) {
            throw new Exception("Cannot remove system schemes.");
        }
        
        int nextEnabled = getNextEnabledAuthenticationScheme(scheme);
        if (nextEnabled == -1) {
            saveError(request, "authenticationSchemes.error.mustHaveOneEnabledScheme", scheme);
            return list(mapping, form, request, response);
        }

        List resourceIds = ResourceUtil.getSignonAuthenticationSchemeIDs(getSessionInfo(request).getUser());
        resourceIds.remove(new Integer(id));
        if (resourceIds.size() == 0) {
            saveError(request, "authenticationSchemes.error.mustHavePolicySuperUserAssociation", scheme);
            return list(mapping, form, request, response);
        }

        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_DELETE, request);
        return mapping.findForward("confirmRemove");
    }
    
    private int getNextEnabledAuthenticationScheme(AuthenticationScheme scheme) throws Exception {
        List<AuthenticationScheme> allSchemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        int nextEnabled = -1;
        for (AuthenticationScheme oseq : allSchemes) {
            if (!oseq.equals(scheme) && oseq.getEnabled() && !oseq.isSystemScheme()) {
                nextEnabled = oseq.getResourceId();
            }            
        }
        return nextEnabled;
    }

    /**
     * Delete the selected authentication scheme.
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_DELETE, request);
        AuthenticationScheme scheme = getAuthenticationScheme(form);
        int nextEnabled = getNextEnabledAuthenticationScheme(scheme);
        if (nextEnabled == -1) {
            saveError(request, "authenticationSchemes.error.mustHaveOneEnabledScheme", scheme);
            return list(mapping, form, request, response);
        }
        super.remove(mapping, form, request, response);
        saveMessage(request, "authenticationSchemes.message.schemeDeleted", scheme);
        return getRedirectWithMessages(mapping, request);
    }

    /**
     * Disable the selected authentication scheme.
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward disable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, PolicyConstants.PERM_EDIT_AND_ASSIGN, request);
        AuthenticationSchemesForm schemesForm = (AuthenticationSchemesForm) form;
        int id = schemesForm.getSelectedResource();
        AuthenticationScheme scheme = getAuthenticationScheme(form);

        List resourceIds = PolicyDatabaseFactory.getInstance().getGrantedResourcesOfType(getSessionInfo(request).getUser(),
            PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
        // remove the WebDav and Embedded Client as they are not sign-on-able.
        resourceIds.remove(new Integer(3));
        resourceIds.remove(new Integer(4));
        resourceIds.remove(new Integer(id));

        if (resourceIds.size() == 0) {
            saveError(request, "authenticationSchemes.error.mustHavePolicySuperUserAssociation", scheme);
            return list(mapping, form, request, response);
        }

        List<AuthenticationScheme> authSchemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        int enabled = 0;
        for (Iterator i = authSchemes.iterator(); i.hasNext();) {
            AuthenticationScheme oseq = (DefaultAuthenticationScheme) i.next();
            if (oseq.getResourceId() == id && !oseq.getEnabled()) {
                throw new Exception("Scheme already disabled.");
            }
            if (oseq.getEnabled() && !oseq.isSystemScheme()) {
                enabled++;
            }
        }
        if (enabled == 1) {
            saveError(request, "authenticationSchemes.error.cantDisableLastEnabledScheme", scheme);
            return list(mapping, form, request, response);
        }
        scheme.setEnabled(false);
        SystemDatabaseFactory.getInstance().updateAuthenticationSchemeSequence(scheme);
        saveMessage(request, "authenticationSchemes.message.schemeDisabled", scheme);
        return getRedirectWithMessages(mapping, request);
    }

    /**
     * Enable the selected authentication scheme.
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward enable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, PolicyConstants.PERM_EDIT_AND_ASSIGN, request);
        AuthenticationScheme scheme = getAuthenticationScheme(form);
        if (scheme.getEnabled()) {
            throw new Exception("Alreadty enabled.");
        }
        scheme.setEnabled(true);
        SystemDatabaseFactory.getInstance().updateAuthenticationSchemeSequence(scheme);
        saveMessage(request, "authenticationSchemes.message.schemeEnabled", scheme);
        return getRedirectWithMessages(mapping, request);
    }

    /**
     * Edit the selected authentication scheme.
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermissions(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, new Permission[] {
                        PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
                        PolicyConstants.PERM_ASSIGN }, request);
        AuthenticationScheme seq = getAuthenticationScheme(form);
        ResourceStack.pushToEditingStack(request.getSession(), seq);
        return mapping.findForward("edit");
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward moveUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AuthenticationScheme scheme = getAuthenticationScheme(form);
        if (scheme.getPriorityInt() == 1) {
            saveError(request, "authenticationSchemes.error.moveup.top", scheme);
            return unspecified(mapping, form, request, response);
        }
        
        PolicyUtil.checkPermission(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, PolicyConstants.PERM_EDIT_AND_ASSIGN, request);
        List<AuthenticationScheme> schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        SystemDatabaseFactory.getInstance().moveAuthenticationSchemeUp(scheme, schemes);
        saveMessage(request, "authenticationSchemes.message.moveup", scheme);
        return getRedirectWithMessages(mapping, request);
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward moveDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuthenticationScheme scheme = getAuthenticationScheme(form);
        List<AuthenticationScheme> schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        if (schemes.indexOf(scheme) == schemes.size() - 1) {
            saveError(request, "authenticationSchemes.error.movedown.bottom", scheme);
            return unspecified(mapping, form, request, response);
        }
        
        PolicyUtil.checkPermission(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, PolicyConstants.PERM_EDIT_AND_ASSIGN, request);
        SystemDatabaseFactory.getInstance().moveAuthenticationSchemeDown(scheme, schemes);
        saveMessage(request, "authenticationSchemes.message.movedown", scheme);
        return getRedirectWithMessages(mapping, request);
    }

    /**
     * List the authentication schemes configured.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        PolicyUtil.checkPermissions(PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, new Permission[]{ PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN}, request);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "must-revalidate");
        
        CoreUtil.clearFlow(request);

        AuthenticationSchemesForm schemesForm = (AuthenticationSchemesForm) form;
        schemesForm.initialize(getSessionInfo(request), getSessionInfo(request).getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 
                        SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences(getSessionInfo(request).getUser().getRealm().getRealmID()) 
                        : ResourceUtil.getGrantedResource(getSessionInfo(request), getResourceType()));
        Util.noCache(response);
        return mapping.findForward("display");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}