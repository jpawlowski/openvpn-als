
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

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.boot.PropertyList;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.RedirectWithMessages;
import com.adito.core.UserDatabaseManager;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyUtil;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.security.Constants;
import com.adito.security.GroupsRequiredForUserException;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.security.UserDatabaseException;
import com.adito.security.forms.UserAccountForm;


/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedDispatchAction}
 * that allows an administrator to create or edit a user account.
 * <p>
 * If the current <i>User Database</i> does not support account creation then
 * editing of the basic details is not allowed. The generic details such as 
 * 'enabled' and the user attributes may be changed.
 */
public class ShowAccountAction extends AuthenticatedDispatchAction {
    /**
     * Constructor.
     */
    public ShowAccountAction() {
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
        request.getSession().removeAttribute(Constants.EDITING_ITEM);
        return mapping.findForward("display");
    }


    /**
     * Set the password.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward setPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
    	UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        User user = udb.getAccount(((UserAccountForm) form).getUsername());
		request.getSession().setAttribute("setPassword.user", user);
		return mapping.findForward("setPassword");		
	}
    
    /**
     * Create a new account.
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
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);        
        ((UserAccountForm) form).initialize(null, false, request);
        ((UserAccountForm) form).setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("refresh");
    }

    /**
     * Edit an existing account.
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
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        String username = request.getParameter("username");
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        User user = udb.getAccount(username);
        ((UserAccountForm) form).initialize(user, true, request);
        ((UserAccountForm) form).setReferer(CoreUtil.getReferer(request));
        return mapping.findForward("display");
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");        
    }

    /**
     * Commit the details to the user database.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response 
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        UserAccountForm account = (UserAccountForm) form;
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        SessionInfo info = this.getSessionInfo(request);
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        User user = null;
        if(udb.supportsAccountCreation()) {
            PropertyList roleList = account.getRolesList();
            int idx = 0;
            Role[] roles = new Role[roleList.size()];
            for(Iterator i = roleList.iterator(); i.hasNext(); ) {
                roles[idx++] = udb.getRole((String)i.next()); 
            }

            if (account.getEditing()) {
                user = udb.getAccount(account.getUsername());
                try {
                    udb.updateAccount(user, account.getEmail(), account.getFullname(), roles);
                    CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.USER_EDITED, user, info)
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName())
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_FULL_NAME, user.getFullname())
                    	.addAttribute(CoreAttributeConstants.EVENT_ATTR_ACCOUNT_EMAIL, user.getEmail());

                    if(roles.length != 0) {
                        for(int i = 0; i < roles.length; i++ ) {
                            coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_GROUP + Integer.toString(i+1), roles[i].getPrincipalName()); 
                        }
                    }
                    CoreServlet.getServlet().fireCoreEvent(coreEvent);
                } catch (GroupsRequiredForUserException e) {
                    saveError(request, "createAccount.error.groupsRequired");
                    return mapping.findForward("display");
                } catch (UserDatabaseException e) {
                    if(UserDatabaseException.INTERNAL_ERROR == e.getCode()) {
                        handleException(CoreEventConstants.USER_CREATED, account, info, roles, e);
                        throw e;
                    } else {
                        saveError(request, e.getBundleActionMessage());
                        return mapping.findForward("display");
                    }
                } catch (Exception e) {
                    handleException(CoreEventConstants.USER_EDITED, account, info, roles, e);
                	throw e;
                }
            } else {
            	try {
                    user = udb.createAccount(account.getUsername(), String.valueOf((int) (Math.random() * 100000)),
                    // Set a random password
                         account.getEmail(), account.getFullname(), roles);
                    CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.USER_CREATED, null, info, CoreEvent.STATE_SUCCESSFUL)
                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, account.getUsername())
                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_FULL_NAME, account.getFullname())
                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_ACCOUNT_EMAIL, account.getEmail());

                    if(roles.length != 0) {
                        for(int i = 0; i < roles.length; i++ ) {
                            coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_GROUP + Integer.toString(i+1), roles[i].getPrincipalName()); 
                        }
                    }
                    CoreServlet.getServlet().fireCoreEvent(coreEvent);
            	} catch (GroupsRequiredForUserException e) {
                    saveError(request, "createAccount.error.groupsRequired");
                    return mapping.findForward("display");
            	} catch (UserDatabaseException e) {
                    if(UserDatabaseException.INTERNAL_ERROR == e.getCode()) {
                        handleException(CoreEventConstants.USER_CREATED, account, info, roles, e);
                        throw e;
                    } else {
                        saveError(request, e.getBundleActionMessage());
                        return mapping.findForward("display");
                    }
            	} catch (Exception e) {
                    handleException(CoreEventConstants.USER_CREATED, account, info, roles, e);
            		throw e;
            	}
            }
        }
        else {
            user = udb.getAccount(account.getUsername());
        }

        // Update the attributes
        for(Iterator i = account.getAttributeValueItems().iterator(); i.hasNext(); ) {
           AttributeValueItem v = (AttributeValueItem)i.next();
           if(v.getDefinition().getVisibility() != AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
               Property.setProperty(new UserAttributeKey(user, v.getDefinition().getName()), v.getDefinition().formatAttributeValue(v.getValue()), info);
           }
        }
        // XXX HACK to ensure user attributes in memory are the same as persisted
        for(Iterator j = LogonControllerFactory.getInstance().getActiveSessions().entrySet().iterator(); j.hasNext(); ) {
            Map.Entry e = (Map.Entry)j.next();
            SessionInfo sinfo = (SessionInfo)e.getValue();
            if(sinfo.getUser().getPrincipalName().equals(user.getPrincipalName())) {
                sinfo.setUser(user);
            }
        }

        // Reset the enabled state if it is different
        if (PolicyUtil.isEnabled(user) != account.isEnabled()) {
            PolicyUtil.setEnabled(user, account.isEnabled(), null, null);
		}

        // we need to reset the menu items as they could have changed here.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);

        // Go to the set password page if this is a new account and set password was selected
        if (udb.supportsPasswordChange() && (account.isSetPassword() || !account.getEditing())) {
            request.getSession().setAttribute("setPassword.user", user);
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createAccount.message.accountSaved"));
            saveMessages(request, msgs);
            return mapping.findForward("setPassword");
        } else {
            return new RedirectWithMessages(mapping.findForward("success"), request);
        }
    }

    private void handleException(int eventId, UserAccountForm account, SessionInfo info, Role[] roles, Exception e) {
        CoreEvent coreEvent = new CoreEvent(this, eventId, null, info, e)
            .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, account.getUsername())
            .addAttribute(CoreAttributeConstants.EVENT_ATTR_FULL_NAME, account.getFullname())
        	.addAttribute(CoreAttributeConstants.EVENT_ATTR_ACCOUNT_EMAIL, account.getEmail());

        if(roles.length != 0) {
            for(int i = 0; i < roles.length; i++ ) {
                coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_GROUP + Integer.toString(i+1), roles[i].getPrincipalName()); 
            }
        }
        CoreServlet.getServlet().fireCoreEvent(coreEvent);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return new RedirectWithMessages(mapping.findForward("cancel"), request);
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /**
     * Reset all user attributes.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward resetUserAttributes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        UserAccountForm account = (UserAccountForm) form;
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        for(Iterator i = account.getAttributeValueItems().iterator(); i.hasNext(); ) {
            AttributeValueItem v = (AttributeValueItem)i.next();
            v.setValue(v.getDefinition().parseValue(v.getDefinition().getDefaultValue()));
        }
        return mapping.findForward("display");
    }
}