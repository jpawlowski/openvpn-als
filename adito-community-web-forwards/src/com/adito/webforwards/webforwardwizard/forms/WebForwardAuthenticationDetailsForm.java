
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
			
package com.adito.webforwards.webforwardwizard.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.maverick.http.HttpAuthenticatorFactory;
import com.adito.core.forms.AbstractResourceDetailsWizardForm;
import com.adito.webforwards.WebForwardPlugin;
import com.adito.webforwards.WebForwardTypes;
import com.adito.wizard.AbstractWizardSequence;

/**
 * The form for all other attributes associated with the TunneledSite resource.
 */
public class WebForwardAuthenticationDetailsForm extends AbstractResourceDetailsWizardForm {

    final static Log log = LogFactory.getLog(WebForwardAuthenticationDetailsForm.class);

    private int type = -1;

    // Authenticating web forward
    static final String ATTR_NO_AUTHENTICATION = "none";
    static final String ATTR_FORM_BASED_AUTHENTICATION = "form";
    static final String ATTR_HTTP_AUTHENTICATION = "http";
    public final static String ATTR_AUTHENTICATION_USERNAME = "authenticationUsername";
    public final static String ATTR_AUTHENTICATION_PASSWORD = "authenticationPassword";
    public final static String ATTR_PREFERRED_AUTHENTICATION_SCHEME = "preferredAuthenticationScheme";
    
    private String authenticationType = ATTR_NO_AUTHENTICATION;
    private String authenticationUsername;
    private String authenticationPassword;
    private String preferredAuthenticationScheme;

    public final static String ATTR_FORM_PARAMETERS = "formParameters";
    public final static String ATTR_FORM_TYPE = "formType";

    // Form parameters
    private String formParameters;
    private String formType;

    /**
     * Construtor
     */
    public WebForwardAuthenticationDetailsForm() {
    	// Autocomplete must be false because this page contains passwords
        super(true, true, "/WEB-INF/jsp/content/webforward/webforwardwizard/webForwardAuthenticationDetails.jspf",
                        "authenticationUsername", false, false, "webForwardAuthenticationDetails", "webForwards",
                        "webForwardWizard.webForwardAuthenticationDetails", 4, WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        type = ((Integer) sequence.getAttribute(WebForwardTypeSelectionForm.ATTR_TYPE, new Integer(0))).intValue();

        this.authenticationUsername = (String) sequence.getAttribute(ATTR_AUTHENTICATION_USERNAME, "");
        this.authenticationPassword = (String) sequence.getAttribute(ATTR_AUTHENTICATION_PASSWORD, "");
        this.preferredAuthenticationScheme = (String) sequence.getAttribute(ATTR_PREFERRED_AUTHENTICATION_SCHEME,
                        HttpAuthenticatorFactory.NONE);

        this.formParameters = (String) sequence.getAttribute(ATTR_FORM_PARAMETERS, "");
        this.formType = (String) sequence.getAttribute(ATTR_FORM_TYPE, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_AUTHENTICATION_USERNAME, this.authenticationUsername);
        sequence.putAttribute(ATTR_AUTHENTICATION_PASSWORD, this.authenticationPassword);
        sequence.putAttribute(ATTR_PREFERRED_AUTHENTICATION_SCHEME, this.preferredAuthenticationScheme);
        sequence.putAttribute(ATTR_FORM_PARAMETERS, this.formParameters);
        sequence.putAttribute(ATTR_FORM_TYPE, this.formType);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (getResourceName() != null && isCommiting()) {
            ActionErrors errs = super.validate(mapping, request);
            return errs;
        }
        return null;
    }

    /**
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }
    
    public boolean isNoAuthentication() {
        return ATTR_NO_AUTHENTICATION.equals(getAuthenticationType());
    }
    
    public boolean isFormBasedAuthentication() {
        return ATTR_FORM_BASED_AUTHENTICATION.equals(getAuthenticationType());
    }
    
    public boolean isHttpAuthentication() {
        return ATTR_HTTP_AUTHENTICATION.equals(getAuthenticationType());
    }
    
    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationPassword() {
        return authenticationPassword;
    }

    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }

    public String getAuthenticationUsername() {
        return authenticationUsername;
    }

    public void setAuthenticationUsername(String authenticationUsername) {
        this.authenticationUsername = authenticationUsername;
    }

    public String getPreferredAuthenticationScheme() {
        return preferredAuthenticationScheme;
    }

    public void setPreferredAuthenticationScheme(String preferredAuthenticationScheme) {
        this.preferredAuthenticationScheme = preferredAuthenticationScheme;
    }

    public List getPreferredAuthenticationSchemeList() {
        return WebForwardTypes.PREFERED_SCHEMES;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(String formParameters) {
        this.formParameters = formParameters;
    }

    public List getFormTypeList() {
        return WebForwardTypes.FORM_SUBMIT_TYPES;
    }

    public List getEncodeingTypeList() {
        return WebForwardTypes.ENCODING_TYPES;
    }
}