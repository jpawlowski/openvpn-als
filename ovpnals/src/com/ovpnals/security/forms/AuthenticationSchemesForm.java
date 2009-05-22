
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
			
package com.ovpnals.security.forms;

import java.util.List;

import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.forms.AbstractResourcesForm;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.AuthenticationSchemeSequenceItem;
import com.ovpnals.security.SessionInfo;

/**
 * Implementation of a {@link com.ovpnals.policyframework.forms.AbstractResourcesForm}
 * that allows an administrator to list and configure <i>Authentication Schemes</i>.
 */
public class AuthenticationSchemesForm extends AbstractResourcesForm<AuthenticationSchemeSequenceItem> {
    /**
     * Constructor
     */
    public AuthenticationSchemesForm() {
        super("authenticationSchemes");
        getPager().setSorts(false);
    }

    /**
     * Initialis this form with a list of authentication schemes.
     * @param session session
     * @param authenticationSchemeSequence array of authentication schemes to display
     * @throws Exception on any error
     */
    public void initialize(SessionInfo session, List<AuthenticationScheme> authenticationSchemeSequence) throws Exception {
        super.initialize(session.getHttpSession(), "");
        for (AuthenticationScheme scheme : authenticationSchemeSequence) {
            List<Policy> policies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(scheme, session.getUser().getRealm());
            AuthenticationSchemeSequenceItem item = new AuthenticationSchemeSequenceItem(scheme, policies);
            getModel().addItem(item);            
        }
        checkSort();
        getPager().rebuild(getFilterText());
    }

    /**
     * Verifies if this item can be moved down the list.
     * @param index
     * @return true if the list contains more than one item and the index isn't one less than the list size.
     */
    public boolean isCanMoveDown(Integer index) {
        int rowCount = getModel().getRowCount();
        return index != null && index + 1 < rowCount;
    }
}