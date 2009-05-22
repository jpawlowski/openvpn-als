
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
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SecurityErrorException;
import com.ovpnals.security.User;

public class CheckPropertyEqualsTag extends TagSupport {

    final static Log log = LogFactory.getLog(CheckPropertyEqualsTag.class);

    String propertyName;
    String propertyValue;
    boolean regExp;
    boolean userProfile;

    public CheckPropertyEqualsTag() {
    }

    public int doStartTag() {

        User user = null;
        try {
            user = LogonControllerFactory.getInstance().getUser(pageContext.getSession(), null);
        } catch (SecurityErrorException ex) {
        }
        String val;
        try {
        	if(userProfile) {
        		val = CoreUtil.getUsersProfileProperty(pageContext.getSession(), propertyName, user);
        	}
        	else {
        		val = Property.getProperty(new SystemConfigKey(propertyName));
        	}
            if (regExp) {
                if (val.matches(propertyValue)) {
                    return EVAL_BODY_INCLUDE;
                } else {
                    return SKIP_BODY;
                }
            } else {
                if (propertyValue.equals(val)) {
                    return EVAL_BODY_INCLUDE;
                } else {
                    return SKIP_BODY;
                }
            }
        } catch (Exception e) {
            log.error("Could not determine property value.", e);
            return SKIP_BODY;
        }
    }

    public void setUserProfile(boolean userProfile) {
        this.userProfile = userProfile;
    }

    public void setRegExp(boolean regExp) {
        this.regExp = regExp;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void release() {
        super.release();
        userProfile = false;
        regExp = false;
        propertyName = null;
        propertyValue = null;
    }

}