
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
			
package net.openvpn.als.install.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.install.forms.SelectCertificateSourceForm;
import net.openvpn.als.install.forms.SelectUserDatabaseForm;
import net.openvpn.als.security.UserDatabaseDefinition;

/**
 * Implementatation of a {@link AbstractInstallWizardAction} that allows the
 * user database to be chosen.
 * 
 * @see net.openvpn.als.install.forms.ConfigureUserDatabaseForm
 */
public class SelectUserDatabaseAction extends AbstractInstallWizardAction {

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#previous(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.previous(mapping, form, request, response);
        String certificateSource = (String)getWizardSequence(request).getAttribute(SelectCertificateSourceForm.ATTR_CERTIFICATE_SOURCE, "");
        if (SelectCertificateSourceForm.CREATE_NEW_CERTIFICATE.equals(certificateSource)) {
            return mapping.findForward("createNewCertificate");
        } else if (SelectCertificateSourceForm.IMPORT_EXISTING_CERTIFICATE.equals(certificateSource)) {
            return mapping.findForward("importExistingCertificate");
        } else if (SelectCertificateSourceForm.USE_CURRENT_CERTIFICATE.equals(certificateSource)) {
            return mapping.findForward("selectCertificateSource");
        } else {
            return unspecified(mapping, form, request, response);
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#next(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = super.next(mapping, form, request, response);
        SelectUserDatabaseForm udf = (SelectUserDatabaseForm)form;
        UserDatabaseDefinition def = UserDatabaseManager.getInstance().getUserDatabaseDefinition(udf.getUserDatabase());
        if(def.getInstallationCategory() > 0) {
            return mapping.findForward("configureUserDatabase");
        }
        return fwd;
    }
}
