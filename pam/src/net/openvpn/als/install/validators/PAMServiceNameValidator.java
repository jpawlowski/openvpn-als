/*
 *  OpenVPNALS-PAM
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
package net.openvpn.als.install.validators;

import java.io.File;
import java.util.Properties;

import net.openvpn.als.boot.CodedException;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyValidator;
import net.openvpn.als.core.CoreException;
import net.openvpn.als.input.validators.ErrorConstants;

/**
 * This validator checks PAM Service Name exists.
 */
public class PAMServiceNameValidator implements PropertyValidator {

	/* (non-Javadoc)
	 * @see net.openvpn.als.boot.PropertyValidator#validate(net.openvpn.als.boot.PropertyDefinition, java.lang.String, java.util.Properties)
	 */
	public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
		if (!(new File("/etc/pam.d/" + value).canRead())) {
			throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR,
					ErrorConstants.CATEGORY_NAME,
					ErrorConstants.BUNDLE_NAME,
					null,
					"<b>"+value+"</b> isn't a valid PAM Service Name. File <b>/etc/pam.d/" + value + "</b> doesn't exist.");
		}
	}

}
