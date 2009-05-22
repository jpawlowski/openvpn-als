
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
import org.apache.struts.taglib.html.BaseFieldTag;
import org.apache.struts.util.MessageResources;

public class AbstractMultiFieldTag extends BaseFieldTag {
    protected String targetTitleKey;    
    protected String addKey;
    protected String removeKey; 
    protected String addAllKey;
    protected String removeAllKey; 
    protected String upKey;
    protected String downKey;
    protected String configureKey;
    protected String configureOnClick;
    protected boolean disabled;
    protected boolean allowReordering;

    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources(
            "org.apache.struts.taglib.bean.LocalStrings");
    
    public AbstractMultiFieldTag() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return Returns the targetTitleKey.
     */
    public String getTargetTitleKey() {
        return targetTitleKey;
    }

    /**
     * @return Returns the addKey.
     */
    public String getAddKey() {
        return addKey;
    }

    /**
     * @return Returns the downKey.
     */
    public String getDownKey() {
        return downKey;
    }

    /**
     * @param downKey The downKey to set.
     */
    public void setDownKey(String downKey) {
        this.downKey = downKey;
    }

    /**
     * @return Returns the upKey.
     */
    public String getUpKey() {
        return upKey;
    }

    /**
     * @param upKey The upKey to set.
     */
    public void setUpKey(String upKey) {
        this.upKey = upKey;
    }

    /**
     * @param addKey The addKey to set.
     */
    public void setAddKey(String addKey) {
        this.addKey = addKey;
    }

    /**
     * @return Returns the removeKey.
     */
    public String getRemoveKey() {
        return removeKey;
    }

    /**
     * @param removeKey The removeKey to set.
     */
    public void setRemoveKey(String removeKey) {
        this.removeKey = removeKey;
    }

    /**
     * @param targetTitleKey The targetTitleKey to set.
     */
    public void setTargetTitleKey(String targetTitleKey) {
        this.targetTitleKey = targetTitleKey;
    }    
    
    protected String prepareTargetTitle() throws JspException {

        if(targetTitleKey != null) {
            String targetTitle =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                targetTitleKey,
                new String[] { });
            
            if (targetTitle == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + targetTitleKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return targetTitle;
        }
        return null;
    }
    
    protected String renderConfigureComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"configure\" onclick=\"");
        results.append(configureOnClick);
        results.append("\" type=\"button\" value=\"");
        results.append(prepareConfigure());
        results.append("\"");
        results.append(prepareDisabled());
        results.append("/>");
        return results.toString();        
    }
    
    protected String renderUpComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiUp\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiMoveUp(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareUp());
        results.append("\"/>");
        return results.toString();        
    }
    
    protected String renderDownComponent() throws JspException {
        StringBuffer results = new StringBuffer("<input class=\"multiDown\"");
        results.append(prepareDisabled());
        results.append(" onclick=\"multiMoveDown(");
        results.append("document.getElementById('");
        if (indexed) {
            this.prepareIndex(results, name);
        }
        results.append(property);
        results.append("'), document.getElementById('");
        results.append(prepareTargetId());
        results.append("'));\" type=\"button\" value=\"");
        results.append(prepareDown());
        results.append("\"/>");
        return results.toString();        
    }
    
    protected String prepareDisabled() {
        if(isDisabled()) {
            return " disabled=\"disabled\"";
        }
        else {
            return "";
        }
    }
    
    protected String prepareConfigure() throws JspException {

        if(addKey != null) {
            String configure =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                configureKey,
                new String[] { });
            
            if (configureKey == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + addKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return configure;
        }
        return ">>";
    }
    
    protected String prepareAdd() throws JspException {

        if(addKey != null) {
            String add =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                addKey,
                new String[] { });
            
            if (addKey == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + addKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return add;
        }
        return ">>";
    }
    
    protected String prepareAllAdd() throws JspException {
        
        if(addAllKey != null) {
            String addAll =
                TagUtils.getInstance().message(
                                pageContext,
                                getBundle(),
                                getLocale(),
                                addAllKey,
                                new String[] { });
            
            if (addAllKey == null) {
                JspException e =
                    new JspException(
                                    messages.getMessage("message.message", "\"" + addAllKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return addAll;
        }
        return ">>";
    }
    
    protected String prepareUp() throws JspException {

        if(upKey != null) {
            String up =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                upKey,
                new String[] { });
            
            if (upKey == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + upKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return up;
        }
        return "Up";
    }
    
    protected String prepareDown() throws JspException {

        if(downKey != null) {
            String down =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                downKey,
                new String[] { });
            
            if (downKey == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + downKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return down;
        }
        return "Down";
    }
    
    protected String prepareRemove() throws JspException {

        if(removeKey != null) {
            String remove =
                TagUtils.getInstance().message(
                pageContext,
                getBundle(),
                getLocale(),
                removeKey,
                new String[] { });
            
            if (remove == null) {
                JspException e =
                    new JspException(
                        messages.getMessage("message.message", "\"" + removeKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return remove;
        }
        return "<<";
    }
    
    protected String prepareRemoveAll() throws JspException {
        
        if(removeAllKey != null) {
            String removeAll =
                TagUtils.getInstance().message(
                                pageContext,
                                getBundle(),
                                getLocale(),
                                removeAllKey,
                                new String[] { });
            
            if (removeAll == null) {
                JspException e =
                    new JspException(
                                    messages.getMessage("message.message", "\"" + removeAllKey + "\""));
                TagUtils.getInstance().saveException(pageContext, e);
                throw e;
            }
            return removeAll;
        }
        return "<<";
    }
    
    protected String prepareTargetId() throws JspException {
        StringBuffer results = new StringBuffer("targetValues_");
        if(indexed) {
            prepareIndex(results, name);
        }
        results.append(property);
        return results.toString();
    }

    /**
     * @param configureKey The configureKey to set.
     */
    public void setConfigureKey(String configureKey) {
        this.configureKey = configureKey;
    }

    /**
     * @param configureOnClick The configureOnClick to set.
     */
    public void setConfigureOnClick(String configureOnClick) {
        this.configureOnClick = configureOnClick;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public boolean isAllowReordering() {
        return allowReordering;
    }

    public void setAllowReordering(boolean allowReordering) {
        this.allowReordering = allowReordering;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseFieldTag#release()
     */
    public void release() {
        super.release();
        targetTitleKey = null;    
        addKey = null;
        removeKey = null; 
        addAllKey = null;
        removeAllKey = null; 
        upKey = null;
        downKey = null;
        configureKey = null;
        configureOnClick = null;
        disabled = false;
        allowReordering = false;
    }

    public String getAddAllKey() {
        return addAllKey;
    }

    public void setAddAllKey(String addAllKey) {
        this.addAllKey = addAllKey;
    }

    public String getRemoveAllKey() {
        return removeAllKey;
    }

    public void setRemoveAllKey(String removeAllKey) {
        this.removeAllKey = removeAllKey;
    }
}
