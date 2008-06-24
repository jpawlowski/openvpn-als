
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
			
package com.adito.properties.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.AbstractPropertyKey;
import com.adito.boot.PropertyDefinition;
import com.adito.policyframework.ResourceStack;
import com.adito.properties.forms.AbstractPropertiesForm;
import com.adito.properties.forms.ProfilePropertiesForm;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;

public class GlobalProfilePropertiesAction extends AbstractProfilePropertiesAction {
    static Log log = LogFactory.getLog(GlobalProfilePropertiesAction.class);

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ((ProfilePropertiesForm)form).setProfileScope(Constants.SCOPE_GLOBAL);
        return super.unspecified(mapping, form, request, response);
    }  

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public AbstractPropertyKey createKey(PropertyDefinition definition, AbstractPropertiesForm form, SessionInfo sessionInfo) {
        ProfilePropertiesForm f = (ProfilePropertiesForm)form;
        return new ProfilePropertyKey(f.getSelectedPropertyProfile(), null, definition.getName(), sessionInfo.getUser().getRealm().getResourceId());
    }
}