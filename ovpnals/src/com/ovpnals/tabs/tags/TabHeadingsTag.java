
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
 * Custom tag to render HTML that displays a row of tabs retrieved from
 * a {@link com.ovpnals.tabs.TabModel}.
 * <p>
 * This tag takes no attributes as it derives all of its attributes from
 * the &lt;tabSet&gt; tag that is must be insed.
 * 
 * @see com.ovpnals.tabs.tags.TabSetTag
 */
public class TabHeadingsTag extends BodyTagSupport {
    
    // Protected instance variables
    
    protected String text;

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {
        Object value = findAncestorWithClass(this, TabSetTag.class);
        if (value == null) {
            throw new JspException("TabHeadingsTag must be contained in a TabSetTag");
        }
        TabModel model = ((TabSetTag)value).getModel();
        String bundle = ((TabSetTag)value).getBundle();
        String locale = ((TabSetTag)value).getLocale();
        String selectedTab = model.getSelectedTab();
        String resourcePrefix =  ((TabSetTag)value).getResourcePrefix();
        StringBuffer buf = new StringBuffer();
        buf.append("<div class=\"tabHeadings\">");
        buf.append("<ul>");
        for(int i = 0 ; i < model.getTabCount(); i++) {
            String tabName = model.getTabName(i);
            String tabTitle = model.getTabTitle(i);
            String tabBundle = model.getTabBundle(i);
            
            // List item
            buf.append("<li id=\"tab_item_");
            buf.append(tabName);
            buf.append("\" class=\"");
            if(selectedTab == null) {
                buf.append(i == 0 ? "selectedTab" : "hiddenTab");
            }
            else {
                buf.append(selectedTab.equals(tabName) ? "selectedTab" : "hiddenTab");
            }
            buf.append("\">");
            
            //	Link
            buf.append("<a id=\"tab_link_");
            buf.append(tabName);
            buf.append("\" ");
            if(selectedTab == null) {
                buf.append(i == 0 ? "class=\"currentTab\" " : "");
            }
            else {
                buf.append(selectedTab.equals(tabName) ? "class=\"currentTab\" " : "");                
            }
            int idx = 0;
            buf.append("onclick=\"javascript: var deselect = new Array();");
            for(int j = 0 ; j < model.getTabCount(); j++) {
                String tn = model.getTabName(j);
                boolean s =  tabName.equals(tn);
                if(!s) {
                    buf.append("deselect[");
                    buf.append(idx++);
                    buf.append("]='");
                    buf.append(tn);
                    buf.append("';");
                }
            }
            buf.append("setSelectedTab('");
            buf.append(tabName);
            buf.append("',deselect);\" href=\"#\">");
            if(tabTitle == null) {
                tabTitle = 
                    TagUtils.getInstance().message(
                        pageContext,
                        tabBundle == null ? bundle : tabBundle,
                        locale,
                        resourcePrefix + "." + tabName + ".title",
                        new String[] { });
            }
            buf.append(tabTitle == null? tabName : tabTitle);
            buf.append("</a>");
            buf.append("</li>");
        }
        buf.append("</ul>");
        buf.append("</div>");
        text = buf.toString();
        return (SKIP_BODY);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    public int doEndTag() throws JspException {
        TagUtils.getInstance().write(this.pageContext, text);        
        return EVAL_PAGE;
    }
    

}
