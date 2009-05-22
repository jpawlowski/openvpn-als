
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
			
package com.ovpnals.setup.forms;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.setup.ActiveSession;
import com.ovpnals.setup.UserSessionsTableModel;
import com.ovpnals.table.forms.AbstractPagerForm;


/**
 * Implementation of a {@link AbstractPagerForm} that is used to display various
 * status details.
 */
public class UserSessionsForm extends AbstractPagerForm {

    /**
     * Constructor.
     *
     */
    public UserSessionsForm() {
        super(new UserSessionsTableModel());
    }
    
    /**
     * Initialise
     * 
     * @param session session
     */
    public void initialize(HttpSession session) {
        super.initialize(session, "user");
        Map active = LogonControllerFactory.getInstance().getActiveSessions();
        for (Iterator i = active.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String ticket = (String) entry.getKey();
            SessionInfo info = (SessionInfo) entry.getValue();
            ActiveSession activeSession = new ActiveSession(info);
            getModel().addItem(activeSession);
        }
        getPager().rebuild(getFilterText());
    }
}