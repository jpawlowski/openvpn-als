
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
			
package com.ovpnals.install.validators;

import java.util.Properties;

import com.ovpnals.boot.CodedException;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyValidator;
import com.ovpnals.input.validators.AsciiValidator;
import com.ovpnals.input.validators.LDAPSyntaxValidator;
import com.ovpnals.properties.impl.realms.RealmProperties;

/**
 */
public class LdapAuthenticationMethodValidator implements PropertyValidator {

    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        /*if (LdapUserDatabaseConfiguration.GSSAPI_AUTHENTICATION_METHOD.equals(value)) {
            setValidationString(AsciiValidator.class.getName());
        } else if (LdapUserDatabaseConfiguration.SIMPLE_AUTHENTICATION_METHOD.equals(value)) {
            setValidationString(LDAPSyntaxValidator.class.getName());
        } else {
            throw new IllegalArgumentException("Unknown authentication method = '" + value + "'");
        }*/
    }

    @SuppressWarnings("deprecation")
    private void setValidationString(String validationString) {
        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(RealmProperties.NAME);
        PropertyDefinition usernameProperty = propertyClass.getDefinition("ldap.serviceAccountUsername");
        usernameProperty.setValidationString(validationString);
    }
}