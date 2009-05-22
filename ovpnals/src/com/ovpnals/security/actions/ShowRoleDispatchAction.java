
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.GroupsRequiredForUserException;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.Role;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.forms.RoleForm;
import com.ovpnals.util.Utils;

/**
 * Implementation of an {@link ProtectedDispatchAction} that allows an
 * administrator to create or edit a <i>Group</i> (previously known as a
 * <i>Role</i>).
 * 
 */
public class ShowRoleDispatchAction extends AuthenticatedDispatchAction {

    /**
     * Constructor.
     */
    public ShowRoleDispatchAction() {
        super(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN });
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
        return mapping.findForward("display");
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
        SessionInfo sessionInfo = getSessionInfo(request);
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(sessionInfo.getUser().getRealm());
        if (!userDatabase.supportsAccountCreation()) {
            throw new Exception("The underlying user database does not support role creation.");
        }
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE,
            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        RoleForm roleForm = (RoleForm) form;
        roleForm.initialize(Collections.<User> emptyList());
        roleForm.setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Edit an existing role. The role to edit must be placed in the request
     * attribute
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
        Role role = (Role) request.getAttribute(Constants.EDITING_ITEM);
        if (role == null) {
            throw new Exception("No role configured for editing.");
        }
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE,
            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        SessionInfo sessionInfo = getSessionInfo(request);
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(sessionInfo.getUser().getRealm());
        List<User> users = Arrays.asList(userDatabase.getUsersInRole(role));

        RoleForm roleForm = (RoleForm) form;
        roleForm.initialize(users);
        roleForm.setRolename(role.getPrincipalName());
        roleForm.setReferer(CoreUtil.getReferer(request));
        roleForm.setEditing();
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Save the new role or update the existing one depending on whether the
     * role is being edited or created.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        RoleForm roleForm = (RoleForm) form;
        SessionInfo sessionInfo = getSessionInfo(request);
        
        if (roleForm.getEditing()) {
            String[] usersNotRemoved = updateRole(roleForm, sessionInfo);
            if (usersNotRemoved.length != 0) {
                saveError(request, "availableRoles.error.groupsRequired", Utils.commaList(usersNotRemoved));
            }

        } else {
            createRole(roleForm, sessionInfo);
        }

        saveMessage(request, "availableRoles.roleCreated", roleForm.getRolename());
        // we need to reset the menu items as they could have changed here.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);
        return cancel(mapping, form, request, response);
    }

    private void createRole(RoleForm roleForm, SessionInfo sessionInfo) throws Exception {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(sessionInfo.getUser().getRealm());
        try {
            Role role = userDatabase.createRole(roleForm.getRolename());
            List<String> selectedUsers = roleForm.getUserList();
            updateUserRoles(role, selectedUsers, userDatabase.getRealm());
            fireSuccessfulEvent(sessionInfo, CoreEventConstants.GROUP_CREATED, role, selectedUsers);
        } catch (Exception expt) {
            fireUnsuccessfulEvent(roleForm, sessionInfo, CoreEventConstants.GROUP_CREATED, expt);
            throw expt;
        }
    }
    
    private String[] updateRole(RoleForm roleForm, SessionInfo sessionInfo) throws Exception {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(sessionInfo.getUser().getRealm());
        try {
            Role role = userDatabase.getRole(roleForm.getRolename());
            List<String> selectedUsers = roleForm.getUserList();
            String[] usersNotRemoved = updateUserRoles(role, selectedUsers, userDatabase.getRealm());
            fireSuccessfulEvent(sessionInfo, CoreEventConstants.GROUP_UPDATED, role, selectedUsers);
            return usersNotRemoved;
        } catch (Exception expt) {
            fireUnsuccessfulEvent(roleForm, sessionInfo, CoreEventConstants.GROUP_CREATED, expt);
            throw expt;
        }
    }

    private void fireSuccessfulEvent(SessionInfo sessionInfo, int eventId, Role role, List<String> selectedUsers) {
        CoreEvent coreEvent = new CoreEvent(this, eventId, role, sessionInfo);
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, role.getPrincipalName());
        if (!selectedUsers.isEmpty()) {
            for (int index = 0; index < selectedUsers.size(); index++) {
                String username = selectedUsers.get(index);
                coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_ACCOUNT + Integer.valueOf(index), username);
            }
        }
        CoreServlet.getServlet().fireCoreEvent(coreEvent);
    }

    private void fireUnsuccessfulEvent(RoleForm roleForm, SessionInfo sessionInfo, int eventId, Exception ex) {
        CoreEvent coreEvent = new CoreEvent(this, eventId, null, sessionInfo, ex);
        coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, roleForm.getRolename());
        CoreServlet.getServlet().fireCoreEvent(coreEvent);
    }
    
    private String[] updateUserRoles(Role role, List<String> selectedUsers, Realm realm) throws Exception {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(realm);
        User[] usersInRole = userDatabase.getUsersInRole(role);
        
        Collection<String> usersNotRemoved = new TreeSet<String>();
        for (User user : usersInRole) {
            String principalName = user.getPrincipalName();
            if (selectedUsers.contains(principalName)) {
                // role is already assigned so make sure this don't happen again
                selectedUsers.remove(principalName);
            } else {
                Role[] updatedRoles = removeRole(role, user.getRoles());
                try {
                    userDatabase.updateAccount(user, user.getEmail(), user.getFullname(), updatedRoles);
                } catch (GroupsRequiredForUserException e) {
                    usersNotRemoved.add(principalName);
                }
            }
        }
        
        for (String principalName : selectedUsers) {
            User user = userDatabase.getAccount(principalName);
            Role[] updatedRoles = addRole(role, user.getRoles());
            userDatabase.updateAccount(user, user.getEmail(), user.getFullname(), updatedRoles);
        }
        return usersNotRemoved.toArray(new String[usersNotRemoved.size()]);
    }
    
    private Role[] addRole(Role role, Role[] userRoles) {
        Role[] updatedRoles = new Role[userRoles.length + 1];
        System.arraycopy(userRoles, 0, updatedRoles, 0, userRoles.length);
        updatedRoles[userRoles.length] = role;
        return updatedRoles;
    }
    
    private Role[] removeRole(Role role, Role[] userRoles) {
        Collection<Role> assignedRoles = new ArrayList<Role>(Arrays.asList(userRoles));
        for (Iterator<Role> itr = assignedRoles.iterator(); itr.hasNext();) {
            Role assignedRole = (Role) itr.next();
            if (role.getPrincipalName().equals(assignedRole.getPrincipalName())) {
                itr.remove();
            }
        }
        return assignedRoles.toArray(new Role[assignedRoles.size()]);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}