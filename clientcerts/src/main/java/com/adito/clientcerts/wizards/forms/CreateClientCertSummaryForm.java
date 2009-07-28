
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.PropertyList;
import com.adito.core.UserDatabaseManager;
import com.adito.security.UserDatabaseDefinition;
import com.adito.tasks.TaskUtil;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class CreateClientCertSummaryForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(CreateClientCertSummaryForm.class);

    // Private statics for sequence attributes
    
    // Private instance variables
    private String hostname;
    private String organisationalUnit;
    private String company;
    private String countryCode;    
    private String city;    
    private String state;
    private HttpSession session;

    public CreateClientCertSummaryForm() {
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
        super(false, true, "/WEB-INF/jsp/content/clientcerts/clientcertwizard/createClientCertSummary.jspf",
            "", true, true,"createClientCertSummary", "clientCerts", "clientCerts.createClientCertSummary", 2);
        //super(true, false, "/WEB-INF/jsp/content/clientcerts/clientcertwizard/createClientCert.jspf", "", false, false,
        //                "createClientCert", "clientCerts", "clientCerts.createClientCertWizard", 1);
    }

    /* @Override
    public String getFinishOnClick() {
        return TaskUtil.getTaskPathOnClick(getWizardSequence().getFinishActionForward().getPath(), "clientCerts", "clientCerts", session, 440, 150);
    } */

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        super.init(sequence, request);
        session = request.getSession();
        hostname = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_HOSTNAME, "");
        countryCode =(String) sequence.getAttribute(CreateClientCertDetailForm.ATTR_COUNTRY_CODE, "");
        organisationalUnit = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_ORGANISATIONAL_UNIT, "");
        company = (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_COMPANY, "");
        city =  (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_CITY, "");
        state =  (String)sequence.getAttribute(CreateClientCertDetailForm.ATTR_STATE, "");
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
