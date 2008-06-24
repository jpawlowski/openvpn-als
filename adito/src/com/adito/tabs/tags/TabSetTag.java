
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
			
package com.adito.tabs.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;

import com.adito.tabs.TabModel;

/**
 * Custom tag that acts as a container for &lt;tabHeadings&gt; tags and
 * &lt;tab&gt; tags.
 * <p>
 * This tag requires access to a bean that is an instance of
 * {@link com.adito.tabs.TabModel} which is supplied using the normal
 * <i>name</i> and <i>property</i> attributes.
 * <p>
 * It also requires <i>resourcePrefix</i> and <i>bundle</i> attributes. These
 * are used for build up the resource keys used for get the individual tab
 * titles (when {@link com.adito.tabs.TabModel#getTabTitle(int)} is null).
 * 
 * @see com.adito.tabs.tags.TabTag
 * @see com.adito.tabs.tags.TabHeadingsTag
 * @see com.adito.tabs.TabModel
 */
public class TabSetTag extends BaseHandlerTag {

    // Protected instance variables

    protected String name;
    protected String property;
    protected String resourcePrefix;
    protected TabModel model;
    protected String text;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {

        Object value = TagUtils.getInstance().lookup(pageContext, name, property, null);
        if (value == null || !(value instanceof TabModel)) {
            throw new JspException("Name / property attributes must specify an instance of TabModel (" + value + ")");
        }
        model = (TabModel) value;
        return (EVAL_BODY_BUFFERED);
    }

    protected String getName() {
        return name;
    }

    /**
     * Set the name of the bean that contains the {@link TabModel}
     * implementation.
     * 
     * @param name name of the bean that is the {@link TabModel} implementation.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the optional property of the bean that contains the {@link TabModel}
     * implementation.
     * 
     * @param property optional property of the bean that contains the
     *        {@link TabModel} implementation
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Set the resource prefix that is used to build up the key used to
     * get the table titles.
     * 
     * @param resourcePrefix resource prefix used to build up key for tab titles
     */
    public void setResourcePrefix(String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException {

        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0)
                text = value;
        }
        return (SKIP_BODY);

    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {

        StringBuffer results = new StringBuffer();
        results.append("<div class=\"tabSet\">");
        results.append(text);
        results.append("</div>");

        // Render this element to our writer
        TagUtils.getInstance().write(pageContext, results.toString());

        // Evaluate the remainder of this page
        return (EVAL_PAGE);
    }

    protected String getResourcePrefix() {
        return resourcePrefix;
    }

    protected String getProperty() {
        return property;
    }

    protected TabModel getModel() {
        return model;
    }
}
