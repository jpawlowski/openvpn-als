
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
			
package net.openvpn.als.security.actions;

import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.boot.PropertyClassManager;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreEventConstants;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.attributes.AttributeDefinition;
import net.openvpn.als.properties.impl.realms.RealmKey;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import net.openvpn.als.properties.impl.userattributes.UserAttributeKey;
import net.openvpn.als.properties.impl.userattributes.UserAttributes;
import net.openvpn.als.security.AuthenticationScheme;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.PasswordPolicyViolationException;
import net.openvpn.als.security.PublicKeyStore;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabase;
import net.openvpn.als.security.forms.SetPasswordForm;

/**
 */
public class SetPasswordAction extends AuthenticatedDispatchAction {
	final static Log log = LogFactory.getLog(SetPasswordAction.class);

	/**
	 */
	public SetPasswordAction() {
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
		User user = (User) request.getSession().getAttribute("setPassword.user");
		((SetPasswordForm) form).initialize(user);
		if (((SetPasswordForm) form).getReferer() == null) {
			((SetPasswordForm) form).setReferer(getReferer(request));
		}
		request.getSession().removeAttribute("setPassword.user");
		ActionMessages messages = new ActionMessages();
		messages.add(Globals.MESSAGE_KEY,
			new ActionMessage("setPassword.message.passwordPolicy",
							Property.getProperty(new RealmKey("security.password.pattern.description", user.getRealm()
											.getResourceId()))));
		saveMessages(request, messages);
		CoreUtil.addRequiredFieldMessage(this, request);
		return mapping.findForward("display");
	}
    
    private static String getReferer(HttpServletRequest request) {
        if(CoreUtil.isRefererInRequest(request)) {
            return CoreUtil.getRequestReferer(request);
        }
        return CoreUtil.getReferer(request);
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
		SetPasswordForm setPasswordForm = (SetPasswordForm) form;
        User user = setPasswordForm.getUser();
		UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());

		if (user == null) {
			user = (User) this.getSessionInfo(request).getHttpSession().getAttribute("newUser");
		}

		if (!udb.supportsPasswordChange()) {
			throw new Exception("Underlying database does not support changing of passwords.");
		}
		SessionInfo info = this.getSessionInfo(request);

		// Read in all of the confidential user attribute values

		/* BPS - Can only do this if the users key is currently loaded */

		Properties confidentialAttributes = new Properties();
		UserAttributes userAttributes = (UserAttributes) PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
		if ("automatic".equals(Property.getProperty(new SystemConfigKey("security.privateKeyMode")))
						&& PublicKeyStore.getInstance().hasLoadedKey(user.getPrincipalName())) {
			for (PropertyDefinition def : userAttributes.getDefinitions()) {
				AttributeDefinition attrDef = (AttributeDefinition) def;
				if (attrDef.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
					String val = attrDef.getPropertyClass()
					.retrieveProperty(new UserAttributeKey(user, def.getName()));
					if(val == null) {
						val = def.getDefaultValue();
					}
					confidentialAttributes.setProperty(def.getName(), val);
				}
			}
		}

		try {

			char[] creds = LogonControllerFactory.getInstance()
							.getPasswordFromCredentials((AuthenticationScheme) request.getSession()
											.getAttribute(Constants.AUTH_SESSION));

			if (creds == null) {
                HttpSession httpSession = getSessionInfo(request).getHttpSession();
                httpSession.setAttribute("newUser", user);
                // as the form will be reset, we need to store the current values to be used later
                httpSession.setAttribute(SetPasswordForm.SAVED_PASSWORD, setPasswordForm.getConfirmPassword());
                httpSession.setAttribute(SetPasswordForm.SAVED_FORCE_PASSWORD_CHANGE, setPasswordForm.getForceChangePasswordAtLogon());
                String forwardTo = Util.urlEncode(CoreUtil.addParameterToPath(request.getServletPath(), "action", "commit"));
                return new ActionForward("/promptForSessionPassword.do?forwardTo=" + forwardTo, false);
			}

			udb.setPassword(user.getPrincipalName(),
				setPasswordForm.getNewPassword(),
				setPasswordForm.getForceChangePasswordAtLogon(),
				LogonControllerFactory.getInstance().getUser(request),
				new String(creds));

			/* Only attempt to re-encrypt user attributes if users key is loaded */
			if ("automatic".equals(Property.getProperty(new SystemConfigKey("security.privateKeyMode")))) {
				if(PublicKeyStore.getInstance().hasLoadedKey(user.getPrincipalName())) {
					PublicKeyStore.getInstance().removeKeys(user.getPrincipalName());
					PublicKeyStore.getInstance().verifyPrivateKey(user.getPrincipalName(), setPasswordForm.getNewPassword().toCharArray());
					for(Iterator i = confidentialAttributes.keySet().iterator(); i.hasNext(); ) {
						String n = (String)i.next();
						AttributeDefinition attrDef = (AttributeDefinition) userAttributes.getDefinition(n);
						if (attrDef.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
							Property.setProperty(new UserAttributeKey(user, n),
								confidentialAttributes.getProperty(n),
								info);
						}
					}
				}
			}
			else {
                PublicKeyStore.getInstance().removeCachedKeys(user.getPrincipalName());
			}

			CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
							CoreEventConstants.CHANGE_PASSWORD,
							null,
							info,
							CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID,
				user.getPrincipalName()));
			return mapping.findForward("success");
		} catch (PasswordPolicyViolationException e) {
			saveError(request, "setPassword.error.doesNotMatchPolicy");
			return mapping.findForward("display");
		} catch (Exception e) {
			CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
							CoreEventConstants.CHANGE_PASSWORD,
							null,
							info,
							CoreEvent.STATE_UNSUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID,
				user.getPrincipalName()));
			throw e;
		} finally {
		}
	}

	public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
	}

}