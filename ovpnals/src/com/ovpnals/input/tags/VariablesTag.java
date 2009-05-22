
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

import java.util.Collection;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseFieldTag;
import org.apache.struts.util.MessageResources;

import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.AttributesPropertyClass;
import com.ovpnals.properties.impl.resource.ResourceAttributes;

/**
 * Tag this inserts a component that allows the user to select a replacement
 * variable to be inserted into an input component.
 */
public class VariablesTag extends BaseFieldTag {

    final static Log log = LogFactory.getLog(VariablesTag.class);

    // Protected instance variables

    protected String variables;
    protected String fragment;
    protected String inputId;
    protected boolean includeUserAttributes;
    protected boolean includeRequestAttributes;
    protected boolean includeSession;
    protected String titleKey;
    protected boolean disabled;

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages = MessageResources.getMessageResources("org.apache.struts.taglib.bean.LocalStrings");

    /**
     * Constructor
     */
    public VariablesTag() {
        includeUserAttributes = true;
        includeSession = true;
        includeRequestAttributes = false;
        disabled = false;
    }

    /**
     * Set the message resources key to use for the title
     * 
     * @param titleKey title message resources key
     */
    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    /**
     * Set the id of the component to insert the chosen variable into.
     * 
     * @param inputId input component id
     */
    public void setInputId(String inputId) {
        this.inputId = inputId;
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
     * Set whether all of the <i>request</i> replacement variables
     * 
     * @param includeRequest include session
     * 
     */
    public void setIncludeRequest(boolean includeRequest) {
        this.includeRequestAttributes = includeRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    public int doEndTag() throws JspException {
        if (fragment != null) {
            TagUtils.getInstance().write(pageContext, fragment);
        }
        return (EVAL_PAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
     */
    public void release() {
        super.release();
        fragment = null;
        variables = null;
        inputId = null;
        includeUserAttributes = true;
        includeSession = true;
        includeRequestAttributes = false;
        disabled = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        if (disabled)
            return SKIP_PAGE;

        if (getBundle() == null || getBundle().equals("")) {
            setBundle("navigation");
        }
        fragment = null;
        if (inputId != null) {
            fragment = generateReplacementVariableChooserFragment(titleKey, pageContext, getBundle(), getLocale(), inputId,
                            variables, includeSession, includeUserAttributes, includeRequestAttributes);
        } else {
            log.warn("Both variables and inputId attributes must be specified for variables tag");
            return SKIP_PAGE;
        }
        return (EVAL_BODY_AGAIN);
    }

    /**
     * Method to generate the Replacement Variable Chooser Fragment.
     * 
     * @param titleKey
     * @param pageContext
     * @param bundle
     * @param locale
     * @param inputId
     * @param variables
     * @param includeSession
     * @param includeUserAttributes
     * @param includeRequest
     * @return String
     * @throws JspException
     */
    public static String generateReplacementVariableChooserFragment(String titleKey, PageContext pageContext, String bundle,
                    String locale, String inputId, String variables, boolean includeSession, boolean includeUserAttributes,
                    boolean includeRequest) throws JspException {
        StringBuffer buf = new StringBuffer();

        String title = null;
        if (titleKey != null) {
            title = TagUtils.getInstance().message(pageContext, bundle, locale, titleKey, new String[] {});
        }
        if (title == null) {
            title = TagUtils.getInstance().message(pageContext, bundle, locale, "replacementVariablesChooser.title",
                            new String[] {});
            if (title == null) {
                title = "Replacement Variables";
            }
        }
        buf.append("<div class=\"component_replacementVariablesToggle\">");

        // buf.append("<input type=\"button\"
        // onclick=\"toggleAndPositionBelow(document.getElementById('replacementVariablesChooser");
        // buf.append(inputId);
        // buf.append("'), document.getElementById('");
        // buf.append(inputId);
        // buf.append("'))\" ");
        // buf.append("value=\"${}\"/>");

        buf.append("<img onclick=\"togglePopupBelowLeft(document.getElementById('replacementVariablesChooser");
        buf.append(inputId);
        buf.append("'), document.getElementById('");
        buf.append(inputId);
        buf.append("'))\" ");
        buf.append("src=\"");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/variables.gif\"/>");

        buf.append("</div>");
        buf.append("<div id=\"replacementVariablesChooser");
        buf.append(inputId);
        buf.append("\" style=\"position:absolute;display: none;overflow:visible;top:4px;left:4px;z-index:900;\">");
        buf.append("<div class=\"replacementVariablesMain\">");
        buf.append("<div class=\"replacementVariablesTitleBar\">");
        buf.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        buf.append("<tr><td class=\"title\">");
        buf.append(title);
        buf.append("</td><td class=\"close\"><img src=\"");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/actions/erase.gif\" ");
        buf.append("onclick=\"document.getElementById('");
        buf.append(inputId);
        buf.append("').value = ''\"/><img src=\"");
        buf.append(CoreUtil.getThemePath(pageContext.getSession()));
        buf.append("/images/actions/closeReplacementVariables.gif\" ");
        buf.append("onclick=\"togglePopup(document.getElementById('replacementVariablesChooser");
        buf.append(inputId);
        buf.append("'))\"/>");
        buf.append("</td></tr></table></div><div class=\"replacementVariablesContent\">");
        buf.append("<ul>");
        if (variables != null) {
            StringTokenizer t = new StringTokenizer(variables, ",");
            while (t.hasMoreTokens()) {
                String n = t.nextToken();
                addVariable(n, n, buf, pageContext, bundle, locale, inputId);
            }
        }
        if (includeSession) {
            addVariable("session:username", null, buf, pageContext, bundle, locale, inputId);
            addVariable("session:password", null, buf, pageContext, bundle, locale, inputId);
            addVariable("session:email", null, buf, pageContext, bundle, locale, inputId);
            addVariable("session:fullname", null, buf, pageContext, bundle, locale, inputId);
            addVariable("session:clientProxyURL", null, buf, pageContext, bundle, locale, inputId);
        }

        if (includeRequest) {
            addVariable("request:serverName", null, buf, pageContext, bundle, locale, inputId);
            addVariable("request:serverPort", null, buf, pageContext, bundle, locale, inputId);
            addVariable("request:userAgent", null, buf, pageContext, bundle, locale, inputId);
        }

        if (includeUserAttributes) {
            Collection<PropertyDefinition> l;
            try {
                for (PropertyClass propertyClass : PropertyClassManager.getInstance().getPropertyClasses()) {
                    if (propertyClass instanceof AttributesPropertyClass
                                    && !propertyClass.getName().equals(ResourceAttributes.NAME)) {
                        l = propertyClass.getDefinitions();
                        for (PropertyDefinition d : l) {
                            AttributeDefinition def = (AttributeDefinition) d;
                            if (def.isReplaceable()) {
                                addVariable(def.getPropertyClass().getName() + ":" + def.getName(), def.getDescription(), buf,
                                                pageContext, bundle, locale, inputId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to get user attribute definitions.");
            }
        }
        buf.append("</ul>");
        buf.append("</div>");
        buf.append("</div>");
        return buf.toString();
    }

    /**
     * Method to add a variable to the replacement fragment.
     * 
     * @param variable
     * @param desc
     * @param buf
     * @param pageContext
     * @param bundle
     * @param locale
     * @param inputId
     * @throws JspException
     */
    static void addVariable(String variable, String desc, StringBuffer buf, PageContext pageContext, String bundle, String locale,
                    String inputId) throws JspException {
        buf.append("<li><a onmouseover=\"return escape('");
        if (desc == null) {
            desc = TagUtils.getInstance().message(pageContext, bundle, locale, "replacementVariable." + variable + ".description",
                            new String[] {});
            if (desc == null) {
                desc = "${" + variable + "}";
            }
        }
        buf.append(desc);
        buf.append("')\" href=\"#\" onclick=\"document.getElementById('");
        buf.append(inputId);
        buf.append("').value = document.getElementById('");
        buf.append(inputId);
        buf.append("').value + '${");
        buf.append(variable);
        buf.append("}'; togglePopup(document.getElementById('replacementVariablesChooser");
        buf.append(inputId);
        buf.append("'))\">");
        buf.append(variable);
        buf.append("</a></li>");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.taglib.html.BaseHandlerTag#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
