
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
			
package com.ovpnals.security;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.policyframework.ResourceItem;

/**
 * Implementation of a {@link com.ovpnals.table.TableItem} that is used to
 * wrap {@link com.ovpnals.security.AuthenticationScheme} objects for
 * display. 
 */
public class AuthenticationSchemeSequenceItem extends ResourceItem<AuthenticationScheme> {

    /**
     * Constructor
     * 
     * @param sequence sequence
     * @param policies 
     */
    public AuthenticationSchemeSequenceItem(AuthenticationScheme sequence, List policies) {
        super(sequence, policies);
    }

    /**
     * Get the authentication scheme sequence object this item wraps
     * 
     * @return authentication scheme sequence
     */
    public AuthenticationScheme getSequence() {
        return (AuthenticationScheme) getResource();
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.ResourceItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return ((AuthenticationScheme) getResource()).getEnabled() ? CoreUtil.getThemePath(request.getSession())
                        + "/images/actions/start.gif" : CoreUtil.getThemePath(request.getSession()) + "/images/actions/stop.gif";
    }

    /**
     * Verifies if this item can be moved up the list.
     * 
     * @return true if it is not already the first item the list.
     */
    public boolean isCanMoveUp() {
        AuthenticationScheme scheme = (AuthenticationScheme) getResource();
        try {
        List<AuthenticationScheme> schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
            return schemes.indexOf(scheme) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies if this item can be moved down the list.
     * 
     * @return true if it is not already the last item the list.
     */
    public boolean isCanMoveDown() {
        AuthenticationScheme scheme = (AuthenticationScheme) getResource();
        try {
        List<AuthenticationScheme> schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
            return schemes.indexOf(scheme) != schemes.size() - 1;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get a description of the modules that the scheme consists of.
     * 
     * @return description of the modules that the scheme consists of.
     * 
     * TODO This needs to be done in a language neutral way
     */
    public String getDescription() {
        StringBuffer buf = new StringBuffer();
        buf.append("Consists of ");
        buf.append(getSequence().getModuleCount());
        if (getSequence().getModuleCount() == 1) {
            buf.append(" module");
        } else {
            buf.append(" modules");
        }
        buf.append(".<br/>");
        for (Iterator i = getSequence().modules(); i.hasNext();) {
            buf.append("&nbsp;&nbsp;<b>");
            buf.append(i.next());
            buf.append("</b><br/>");
        }
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see com.ovpnals.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch (col) {
            case 0:
                return getSequence().getSchemeName();
            case 1:
                return Boolean.valueOf(getSequence().getEnabled());
            default:
                return new Integer(getSequence().getPriority());
        }
    }
}
