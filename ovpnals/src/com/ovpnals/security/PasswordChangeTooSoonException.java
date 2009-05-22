package com.ovpnals.security;

import java.util.Date;

/**
 * Exception thrown by the {@link com.ovpnals.security.UserDatabase}.
 * The intention of this exception is to tell the caller the minimum time
 * required before password changes hasn't yet passed.
 */
public final class PasswordChangeTooSoonException extends UserDatabaseException {
	private final Date requiredDate;

	/**
	 * @param requiredDate first valid time for a password change
	 */
	public PasswordChangeTooSoonException(Date requiredDate) {
		super("Failed to change password, the minimum change date is in the future");
		this.requiredDate = requiredDate;
	}

	/**
	 * @return Date
	 */
	public Date getRequiredDate() {
		return requiredDate;
	}
}
