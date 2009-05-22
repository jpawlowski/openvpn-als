
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
			
package com.ovpnals.navigation.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.core.CoreUtil;

public class MenuNotAvailableTag extends TagSupport {
  
  final static Log log = LogFactory.getLog(MenuNotAvailableTag.class);

  String propertyName;
  String propertyValue;
  boolean regExp;
  boolean userProfile;

  public MenuNotAvailableTag() {
  }

  public int doStartTag() {
      if(CoreUtil.isMenuAvailable(((HttpServletRequest)pageContext.getRequest()))) {
          return SKIP_BODY;
      }
      else {
          return EVAL_BODY_INCLUDE;
      }
  }

}