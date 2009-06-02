
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.keystore.actions.ShowKeyStoreDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.DefaultWizardSequence;
import net.openvpn.als.wizard.WizardStep;
import net.openvpn.als.wizard.actions.AbstractWizardAction;

/**
 * Implemetnation of an {@link net.openvpn.als.wizard.actions.AbstractWizardAction}
 * used in the key store import wizard. Here the administrator selects the 
 * type of import.
 */
public class KeyStoreImportTypeAction extends AbstractWizardAction {

    static Log log = LogFactory.getLog(ShowKeyStoreDispatchAction.class);
    
    /**
     * Constructor
     */
    public KeyStoreImportTypeAction() {
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
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = mapping.findForward("finish");
        DefaultWizardSequence seq = new DefaultWizardSequence(fwd, "keystore", "keyStoreImportWizard", CoreUtil.getReferer(request), "keyStoreImportWizard", this.getSessionInfo(request));
        seq.addStep(new WizardStep("/keyStoreImportType.do", true));
        seq.addStep(new WizardStep("/keyStoreImportFile.do"));
        seq.addStep(new WizardStep("/keyStoreImportSummary.do"));
        return seq;
    }
}
