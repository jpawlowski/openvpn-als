
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
			
package com.adito.table.tags;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.taglib.TagUtils;

import com.adito.core.CoreUtil;
import com.adito.core.tags.FormTag;
import com.adito.table.Pager;

public class PageSizeTag extends TagSupport {
    protected String pagerProperty = null;
    protected String pagerName = null;
    protected String action = null;
    protected String styleId = null;
    protected String styleClass = null;
    protected String style = null;
    protected String forward;
    protected String attributesName;
    protected String attributesProperties;
    protected String subForm = null;
    protected String userDefinedSubForm = null;
    protected boolean allowShowAll = true;
    
    public final static String[] PAGE_SIZES = { "10", "20", "50", "100", "*" };

    public String getPagerName() {
        return (this.pagerName);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public void setPagerName(String pagerName) {
        this.pagerName = pagerName;
    }

    public void setSubForm(String userDefinedSubForm) {
        this.userDefinedSubForm = userDefinedSubForm;
    }

    public String getSubForm() {
        return userDefinedSubForm;
    }

    public String getPagerProperty() {
        return (this.pagerProperty);
    }

    public void setPagerProperty(String pagerProperty) {
        this.pagerProperty = pagerProperty;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getStyleId() {
        return styleId;
    }

    public String isAllowShowAll() {
        return String.valueOf(allowShowAll);
    }

    public void setAllowShowAll(String allowShowAll) {
        this.allowShowAll = Boolean.valueOf(allowShowAll);
    }
    
    public void release() {
        super.release();
        pagerName = null;
        pagerProperty = null;
        action = null;
        styleId = null;
        styleClass = null;
        style = null;
        forward = null;
        attributesName = null;
        attributesProperties = null;
        subForm = null;
        userDefinedSubForm = null;
        allowShowAll = true;
    }
    
    public void setAttributesName(String attributesName) {
        this.attributesName = attributesName;
    }

    public void setAttributesProperties(String attributesProperties) {
        this.attributesProperties = attributesProperties;
    }

    public int doEndTag() throws JspException {
        StringBuilder results = new StringBuilder();
        Pager pager = (Pager) TagUtils.getInstance().lookup(pageContext, pagerName, pagerProperty, null);
        if (pager != null && pager.getModel().getRowCount() >= Integer.parseInt(PAGE_SIZES[0])) {

            // Start of table
            results.append("<ul>");
            for (String page : PAGE_SIZES) {
                if (page.equals("*")) {
                    if (allowShowAll) {
                        results.append(getPageSizeLink(pager, 0, "*"));
                    }
                } else {
                    int pageSize = Integer.parseInt(page);
                    if (pager.getModel().getRowCount() >= pageSize) {
                        results.append(getPageSizeLink(pager, pageSize, String.valueOf(pageSize)));
                    }
                }
            }
            results.append("</ul>");
        }
        else {
            results.append("&nbsp;");
        }

        TagUtils.getInstance().write(pageContext, results.toString());
        return (EVAL_PAGE);

    }

    private String getPageSizeLink(Pager pager, int pageSize, String pageValue) throws JspException {
        StringBuilder results = new StringBuilder();
        results.append("<li><a href=\"");
        results.append(getPageLink(pageSize, pager));
        results.append("\"");
        results.append(makeStyles(false));
        results.append("><div class=\"pageSize\">");
        results.append(pageValue);
        results.append("</div></a></li>");
        return results.toString();
    }

    protected String getPageLink(int pageSize, Pager pager) {
        String url = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pageSize", String.valueOf(pageSize));
            params.put("sortName", pager.getSortName());
            params.put("sortReverse", String.valueOf(pager.getSortReverse()));
            if(subForm!=null)
            	params.put("subForm", subForm);            
            if (pageSize == 0 || pageSize > pager.getFilteredRowCount()) {
                params.put("startRow", "0");
            }
            else {
                params.put("startRow", String.valueOf(pager.getStartRow()));                
            }            
            if(attributesName != null) {
                StringTokenizer t = new StringTokenizer(attributesProperties, ",");
                while (t.hasMoreTokens()) {
                    String attr = t.nextToken();
                    try {
                        params.put(attr, TagUtils.getInstance().lookup(pageContext, attributesName, attr, null).toString());
                    } catch (Exception e) {
                    }
                }
            }
            url = TagUtils.getInstance().computeURLWithCharEncoding(pageContext, forward, null, "", action, null, params, null,
                            false, false);
        } catch (MalformedURLException e) {
        }
        return (url);
    }

    protected String makeStyles(boolean disabled) throws JspException {
        StringBuilder results = new StringBuilder();
        String s = getStyleClass();
        if (s != null) {
            results.append(" class=\"");
            results.append(s);
            results.append("\"");
        }
        s = getStyle();
        if (s != null) {
            results.append(" style=\"");
            results.append(s);
            results.append("\"");
        }
        s = getStyleId();
        if (s != null) {
            results.append(" id=\"");
            results.append(s);
            results.append("\"");
        }
        return results.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        // JDR change so that task processes return to the correct location
        FormTag formTag = (FormTag) CoreUtil.getParentTagOfClass(FormTag.class, this);
        if (formTag != null) {
            subForm = formTag.getSubFormName();
        }
        else {
            subForm = null;
        }
        return super.doStartTag();
    }
}
