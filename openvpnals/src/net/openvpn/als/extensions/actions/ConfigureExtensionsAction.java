
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
			
package net.openvpn.als.extensions.actions;

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

import net.openvpn.als.core.actions.AbstractMultiFormDispatchAction;
import net.openvpn.als.extensions.forms.ConfigureExtensionsForm;
import net.openvpn.als.extensions.store.ExtensionStore;
import net.openvpn.als.extensions.store.ExtensionStoreDescriptor;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;

public final class ConfigureExtensionsAction extends AbstractMultiFormDispatchAction {

    final static Log log = LogFactory.getLog(ConfigureExtensionsAction.class);

    public ConfigureExtensionsAction() {
		super(PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE }, ConfigureExtensionsForm.EXTENSIONS_TAB_ID);
	}
    
    @SuppressWarnings("unchecked")
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response, String name) throws Exception {
        ActionForward actionForward = super.dispatchMethod(mapping, form, request, response, name);
        ConfigureExtensionsForm configureExtensionsForm = (ConfigureExtensionsForm) form;
        ActionMessages errs = new ActionMessages();
        try {
            ExtensionStoreDescriptor descriptor = ExtensionStore.getInstance().getDownloadableExtensionStoreDescriptor( request.getParameter("connect") != null || 
                Property.getPropertyBoolean(new SystemConfigKey("updates.automaticallyConnectToApplicationStore")));
            configureExtensionsForm.setDescriptor(descriptor);
        } catch (Exception e) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("extensionStore.failedToContactStore", e.getMessage()));
            this.saveErrors(request, errs);
        }
        return actionForward;
    }

}