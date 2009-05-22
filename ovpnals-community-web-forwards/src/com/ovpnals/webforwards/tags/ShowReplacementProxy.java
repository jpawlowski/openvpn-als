
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
			
package com.ovpnals.webforwards.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.webforwards.WebForward;
import com.ovpnals.webforwards.forms.WebForwardForm;
import com.ovpnals.webforwards.webforwardwizard.forms.WebForwardTypeSelectionForm;

/**
 * A custom tag which checks to see if the contents of the tag can be deleted.
 */
public class ShowReplacementProxy extends TagSupport {
    final static Log log = LogFactory.getLog(ShowReplacementProxy.class);

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() {
        WebForwardForm fsf = (WebForwardForm) pageContext.getSession().getAttribute("webForwardForm");
        int type = -1; 
        if (fsf == null){
            WebForwardTypeSelectionForm wfsdf = (WebForwardTypeSelectionForm) pageContext.getSession().getAttribute("webForwardTypeSelectionForm");
            type = wfsdf.getType();
        }
        else {
        	type = fsf.getType();
        }
        if (type == WebForward.TYPE_REPLACEMENT_PROXY) {
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }
}