
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

import com.adito.boot.Util;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.SessionInfo;
import com.adito.security.UserDatabase;
import com.adito.security.forms.ShowAvailableRolesForm;
import com.adito.table.actions.AbstractPagerAction;

/**
 * Implementation of an {@link AbstractPagerAction} that lists all of the
 * configured <i>Groups</i> (previously known as <i>Roles</i>).
 * <p> 
 * Depending onf the user database in use, different actions will be available
 * (edit. create or delete). 
 * <p>
 * With user databases that do not support account creation, the admin will
 * be able to use the edit function, but this will not allow any information
 * to be changed, only viewd.
 */
public class ShowAvailableRolesDispatchAction extends AbstractPagerAction {
    final static Log log = LogFactory.getLog(ShowAvailableRolesDispatchAction.class);
    
    /**
     * Constructor.
     */
    public ShowAvailableRolesDispatchAction() {
        super(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] {
                        PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
                        PolicyConstants.PERM_DELETE
        });
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }
    
    @Override
    public ActionForward filter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        list(mapping, form, request, response);
        return super.filter(mapping, form, request, response);
    }

    /**
     * List of all the available roles.
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
    	ShowAvailableRolesForm showAvailableRolesForm = ((ShowAvailableRolesForm) form);
        
        try {
        	UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            Role[] roles = null;
            try {
                String filterText = showAvailableRolesForm.getFilterText();
                int maxRoleResults = userDatabase.getMaxRoleResults();
                String filter = Util.isNullOrTrimmedBlank(filterText) ? UserDatabase.WILDCARD_SEARCH : filterText;
                roles = userDatabase.listAllRoles(filter, maxRoleResults);
                if (roles.length > maxRoleResults) {
                    int newLength = roles.length - 1;
                    Role[] copy = new Role[newLength];
                    System.arraycopy(roles, 0, copy, 0, newLength);
                    roles = copy;
                    saveMessage(request, "availableRoles.match.limited", maxRoleResults);
                }
            } catch (Exception e) {
                log.error("Failed to get available roles.", e);
                ActionMessages errs = new ActionMessages();
                errs.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.cannotListRoles", Util.getExceptionMessageChain(e)));
                saveErrors(request, errs);
                roles = new Role[0];
            }
            showAvailableRolesForm.initialize(roles, request.getSession());
            ActionMessages msgs = new ActionMessages();
            if (!userDatabase.supportsAccountCreation()) {
                msgs.add(Globals.MESSAGE_KEY, new ActionMessage("availableRoles.noRoleCreation.text"));
            }
            if (msgs.size() > 0) {
                saveMessages(request, msgs);
            }
        } catch (Exception ex) {
            log.error("Failed to reset administration form", ex);
        }
        return mapping.findForward("success");
    }


    /**
     * Create a new role.
     * 
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
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward redisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ShowAvailableRolesForm showAvailableRolesForm = (ShowAvailableRolesForm) form;
        showAvailableRolesForm.reInitialize(request.getSession());
        return mapping.findForward("success");
    }
    
    /**
     * Edit an existing role.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        Role r = udb.getRole(
                        ((ShowAvailableRolesForm) form).getSelectedItem());
        request.setAttribute(Constants.EDITING_ITEM, r);
        return mapping.findForward("edit");
    }

    /**
     * Confirm deletion of an existing role.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward confirmRoleDeletion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
		PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);
		String rolename = request.getParameter("rolename");
		if (rolename == null) {
			ActionMessages mesgs = new ActionMessages();
			mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.singleRoleNotSelected"));
			saveErrors(request, mesgs);
			return list(mapping, form, request, response);
		} else {
			return mapping.findForward("confirmRoleDeletion");
		}
    }

    /**
     * Delete an existing role.
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
    	PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);
    	UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        String rolename = request.getParameter("rolename"); 
        Role role = udb.getRole(rolename);
        SessionInfo info = this.getSessionInfo(request);
        try {
            // Revoke all polices from the user
            PolicyDatabaseFactory.getInstance().revokeAllPoliciesFromPrincipal(role);
            
  			udb.deleteRole(rolename);
  			CoreServlet.getServlet().fireCoreEvent(
  							new CoreEvent(this, CoreEventConstants.GROUP_REMOVED, role, info)
  									.addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, rolename));
  			return mapping.findForward("refresh");
  		} catch (Exception e) {
  			CoreServlet.getServlet().fireCoreEvent(
  							new CoreEvent(this, CoreEventConstants.GROUP_REMOVED, role, info, CoreEvent.STATE_UNSUCCESSFUL)
  									.addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, rolename));
  			throw e;
  		}
        finally{
            // we need to reset the menu items as they could have changed here.
            LogonControllerFactory.getInstance().applyMenuItemChanges(request);
        }
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}