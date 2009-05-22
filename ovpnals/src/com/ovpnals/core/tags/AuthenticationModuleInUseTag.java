
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
			
package com.ovpnals.core.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.core.CoreUtil;

/**
 * A custom struts tag that will include contain if the supplied authentication
 * module is in use by one of the configuration <i>Authentication Schemes</i>.
 */
public class AuthenticationModuleInUseTag extends TagSupport {

    final static Log log = LogFactory.getLog(AuthenticationModuleInUseTag.class);

    // Instance variables
    String name;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() {
    	try {
    		if(CoreUtil.isAuthenticationModuleInUse(name)) {
                return EVAL_BODY_INCLUDE;
            }
        } catch (Exception e) {
            log.error("Failed to get authentication schemes.", e);
        }
        return SKIP_BODY;
    }

    /**
     * Set the name of the authentication module that if available will cause
     * the body content of the tag to be included. 
     * 
     * @param name name of authentication module
     */
    public void setName(String name) {
        this.name = name;
    }

}