
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
			
package com.ovpnals.tabs.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;

import com.ovpnals.tabs.TabModel;

/**
 * Custom tag used to render an invidual tab provided by a
 * {@link com.ovpnals.tabs.TabModel}.
 * <p>
 * This tag takes a single attribute <i>tabName</i> which must match as it
 * derives all of its attributes from the &lt;tabSet&gt; tag that is must be
 * insed.
 * 
 * @see com.ovpnals.tabs.tags.TabSetTag
 * @see com.ovpnals.tabs.tags.TabHeadingsTag
 * @see com.ovpnals.tabs.TabModel
 */
public class TabTag extends BodyTagSupport {

    // Protected instance variables

    protected String tabName;
    protected TabModel model;
    protected String text;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {
        Object value = findAncestorWithClass(this, TabSetTag.class);
        if (value == null) {
            throw new JspException("TabTag must be contained in a TabSetTag");
        }
        model = ((TabSetTag) value).getModel();
        text = null;
        return EVAL_BODY_BUFFERED;
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

        boolean selected = (tabName.equals(model.getSelectedTab() == null ? model.getTabName(0) : model.getSelectedTab()));

        StringBuffer results = new StringBuffer();
        results.append("<div id=\"");
        results.append("tab_panel_");
        results.append(tabName);
        results.append("\" ");
        if (selected) {
            // results.append("style=\"visiblity: hidden\" ");
            results.append("class=\"tabPanel\"><div class=\"tabContent\">");
        } else {
            results.append("class=\"tabPanelHidden\"><div class=\"tabContent\">");

        }
        // results.append("class=\"tabPanel\"><div class=\"tabContent\">");
        results.append(text);
        results.append("</div></div>");
        // Render this element to our writer
        TagUtils.getInstance().write(pageContext, results.toString());
        // Evaluate the remainder of this page
        return (EVAL_PAGE);
    }

    /**
     * Set the tab name display. This must match the name returned by
     * {@link TabModel#getTabName(int)}.
     * 
     * @param tabName tab name
     */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
}
