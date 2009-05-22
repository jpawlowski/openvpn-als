
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
			
package com.ovpnals.security.tags;

import javax.servlet.jsp.tagext.TagSupport;

import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;

public class CheckUserDatabaseSupportTag
    extends TagSupport {

  boolean requiresPasswordChange = true;
  boolean requiresAccountCreation = true;

  public CheckUserDatabaseSupportTag() {
  }

  public int doStartTag() {
    UserDatabase udb;
    try {
        User user = LogonControllerFactory.getInstance().getUser(pageContext.getSession(), null);
        udb = UserDatabaseManager.getInstance().getUserDatabase(user.getRealm().getResourceId());
    } catch (Exception e1) {
        return SKIP_BODY;
    } 

    if(!udb.supportsAccountCreation() && requiresAccountCreation) {
      return SKIP_BODY;
    }
    if(!udb.supportsPasswordChange() && requiresPasswordChange) {
      return SKIP_BODY;
    }

    return EVAL_BODY_INCLUDE;
  }

  public void setRequiresPasswordChange(boolean requiresPasswordChange) {
    this.requiresPasswordChange = requiresPasswordChange;
  }

  public void setRequiresAccountCreation(boolean requiresAccountCreation) {
    this.requiresAccountCreation = requiresAccountCreation;
  }
}