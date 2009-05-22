
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

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;

import com.ovpnals.core.CoreUtil;

/**
 * Custom tag than renders a component that allows the user to entry some value
 * in a field, click on an <i>Add</i> button and have the value moved into a
 * second list field. Values may also be removed from the second list. *
 * <p>
 * Originally written to lists of users to be built up, this component is now
 * generic.
 * <p>
 * Values are passed between the component and the format implementation in
 * newline delimited <i>Property List</i> format. See
 * {@link com.ovpnals.boot.PropertyList} for more details on this.
 * <p>
 * Titles may be provide for each side of the component using message resource
 * keys.
 * <h3>Supported Attributes</h3>
 * 
 * TODO document the supported attributes
 */
public class MultiEntryListBoxTag extends AbstractMultiFieldTag {
	
	final static Log log = LogFactory.getLog(MultiEntryListBoxTag.class);

    // Protected instance variables

    protected String entrySize;
    protected String entryTitleKey;
    protected String entryStyleClass;
    protected String entryName;
    protected String targetUnique;
    protected String indicator;
    protected boolean includeUserAttributes;
    protected boolean includeSession;
    protected boolean showReplacementVariables;
    protected String replacementVariablesTitleKey;
    protected String variables;
    protected String replacementVariablesBundle;

    /**
     * Constructor
     */
    public MultiEntryListBoxTag() {
        super();
        this.type = "hidden";
        targetUnique = "true";
        indicator = null;
        name = null;
        showReplacementVariables = false;
        replacementVariablesBundle = null;
        includeSession = true;
        includeUserAttributes = true;
        setStyleId("multiEntry");
        setRows("5");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {

        String entryTitle = null;
        if (entryTitleKey != null) {
            entryTitle = TagUtils.getInstance().message(pageContext, getBundle(), getLocale(), entryTitleKey, new String[] {});

            if (entryTitle == null) {
                JspException e = new JspException(messages.getMessage("message.message", "\"" + entryTitleKey + "\""));
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
        if (entryTitle != null || targetTitle != null) {
            results.append("<tr class=\"header\"><td class=\"entry\">");
            if (entryTitle != null) {
                results.append(entryTitle);
            }
            results.append("</td><td class=\"actions\"/><td class=\"target\">");
            if (targetTitle != null) {
                results.append(targetTitle);
            }
            results.append("</td></tr>");
        }
        results.append("<tr class=\"body\"><td class=\"entry\">");
        results.append(renderEntryComponent());
        results.append("</td><td class=\"actions\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        results.append("<tr><td>");
        results.append(renderAddComponent());
        results.append("</td></tr><tr><td>");
        results.append(renderRemoveSelectedComponent());
        if (isAllowReordering()) {
            results.append("</td></tr><tr><td>");
            results.append(renderUpComponent());
            results.append("</td></tr><tr><td>");
            results.append(renderDownComponent());
        }
        results.append("</td></tr></table></td><td class=\"target\">");
        results.append(renderTargetComponent());
        results.append("</td></tr></table></div>");
        TagUtils.getInstance().write(this.pageContext, results.toString());

        return (EVAL_BODY_BUFFERED);
    }

    /**
     * Get the size of the field to use for the entry side.
     * 
     * @return the entrySize.
     */
    public String getEntrySize() {
        return entrySize;
    }

    /**
     * Set the size of the field to use on the entry side.
     * 
     * @param entrySize size of entry field
     */
    public void setEntrySize(String entrySize) {
        this.entrySize = entrySize;
    }

    /**
     * Get the message resources key to use for the title on the entry side
     * 
     * @return message resources key for entry title
     */
    public String getEntryTitleKey() {
        return entryTitleKey;
    }

    /**
     * Set the message resources key to use for the title on the entry side
     * 
     * @param entryTitleKey message resources key for entry title
     */
    public void setEntryTitleKey(String entryTitleKey) {
        this.entryTitleKey = entryTitleKey;
    }

    /**
     * Set the CSS class to use for the entry field
     * 
     * @param entryStyleClass CSS class to use for entry field
     */
    public void setEntryStyleClass(String entryStyleClass) {
        this.entryStyleClass = entryStyleClass;
    }

    /**
     * Set the name attribute to use for the entry field
     * 
     * @param entryName name attribute use for entry field
     */
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    /**
     * Set the name of the <i>Indicator</i>. Using this in conjunction with
     * AjaxTags allows a throbber to be displayed whilst the XML data is being
     * retrieved.
     * <p>
     * The value of the indicator must match the value provided to the AjaxTags
     * autocomplete tag and there must also be an image with the same name (with
     * .gif append) in the themes top level image directory.
     * 
     * @param indicator indicator name
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     * Set the messsage resources key to use for the title of the replacement
     * variables popup.
     * 
     * @param replacementVariablesTitleKey replacement variables title message
     *        resources key
     */
    public void setReplacementVariablesTitleKey(String replacementVariablesTitleKey) {
        this.replacementVariablesTitleKey = replacementVariablesTitleKey;
    }

    /**
     * Set the messsage resources bundle to use for the title of the replacement
     * variables popup and the descriptions of the replacement variables
     * themselves
     * 
     * @param replacementVariablesBundle replacement variables message resources
     *        bundle
     */
    public void setReplacementVariablesBundle(String replacementVariablesBundle) {
        this.replacementVariablesBundle = replacementVariablesBundle;
    }

    /**
     * Set the comma separated list of variables names
     * 
     * @param variables variables.
     */
    public void setVariables(String variables) {
        this.variables = variables;
    }

    /**
     * Set whether all of the user attributes should be added to the list of
     * variables
     * 
     * @param includeUserAttributes include user attributes
     * 
     */
    public void setIncludeUserAttributes(boolean includeUserAttributes) {
        this.includeUserAttributes = includeUserAttributes;
    }

    /**
     * Set whether all of the <i>session</i> replacement variables
     * 
     * @param includeSession include session
     * 
     */
    public void setIncludeSession(boolean includeSession) {
        this.includeSession = includeSession;
    }

    /**
     * Set whether the replacement variables selection box should be displayed.
     * 
     * @param showReplacementVariables show replacement variables
     */
    public void setShowReplacementVariables(boolean showReplacementVariables) {
        this.showReplacementVariables = showReplacementVariables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        this.type = "hidden";
        entryName = null;
        targetUnique = "true";
        indicator = null;
        setStyleId("multiEntry");
        setRows("5");
        variables = null;
        includeUserAttributes = true;
        includeSession = true;
        showReplacementVariables = false;
        replacementVariablesBundle = null;
    }

    protected String renderEntryComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input type=\"text\"");
        results.append(prepareDisabled());
        results.append(" id=\"");
        String entryId = prepareEntryId();
        results.append(entryId);
        results.append("\"");
        results.append(" class=\"");
        results.append(showReplacementVariables ? (indicator != null && !indicator.equals("") ? "twoButtons" : "oneButton")
                        : (indicator != null && !indicator.equals("") ? "oneButton" : "noButtons"));
        results.append("\"");
        if (entrySize != null && !entrySize.equals("")) {
            results.append(" size=\"");
            results.append(entrySize);
            results.append("\"");
        }
        if (entryName != null && !entryName.equals("")) {
            results.append(" name=\"");
            results.append(entryName);
            results.append("\"");
        }
        results.append("/>");
        if (showReplacementVariables) {
            results.append(VariablesTag.generateReplacementVariableChooserFragment(replacementVariablesTitleKey, pageContext,
                            replacementVariablesBundle == null ? "navigation" : replacementVariablesBundle, getLocale(), entryId,
                            variables, includeSession, includeUserAttributes, false));
        }
        if (indicator != null && !indicator.equals("")) {
            results.append("<span id=\"");
            results.append(indicator);
            results.append("\" style=\"display:none;\"><img src=\"");
            results.append(CoreUtil.getThemePath(pageContext.getSession()));
            results.append("/images/indicator.gif\"/></span>");
        }
        return results.toString();

    }

    protected String renderAddComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiAdd\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiEntryAddEntry(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'), document.getElementById('");
        results.append(prepareEntryId());
        results.append("'),'");
        results.append(targetUnique);
        results.append("');\" type=\"button\" value=\"");
        results.append(prepareAdd());
        results.append("\"/>");
        return results.toString();
    }

    protected String renderRemoveSelectedComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiRemove\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiEntryRemoveSelectedEntry(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'), document.getElementById('");
        results.append(prepareEntryId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareRemove());
        results.append("\"/>");
        return results.toString();
    }

    protected String prepareEntryId() throws JspException {
        StringBuffer results = new StringBuffer("entryValue_");
        if (indexed) {
            prepareIndex(results, name);
        }
        results.append(property);
        return results.toString();
    }

    protected String renderTargetComponent() throws JspException {
        StringBuffer results = new StringBuffer("<select multiple id=\"");
        results.append(prepareTargetId());
        results.append("\"");
        if (rows != null) {
            results.append(" size=\"");
            results.append(rows);
            results.append("\"");
        }
        results.append(">");
        Object value = TagUtils.getInstance().lookup(pageContext, name, property, null);
        if (value == null || !(value instanceof String)) {
            log.error("Name / property attributes (" + name + "/" + property + ") must specify a newline (\\n) separated string.");
        }
        else {
            StringTokenizer t = new StringTokenizer(value.toString(), "\n");
            while (t.hasMoreTokens()) {
                String val = t.nextToken();
                results.append("<option value=\"");
                results.append(val);
                results.append("\">");
                results.append(val);
                results.append("</option>");
            }
        }
        results.append("</select>");
        return results.toString();

    }

    protected String renderInputElement() throws JspException {
        StringBuffer results = new StringBuffer("<input type=\"");
        results.append(this.type);
        results.append("\" name=\"");

        if (indexed) {
            this.prepareIndex(results, name);
        }

        results.append(property);
        results.append("\"");

        if (entryStyleClass != null) {
            results.append(" class=\"");
            results.append(entryStyleClass);
            results.append("\"");
        }

        results.append(" id=\"");
        if (indexed) {
            this.prepareIndex(results, name);
        }

        results.append(property);
        results.append("\"");

        results.append(" value=\"");
        if (value != null) {
            results.append(this.formatValue(value));

        } else if (redisplay || !"password".equals(type)) {
            Object value = TagUtils.getInstance().lookup(pageContext, name, property, null);

            results.append(this.formatValue(value));
        }
        results.append('"');
        results.append(prepareDisabled());
        results.append(this.prepareEventHandlers());
        results.append(this.getElementClose());
        return results.toString();
    }
}
