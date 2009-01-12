
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
			
package com.adito.install.actions;

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

import com.adito.boot.KeyStoreManager;
import com.adito.install.forms.SelectCertificateSourceForm;
import com.adito.install.forms.SetKeyStorePasswordForm;
import com.adito.keystore.actions.ShowKeyStoreDispatchAction;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.DefaultWizardSequence;
import com.adito.wizard.WizardStep;


/**
 * Implementatation of a {@link AbstractInstallWizardAction} that allows a
 * source of Aditos main certificate to be chosed. This is the first
 * page in the installation wizard.
 * 
 * @see com.adito.install.forms.ConfigureUserDatabaseForm
 */
public class SelectCertificateSourceAction extends AbstractInstallWizardAction {

    static Log log = LogFactory.getLog(ShowKeyStoreDispatchAction.class);

    /* (non-Javadoc)
     * @see com.adito.wizard.actions.AbstractWizardAction#next(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        super.next(mapping, form, request, response);
        SelectCertificateSourceForm wizardForm = (SelectCertificateSourceForm)form;
        if(SelectCertificateSourceForm.CREATE_NEW_CERTIFICATE.equals(wizardForm.getCertificateSource())) {
            return mapping.findForward("setKeyStorePassword");
        }
        else if(SelectCertificateSourceForm.IMPORT_EXISTING_CERTIFICATE.equals(wizardForm.getCertificateSource())) {
            getWizardSequence(request).removeAttribute(SetKeyStorePasswordForm.ATTR_KEY_STORE_PASSWORD);
            return mapping.findForward("importExistingCertificate");
        }
        else if(SelectCertificateSourceForm.USE_CURRENT_CERTIFICATE.equals(wizardForm.getCertificateSource())) {
            getWizardSequence(request).removeAttribute(SetKeyStorePasswordForm.ATTR_KEY_STORE_PASSWORD);
            return mapping.findForward("useCurrentCertificate");
        }  
        return unspecified(mapping, form, request, response);
    }
    

    /* (non-Javadoc)
     * @see com.adito.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        /* If there was an exception loading the keystore, display it as an error */
        ActionMessages errs = new ActionMessages();
        Throwable ex = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getKeyStoreException();
        if(ex != null) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.selectCertificateSource.error.couldNotLoadKeystore", ex.getMessage()));
            saveErrors(request, errs);
        }
        return super.unspecified(mapping, form, request, response);
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.actions.AbstractWizardAction#previous(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        throw new Exception("No previous steps.");
    }

    
    /* (non-Javadoc)
     * @see com.adito.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = mapping.findForward("finish");
        DefaultWizardSequence seq = new DefaultWizardSequence(fwd, "install", "installation", "/shutdown.do?actionTarget=installShutdown", "installWizard", this.getSessionInfo(request));
        seq.addStep(new WizardStep("/selectCertificateSource.do", true));
        seq.addStep(new WizardStep("/selectUserDatabase.do"));
        seq.addStep(new WizardStep("/configureSuperUser.do"));
        seq.addStep(new WizardStep("/webServer.do"));
        seq.addStep(new WizardStep("/configureProxies.do"));
        seq.addStep(new WizardStep("/installationSummary.do"));
        return seq;
    }
}
