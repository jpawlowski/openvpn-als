
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
			
package com.adito.core.tags;

import javax.servlet.jsp.tagext.TagSupport;

import com.adito.boot.ContextHolder;

public class RestartAvailableTag extends TagSupport {

  boolean requiresRestartAvailable;

  public RestartAvailableTag() {
  }

  public int doStartTag() {
    if (ContextHolder.getContext().isRestartAvailableMode()) {
      return requiresRestartAvailable ? EVAL_BODY_INCLUDE : SKIP_BODY;
    } else {
      return requiresRestartAvailable ? SKIP_BODY : EVAL_BODY_INCLUDE;
    }
  }

  public void setRequiresRestartAvailable(boolean requiresRestartAvailable) {
    this.requiresRestartAvailable = requiresRestartAvailable;
  }

}