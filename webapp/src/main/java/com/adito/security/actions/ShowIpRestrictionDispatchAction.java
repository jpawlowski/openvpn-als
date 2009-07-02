
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

import java.util.ArrayList;
import java.util.Arrays;
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
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyUtil;
import com.adito.security.Constants;
import com.adito.security.IpRestriction;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabase;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.forms.IpRestrictionForm;

/**
 * Implementation of a
 * {@link com.adito.core.actions.AuthenticatedDispatchAction} that allows
 * an administrator to create an <i>IP Restrictions</i>.
 */
public class ShowIpRestrictionDispatchAction extends AuthenticatedDispatchAction {

    /**
     * Constructor.
     */
    public ShowIpRestrictionDispatchAction() {
        super(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE, PolicyConstants.PERM_DELETE });
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("display");
    }

    /**
     * This will setup the IP Restriction Form.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE, request);
        IpRestriction[] ipRestriction = SystemDatabaseFactory.getInstance().getIpRestrictions();
        IpRestrictionForm ipRestrictionForm = (IpRestrictionForm) form;
        ipRestrictionForm.initialize(new IpRestriction(ipRestriction.length > 0 && ipRestriction[0].getDenied()), false);
        ipRestrictionForm.setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * This will setup the IP Restriction Form.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, PolicyConstants.PERM_EDIT, request);
        IpRestrictionForm ipRestrictionForm = (IpRestrictionForm) form;
        ipRestrictionForm.initialize((IpRestriction)request.getAttribute(Constants.EDITING_ITEM), true);
        ipRestrictionForm.setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * This will attempt to write the IP Restriction to the database.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {        
        if (isAdditionValid(request, ((IpRestrictionForm)form))) {
            return doCommit(mapping, form, request, response);
        }
        else {
            return mapping.findForward("confirmCreateWithLockout");
        }
    }
    
    /**
     * This will attempt to write the IP Restriction to the database.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward doCommit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                        throws Exception {
        IpRestrictionForm ipRestrictionForm = (IpRestrictionForm)form;
        ipRestrictionForm.apply();
        Permission permission = ipRestrictionForm.isEditing() ? PolicyConstants.PERM_EDIT : PolicyConstants.PERM_CREATE;
        PolicyUtil.checkPermission(PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, permission, request);
        
        try {
            if(ipRestrictionForm.isEditing()) {        
                SystemDatabaseFactory.getInstance().updateIpRestriction(ipRestrictionForm.getRestriction());                
            }
            else {        
                SystemDatabaseFactory.getInstance().addIpRestriction(ipRestrictionForm.getRestriction().getAddress(), ipRestrictionForm.getRestriction().getType());
            }
            fireCoreEvent(request, ipRestrictionForm, CoreEvent.STATE_SUCCESSFUL);
            saveMessage(request, "editIpRestriction.message.restrictionSaved", ipRestrictionForm.getRestriction().getAddress());
        } catch (Exception e) {
            fireCoreEvent(request, ipRestrictionForm, CoreEvent.STATE_UNSUCCESSFUL);
            throw e;
        }
        return cancel(mapping, form, request, response);
    }
    
    private boolean isAdditionValid(HttpServletRequest request, IpRestrictionForm form) throws Exception {
        IpRestriction[] ipRestrictions = findIpRestrictions(form.getRestriction().getAddress(), form.getType().equals(IpRestrictionForm.ALLOW_TYPE));
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        String remoteAddr = request.getRemoteAddr();
        return database.verifyIPAddress(remoteAddr, ipRestrictions);
    }

    private IpRestriction[] findIpRestrictions(String restriction, boolean isAllow) throws Exception {
        SystemDatabase database = SystemDatabaseFactory.getInstance();
        IpRestriction[] restrictions = database.getIpRestrictions();
        int i = 0;
        for(; i < restrictions.length; i++) {
        	if(restrictions[i].getAddress().equals(restriction)) {
        		restrictions[i] = new IpRestriction(restrictions[i].getAddress(), isAllow, restrictions[i].getPriority());
        		break;
        	}
        }
        if(i == restrictions.length) {
        	List<IpRestriction> newRestrictions = new ArrayList<IpRestriction>(Arrays.asList(restrictions));
        	newRestrictions.add(new IpRestriction(restriction, isAllow, Integer.MAX_VALUE));
            return newRestrictions.toArray(new IpRestriction[newRestrictions.size()]);
        }
        return restrictions;
    }
    
    private void fireCoreEvent(HttpServletRequest request, IpRestrictionForm ipRestrictionForm, int state) {
        IpRestriction restriction = ipRestrictionForm.getRestriction();
        int eventType = ipRestrictionForm.isEditing() ? CoreEventConstants.EDIT_IP_RESTRICTION : CoreEventConstants.CREATE_IP_RESTRICTION;
        CoreEvent coreEvent = new CoreEvent(this, eventType, null, getSessionInfo(request), state);
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_RESTRICTION_ADDRESS, restriction.getAddress());
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_RESTRICTION_IS_AUTHORIZED, String.valueOf(restriction.getAllowed()));
        CoreServlet.getServlet().fireCoreEvent(coreEvent);

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
}