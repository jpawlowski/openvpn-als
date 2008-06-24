
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
			
package com.adito.input.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;

import com.adito.boot.Util;
import com.adito.core.CoreUtil;

/**
 */
public class ToolTipTag extends BodyTagSupport {
    
    final static Log log = LogFactory.getLog(ToolTipTag.class);
    
    protected String key;
    protected String bundle;
    protected String displayText;
    protected String property;
    protected String scope;
    protected String name;
    protected String toolTipText;
    protected String width;
    protected String textAlign;
    protected String value;
    protected String styleId;
    protected String href = "javascript:void(0);";
    protected String target;
    protected String onclick;
    protected boolean enabled;
    protected boolean showToolTip = false;
    protected String additionalAttributeName;
    protected String additionalAttributeValue;
    protected String contentLocation;
    protected String backgroundColor;
    protected String shadowWidth;
    protected String padding;
    protected String borderWidth;

    protected String localeKey = Globals.LOCALE_KEY;

    public String getLocale() {
        return (this.localeKey);
    }

    public void setLocale(String localeKey) {
        this.localeKey = localeKey;
    }

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources(
            "org.apache.struts.taglib.bean.LocalStrings");

    /**
     * 
     */
    public ToolTipTag() {
        super();
    }
    
    /**
     * Set the a request location where content may be 
     * dynamically retrieved from
     * 
     * @param contentLocation location of content
     */
    public void setContentLocation(String contentLocation) {
    	this.contentLocation = contentLocation;
    }
    
    /**
     * @return Returns the bundle.
     */
    public String getBundle() {
        return bundle;
    }
    /**
     * @param bundle The bundle to set.
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
    /**
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }
    /**
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        enabled =  CoreUtil.getToolTipsEnabled(pageContext.getSession());
       
        displayText = null;
        if(value == null) {

	        if (key == null && name != null) {
	            // Look up the requested property value
	            Object value = TagUtils.getInstance().lookup(pageContext, name, property, scope);
	            if (value != null && !(value instanceof String)) {
	                JspException e =
	                    new JspException(messages.getMessage("message.property", key));
	               TagUtils.getInstance().saveException(pageContext, e);
	                throw e;
	            }
	            displayText = (String) value;
	        }
	
	        // Retrieve the message string we are looking for
	        if(displayText == null && key != null) {
		        displayText =
		            TagUtils.getInstance().message(
		                pageContext,
		                this.bundle,
		                this.localeKey,
		                key);
                if(displayText == null) {
                	log.error("Missing message for key "+ key + " bundle = " + bundle);
                }
	        }

	        // Content location
        	if(contentLocation != null) {
        		displayText = "";
        	}
        }
        else {
       		displayText = value;
        }
        
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Save the associated label from the body content (if any).
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0) {
                if(displayText == null) {
                    displayText = value;
                }
                else {
                    toolTipText = value;
                }
            }
        }
        return (SKIP_BODY);

    }

    public int doEndTag() throws JspException {

        StringBuffer results = new StringBuffer();
        if(displayText != null) {
	        results.append("<a ");
            if(enabled && !showToolTip) {
                results.append("onmouseover=\"");
                if(backgroundColor != null) {
                    results.append("this.T_BGCOLOR='");
                    results.append(backgroundColor);
                    results.append("';");                	
                }
                if(padding != null) {
                    results.append("this.T_PADDING=");
                    results.append(padding);
                    results.append(";");                	
                }
                if(shadowWidth != null) {
                    results.append("this.T_SHADOWWIDTH=");
                    results.append(shadowWidth);
                    results.append(";");                	
                }
                if(borderWidth != null) {
                    results.append("this.T_BORDERWIDTH=");
                    results.append(borderWidth);
                    results.append(";");                	
                }
                if(width != null) {
                    results.append("this.T_WIDTH=");
                    results.append(width);
                    results.append(";");
                }
                if(textAlign != null) {
                    results.append("this.T_TEXTALIGN='");
                    results.append(textAlign);
                    results.append("';");
                }

//                results.append("return escape(");
//                if(contentLocation != null) {
//                    results.append("loadXMLDoc('");
//                    results.append(Util.escapeForJavascriptString(contentLocation));
//                    results.append("')");
//                }
//                else {
//                results.append("'");
//                results.append(Util.escapeForJavascriptString(displayText));
//                results.append("'");
//                }
//                results.append(");\" ");
                
                
                if(contentLocation != null) {
                	results.append("return escape('");
                	results.append("!" + Util.escapeForJavascriptString(contentLocation));
                	results.append("');\" ");
                }
                else {
                    results.append("return escape(");
                	results.append("'");
                	results.append(Util.escapeForJavascriptString(displayText));
                	results.append("'");
                    results.append(");\" ");
                }
            }
            if(styleId != null) {
                results.append("class=\"" + styleId + "\" ");
            }
            if (target != null){
            	results.append("target=\"" + target + "\" ");
            }
            results.append("href=\"" + href + "\"");
            if (onclick != null)
            	results.append(" onclick=\"" + onclick + "\"");
            if(additionalAttributeName != null && !"".equals(additionalAttributeName)) 
            	results.append(" " + additionalAttributeName + "=\"" + additionalAttributeValue + "\"");
            
            results.append(">");
        }
        if(toolTipText != null) {
            results.append(toolTipText);
        }
        if(displayText != null) {
            results.append("</a>");
        }
        // Render this element to our writer
        TagUtils.getInstance().write(pageContext, results.toString());

        // Evaluate the remainder of this page
        return (EVAL_PAGE);
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
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }
    
    public String getStyleId() {
        return styleId;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getHref() {
        return href;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
     */
    public void release() {
        super.release();
        key = null;
        bundle = null;
        displayText = null;
        property = null;
        scope = null;
        name = null;
        toolTipText = null;
        width = null;
        textAlign = null;
        value = null;
        styleId = null;
        onclick = null;
        href = "javascript:void(0);";
        target = null;
        additionalAttributeName = "";
        additionalAttributeValue = "";
        padding = null;
        shadowWidth = null;
        borderWidth = null;
        backgroundColor = null;
        
    }

    public void setWidth(String width) {
        this.width = width;
    }
    
    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public void setText(String text) {
        this.toolTipText = text;
    }

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

    public boolean isShowToolTip() {
        return showToolTip;
    }

    public void setShowToolTip(boolean showToolTip) {
        this.showToolTip = showToolTip;
    }

	public void setAdditionalAttributeName(String additionalAttributeName) {
		this.additionalAttributeName = additionalAttributeName;
	}

	public void setAdditionalAttributeValue(String additionalAttributeValue) {
		this.additionalAttributeValue = additionalAttributeValue;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(String borderWidth) {
		this.borderWidth = borderWidth;
	}

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	public String getShadowWidth() {
		return shadowWidth;
	}

	public void setShadowWidth(String shadowWidth) {
		this.shadowWidth = shadowWidth;
	}
}
