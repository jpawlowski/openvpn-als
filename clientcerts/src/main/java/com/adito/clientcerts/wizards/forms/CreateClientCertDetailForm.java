
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
			
package com.adito.clientcerts.wizards.forms;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 */
public class CreateClientCertDetailForm extends DefaultWizardForm {
    public final static String ATTR_HOSTNAME = "hostname";
    public final static String ATTR_ORGANISATIONAL_UNIT = "organisationalUnit";
    public final static String ATTR_COMPANY = "company";
    public final static String ATTR_COUNTRY_CODE = "countryCode";
    public final static String ATTR_CITY = "city";
    public final static String ATTR_STATE = "state";
    
    private static final Log LOG = LogFactory.getLog(CreateClientCertDetailForm.class);
    public final static Preferences PREF_NODE = ContextHolder.getContext().getPreferences().node("clientCerts").node("certificate");

    private String hostname;
    private String organisationalUnit;
    private String company;
    private String countryCode;    
    private String city;    
    private String state;

    /**
     * Construtor
     */
    public CreateClientCertDetailForm() {
    /**
     * @param nextAvailable
     * @param previousAvailable
     * @param page
     * @param focussedField
     * @param autoComplete
     * @param finishAvailable
     * @param pageName
     * @param resourceBundle
     * @param resourcePrefix
     * @param stepIndex
     */
        super(true, false, "/WEB-INF/jsp/content/clientcerts/clientcertwizard/createClientCert.jspf", "", true, false,
                        "createClientCert", "clientCerts", "clientCerts.createClientCertWizard", 1);
    }
        
    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        hostname = (String)sequence.getAttribute(ATTR_HOSTNAME, "");
        if (hostname.equals("")) {
		hostname = request.getParameter("user");
        }

        countryCode =(String) sequence.getAttribute(ATTR_COUNTRY_CODE, PREF_NODE.get("countryCode", ""));
        if (countryCode.equals("")) {
            countryCode = Locale.getDefault().getCountry();
        }
        organisationalUnit = (String)sequence.getAttribute(ATTR_ORGANISATIONAL_UNIT, PREF_NODE.get("organisationalUnit", ""));
        company = (String)sequence.getAttribute(ATTR_COMPANY, PREF_NODE.get("company", ""));
        city =  (String)sequence.getAttribute(ATTR_CITY, PREF_NODE.get("city", ""));
        state =  (String)sequence.getAttribute(ATTR_STATE, PREF_NODE.get("state", ""));
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
	LOG.info("apply called");
        super.apply(sequence);
        sequence.putAttribute(ATTR_CITY, city);
        sequence.putAttribute(ATTR_COMPANY, company);
        sequence.putAttribute(ATTR_COUNTRY_CODE, countryCode);
        sequence.putAttribute(ATTR_HOSTNAME, hostname);
        sequence.putAttribute(ATTR_ORGANISATIONAL_UNIT, organisationalUnit);
        sequence.putAttribute(ATTR_STATE, state);
	LOG.info("apply finished");
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	LOG.info("validate called");
        ActionErrors errs = new ActionErrors();
        if(isCommiting()) {
            if ("".equals(hostname)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noHost"));
            } 
            // LDP - This is a problem because it stops wildcard certificates from being generated with
            // hostnames such as *.3sp.co.uk
            /*else if (!isValidIpAddress(hostname)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.invalidHost"));
            }*/

            if ("".equals(organisationalUnit)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noOrganisationalUnit"));
            }

            if ("".equals(company)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noCompany"));
            }
            
            if ("".equals(city)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noCity"));
            }
            
            if ("".equals(state)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noState"));
            }

            if ("".equals(countryCode)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("clientCerts.createNewCertificate.error.noCountryCode"));
            }
        }
	LOG.info("validate finished");
        return errs;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getOrganisationalUnit() {
        return organisationalUnit;
    }

    public void setOrganisationalUnit(String organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
    }
    
}
