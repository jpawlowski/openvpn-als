
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class VPNClientLinkTag
    extends TagSupport {

  String action;
  String linkname;
  String style;
  public VPNClientLinkTag() {
  }

  /**
   * Defer our checking until the end of this tag is encountered.
   *
   * @exception JspException if a JSP exception has occurred
   */
  public int doStartTag() throws JspException {

    if (action == null) {
      throw new JspException("action attribute required");
    }

    if (linkname == null) {
      throw new JspException("linkname attribute required");
    }

    return (SKIP_BODY);
  }

  /**
   * Perform our logged-in user check by looking for the existence of
   * a session scope bean under the specified name.  If this bean is not
   * present, control is forwarded to the specified logon page.
   *
   * @exception JspException if a JSP exception has occurred
   */
  public int doEndTag() throws JspException {

//    VPNSession session = LogonControllerFactory.getInstance().getPrimaryVPNSession(
//        LogonControllerFactory.getInstance().getVPNSessionsByLogon(
//        (String) pageContext.getSession().getAttribute(Constants.LOGON_TICKET)));

//    if (session != null) {
//      try {
//        pageContext.getOut().println("<a "
//                                     +
//                                     (style == null ? "" :
//                                      "class=\"" + style + "\" ")
//                                     + "href=\"http://localhost:"
//                                     + session.getClientPort()
//                                     + (action.startsWith("/") ? "" : "/")
//                                     + action
//                                     + "\">"
//                                     + linkname
//                                     + "</a>"
//                                     );
//      }
//      catch (IOException ex) {
//        throw new JspException("Failed to create VPN link", ex);
//      }
//    }

    return (EVAL_PAGE);

  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setLinkname(String linkname) {
    this.linkname = linkname;
  }

  public void setStyle(String htmlclass) {
    this.style = htmlclass;
  }

}