
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
			
package net.openvpn.als.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.table.TableItem;

/**
 * Implementation of a {@link net.openvpn.als.table.TableItem} that wraps a
 * {@link net.openvpn.als.security.User} for display in a pager.
 */
public class UserItem implements TableItem {
    
    final static Log log = LogFactory.getLog(UserItem.class);

    // Private instance variables
    private User user;
    private int numberRoles;

    /**
     * Constructor
     * 
     * @param user user to wrap
     */
    public UserItem(User user) {
        this.user = user;
        this.numberRoles = this.user.getRoles().length;
    }

    /**
     * Get the user status. See {@link LogonController#getUserStatus(User)}
     * for more details.
     * 
     * @return user status
     */
    public int getStatus() {
        try {
            return LogonControllerFactory.getInstance().getUserStatus(user);
        }
        catch(Exception e) {
            log.error("Failed to determine user status.", e);
            return LogonController.ACCOUNT_UNKNOWN;
        }
    }

    /**
     * Get if this user is the super user.
     * 
     * @return is super user
     */
    public boolean getAdministrator() {
        return LogonControllerFactory.getInstance().isAdministrator(user);
    }

    /**
     * Get the user object this item wraps
     * 
     * @return user object
     */
    public User getUser() {
        return user;
    }

    /**
     * @return <tt>true</tt> if the account is authorised.
     */
    public boolean isAuthorized() {
        return LogonController.ACCOUNT_ACTIVE == getStatus() || LogonController.ACCOUNT_GRANTED == getStatus();
    }
    
    /**
     * @return <tt>true</tt> if the account is locked.
     */
    public boolean isLocked() {
        return LogonController.ACCOUNT_LOCKED == getStatus();
    }

    /**
     * @return <tt>true</tt> if the account is disabled.
     */
    public boolean isDisabled() {
        return LogonController.ACCOUNT_DISABLED == getStatus();
    }
    
    
    /**
     * Get if this user is enabled
     * 
     * @return enabled
     * @throws Exception on any error
     */
    public boolean getEnabled() throws Exception {
        return PolicyUtil.isEnabled(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        if(col == 0) {
            return getUser().getPrincipalName();
        }
        return "";
    }

    /**
     * @return String
     */
    public String getLink() {
        return "#";
    }
    
    /**
     * @return String
     */
    public String getOnClick() {
        return "";
    }
    
	/**
	 * @return int
	 */
	public int getNumberRoles() {
		return numberRoles;
	}

	/**
	 * @param numberRoles
	 */
	public void setNumberRoles(int numberRoles) {
		this.numberRoles = numberRoles;
	}

    /**
     * @param request
     * @return String
     */
    public String getSmallIconPath(HttpServletRequest request) {
        switch (getStatus()) {
            case LogonController.ACCOUNT_GRANTED:
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userActive.gif";
            case LogonController.ACCOUNT_DISABLED:
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userDisabled.gif";
            case LogonController.ACCOUNT_LOCKED:
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userLocked.gif";
            case LogonController.ACCOUNT_REVOKED:
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userInactive.gif";
            case LogonController.ACCOUNT_ACTIVE:
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userOnLine.gif";
            default: 
                return CoreUtil.getThemePath(request.getSession()) + "/images/actions/userError.gif";
        }
    }
}