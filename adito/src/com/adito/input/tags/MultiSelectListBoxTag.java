
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

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;

import com.adito.core.CoreUtil;
import com.adito.input.MultiSelectSelectionModel;

/**
 */
public class MultiSelectListBoxTag extends AbstractMultiFieldTag {
    
    protected String modelName;
    protected String modelProperty;
    protected String sourceTitleKey;
    protected String valueKeyPrefix;

    public MultiSelectListBoxTag() {
        super();
        release();
    }

    public void release() {
        super.release();
        this.type = "hidden";
        setStyleId("multiSelect");
        setRows("5");
        modelName = null;
        modelProperty = null;
        sourceTitleKey = null;
        valueKeyPrefix = null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseFieldTag#doStartTag()
     */
    public int doStartTag() throws JspException {
        
        String sourceTitle = null;
        if(sourceTitleKey != null) {
            sourceTitle =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                sourceTitleKey,
                new String[] { });
            
            if (sourceTitle == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + sourceTitleKey + "\""));
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
        if(sourceTitle != null || targetTitle != null) {
            results.append("<tr class=\"header\"><td class=\"source\">");
            if(sourceTitle != null) {
                results.append(sourceTitle);
            }
            results.append("</td><td class=\"actions\"/><td class=\"target\">");
            if(targetTitle != null) {
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
        if (addAllKey != null){
            results.append("<tr><td>");
            results.append(renderSelectAllComponent());
            results.append("</td></tr>");
        }
        results.append("<tr><td>");
        results.append(renderDeselectComponent());
        if (removeAllKey != null){
            results.append("</td></tr>");
            
            results.append("<tr><td>");
            results.append(renderDeselectAllComponent());
        }
        if(configureKey != null) {
            results.append("</td></tr><tr><td>");
            results.append(renderConfigureComponent());
        }
        if(isAllowReordering()) {
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
    
    protected String prepareSourceId() throws JspException {
        StringBuffer results = new StringBuffer("sourceValues_");
        if(indexed) {
            prepareIndex(results, name);
        }
        results.append(property);
        return results.toString();
    }
    
    protected String renderSourceComponent() throws JspException {
        StringBuffer results = new StringBuffer("<select multiple id=\"");
        results.append(prepareSourceId());
        results.append("\"");
        if (rows != null) {
            results.append(" size=\"");
            results.append(rows);
            results.append("\"");
        }
        results.append(prepareDisabled());
        results.append(">");
        Object value = TagUtils.getInstance().lookup(pageContext, modelName, modelProperty,
            null);
        if (value == null || ! ( value instanceof MultiSelectSelectionModel )) {
            throw new JspException("Model attributes must specify an instance of MultiSelectListDataSourceModel (" + value + ")");
        } 
        MultiSelectSelectionModel model = (MultiSelectSelectionModel)value;
        for(Iterator i = model.getAvailableValues().iterator(); i.hasNext(); ) {
            LabelValueBean lvb = (LabelValueBean)i.next();
            results.append("<option value=\"");
            results.append(lvb.getValue());
            results.append("\">");
            results.append(getLocalisedLabel(lvb));
            results.append("</option>");
        }
        results.append("</select>");
        return results.toString();
        
    }
    
    protected String renderSelectComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiAdd\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiSelectSelectValue(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareSourceId());
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareAdd());
        results.append("\"/>");
        return results.toString();        
    }
    
    protected String renderSelectAllComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiAdd\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiSelectAllSelectValue(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareSourceId());
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareAllAdd());
        results.append("\"/>");
        return results.toString();        
    }
    
    protected String renderDeselectComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiRemove\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiSelectDeselectValue(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareSourceId());
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareRemove());
        results.append("\"/>");
        return results.toString();        
    }
    
    protected String renderDeselectAllComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiRemove\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiSelectAllDeselectValue(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareSourceId());
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareRemoveAll());
        results.append("\"/>");
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
        Object value = TagUtils.getInstance().lookup(pageContext, modelName, modelProperty,
            null);
        if (value == null || ! ( value instanceof MultiSelectSelectionModel )) {
            throw new JspException("Model attributes must specify an instance of MultiSelectListDataSourceModel (" + value + ")");
        } 
        MultiSelectSelectionModel model = (MultiSelectSelectionModel)value;
        for(Iterator i = model.getSelectedValues().iterator(); i.hasNext(); ) {
            LabelValueBean lvb = (LabelValueBean)i.next();
            results.append("<option value=\"");
            results.append(lvb.getValue());
            results.append("\">");
            results.append(getLocalisedLabel(lvb));
            results.append("</option>");
        }
        results.append("</select>");
        return results.toString();
        
    }
    
    protected String getLocalisedLabel(LabelValueBean lvb) {
        if(valueKeyPrefix != null) {
            Locale locale = (Locale)pageContext.getSession().getAttribute(Globals.LOCALE_KEY);
            if(locale != null) {
                MessageResources mr = CoreUtil.getMessageResources(pageContext.getSession(), getBundle());
                if(mr != null) {
                    String locLabel = mr.getMessage(locale, valueKeyPrefix + ".value." + lvb.getValue());
                    if(locLabel != null) {
                        return locLabel;
                    }
                }
            }
        }
        return lvb.getLabel();
    }

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseFieldTag#renderInputElement()
     */
    protected String renderInputElement() throws JspException {
        StringBuffer results = new StringBuffer("<input type=\"");
        results.append(this.type);
        results.append("\"");
        results.append(prepareDisabled());
        results.append(" name=\"");

        if (indexed) {
            this.prepareIndex(results, name);
        }

        results.append(property);
        results.append("\"");
        
        results.append(" id=\"");
        if(indexed) {
            this.prepareIndex(results, name);
        }

        results.append(property);
        results.append("\"");

        results.append(" value=\"");
        if (value != null) {
            results.append(this.formatValue(value));

        } else if (redisplay || !"password".equals(type)) {
            Object value =
                TagUtils.getInstance().lookup(pageContext, name, property, null);

            results.append(this.formatValue(value));
        }
        results.append('"');
        results.append(this.prepareEventHandlers());
        results.append(this.getElementClose());
        return results.toString();
    }
    

    /**
     * @return Returns the modelName.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName The modelName to set.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return Returns the modelProperty.
     */
    public String getModelProperty() {
        return modelProperty;
    }

    /**
     * @param modelProperty The modelProperty to set.
     */
    public void setModelProperty(String modelProperty) {
        this.modelProperty = modelProperty;
    }

    /**
     * @return Returns the sourceTitleKey.
     */
    public String getSourceTitleKey() {
        return sourceTitleKey;
    }

    /**
     * @param sourceTitleKey The sourceTitleKey to set.
     */
    public void setSourceTitleKey(String sourceTitleKey) {
        this.sourceTitleKey = sourceTitleKey;
    }
    
    public void setValueKeyPrefix(String valueKeyPrefix) {
        this.valueKeyPrefix = valueKeyPrefix;
    }
}
