
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

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
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
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.properties.impl.userattributes.UserAttributes;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.PublicKeyStore;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.security.forms.ShowAvailableAccountsForm;
import com.adito.security.forms.UserAccountForm;
import com.adito.table.actions.AbstractPagerAction;

/**
 */
public class ShowAvailableAccountsDispatchAction extends AbstractPagerAction {
    private static final Log LOG = LogFactory.getLog(SetPasswordAction.class);

    /**
     */
    public ShowAvailableAccountsDispatchAction() {
        super(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE });
    }

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
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        CoreUtil.clearFlow(request);
        ShowAvailableAccountsForm accountsForm = (ShowAvailableAccountsForm) form;
        
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        User[] users;
        try {
            String filterText = accountsForm.getFilterText();
            String filter = Util.isNullOrTrimmedBlank(filterText) ? UserDatabase.WILDCARD_SEARCH : filterText;
            int maxUserResults = userDatabase.getMaxUserResults();
            users = userDatabase.listAllUsers(filter, maxUserResults + 1);
            
            if (users.length > maxUserResults) {
                int newLength = users.length - 1;
                User[] copy = new User[newLength];
                System.arraycopy(users, 0, copy, 0, newLength);
                users = copy;
                saveMessage(request, "availableAccounts.match.limited", maxUserResults);
            }
        } catch (Exception e) {
            LOG.error("Could not list users.", e);
            String exceptionMessageChain = Util.getExceptionMessageChain(e);
            saveError(request, "availableAccounts.cannotListAccounts", exceptionMessageChain);
            users = new User[0];
        }
        accountsForm.initialize(users, request.getSession());
        ActionMessages messages = new ActionMessages();
        if (userDatabase.supportsAccountCreation() && !userDatabase.supportsPasswordChange()) {
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("availableAccounts.noPasswordChange.text"));
        }
        if (!userDatabase.supportsAccountCreation() && userDatabase.supportsPasswordChange()) {
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("availableAccounts.noAccountCreation.text"));
        } else if (!userDatabase.supportsAccountCreation() && !userDatabase.supportsPasswordChange()) {
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("availableAccounts.noAccountCreationAndNoPasswordChange.text"));
        }
        if (messages.size() > 0) {
            saveMessages(request, messages);
        }
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
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
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
    public ActionForward redisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ShowAvailableAccountsForm accountsForm = (ShowAvailableAccountsForm) form;
        accountsForm.reInitialize(request.getSession());
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
    public ActionForward password(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        String[] accounts = request.getParameterValues("username");
        if (accounts == null || accounts.length != 1) {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.singleAccountNotSelected"));
            saveErrors(request, mesgs);
            return list(mapping, form, request, response);
        } else {
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            User user = udb.getAccount(accounts[0]);
            request.getSession().setAttribute("setPassword.user", user);
            return mapping.findForward("setPassword");
        }
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward setPassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        User user = udb.getAccount(((UserAccountForm) form).getUsername());
        request.getSession().setAttribute("setPassword.user", user);
        return mapping.findForward("setPassword");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        String[] accounts = request.getParameterValues("username");
        if (accounts == null || accounts.length != 1) {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.singleAccountNotSelected"));
            saveErrors(request, mesgs);
            return list(mapping, form, request, response);
        } else {
            return mapping.findForward("edit");
        }
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmAccountDeletion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);
        String[] accounts = request.getParameterValues("username");
        if (accounts == null || accounts.length != 1) {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.singleAccountNotSelected"));
            saveErrors(request, mesgs);
            return list(mapping, form, request, response);
        } else {
            return mapping.findForward("confirmAccountDeletion");
        }
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward accountDeletion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);

        User currentUser = isSetupMode() ? null : LogonControllerFactory.getInstance().getUser(request);

        String[] accounts = request.getParameterValues("username");
        boolean found = false;
        for (int i = 0; i < accounts.length; i++) {
            if (currentUser != null && accounts[i].equals(currentUser.getPrincipalName())) {
                found = true;
            }
        }
        if (!found) {
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            for (int i = 0; accounts != null && i < accounts.length; i++) {
                User user = udb.getAccount(accounts[i]);
                if (udb.supportsAccountCreation()) {
                    try {
                        // check to see if the user has a session, if so then logoff.
                        Iterator loggedOnUserSessions = LogonControllerFactory.getInstance().getActiveSessions().values()
                                        .iterator();
                        while (loggedOnUserSessions.hasNext()) {
                            SessionInfo element = (SessionInfo) loggedOnUserSessions.next();
                            if (element.getUser().equals(user)) {
                                element.invalidate();
                            }
                        }
                        // Revoke all polices from the user
                        PolicyDatabaseFactory.getInstance().revokeAllPoliciesFromPrincipal(user);
                        udb.deleteAccount(user);
                        CoreServlet.getServlet().fireCoreEvent(
                            new CoreEvent(this, CoreEventConstants.USER_REMOVED, null, null, CoreEvent.STATE_SUCCESSFUL)
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName())
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_FULL_NAME, user.getFullname()));
                    } catch (Exception e) {
                        CoreServlet.getServlet().fireCoreEvent(
                            new CoreEvent(this, CoreEventConstants.USER_REMOVED, null, null, e)
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName())
                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_FULL_NAME, user.getFullname()));
                        throw e;
                    }
                }
            }
        } else {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.cannotDeleteOwnAccount"));
            saveErrors(request, mesgs);
        }
        return mapping.findForward("refresh");
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward enable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        String[] accounts = request.getParameterValues("username");
        ActionMessages mesgs = new ActionMessages();
        if (accounts == null || accounts.length == 0) {
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.atLeastOneAccountNotSelected"));
            saveErrors(request, mesgs);
        } else {
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            for (int i = 0; accounts != null && i < accounts.length; i++) {
                User user = udb.getAccount(accounts[i]);
                boolean disabled = !PolicyUtil.isEnabled(user);
                SessionInfo session = this.getSessionInfo(request);
                if (disabled) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Re-enabling user " + user.getPrincipalName());
                    }
                    PolicyUtil.setEnabled(user, true, null, session);
                }
                LogonControllerFactory.getInstance().unlockUser(user.getPrincipalName());
            }
        }
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
    public ActionForward confirmDisableAccount(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_DELETE, request);
        String[] accounts = request.getParameterValues("username");
        if (accounts == null || accounts.length != 1) {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.atLeastOneAccountNotSelected"));
            saveErrors(request, mesgs);
            return list(mapping, form, request, response);
        } else {
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            for (int i = 0; accounts != null && i < accounts.length; i++) {
                User user = udb.getAccount(accounts[i]);
                if (null != user.getPrincipalName() && user.getPrincipalName().equals(this.getSessionInfo(request).getUser().getPrincipalName())) {
                    ActionMessages mesgs = new ActionMessages();
                    mesgs.add(Globals.ERROR_KEY, new ActionMessage("status.sessions.cannotLogoffYourself"));
                    saveErrors(request, mesgs);
                    return new ActionForward("/confirmDisableAccount.do");
                }
            }
            disable(mapping, form, request, response);
        }
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
    public ActionForward disable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        String[] accounts = request.getParameterValues("username");
        ActionMessages mesgs = new ActionMessages();
        if (accounts == null || accounts.length == 0) {
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("availableAccounts.atLeastOneAccountNotSelected"));
            saveErrors(request, mesgs);
        } else {
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
            for (int i = 0; accounts != null && i < accounts.length; i++) {
                User user = udb.getAccount(accounts[i]);
                SessionInfo info = this.getSessionInfo(request);
                boolean disabled = !PolicyUtil.isEnabled(user);
                if (!disabled) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Disabling user " + user.getPrincipalName());
                    }
                    PolicyUtil.setEnabled(user, false, null, info);
                    if (LogonControllerFactory.getInstance().isAdministrator(user)) {
                        mesgs.add(Globals.MESSAGE_KEY, new ActionMessage("info.superUserDisabled"));
                        saveErrors(request, mesgs);
                    }

                }
            }
        }
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
    public ActionForward sendMessage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] accounts = request.getParameterValues("username");
        if (accounts != null && accounts.length == 1) {
            return CoreUtil.addParameterToForward(mapping.findForward("sendMessage"), "users", accounts[0]);
        }
        return list(mapping, form, request, response);
    }
    
    /**
     * Confirm the reset of a users private key.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward confirmResetPrivateKey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[] accounts = request.getParameterValues("username");
        if (accounts != null && accounts.length == 1) {
            int resourceId = getSessionInfo(request).getUser().getRealm().getResourceId();
            User account = UserDatabaseManager.getInstance().getUserDatabase(resourceId).getAccount(accounts[0]);
            request.getSession().setAttribute(Constants.EDITING_ITEM, account);
            return mapping.findForward("confirmResetPrivateKey");
        }
        return list(mapping, form, request, response);
    }

    /**
     * Reset of a users private key.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward resetPrivateKey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = (User) request.getSession().getAttribute(Constants.EDITING_ITEM);
        PolicyUtil.checkPermission(PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, request);
        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
        /* We must delete all confidential attribute values */
        for (PropertyDefinition propertyDefinition : propertyClass.getDefinitions()) {
            AttributeDefinition attributeDefinition = (AttributeDefinition) propertyDefinition;
            if (attributeDefinition.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                Property.setProperty(new UserAttributeKey(user, attributeDefinition.getName()), (String) null, getSessionInfo(request));
            }
        }
        PublicKeyStore.getInstance().removeKeys(user.getPrincipalName());
        return list(mapping, form, request, response);
    }
    
    /**
     * Toggle only show enabled accounts.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward displayFilterChanged(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        ShowAvailableAccountsForm accountsForm = (ShowAvailableAccountsForm) form;
        accountsForm.rebuildModel();
        return mapping.findForward("display");
    }
    
    
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}