
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

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.LinkTag;
import org.apache.struts.taglib.logic.IterateTag;

import com.adito.core.CoreUtil;
import com.adito.core.tags.FormTag;
import com.adito.table.Pager;

public class ColumnHeaderTag extends LinkTag {

    /**
     * Name of the bean that contains the {@link com.adito.table.Pager}
     * object
     */
    protected String pagerName = null;

    protected String attributesName;
    protected String attributesProperties;
    protected String subForm = null;
    protected String userDefinedSubForm = null;

    public String getPagerName() {
        return (this.pagerName);
    }

    public void setPagerName(String pagerName) {
        this.pagerName = pagerName;
    }

    /**
     * Name of the property to be accessed on the specified bean that contains
     * the {@link com.adito.table.Pager}.
     */
    protected String pagerProperty = null;

    public String getPagerProperty() {
        return (this.pagerProperty);
    }

    public void setPagerProperty(String pagerProperty) {
        this.pagerProperty = pagerProperty;
    }

    /**
     * The column index.
     */
    protected int columnIndex = 0;

    public String getColumnIndex() {
        return String.valueOf(this.columnIndex);
    }

    public void setColumnIndex(String columnIndex) {
        this.columnIndex = Integer.parseInt(columnIndex);
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
            FormTag formTag = (FormTag) CoreUtil.getParentTagOfClass(FormTag.class, this);
            if (formTag != null) {
                subForm = formTag.getSubFormName();
            } else {
                subForm = null;
            }
        }
        userDefinedSubForm = null;
        return super.doStartTag();
    }

    public int doEndTag() throws JspException {

        StringBuffer results = new StringBuffer();
        Pager pager = (Pager) TagUtils.getInstance().lookup(pageContext, pagerName, pagerProperty, scope);
        if (text != null) {
            results.append(text);
        }
        if (pager != null) {
            String name = pager.getModel().getColumnName(columnIndex);
            if (name.equals(pager.getSortName())) {
                results.append("&nbsp;<img border=\"0\" src=\"");
                if (pager.getSortReverse()) {
                    results.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/actions/descending.gif");
                } else {
                    results.append(CoreUtil.getThemePath(pageContext.getSession()) + "/images/actions/ascending.gif");
                }
                results.append("\"/>");
            }
        }
        results.append("</a>");

        TagUtils.getInstance().write(pageContext, results.toString());
        return (EVAL_PAGE);

    }

    /**
     * Return the complete URL to which this hyperlink will direct the user.
     * Support for indexed property since Struts 1.1
     * 
     * @exception JspException if an exception is thrown calculating the value
     */
    protected String calculateURL() throws JspException {

        // Identify the parameters we will add to the completed URL
        Map params = TagUtils.getInstance().computeParameters(pageContext, paramId, paramName, paramProperty, paramScope, name,
            property, scope, transaction);

        // if "indexed=true", add "index=x" parameter to query string
        // * @since Struts 1.1
        if (indexed) {

            // look for outer iterate tag
            IterateTag iterateTag = (IterateTag) findAncestorWithClass(this, IterateTag.class);
            if (iterateTag == null) {
                // This tag should only be nested in an iterate tag
                // If it's not, throw exception
                JspException e = new JspException(messages.getMessage("indexed.noEnclosingIterate"));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }

            // calculate index, and add as a parameter
            if (params == null) {
                params = new HashMap(); // create new HashMap if no other params
            }
            if (indexId != null) {
                params.put(indexId, Integer.toString(iterateTag.getIndex()));
            } else {
                params.put("index", Integer.toString(iterateTag.getIndex()));
            }

        }

        // get the pager object
        Pager pager = (Pager) TagUtils.getInstance().lookup(pageContext, pagerName, pagerProperty, scope);
        if (pager != null) {
            if (params == null) {
                params = new HashMap(); // create new HashMap if no other params
            }
            String name =  pager.getModel().getId() + "." + pager.getModel().getColumnName(columnIndex);

            // NOTE - These are now stored in the session anyway - BPS
            params.put("pageSize", String.valueOf(pager.getPageSize()));
            params.put("startRow", String.valueOf(pager.getStartRow()));
            params.put("sortName", name);
            params.put("sortReverse", name.equals(pager.getModel().getId() + "." + pager.getSortName()) ? String.valueOf(!pager.getSortReverse()) : "false");
        }
        
        if (subForm != null)
            params.put("subForm", subForm);

        if(attributesName != null) {
            StringTokenizer t = new StringTokenizer(attributesProperties, ",");
            while (t.hasMoreTokens()) {
                String attr = t.nextToken();
                try {
                    params.put(attr, TagUtils.getInstance().lookup(pageContext, attributesName, attr, scope).toString());
                } catch (Exception e) {
                }
            }
        }
        String newPage = page;

        FormTag formTag = (FormTag)CoreUtil.getParentTagOfClass(FormTag.class, this);

        if(forward == null && newPage == null && action == null && href == null &&
                        formTag != null) {
            newPage = formTag.getAction();
        }
        
        String url = null;
        try {
            url = TagUtils.getInstance().computeURLWithCharEncoding(pageContext, forward, href, newPage, action, module, params,
                anchor, false, useLocalEncoding);
        } catch (MalformedURLException e) {
            TagUtils.getInstance().saveException(pageContext, e);
            throw new JspException(messages.getMessage("rewrite.url", e.toString()));
        }
        return (url);

    }

    /**
     * Release all allocated resources.
     */
    public void release() {
        super.release();
        pagerName = null;
        pagerProperty = null;
        columnIndex = 0;
        attributesName = null;
        attributesProperties = null;
        page = null;
        subForm = null;
        userDefinedSubForm = null;
    }
    
    public void setAttributesName(String attributesName) {
        this.attributesName = attributesName;
    }

    public void setAttributesProperties(String attributesProperties) {
        this.attributesProperties = attributesProperties;
    }

    public void setSubForm(String userDefinedSubForm) {
        this.userDefinedSubForm = userDefinedSubForm;
    }

    public String getSubForm() {
        return userDefinedSubForm;
    }
}
