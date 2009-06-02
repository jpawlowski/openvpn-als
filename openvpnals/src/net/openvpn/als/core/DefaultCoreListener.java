
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
			
package net.openvpn.als.core;

import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.GlobalWarning.DismissType;
import net.openvpn.als.properties.PropertyChangeEvent;


/**
 * Default listener of {@link CoreEvent}s. 
 */
public class DefaultCoreListener implements CoreListener {

    public void coreEvent(CoreEvent evt) {
        if (evt instanceof PropertyChangeEvent) {
            PropertyChangeEvent pce = (PropertyChangeEvent) evt;
            StringBuffer buf = new StringBuffer("Property ");
            buf.append(pce.getDefinition().getName());
            if (pce.getDefinition().getType() == PropertyDefinition.TYPE_PASSWORD) {
                buf.append(" changed");
            } else {
                buf.append(" from ");
                buf.append(pce.getOldValue());
                buf.append(" to ");
                buf.append(pce.getNewValue());
            }
            if (pce.getSessionInfo() != null) {
                buf.append(" (by user ");
                buf.append(pce.getSessionInfo().getUser().getPrincipalName());
                buf.append(" )");
            }
            if (CoreServlet.log.isInfoEnabled())
            	CoreServlet.log.info(buf.toString());
            if(pce.getDefinition().isRestartRequired()) {
                GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS,
                    new BundleActionMessage("properties", "event.configurationChangeRestartRequired.warning"), DismissType.DISMISS_FOR_USER));
            }
        } else if (evt.getId() >= CoreEventConstants.KEYSTORE_CREATED && evt.getId() <= CoreEventConstants.KEYSTORE_PKCS12_KEY_KEY_IMPORTED
                        && evt.getSessionInfo() != null) {
            GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS,
                    new BundleActionMessage("properties", "event.configurationChangeRestartRequired.warning"), DismissType.DISMISS_FOR_USER));
        }
    }
}