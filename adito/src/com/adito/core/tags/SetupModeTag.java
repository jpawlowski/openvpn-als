
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

public class SetupModeTag extends TagSupport {

  boolean requiresSetupMode;

  public SetupModeTag() {
  }

  public int doStartTag() {
	// PLUNDEN: Removing the context
	// if (ContextHolder.getContext().isSetupMode()) {
	  if (false) {
	// end change
      return requiresSetupMode ? EVAL_BODY_INCLUDE : SKIP_BODY;
    } else {
      return requiresSetupMode ? SKIP_BODY : EVAL_BODY_INCLUDE;
    }
  }

  public void setRequiresSetupMode(boolean requiresSetupMode) {
    this.requiresSetupMode = requiresSetupMode;
  }

}