
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

import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.tags.FormTag;
import com.adito.table.Pager;

public class NavigationTag extends TagSupport {
    protected String pagerProperty = null;
    protected String pagerName = null;
    protected String action = null;
    protected String styleId = null;
    protected String styleClass = null;
    protected String style = null;
    protected String disabledStyleId = null;
    protected String disabledStyleClass = null;
    protected String disabledStyle = null;
    protected String forward = null;
    protected String attributesName;
    protected String attributesProperties;
    protected String requestAttributes;
    protected String subForm = null;
    protected String userDefinedSubForm = null;
    private String selectedStyle;
    private String selectedStyleClass;
    private String selectedStyleId;

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

    public void setSubForm(String userDefinedSubForm) {
        this.userDefinedSubForm = userDefinedSubForm;
    }

    public String getSubForm() {
        return userDefinedSubForm;
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

    public void setDisabledStyle(String disabledStyle) {
        this.disabledStyle = disabledStyle;
    }

    public String getDisabledStyle() {
        return disabledStyle;
    }

    public void setDisabledStyleClass(String disabledStyleClass) {
        this.disabledStyleClass = disabledStyleClass;
    }

    public String getDisabledStyleClass() {
        return disabledStyleClass;
    }

    public void setDisabledStyleId(String disabledStyleId) {
        this.disabledStyleId = disabledStyleId;
    }

    public String getDisabledStyleId() {
        return disabledStyleId;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    public String getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyleClass(String selectedStyleClass) {
        this.selectedStyleClass = selectedStyleClass;
    }

    public String getSelectedStyleClass() {
        return selectedStyleClass;
    }

    public void setSelectedStyleId(String selectedStyleId) {
        this.selectedStyleId = selectedStyleId;
    }

    public String getSelectedStyleId() {
        return selectedStyleId;
    }

    public void release() {
        super.release();
        pagerName = null;
        pagerProperty = null;
        action = null;
        styleId = null;
        styleClass = null;
        style = null;
        disabledStyleId = null;
        disabledStyleClass = null;
        disabledStyle = null;
        selectedStyleId = null;
        selectedStyleClass = null;
        selectedStyle = null;
        attributesName = null;
        attributesProperties = null;
        requestAttributes = null;
        subForm = null;
        userDefinedSubForm = null;
    }

    public void setAttributesName(String attributesName) {
        this.attributesName = attributesName;
    }

    public void setAttributesProperties(String attributesProperties) {
        this.attributesProperties = attributesProperties;
    }
    
    public void setRequestAttributes(String requestAttributes) {
        this.requestAttributes = requestAttributes;
    }

    public int doEndTag() throws JspException {
        StringBuffer results = new StringBuffer();
        Pager pager = (Pager) TagUtils.getInstance().lookup(pageContext, pagerName, pagerProperty, null);
        if (pager != null && pager.getModel().getRowCount() >= Integer.parseInt(PageSizeTag.PAGE_SIZES[0])) {
            // String imagePath =
            // CoreUtil.getThemePath(pageContext.getSession()) +
            // "/images/actions/";

            // Start of table
            results.append("<ul><li>");
            results.append("<a href=\"");
            if (pager.getStartRow() > 0) {
                results.append(getPageLink(0, pager));
            } else {
                results.append("javascript: void();");
            }
            results.append("\"");
            results.append(makeStyles(false, false));
            results.append(">");
            results.append("<div class=\"firstPage\"><span>&lt;&lt;</span></div>");
            results.append("</a></li>");

            // Previous page
            results.append("<li><a href=\"");
            if (pager.getHasPreviousPage()) {
                results.append(getPageLink(pager.getStartRow() - pager.calcPageSize(), pager));
            } else {
                results.append("javascript: void();");
            }
            results.append("\"");
            results.append(makeStyles(false, false));
            results.append(">");
            results.append("<div class=\"previousPage\"><span>&lt;</span></div>");
            results.append("</a></li>");

            // Pages
            int currPage = pager.getPageSize() == 0 ? 0 : (pager.getStartRow() / pager.getPageSize()) + 1;
            int maxToDisplay = 10;
            int page = Math.max(1, currPage - (maxToDisplay / 2));
            int idx = (page - 1) * pager.getPageSize();

            for (int pageIdx = 0; pageIdx < maxToDisplay && idx < pager.getFilteredRowCount(); pageIdx++) {
                boolean selected = idx >= pager.getStartRow() && idx < pager.getStartRow() + pager.calcPageSize();
                results.append("<li><a href=\"");
                results.append(selected ? "#" : getPageLink(idx, pager));
                results.append("\" ");
                results.append(makeStyles(false, selected));
                results.append("><div class=\"pageNumber\">");
                results.append(page);
                results.append("</div></a></li>");
                page++;
                idx += pager.calcPageSize();
            }

            // Next page
            results.append("<li><a href=\"");
            if (pager.getHasNextPage()) {
                results.append(getPageLink(pager.getStartRow() + pager.getPageSize(), pager));
            } else {
                results.append("javascript: void();");
            }
            results.append("\"");
            results.append(makeStyles(false, false));
            results.append(">");
            results.append("<div class=\"nextPage\"><span>&gt;</span></div>");
            results.append("</a></li>");

            // Last page
            results.append("<li><a href=\"");
            if (pager.getPageSize() != 0) {
                int leftOverRows = pager.getFilteredRowCount() % pager.getPageSize();
                int lastPageRow = pager.getFilteredRowCount() - leftOverRows;
                if (pager.getStartRow() < lastPageRow && lastPageRow < pager.getFilteredRowCount()) {
                    results.append(getPageLink(lastPageRow, pager));
                } else if (leftOverRows == 0) {
                    results.append(getPageLink(lastPageRow - pager.getPageSize(), pager));
                } else {
                    results.append("javascript: void();");
                }
            } else {
                results.append("javascript: void();");
            }

            // results.append(getPageLink(pager.getModel().getRowCount() -
            // pager.getPageSize(), pager));
            // if (pager.getPageSize() != 0 && pager.getStartRow() < (
            // pager.getModel().getRowCount() - pager.getPageSize() ) ) {
            //                
            // results.append(getPageLink(pager.getModel().getRowCount() -
            // pager.getPageSize(), pager));
            // } else {
            // results.append("javascript: void();");
            // }
            results.append("\"");
            results.append(makeStyles(false, false));
            results.append(">");
            results.append("<div class=\"lastPage\"><span>&gt;&gt;</span></div>");
            results.append("</a></li>");
            results.append("</ul>");
        } else {
            results.append("&nbsp;");
        }

        TagUtils.getInstance().write(pageContext, results.toString());
        return (EVAL_PAGE);

    }

    protected String getPageLink(int startRow, Pager pager) {
        String url = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("startRow", String.valueOf(startRow));
            params.put("pageSize", String.valueOf(pager.getPageSize()));
            params.put("sortName", pager.getSortName());
            params.put("sortReverse", String.valueOf(pager.getSortReverse()));

            if (subForm != null) {
                params.put("subForm", subForm);
            }
            
            if (attributesName != null) {
                params.putAll(getAttributesProperties());
            }
            
            if (requestAttributes != null) {
                params.putAll(Util.toMap(requestAttributes));
            }
            
            url = TagUtils.getInstance().computeURLWithCharEncoding(pageContext, forward, null, "", action, null, params, null,
                false, false);
        } catch (MalformedURLException e) {
        }
        return (url);
    }
    
    private Map<String, String> getAttributesProperties() {
        Map<String, String> params = new HashMap<String, String>();
        for (StringTokenizer tokenizer = new StringTokenizer(attributesProperties, ","); tokenizer.hasMoreTokens();) {
            String attr = tokenizer.nextToken();
            try {
                String value = TagUtils.getInstance().lookup(pageContext, attributesName, attr, null).toString();
                params.put(attr, value);
            } catch (Exception e) {
                // ignore
            }
        }
        return params;
    }

    protected String makeStyles(boolean disabled, boolean selected) throws JspException {
        StringBuffer results = new StringBuffer();
        String s = selected ? getSelectedStyleClass() : (disabled ? getDisabledStyleClass() : getStyleClass());
        if (s != null) {
            results.append(" class=\"");
            results.append(s);
            results.append("\"");
        }
        s = selected ? getSelectedStyle() : (disabled ? getDisabledStyle() : getStyle());
        if (s != null) {
            results.append(" style=\"");
            results.append(s);
            results.append("\"");
        }
        s = selected ? getSelectedStyleId() : (disabled ? getDisabledStyleId() : getStyleId());
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
    	if (userDefinedSubForm != null) {
			subForm = userDefinedSubForm;
		} else {
			FormTag formTag = (FormTag) CoreUtil.getParentTagOfClass(
					FormTag.class, this);
			if (formTag != null) {
				subForm = formTag.getSubFormName();
			} else {
				subForm = null;
			}
		}
    	userDefinedSubForm = null;
        return super.doStartTag();
    }
}
