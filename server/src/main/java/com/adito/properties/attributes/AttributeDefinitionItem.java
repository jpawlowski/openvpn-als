
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
			
package com.adito.properties.attributes;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.MessageResources;

import com.adito.core.CoreUtil;
import com.adito.table.TableItem;

/**
 * Implementation of a {@link com.adito.table.TableItem} that is used to
 * wrap {@link com.adito.security.AuthenticationScheme} objects for
 * display.
 */
public class AttributeDefinitionItem implements TableItem {

    // Private instance variables

    private AttributeDefinition definition;
    private String categoryLabel;
    private String description;
    private String label;

    /**
     * Constructor
     * 
     * @param definition definition
     * @param messageResources message resources for label, categoryLabel and
     *        description. Or <code>null</code> to use values in definition
     */
    public AttributeDefinitionItem(AttributeDefinition definition, MessageResources messageResources) {
        this(null, definition, messageResources);
    }
    
    /**
     * Constructor
     * 
     * @param definition definition
     * @param messageResources message resources for label, categoryLabel and
     *        description. Or <code>null</code> to use values in definition
     * @param locale
     */
    public AttributeDefinitionItem(Locale locale, AttributeDefinition definition, MessageResources messageResources) {

        try {
            this.definition = definition;
            String s = messageResources == null ? null : messageResources.getMessage(locale, "attributeCategory."
                            + definition.getCategory() + ".title");
            if (s != null && !s.equals("")) {
                categoryLabel = s;
            } else {
                categoryLabel = definition.getCategoryLabel() != null && !definition.getCategoryLabel().equals("") ? definition
                                .getCategoryLabel() : "Attributes";
            }
            s = messageResources == null ? null : messageResources.getMessage(locale, definition.getPropertyClass().getName() + "."
                            + definition.getName() + ".title");
            if (s != null && !s.equals("")) {
                label = s;
            } else {
                label = definition.getLabel() != null && !definition.getLabel().equals("") ? definition.getLabel() : definition
                                .getName();
            }
            s = messageResources == null ? null : messageResources.getMessage(locale, definition.getPropertyClass().getName() + "."
                            + definition.getName() + ".description");
            if (s != null && !s.equals("")) {
                description = s;
            } else {
                description = definition.getDescription() != null && !definition.getDescription().equals("") ? definition
                                .getDescription() : label;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Get the description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     * 
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the label
     * 
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label
     * 
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the attribute definition object this item wraps
     * 
     * @return attribute definition
     */
    public AttributeDefinition getDefinition() {
        return definition;
    }

    /**
     * Get the category label
     * 
     * @return category label
     */
    public String getCategoryLabel() {
        return categoryLabel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch (col) {
            case 0:
                return getCategoryLabel() + " - " + getDefinition().getLabel();
            default:
                return getCategoryLabel() + " - " + getDefinition().getLabel();
        }
    }

    /**
     * Method to show where the icon for the item is located.
     * 
     * @param request
     * @return String
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/attributeDefinition.gif";
    }

}
