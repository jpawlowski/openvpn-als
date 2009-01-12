
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

import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.properties.Property;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.security.forms.ChangePasswordForm;

/**
 */
public class ShowChangePasswordAction extends AuthenticatedAction {
    final static Log log = LogFactory.getLog(SetPasswordAction.class);

    /**
     */
    public ShowChangePasswordAction() {
        super(PolicyConstants.PASSWORD_RESOURCE_TYPE, new Permission[] {
            PolicyConstants.PERM_CHANGE
        });
    }

    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {        
        ChangePasswordForm f = (ChangePasswordForm) form;
        ActionMessages messages = new ActionMessages();
        if (request.getSession().getAttribute(Constants.PASSWORD_CHANGE_REASON_MESSAGE) != null) {
            messages.add(Globals.MESSAGE_KEY, (ActionMessage) request.getSession().getAttribute(
                Constants.PASSWORD_CHANGE_REASON_MESSAGE));
        }
        try {
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("changePassword.message.passwordPolicy", 
            	Property
                            .getProperty(new RealmKey("security.password.pattern.description", getSessionInfo(request).getUser()
                                            .getRealm().getResourceId()))));
        } catch (Exception e) {
            log.error("Failed to get password policy text.", e);
        }
        f.setReferer(getReferer(request));
        f.init(getSessionInfo(request).getUser().getPrincipalName());
        saveMessages(request, messages);
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("success");

    }
    
    private static String getReferer(HttpServletRequest request) {
        if(CoreUtil.isRefererInRequest(request)) {
            return CoreUtil.getRequestReferer(request);
        }
        return CoreUtil.getReferer(request);
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT;
    }
}