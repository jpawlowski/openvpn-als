
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
			
package com.adito.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreUtil;
import com.adito.core.RedirectWithMessages;
import com.adito.core.actions.AuthenticatedAction;


/**
 * Encapsulate an <i>Account Lock</i>. These occur in two stages. The first
 * when a preset number of <i>Invalid Credentials</i> errors have occured,
 * the lock will become <i>Locked</i>.
 * <p>
 * The user then cannot attempt further
 * authentication until a timeout has occured. Once this timeout has 
 * expired, the lock is <i>Unlocked</i> and the user may try again.
 * <p>
 * The first stage is then repeated until either the user manages to logon
 * or the lock has be <i>Locked</i> for a preset number numbers.
 * <p>
 * If this occurs then the account is <i>Disable</i> and intervention is then
 * required by an adminstrator to unlock the account.
 */
public class AccountLock {

	final static Log log = LogFactory.getLog(AccountLock.class);

	//	Private instance variables
	
	private String username;
	private int attempts;
	private long lockedTime;
	private int locks;

	/**
	 * Constructor.
	 *
	 * @param username
	 */
	public AccountLock(String username) {
		this.username = username;
		attempts = 0;
		lockedTime = -1;
		locks = 0;
	}

	/**
	 * Get the number of locks this locks has had. When maximum is 
	 * reached account should be disabled.
	 * 
	 * @return locks
	 */
	public int getLocks() {
		return locks;
	}

	/**
	 * Set the number of locks this locks has had. When maximum is 
	 * reached account should be disabled.
	 * 
	 * @param locks locks
	 */
	protected void setLocks(int locks) {
		this.locks = locks;
	}

	/**
	 * Get if this lock is in the <i>Locked</i> state.
	 * 
	 * @return locked
	 */
	public boolean isLocked() {
		return lockedTime != -1;
	}

	/**
	 * Get the number of authentication attempts since this lock was
	 * created or reset.
	 * 
	 * @return attempts
	 */
	public int getAttempts() {
		return attempts;
	}

	/**
	 * Set the number of authentication attempts since this lock was
	 * created or reset.
	 * 
	 * @param attempts attempts
	 */
	protected void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	/**
	 * Get the time this lock was last made <i>Locked</i>.
	 * 
	 * @return locked time
	 */
	public long getLockedTime() {
		return lockedTime;
	}

	/**
	 * Set the time this lock was last made <i>Locked</i>.
	 * 
	 * @param lockedTime locked time
	 */
	protected void setLockedTime(long lockedTime) {
		this.lockedTime = lockedTime;
	}

	/**
	 * Get the username of the user that this lock is for.
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
}