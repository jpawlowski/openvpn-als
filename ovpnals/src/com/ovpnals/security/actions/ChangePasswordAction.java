
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.GlobalWarning;
import com.ovpnals.core.GlobalWarningManager;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.core.GlobalWarning.DismissType;
import com.ovpnals.core.actions.AuthenticatedAction;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.properties.impl.userattributes.UserAttributeKey;
import com.ovpnals.properties.impl.userattributes.UserAttributes;
import com.ovpnals.security.Constants;
import com.ovpnals.security.InvalidLoginCredentialsException;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.PasswordChangeTooSoonException;
import com.ovpnals.security.PasswordPolicyViolationException;
import com.ovpnals.security.PublicKeyStore;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.forms.ChangePasswordForm;

/**
 */
public class ChangePasswordAction extends AuthenticatedAction {
    /**
     */
    public ChangePasswordAction() {
        super(PolicyConstants.PASSWORD_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        ChangePasswordForm f = (ChangePasswordForm) form;
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        if (!udb.supportsPasswordChange()) {
            throw new Exception("Changing of passwords is not supported by the underlying user database.");
        }
        User user = LogonControllerFactory.getInstance().getUser(request);

        SessionInfo info = this.getSessionInfo(request);

        // Read in all of the confidential user attribute values
        Properties confidentialAttributes = new Properties();
        UserAttributes userAttributes = (UserAttributes) PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
        for (PropertyDefinition def : userAttributes.getDefinitions()) {
            AttributeDefinition attrDef = (AttributeDefinition) def;
            if (attrDef.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                confidentialAttributes.setProperty(def.getName(), attrDef.getPropertyClass()
                                .retrieveProperty(new UserAttributeKey(info.getUser(), def.getName())));
            }
        }

        try {

            // Change the password

            udb.changePassword(user.getPrincipalName(), f.getOldPassword(), f.getNewPassword(), false);
            
            PublicKeyStore publicKeyStore = PublicKeyStore.getInstance();
            if ("automatic".equals(Property.getProperty(new SystemConfigKey("security.privateKeyMode")))) {  
                if (publicKeyStore.isPassphraseValid(user.getPrincipalName(), f.getOldPassword())) {
                    publicKeyStore.changePrivateKeyPassphrase(user.getPrincipalName(), f.getOldPassword(), f.getNewPassword());
                    publicKeyStore.removeCachedKeys(user.getPrincipalName());
                    publicKeyStore.verifyPrivateKey(user.getPrincipalName(), f.getNewPassword().toCharArray());

                    // Write back all of the confidential user attribute values
                    for (PropertyDefinition def : userAttributes.getDefinitions()) {
                        AttributeDefinition attrDef = (AttributeDefinition) def;
                        if (attrDef.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                            Property.setProperty(new UserAttributeKey(info.getUser(), def.getName()), confidentialAttributes
                                            .getProperty(def.getName()), info);
                        }
                    }
                } else {
                    // if the keystore is out of sync, the only option is to remove the key
                    // this can happen if the password is changed natively
                    publicKeyStore.removeKeys(user.getPrincipalName());
                    CoreUtil.removePageInterceptListener(request.getSession(), "updatePrivateKeyPassphrase");
                    BundleActionMessage message = new BundleActionMessage("security", "resetPrivateKey.required.message");
                    GlobalWarningManager.getInstance().addToSession(new GlobalWarning(request.getSession(), message));
                }
            }
            else {
                publicKeyStore.removeCachedKeys(user.getPrincipalName());
            }
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
                            CoreEventConstants.CHANGE_PASSWORD,
                            null,
                            info,
                            CoreEvent.STATE_SUCCESSFUL));
            request.getSession().removeAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE);
            GlobalWarningManager.getInstance().removeGlobalWarning(request.getSession(), "globalWarning.passwordNearExpiry");
            CoreUtil.removePageInterceptListener(request.getSession(), "changePassword");            
        } catch (InvalidLoginCredentialsException e) {
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.CHANGE_PASSWORD, null, info, e));

            ActionMessages errors = new ActionMessages();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("security.cannotChangePassword", e.getMessage()));
            saveErrors(request, errors);
            return mapping.findForward("failure");
            
        } catch (PasswordChangeTooSoonException e) {
        	CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.CHANGE_PASSWORD, null, info, e));
            Date requiredData = ((PasswordChangeTooSoonException) e).getRequiredDate();
            saveError(request, "security.cannotChangePassword.tooSoon", formatDate(requiredData));
            return mapping.findForward("failure");
        } catch (PasswordPolicyViolationException e) {
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.CHANGE_PASSWORD, null, info, e));
            saveError(request, "changePassword.error.doesNotMatchPolicy");
            return mapping.findForward("failure");
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.CHANGE_PASSWORD, null, info, e));
            throw e;
        }

        return mapping.findForward("success");
    }

    private static String formatDate(Date toFormat) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(toFormat);
    }
    
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT;
    }
}