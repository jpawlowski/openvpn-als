
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.userattributes.UserAttributeKey;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.PersonalAnswer;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.security.forms.PersonalAnswersForm;

public class SetPersonalAnswersAction extends AuthenticatedAction {

    public SetPersonalAnswersAction() {
        super(PolicyConstants.ATTRIBUTES_RESOURCE_TYPE, new Permission[] {
            PolicyConstants.PERM_MAINTAIN
        });
    }

    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	
        PersonalAnswersForm f = (PersonalAnswersForm) form;
        User user = LogonControllerFactory.getInstance().getUser(request);
        
    	PersonalAnswer answer;
    	for(Iterator it = f.getPersonalAnswers().iterator(); it.hasNext();) {
    		answer = (PersonalAnswer) it.next();
            Property.setProperty(new UserAttributeKey(user, answer.getId()), answer.getAnswer(), getSessionInfo(request));
    	}

        CoreUtil.removePageInterceptListener(request.getSession(), "changePersonalAnswers");
        return mapping.findForward("success");
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}