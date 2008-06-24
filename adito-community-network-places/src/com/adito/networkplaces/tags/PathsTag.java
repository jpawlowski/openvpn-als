
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
			
package com.adito.networkplaces.tags;

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;

import com.maverick.util.URLUTF8Encoder;
import com.adito.networkplaces.forms.FileSystemForm;
import com.adito.policyframework.LaunchSession;

/**
 * A custom tag which checks to see if the contents of the tag can be deleted.
 */
public class PathsTag extends BodyTagSupport {
    final static Log log = LogFactory.getLog(PathsTag.class);
    
    private String paths;
    protected String scope;
    protected String property;
    protected String name;

    public int doStartTag() throws JspException {

        // Look up the requested property value
        LaunchSession launchSession = (LaunchSession)TagUtils.getInstance().lookup(pageContext, name, property, scope);
        
        
    	FileSystemForm fsf = (FileSystemForm) pageContext.getRequest().getAttribute("fileSystemForm");
        StringBuffer results = new StringBuffer();
        results.append("<div class=\"path\"><span>");
        StringTokenizer tok = new StringTokenizer(fsf.getPath(), "/");
        
        String currentPath = tok.nextToken(); // Important: skip the file store
        // we only remove the second entry if the first one is the store, else we are straight into the proper paths. 
        if (currentPath.equals("fs")){
            currentPath = tok.nextToken(); // Important: skip the file store type
        }
        while(tok.hasMoreTokens()) {
        	String element = tok.nextToken();
        	currentPath += "/" + element;
            results.append("<a href=\"fileSystem.do?actionTarget=list&" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId() + "&path=" + URLUTF8Encoder.encode(currentPath, false) + "\">" + element +"</a>&nbsp/&nbsp");
		}
        results.append(" </span></div>");
        paths = results.toString();
        return (SKIP_BODY);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    public int doEndTag() throws JspException {
        TagUtils.getInstance().write(this.pageContext, paths);        
        return EVAL_PAGE;
    }

    @Override
    public void release() {
        super.release();
        name = null;
        property = null;
        scope = null;
    }

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }
    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}