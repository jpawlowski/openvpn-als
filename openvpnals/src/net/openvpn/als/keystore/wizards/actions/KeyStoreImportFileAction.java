
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
			
package net.openvpn.als.keystore.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.keystore.wizards.AbstractKeyStoreImportType;
import net.openvpn.als.keystore.wizards.KeyStoreImportTypeManager;
import net.openvpn.als.keystore.wizards.forms.KeyStoreImportTypeForm;
import net.openvpn.als.keystore.wizards.types.ReplyFromCAImportType;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.actions.AbstractWizardAction;

/**
 * Implementation of {@link net.openvpn.als.wizard.actions.AbstractWizardAction} that
 * is used in the key store import wizard. Here the administrator selects
 * the type of import. 
 */

public class KeyStoreImportFileAction extends AbstractWizardAction {

    /**
     * Constructor
     */
    public KeyStoreImportFileAction() {
        super(PolicyConstants.KEYSTORE_RESOURCE_TYPE, new Permission[] {
                        PolicyConstants.PERM_CHANGE
                    });
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        AbstractKeyStoreImportType importType = KeyStoreImportTypeManager.getInstance().getType(
            (String)getWizardSequence(request).getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
        importType.init(request);
        return fwd;
    }
}
