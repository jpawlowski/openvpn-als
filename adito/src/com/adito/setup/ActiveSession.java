
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
			
package com.adito.setup;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.CoreUtil;
import com.adito.security.SessionInfo;
import com.adito.table.TableItem;

/**
 * Wrapper bean for display an item an the list of sessions on the status 
 * page.
 */

public class ActiveSession implements TableItem {

    final static DateFormat dateFormat = DateFormat.getDateInstance();
    final static DateFormat timeFormat = DateFormat.getTimeInstance();
    
    // Private instance variables
	private SessionInfo info;

    /**
     * Constructor
     * 
     * @param info session
     * @param vpnSessions list of VPN client session the web session is attached to
     */
	public ActiveSession(SessionInfo info) {
		super();
		this.info = info;
	}


    /**
     * Get the {@link SessionInfo} this object wraps.
     * 
     * @return session
     */
	public SessionInfo getInfo() {
		return info;
	}

    /**
     * Get the time the session logged on as plain text in the current
     * locales format.
     * 
     * @return logon time as text
     */
	public String getLogonTimeText() {
		Calendar c = getInfo().getLogonTime();
		Calendar startOfDay = new GregorianCalendar();
		startOfDay.set(Calendar.HOUR, 0);
		startOfDay.set(Calendar.MINUTE, 0);
		startOfDay.set(Calendar.SECOND, 0);
		startOfDay.set(Calendar.MILLISECOND, 0);
		Calendar endOfDay = new GregorianCalendar();
		endOfDay.set(Calendar.HOUR, 23);
		endOfDay.set(Calendar.MINUTE, 59);
		endOfDay.set(Calendar.SECOND, 59);
		endOfDay.set(Calendar.MILLISECOND, 999);
		if(c.compareTo(endOfDay) <= 0 && c.compareTo(startOfDay) >= 0) {
			return timeFormat.format(getInfo().getLogonTime().getTime());
		}
		else {
			return dateFormat.format(getInfo().getLogonTime().getTime());
		}
	}

    /* (non-Javadoc)
     * @see com.adito.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch(col) {
            case 0:
                return getInfo().getUser().getPrincipalName();
            case 1:
                return getInfo().getAddress();
            default:
                return getInfo().getLogonTime();            
        }
    }
    
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/user.gif";
    }

}
