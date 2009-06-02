
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
			
package net.openvpn.als.core.tags;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SecurityErrorException;
import net.openvpn.als.security.User;

public class ClientProxyURLTag extends BaseHandlerTag {

    private static final long serialVersionUID = -1096155473408849570L;

    final static Log log = LogFactory.getLog(ClientProxyURLTag.class);

    String value;

    public ClientProxyURLTag() {
    }

    public int doEndTag() throws JspException {
        TagUtils.getInstance().write(pageContext, value);
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        value = null;
    }

    public int doStartTag() {
        User user = null;
        try {
            user = LogonControllerFactory.getInstance().getUser(pageContext.getSession(), null);
        } catch (SecurityErrorException ex) {
        }
        try {
            value = CoreUtil.getProxyURL(user, CoreUtil.getCurrentPropertyProfileId(
                                                    pageContext.getSession()));
            if(value==null)
                value = "";
        } catch (Exception e) {
            log.error("Could not determine client proxy value.", e);
            return SKIP_BODY;
        }
        return (EVAL_BODY_AGAIN);
    }
}
