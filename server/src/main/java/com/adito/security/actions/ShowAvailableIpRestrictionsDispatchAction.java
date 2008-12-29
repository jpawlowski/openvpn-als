
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
			
package com.adito.security.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyUtil;
import com.adito.security.Constants;
import com.adito.security.IpRestriction;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabase;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.forms.ShowAvailableIpRestrictionsForm;
import com.adito.table.actions.AbstractPagerAction;
import com.adito.table.forms.AbstractPagerForm;

/**
 * Implementation of
 * {@link com.adito.core.actions.AuthenticatedDispatchAction} that allows
 * an adminstrator to view all configured <i>IP Restrictions</i>
 */
public class ShowAvailableIpRestrictionsDispatchAction extends AbstractPagerAction {

    /**
     * Constructor.
     */
    public ShowAvailableIpRestrictionsDispatchAction() {
        super(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE, PolicyConstants.PERM_DELETE });
    }

    /*
     * (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /**
     * List all available restrictions
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
    	CoreUtil.clearFlow(request);
        IpRestriction[] restrictions = null;

        SystemDatabase sdb = SystemDatabaseFactory.getInstance();
        restrictions = sdb.getIpRestrictions(); 
        
        ((ShowAvailableIpRestrictionsForm) form).initialize(restrictions, request.getSession());
        return mapping.findForward("success");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
                    throws Exception {

        String[] id = request.getParameterValues("id");
        if (id != null) {    
            IpRestriction[] ipRestrictions = findRemainingIpRestrictions(id);
            SystemDatabase database = SystemDatabaseFactory.getInstance();
            String remoteAddr = request.getRemoteAddr();
            boolean isValid = database.verifyIPAddress(remoteAddr, ipRestrictions);
            return mapping.findForward(isValid ? "confirmDelete" : "confirmDeleteWithLockout");
        }
        return mapping.findForward("refresh");
    }
    
    private IpRestriction[] findRemainingIpRestrictions(String[] restrictionIds) throws Exception {
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        IpRestriction[] restrictions = database.getIpRestrictions();
        
        Collection<IpRestriction> differences = new HashSet<IpRestriction>(Arrays.asList(restrictions));
        for (String restrictionId : restrictionIds) {
            IpRestriction ipRestriction = findIpRestriction(restrictions, Integer.valueOf(restrictionId));
            if (ipRestriction !=null) {
                differences.remove(ipRestriction);
            }
        }
        return differences.toArray(new IpRestriction[differences.size()]);
    }
    
    /**
     * Delete a IP restrictions
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);
        String[] id = request.getParameterValues("id");
        if (id != null) {    
            deleteIpRestrictions(request, id);
        }
        return mapping.findForward("refresh");
    }
    
    /**
     * Move an IP restriction down in priority by swapping the priority with
     * the restriction below the one selected.
     * 
     * @param mapping mapping
     * @param form form 
     * @param request request
     * @param response response
     * @return ActionForward forward
     * @throws Exception on any error
     */
    public ActionForward moveDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, PolicyConstants.PERM_EDIT, request);
        int id = Integer.parseInt(request.getParameter("id"));
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        IpRestriction restriction1 = database.getIpRestriction(id);
        String ipAddress = restriction1.getAddress();
        String ipPermission = restriction1.getAllowed() ? "Allowed" : "Denied";
        try {
            List<IpRestriction> restrictions = Arrays.asList(database.getIpRestrictions());
            database.swapIpRestrictions(restriction1, restrictions.get(restrictions.indexOf(restriction1) + 1));
            fireCoreEvent(request, CoreEventConstants.IP_RESTRICTION_MOVE_DOWN, ipAddress, ipPermission, CoreEvent.STATE_SUCCESSFUL);
        } catch (Exception e) {
            fireCoreEvent(request, CoreEventConstants.IP_RESTRICTION_MOVE_DOWN, ipAddress, ipPermission, CoreEvent.STATE_UNSUCCESSFUL);
            throw e;
        }        
        return mapping.findForward("refresh");
    }
    
    /**
     * Move an IP restriction up in priority by swapping the priority with
     * the restriction above the one selected.
     * 
     * @param mapping mapping
     * @param form form 
     * @param request request
     * @param response response
     * @return ActionForward forward
     * @throws Exception on any error
     */
    public ActionForward moveUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, PolicyConstants.PERM_EDIT, request);
        int id = Integer.parseInt(request.getParameter("id"));
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        IpRestriction restriction1 = database.getIpRestriction(id);
        String ipAddress = restriction1.getAddress();
        String ipPermission = restriction1.getAllowed() ? "Allowed" : "Denied";
        try {
            List<IpRestriction> restrictions = Arrays.asList(database.getIpRestrictions());
            database.swapIpRestrictions(restriction1, restrictions.get(restrictions.indexOf(restriction1) - 1));
            fireCoreEvent(request, CoreEventConstants.IP_RESTRICTION_MOVE_UP, ipAddress, ipPermission, CoreEvent.STATE_SUCCESSFUL);
        } catch (Exception e) {
            fireCoreEvent(request, CoreEventConstants.IP_RESTRICTION_MOVE_UP, ipAddress, ipPermission, CoreEvent.STATE_UNSUCCESSFUL);
            throw e;
        }        
        return mapping.findForward("refresh");
    }
    
    private void deleteIpRestrictions(HttpServletRequest request, String[] restrictionIds) throws Exception {
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        IpRestriction[] restrictions = database.getIpRestrictions();
        
        for (String restrictionId : restrictionIds) {
            IpRestriction ipRestriction = findIpRestriction(restrictions, Integer.valueOf(restrictionId));
            if (ipRestriction != null) {
                deleteIpRestriction(request, ipRestriction);
            }
        }
    }
    
    private void deleteIpRestriction(HttpServletRequest request, IpRestriction restriction) throws Exception {
        String ipAddress = restriction.getAddress();
        String ipPermission = restriction.getAllowed() ? "Allowed" : "Denied";
        
        try {
            SystemDatabase database = SystemDatabaseFactory.getInstance();
            database.removeIpRestriction(restriction.getID());
            fireCoreEvent(request, CoreEventConstants.DELETE_IP_RESTRICTION, ipAddress, ipPermission, CoreEvent.STATE_SUCCESSFUL);
        } catch (Exception e) {
            fireCoreEvent(request, CoreEventConstants.DELETE_IP_RESTRICTION, ipAddress, ipPermission, CoreEvent.STATE_UNSUCCESSFUL);
            throw e;
        }        
    }
    
    private static IpRestriction findIpRestriction(IpRestriction[] restrictions, int id) {
        for (IpRestriction restriction : restrictions) {
            if (restriction.getID() == id)
                return restriction;
        }
        return null;
    }

    private void fireCoreEvent(HttpServletRequest request, int eventID, String ipAddress, String ipPermission, int state) {
        CoreEvent coreEvent = new CoreEvent(this, eventID, null, getSessionInfo(request), state);
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_RESTRICTION_ADDRESS, ipAddress);
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_RESTRICTION_IS_AUTHORIZED, ipPermission);
        CoreServlet.getServlet().fireCoreEvent(coreEvent);
    }

    /**
     * Create a new restriction
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("create");
    }

    /**
     * Create an existing restriction
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        IpRestriction restriction = SystemDatabaseFactory.getInstance().getIpRestriction(id);
        request.setAttribute(Constants.EDITING_ITEM, restriction);
        return mapping.findForward("edit");
    }

    /*
     * (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward filter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return unspecified(mapping, form, request, response);
    }
}