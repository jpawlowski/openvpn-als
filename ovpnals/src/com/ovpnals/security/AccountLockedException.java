
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



/**
 * This exception is thrown by the {@link LogonController} when an
 * authentication attempt fails becuase the account has been locked. The lock
 * may either be temporary (for a number of seconds defined by the
 * administrator) or disabled in which case the lock is stored in the user
 * database. If a temporary lock, the amount of time left in milliseconds before
 * the lock expires is also supplied.
 */
public class AccountLockedException extends Exception {

	private boolean disabled;
	private long timeLeft;
	private String username;

	/**
	 * Constructor.
	 *
	 * @param username
	 * @param msg
	 * @param disabled
	 * @param timeLeft
	 */
	public AccountLockedException(String username, String msg, boolean disabled, long timeLeft) {
		super(msg);
		this.username = username;
		this.disabled = disabled;
		this.timeLeft = timeLeft;
	}

	/**
	 * Get if this exception should disable the account.
	 * 
	 * @return disable account
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Get the number of milliseconds before this lock
	 * expires.
	 * 
	 * @return lock expiry time in milliseconds
	 */
	public long getTimeLeft() {
		return timeLeft;
	}
	
	/**
	 * Get the username that is locked.
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

}