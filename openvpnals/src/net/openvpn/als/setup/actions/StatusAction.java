
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
			

package net.openvpn.als.setup.actions;

import net.openvpn.als.core.Panel;
import net.openvpn.als.core.actions.AbstractMultiFormDispatchAction;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;

/**
 * Implementation of {@link AuthenticatedDispatchAction} that is used to display
 * various system information.
 */
public class StatusAction extends AbstractMultiFormDispatchAction {
    /**
     * Constructor.
     */
    public StatusAction() {
        super(PolicyConstants.STATUS_TYPE_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_VIEW }, Panel.STATUS_TAB);
    }
}