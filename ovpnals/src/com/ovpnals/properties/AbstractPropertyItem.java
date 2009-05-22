
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
			
package com.ovpnals.properties;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;

/**
 * Abstract class for property items.
 */
public abstract class AbstractPropertyItem implements Comparable<AbstractPropertyItem>, PropertyItem {

    protected PropertyDefinition definition;
    protected Object value;
    protected Pair[] listItems;
    protected int rows;
    protected int columns;
    protected String label;
    protected String categoryLabel;
    protected String categoryId;

    /**
     * Constructor
     * 
     * @param definition
     * @param request
     */
    public AbstractPropertyItem(PropertyDefinition definition, HttpServletRequest request) {
        this(definition, request, null);
    }
        /**
         * Constructor
         * 
         * @param definition
         * @param request
         * @param subcategory for if the category is split into sub categories.
         */
        public AbstractPropertyItem(PropertyDefinition definition, HttpServletRequest request, String subcategory) {
            MessageResources messageResources = null;
        if (definition.getMessageResourcesKey() != null) {
            messageResources = CoreUtil.getMessageResources(request.getSession(), definition.getMessageResourcesKey());
        }

        this.definition = definition;

        categoryLabel = this.definition.getCategoryLabel();
        String messageId = subcategory == null ? "attributeCategory." + definition.getCategory() + ".title" : "attributeCategory."
                        + definition.getCategory() + "." + subcategory + ".title";
        
        Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
        // Get an internationalised category label if possible
        String s = messageResources == null ? null : messageResources.getMessage(locale, messageId);
        if (s != null && !s.equals("")) {
            this.categoryLabel = s;
        } else {
            if (categoryLabel == null || categoryLabel.equals("")) {
                this.categoryLabel = "Attributes";
            }
        }

        label = this.definition.getLabel();
        // Get an internationalised label if possible, otherwise use default
        // label, otherwise use name
        s = messageResources == null ? null : messageResources.getMessage(locale, this.definition.getPropertyClass().getName() + "."
                        + definition.getName() + ".title");
        if (s != null && !s.equals("")) {
            label = s;
        } else {
            if (label != null && label.equals("")) {
                label = definition.getName();
            }
        }

        if (getCategoryLabel() != null)
            categoryId = Util.makeConstantKey(getCategoryLabel());

        // If there are ApplicationResources for the definition then set the
        // label and description with these.
        String tmpLabel = messageResources == null ? null : messageResources.getMessage(locale, definition.getNameMessageResourceKey());
        if (tmpLabel != null) {
            this.definition.setLabel(tmpLabel);
            label = tmpLabel;
        }
        String description = messageResources == null ? null : messageResources.getMessage(locale, definition
                        .getDescriptionMessageResourceKey());
        if (description != null) {
            this.definition.setDescription(description);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getMessageResourcesKey()
     */
    public String getMessageResourcesKey() {
        return definition.getMessageResourcesKey();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getColumns()
     */
    public int getColumns() {
        return columns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getRows()
     */
    public int getRows() {
        return rows;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getDefinition()
     */
    public PropertyDefinition getDefinition() {
        return definition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#setDefinition(com.ovpnals.boot.PropertyDefinition)
     */
    public void setDefinition(PropertyDefinition definition) {
        this.definition = definition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getName()
     */
    public String getName() {
        return definition.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getCategory()
     */
    public int getCategory() {
        return definition.getCategory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getDefaultValue()
     */
    public String getDefaultValue() {
        return definition.getDefaultValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getDefaultText()
     */
    public String getDefaultText() {
        String val = getDefaultValue();
        try {
            if (definition.getType() == PropertyDefinition.TYPE_PASSWORD) {
                val = "";
            } else if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST) {
                PropertyList list = new PropertyList(definition.getDefaultValue());
                val = list.size() > 0 ? list.getPropertyItem(0) : "";
            } else if (definition.getType() == PropertyDefinition.TYPE_LIST) {
                for (int i = 0; i < listItems.length; i++) {
                    if (definition.getDefaultValue().equals(listItems[i].getValue())) {
                        val = listItems[i].getLabel();
                        break;
                    }
                }
            } else if (definition.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
                try {
                    int defaultItem = Integer.parseInt(definition.getDefaultValue());
                    if (definition.getTypeMeta().equalsIgnoreCase("s")) {
                        val = String.valueOf(defaultItem / 1000);
                    } else if (definition.getTypeMeta().equalsIgnoreCase("m")) {
                        val = String.valueOf(defaultItem / 1000 / 60);
                    } else if (definition.getTypeMeta().equalsIgnoreCase("h")) {
                        val = String.valueOf(defaultItem / 1000 / 60 / 60);
                    } else if (definition.getTypeMeta().equalsIgnoreCase("d")) {
                        val = String.valueOf(defaultItem / 1000 / 60 / 60 / 24);
                    } else {
                        val = String.valueOf(val);
                    }
                } catch (Exception e) {
                    val = String.valueOf(val);
                }
            }
            if (val.length() > 15) {
                val = val.substring(0, 15);
            }
        } catch (Throwable t) {
        }
        return val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getTypeMeta()
     */
    public String getTypeMeta() {
        return definition.getTypeMeta();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getListItems()
     */
    public Pair[] getListItems() {
        return listItems;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getValue()
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getSelected()
     */
    public boolean getSelected() {
        return value.equals(Boolean.TRUE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#setSelected(boolean)
     */
    public void setSelected(boolean selected) {
        this.value = Boolean.valueOf(selected);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getType()
     */
    public int getType() {
        return definition.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getPropertyValue()
     */
    public Object getPropertyValue() {
        if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST
                        || definition.getType() == PropertyDefinition.TYPE_MULTI_SELECT_LIST) {
            PropertyList l = new PropertyList();
            l.setAsTextFieldText(getValue().toString());
            return l.getAsPropertyText();
        } else if (getDefinition().getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
            try {
                int v = Integer.parseInt(getValue().toString());
                if (getDefinition().getTypeMeta().equalsIgnoreCase("s")) {
                    v = v * 1000;
                } else if (getDefinition().getTypeMeta().equalsIgnoreCase("m")) {
                    v = v * 1000 * 60;
                } else if (getDefinition().getTypeMeta().equalsIgnoreCase("h")) {
                    v = v * 1000 * 60 * 60;
                } else if (getDefinition().getTypeMeta().equalsIgnoreCase("d")) {
                    v = v * 1000 * 60 * 60 * 24;
                }
                return String.valueOf(v);
            } catch (Exception e) {

            }
        }
        return getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyItem#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the category label
     * 
     * @return category label
     */
    public String getCategoryLabel() {
        return categoryLabel;
    }

    /**
     * Get the category Id
     * 
     * @return category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Compare this value item with another based on its definitions sort order
     * and name.
     * 
     * @param arg0 object to compare against.
     * @return comparison
     */
    public int compareTo(AbstractPropertyItem arg0) {
        int i = (categoryId == null ? "" : categoryId).compareTo(((AbstractPropertyItem) arg0).categoryId == null ? ""
                        : ((AbstractPropertyItem) arg0).categoryId);
        if (i == 0) {
            i = new Integer(getDefinition().getSortOrder()).compareTo(new Integer(((AbstractPropertyItem) arg0).getDefinition()
                            .getSortOrder()));
            return i == 0 ? (getDefinition().getName().compareTo(((AbstractPropertyItem) arg0).getDefinition().getName())) : i;
        } else {
            return i;
        }
    }

}
