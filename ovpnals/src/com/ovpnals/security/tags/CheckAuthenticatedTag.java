
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

import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SecurityErrorException;
import com.ovpnals.security.User;

public class CheckAuthenticatedTag
    extends TagSupport {

  boolean requiresAuthentication = true;
  boolean requiresAdministrator = false;

  public CheckAuthenticatedTag() {
  }

  public int doStartTag() {

    User user = null;

    try {
      user = LogonControllerFactory.getInstance().getUser(pageContext.getSession(), null);
    }
    catch (SecurityErrorException ex) {
    }

    if (user == null) {
      return (requiresAuthentication ? SKIP_BODY : EVAL_BODY_INCLUDE);
    }
    else {
      if (requiresAuthentication) {
        if (requiresAdministrator && !LogonControllerFactory.getInstance().isAdministrator(user)) {
          return SKIP_BODY;
        }
        else {
          return EVAL_BODY_INCLUDE;
        }
      }
      else {
        return SKIP_BODY;
      }
    }
  }

  public void setRequiresAuthentication(boolean requiresAuthentication) {
    this.requiresAuthentication = requiresAuthentication;
  }

  public void setRequiresAdministrator(boolean requiresAdministrator) {
    this.requiresAdministrator = requiresAdministrator;
  }

}