
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
			
package net.openvpn.als.tunnels.itemactions;

import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.AvailableTableItemAction;
import net.openvpn.als.table.TableItemAction;
import net.openvpn.als.tunnels.forms.TunnelItem;

/**
 * <i>Table Item Action</i> that launchs a tunnel under the default policy.
 */
public final class SwitchOnAction extends TableItemAction {

    public static final String TABLE_ITEM_ACTION_ID = "switchOn";

    /**
     * Constructor.
     * 
     */
    public SwitchOnAction() {
        super(TABLE_ITEM_ACTION_ID, "tunnels", 300, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT |SessionInfo.USER_CONSOLE_CONTEXT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.table.TableItemAction#isEnabled(net.openvpn.als.table.AvailableTableItemAction)
     */
    public boolean isEnabled(AvailableTableItemAction availableItem) {
        TunnelItem item = (TunnelItem) availableItem.getRowItem();
        return !item.getOpen().equals("true");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.table.TableItemAction#getPath(net.openvpn.als.table.AvailableTableItemAction)
     */
    public String getPath(AvailableTableItemAction availableItem) {
        TunnelItem item = (TunnelItem) availableItem.getRowItem();
        return item.getOpenLink(-1, "/showUserTunnels.do", availableItem.getRequest());
    }
}