
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.keystore.wizards.AbstractKeyStoreImportType;
import net.openvpn.als.keystore.wizards.KeyStoreImportTypeManager;
import net.openvpn.als.keystore.wizards.forms.KeyStoreImportFileForm;
import net.openvpn.als.keystore.wizards.forms.KeyStoreImportTypeForm;
import net.openvpn.als.keystore.wizards.types.ReplyFromCAImportType;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.actions.AbstractWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardFinishForm;

/**
 * Implementation of an {@link net.openvpn.als.wizard.actions.AbstractWizardAction}
 * that is used in the key store import wizard. This is the final action and 
 * performs the actual import using all data gathered in the wizard.  
 */

public class KeyStoreImportFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(KeyStoreImportFinishAction.class);

    /**
     * Constructor
     *
     */
    public KeyStoreImportFinishAction() {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        // Do the install
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence seq = getWizardSequence(request);
        AbstractKeyStoreImportType importType = KeyStoreImportTypeManager.getInstance().getType(
            (String)seq.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
        File f = (File)seq.getAttribute(KeyStoreImportFileForm.ATTR_UPLOADED_FILE, null);
        String alias = (String)seq.getAttribute(KeyStoreImportFileForm.ATTR_ALIAS, null);
        String passphrase = (String)seq.getAttribute(KeyStoreImportFileForm.ATTR_PASSPHRASE, null);
        try {
            importType.doInstall(f, alias, passphrase, seq, LogonControllerFactory.getInstance().getSessionInfo(request));
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "keyStoreImportType." + importType.getName() + ".installed", alias, "", "", "", "", importType.getBundle()));
        }
        catch(Exception e) {
            log.error("Failed to load key.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS, "keyStoreImportType." + importType.getName() + ".installFailed", alias, e.getMessage(), "", "", "", importType.getBundle()));            
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /**
     * Exit the wizard.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        AbstractWizardSequence seq = getWizardSequence(request);
        AbstractKeyStoreImportType importType = KeyStoreImportTypeManager.getInstance().getType(
            (String)seq.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
        ActionForward fwd = cancel(mapping, form, request, response);
        if(importType != null && importType.isRestartRequired()) {
            String orig = fwd.getPath();
            fwd = mapping.findForward("restartRequired"); 
            fwd = CoreUtil.addParameterToForward(fwd, "no", orig);
        }
        return fwd;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#rerun(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward rerun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("rerun");
    }

    protected WizardActionStatus serverAuthenticationKey(AbstractWizardSequence seq) {
        try {
           
        } catch (Exception e) {
            log.error("Failed to install server authentication key.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                "keyStoreImportWizard.keyStoreImportFinish.status.failedToInstallServerAuthenticationKey", e.getMessage());
        }
        return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "keyStoreImportWizard.keyStoreImportFinish.status.serverAuthenticationKeyInstalled");
    }
}
