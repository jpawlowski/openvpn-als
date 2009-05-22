
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
 * This exception is thrown by the {@link DefaultLogonController} when an authentication
 * attempt results in logging on as a user whoses password needs changing
 * for some reason. This may because the password does not conform to a newly
 * configured password policy, or the password has expired.
 *
 * @author Brett Smith
 */
public class PasswordChangeRequiredException
    extends Exception {
  
  public final static int PASSWORD_EXPIRED = 0;
  public final static int PASSWORD_DOES_NOT_CONFORM_TO_POLICY = 1;
  
  private int reason;
  
  public PasswordChangeRequiredException(int reason) {
    this(reason, "Password expired.");
  }

  public PasswordChangeRequiredException(int reason, String message) {
    super(message);
    this.reason = reason;
  }
  
  public int getReason() {
    return reason;
  }

}