
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.html.Constants;

import com.adito.boot.SystemProperties;
import com.adito.core.CoreScript;

/**
 */
public class FormTag extends org.apache.struts.taglib.html.FormTag {
    protected String autocomplete;
    protected List scripts;
    protected String subFormName;
    
    public FormTag() {
        super();
        scripts = new ArrayList();
    }

    
    /**
     * @return Returns the autocomplete.
     */
    public String getAutocomplete() {
        return autocomplete;
    }
    /**
     * @param autocomplete The autocomplete to set.
     */
    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    /**
     * @param subFormName sub form name
     */
    public void setSubFormName(String subFormName) {
        this.subFormName = subFormName;
    }
    
    public String getSubFormName() {
        return subFormName;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.FormTag#renderOtherAttributes(java.lang.StringBuffer)
     */
    protected void renderOtherAttributes(StringBuffer results) {
        super.renderOtherAttributes(results);
        if(autocomplete != null) {
            renderAttribute(results, "AUTOCOMPLETE", getAutocomplete());
        }
        renderAttribute(results, "accept-charset", SystemProperties.get("adito.encoding", "UTF-8"));
    }
    
    /**
     * Add a script to be rendered at the end of the form
     * 
     * @param script script to add
     */
    public void addScript(CoreScript script) {
        scripts.add(script);        
    }


    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.FormTag#doEndTag()
     */
    public int doEndTag() throws JspException {
        pageContext.removeAttribute(Constants.BEAN_KEY, PageContext.REQUEST_SCOPE);
        pageContext.removeAttribute(Constants.FORM_KEY, PageContext.REQUEST_SCOPE);
        StringBuffer results = new StringBuffer();
        for(Iterator i = scripts.iterator(); i.hasNext(); ) {
            CoreScript script = (CoreScript)i.next();
            results.append(script.getRenderedHTML());
        }
        results.append("</form>");
        if (this.focus != null && !this.focus.equals("")) {
            results.append(this.renderFocusJavascript());
        }
        JspWriter writer = pageContext.getOut();
        try {
            writer.print(results.toString());
        } catch (IOException e) {
            throw new JspException(messages.getMessage("common.io", e.toString()));
        }
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        subFormName = null;
    }

    protected void lookup() throws JspException {
        super.lookup();
        if(subFormName != null) {
            beanName = subFormName;
        }
    }
}
