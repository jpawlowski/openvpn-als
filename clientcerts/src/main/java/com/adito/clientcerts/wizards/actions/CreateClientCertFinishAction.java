
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
			
package com.adito.clientcerts.wizards.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyList;
import com.adito.boot.KeyStoreManager;
import com.adito.boot.Util;
import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.WizardActionStatus;
import com.adito.wizard.actions.AbstractFinishWizardAction;
import com.adito.wizard.forms.AbstractWizardFinishForm;

import com.adito.clientcerts.wizards.forms.CreateClientCertDetailForm;
import com.adito.clientcerts.ClientCertsPlugin;

/**
 * The final action in which the resource is created.
 */
public class CreateClientCertFinishAction extends AbstractFinishWizardAction {
    final static Log log = LogFactory.getLog(CreateClientCertFinishAction.class);

    /**
     * Constructor.
     */
    public CreateClientCertFinishAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
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
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence sequence = getWizardSequence(request);
        String hostname = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_HOSTNAME, "");
        String countryCode =(String) sequence.getAttribute(CreateClientCertDetailForm.ATTR_COUNTRY_CODE, "");
        String organisationalUnit = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_ORGANISATIONAL_UNIT, "");
        String company = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_COMPANY, "");
        String city =  (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_CITY, "");
        String state =  (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_STATE, "");
        try {
		KeyStoreManager km = KeyStoreManager.getInstance(ClientCertsPlugin.KEYSTORE_NAME);
		if (km.getCertificate(hostname) != null) {
			km.deleteCertificate(hostname);
		}
		    String dname = "cn="
				    + Util.escapeForDNString(hostname)
				    + ", ou="
				    + Util.escapeForDNString(organisationalUnit) + ", o="
				    + Util.escapeForDNString(company) + ", l="
				    + Util.escapeForDNString(city) + ", st="
				    + Util.escapeForDNString(state) + ", c="
				    + Util.escapeForDNString(countryCode);
		    km.createKey(hostname, dname);
		    km.reloadKeystore();

		    ClientCertsPlugin.getInstance().getClientCertTrustManager().reloadKeyStore(km.getKeyStore());
            /* try {
                    //CoreServlet.getServlet().fireCoreEvent(evt);
                    //webForward = abstractAuthenticatingWebForward;
                } 

            } catch (Exception e) {
                // CoreServlet.getServlet().fireCoreEvent(
                //    new ResourceChangeEvent(this, WebForwardEventConstants.CREATE_WEB_FORWARD, this.getSessionInfo(request), e));
                throw e;
            } */
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "clientCerts.createClientCertFinish.status.clientCertCreated"));
        } catch (Exception e) {
            log.error("Failed to create client certificate.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "clientCerts.createClientCertFinish.status.failedClientCertCreated", e.getMessage()));
        }
        // TODO do the attaching.
        /* if (webForward != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("webForwardWizard.webForwardFinish", seq, webForward, favorite, request));
        } */
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.actions.AbstractFinishWizardAction#exit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        throw new Exception("Cannot create sequence on this page.");
    }

}
