
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

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;

import com.adito.core.CoreUtil;

public class ThemePathTag extends BaseHandlerTag {

    private static final long serialVersionUID = -1096155473408849570L;

    final static Log log = LogFactory.getLog(ThemePathTag.class);

    String value;

    public ThemePathTag() {
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
        value = CoreUtil.getThemePath(pageContext.getSession());
        return (EVAL_BODY_AGAIN);
    }
}
