
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
			
package net.openvpn.als.security.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.security.LogonControllerFactory;

public class AgentAvailabilityTag extends TagSupport {

  boolean requiresClient = true;

  public AgentAvailabilityTag() {
  }

  public int doStartTag() throws JspException {

    if (!DefaultAgentManager.getInstance().hasActiveAgent(
    		LogonControllerFactory.getInstance().getSessionInfo((HttpServletRequest)pageContext.getRequest()))) {
      return (requiresClient ? SKIP_BODY : EVAL_BODY_INCLUDE);
    } else {
      return (requiresClient ? EVAL_BODY_INCLUDE : SKIP_BODY);
    }
  }

  public int doEndTag() throws JspException {
    return (EVAL_PAGE);
  }

  public void setRequiresClient(boolean requiresClient) {
    this.requiresClient = requiresClient;
  }

}