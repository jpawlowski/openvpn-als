
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
			
package com.ovpnals.input.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

/**
 */
public class MultiSelectPoliciesListBoxTag extends MultiSelectListBoxTag {

    protected String sourceTitleKey;
    protected String valueKeyPrefix;
    protected String showPersonalPoliciesKey;
    protected boolean showPersonalPolicies;

    /**
     */
    public MultiSelectPoliciesListBoxTag() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {

        String sourceTitle = null;
        if (sourceTitleKey != null) {
            sourceTitle = TagUtils.getInstance().message(pageContext, getBundle(), getLocale(), sourceTitleKey, new String[] {});

            if (sourceTitle == null) {
                JspException e = new JspException(messages.getMessage("message.message", "\"" + sourceTitleKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
        }

        String targetTitle = prepareTargetTitle();

        StringBuffer results = new StringBuffer("<div ");
        results.append(prepareStyles());
        results.append(">");
        results.append(this.renderInputElement());
        results.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        if (sourceTitle != null || targetTitle != null) {
            results.append("<tr class=\"header\"><td class=\"source\">");
            if (sourceTitle != null) {
                results.append(sourceTitle);
            }
            results.append("</td><td class=\"actions\"/><td class=\"target\">");
            if (targetTitle != null) {
                results.append(targetTitle);
            }
            results.append("</td></tr>");
        }
        results.append("<tr class=\"body\"><td class=\"source\">");
        results.append(renderSourceComponent());
        results.append("</td><td class=\"actions\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        results.append("<tr><td>");
        results.append(renderSelectComponent());
        results.append("</td></tr>");
        if (addAllKey != null) {
            results.append("<tr><td>");
            results.append(renderSelectAllComponent());
            results.append("</td></tr>");
        }
        results.append("<tr><td>");
        results.append(renderDeselectComponent());
        if (removeAllKey != null) {
            results.append("</td></tr>");

            results.append("<tr><td>");
            results.append(renderDeselectAllComponent());
        }
        if (configureKey != null) {
            results.append("</td></tr><tr><td>");
            results.append(renderConfigureComponent());
        }
        if (isAllowReordering()) {
            results.append("</td></tr><tr><td>");
            results.append(renderUpComponent());
            results.append("</td></tr><tr><td>");
            results.append(renderDownComponent());
        }
        results.append("</td></tr></table></td><td class=\"target\">");
        results.append(renderTargetComponent());
        results.append("</td></tr>");
        results.append("<tr><td>");
        results.append(renderCheckboxComponent());
        results.append("</td></tr></table></div>");

        TagUtils.getInstance().write(this.pageContext, results.toString());

        return (EVAL_BODY_BUFFERED);
    }

    protected String renderCheckboxComponent() throws JspException {
        StringBuffer results = new StringBuffer(preparepersonalPoliciesMessage());
        results.append("<input type=\"checkbox\"");
        results.append(showPersonalPolicies ? " checked=\"checked\"" : "");
        results.append(" onclick=\"this.form.action = this.form.action + '?showPersonalPolicies=' + this.checked; setFormActionTarget('toogleShowPersonalPolicies',this.form); this.form.submit();\"");
        results.append(" id=\"showPersonalPolicies\"");
        results.append(" styleClass=\"formCheckbox\"");
        results.append(" />");
        return results.toString();
    }

    protected String preparepersonalPoliciesMessage() throws JspException {
        
        if(showPersonalPoliciesKey != null) {
            String showPersonalPolicies =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                showPersonalPoliciesKey,
                new String[] { });
            
            if (showPersonalPolicies == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + showPersonalPoliciesKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return showPersonalPolicies;
        }
        return null;
    }
    
    /**
     * @return showPersonalPoliciesKey
     */
    public String getShowPersonalPoliciesKey() {
        return showPersonalPoliciesKey;
    }

    /**
     * @param showPersonalPoliciesKey
     */
    public void setShowPersonalPoliciesKey(String showPersonalPoliciesKey) {
        this.showPersonalPoliciesKey = showPersonalPoliciesKey;
    }

    /**
     * @return showPersonalPolicies
     */
    public boolean isShowPersonalPolicies() {
        return showPersonalPolicies;
    }

    /**
     * @param showPersonalPolicies
     */
    public void setShowPersonalPolicies(boolean showPersonalPolicies) {
        this.showPersonalPolicies = showPersonalPolicies;
    }
    
}
