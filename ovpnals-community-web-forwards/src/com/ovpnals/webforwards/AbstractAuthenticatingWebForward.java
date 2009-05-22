
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.webforwards;

import java.util.Calendar;
import java.util.StringTokenizer;

import com.ovpnals.core.CoreEvent;


/**
 * 
 */
public abstract class AbstractAuthenticatingWebForward extends AbstractWebForward {

    private String authenticationUsername;
    private String authenticationPassword;
    private String preferredAuthenticationScheme;
    private String formType = WebForwardTypes.FORM_SUBMIT_NONE;
    private String formParameters = "";

    public AbstractAuthenticatingWebForward(int realmID, int id, int type, String destinationURL, String shortName, String description,
                                            String category, String authenticationUsername,
                                            String authenticationPassword, String preferredAuthenticationScheme,
                                            String formType, String formParameters, boolean autoStart, Calendar dateCreated, Calendar dateAmended) {
        super(realmID, id, type, destinationURL, shortName, description, category, autoStart, dateCreated, dateAmended);
        this.authenticationUsername = authenticationUsername;
        this.authenticationPassword = authenticationPassword;
        this.preferredAuthenticationScheme = preferredAuthenticationScheme;
        this.formParameters = formParameters;
        this.formType = formType;
    }

    /**
     * Get the username to use for authentication with this resource if HTTP
     * authentication is encountered.
     * 
     * @return username
     */
    public String getAuthenticationUsername() {
        return authenticationUsername;
    }

    /**
     * Set the username to use for authentication with this resource if HTTP
     * authentication is encountered.
     * 
     * @return authentication password
     */
    public String getAuthenticationPassword() {
        return authenticationPassword;
    }

    /**
     * Get the preferred authentication scheme. This will be one of
     * {@link com.maverick.http.HttpAuthenticatorFactory#BASIC},
     * {@link com.maverick.http.HttpAuthenticatorFactory#DIGEST} or
     * {@link com.maverick.http.HttpAuthenticatorFactory#NTLM}.
     * 
     * 
     * @return preferred authentication scheme
     */
    public String getPreferredAuthenticationScheme() {
        return preferredAuthenticationScheme;
    }

    /**
     * Set the username to use for authentication with this resource if HTTP
     * authentication is encountered.
     * 
     * @param authenticationUsername authentication username
     */
    public void setAuthenticationUsername(String authenticationUsername) {
        this.authenticationUsername = authenticationUsername;
    }

    /**
     * Set the username to use for authentication with this resource if HTTP
     * authentication is encountered.
     * 
     * @return authenticationPassword
     */
    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }

    /**
     * Set the preferred authentication scheme. This will be one of
     * {@link com.maverick.http.HttpAuthenticatorFactory#BASIC},
     * {@link com.maverick.http.HttpAuthenticatorFactory#DIGEST} or
     * {@link com.maverick.http.HttpAuthenticatorFactory#NTLM}.
     * 
     * @param preferredAuthenticationScheme preferred authentication scheme
     */
    public void setPreferredAuthenticationScheme(String preferredAuthenticationScheme) {
        this.preferredAuthenticationScheme = preferredAuthenticationScheme;
    }
    
    /**
     * @return The form type
     */
    public String getFormType() {
        return formType;
    }

    /**
     * @return The form parameters
     */
    public String getFormParameters() {
        return formParameters;
    }

    /**
     * @param formType
     */
    public void setFormType(String formType) {
        this.formType = formType;
    }

    /**
     * @param formType
     */
    public void setFormParameters(String formParameters) {
        this.formParameters = formParameters;
    }

    public boolean paramsRequirePassword() {
        if (!super.paramsRequirePassword()){
            if (authenticationPassword.contains("${session:password}") | formParameters.contains("${session:password}")){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    public void addFormParametersToEvent(CoreEvent evt, String key){
        int counter = 1;
        StringTokenizer t = new StringTokenizer(this.formParameters, "\n");
        while (t.hasMoreElements()) {
            String element = (String) t.nextElement();
            evt.addAttribute(key+" "+counter, element);
            counter++;
        }
    }


}
