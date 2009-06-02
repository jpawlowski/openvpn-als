
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;

import net.openvpn.als.core.CoreScript;
import net.openvpn.als.core.CoreServlet;

/**
 */
public class PageScriptsTag extends BodyTagSupport {
    private String position = String.valueOf(CoreScript.AFTER_BODY_START);

    /**
     * 
     */
    public PageScriptsTag() {
        super();
    }

    public int doEndTag() throws JspException {
        StringBuffer buf = new StringBuffer();
        List l = new ArrayList();
        for(Iterator i = CoreServlet.getServlet().getPageScripts().iterator(); i.hasNext(); ) {
            CoreScript script = (CoreScript)i.next();
            if(script.getPosition() == Integer.parseInt(position)) {
                l.add(script);
            }
        }
        for(Iterator i = l.iterator(); i.hasNext(); ) {
            CoreScript script = (CoreScript)i.next();
            buf.append("\n<script");
            if(script.getLanguage() != null) {
                buf.append(" language=\"");
                buf.append(script.getLanguage());
                buf.append("\"");
            }
            if(script.getType() != null) {
                buf.append(" type=\"");
                buf.append(script.getType());
                buf.append("\"");
            }
            if(script.getPath() != null) {
                buf.append(" src=\"");
                buf.append(script.getPath());
                buf.append("\">\n");
                buf.append("document.write(\"Included JS file not found\")");
                buf.append("</script>");
            }
            else {
                if(script.getScript() != null) {
                    buf.append(">");
                    buf.append(script.getScript());
                    buf.append("</script>");
                }
                else {
                    buf.append(">\n");
                    buf.append("document.write(\"Included JS file not found\")");
                    buf.append("</script>");
                }
            }
            
            buf.append("\n");
        }
        TagUtils.getInstance().write(pageContext, buf.toString());
        return (EVAL_PAGE);
    }
    
    /**
     * @return Returns the position.
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(String position) {
        this.position = position;
    }
}
