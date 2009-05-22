
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
			
package com.ovpnals.properties.attributes.forms;

import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.AttributeDefinitionItem;
import com.ovpnals.properties.impl.resource.ResourceAttributes;
import com.ovpnals.table.AbstractTableItemTableModel;
import com.ovpnals.table.forms.AbstractPagerForm;

/**
 * Implementation of a {@link com.ovpnals.table.forms.AbstractPagerForm}
 * that allows an administrator to list and configure <i>Attribute Definitions</i>.
 */
public class AttributeDefinitionsForm extends AbstractPagerForm {

    static Log log = LogFactory.getLog(AttributeDefinitionsForm.class);

    private String propertyClassName;

    /**
     * Constructor
     */
    public AttributeDefinitionsForm() {
        super(new AttributeDefinitionsModel());
    }

    /**
     * Initialise this form with a list of attribute definitions.
     * 
     * @param session session
     * @param definitions list of
     *        {@link com.ovpnals.properties.attributes.AttributeDefinition}
     *        objects
     * @throws Exception on any error
     */
    public void initialize(HttpSession session, Collection<AttributeDefinition> definitions) throws Exception {
        super.initialize(session, "name");
        for (AttributeDefinition def : definitions) {
            if (def.isHidden() || def.getPropertyClass().getName().equals(ResourceAttributes.NAME))
                continue;
            MessageResources mr = null;
            if (def.getMessageResourcesKey() != null) {
                mr = CoreUtil.getMessageResources(session, def.getMessageResourcesKey());
            }
            Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
            AttributeDefinitionItem item = new AttributeDefinitionItem(locale, def, mr);
            getModel().addItem(item);
        }
        getPager().rebuild(getFilterText());
    }

    /**
     * Get the property class name to create
     * 
     * @return property class name
     */
    public String getPropertyClassName() {
        return propertyClassName;
    }

    /**
     * Set the property class name to create
     * 
     * @param propertyClassName property class name to create
     */
    public void setPropertyClassName(String propertyClassName) {
        this.propertyClassName = propertyClassName;
    }

    /*
     * Supporting classes
     */

    /**
     * Table model for displaying attribute definitions.
     */
    static class AttributeDefinitionsModel extends AbstractTableItemTableModel<AttributeDefinitionItem> {

        /*
         * (non-Javadoc)
         * 
         * @see com.ovpnals.table.AbstractTableItemTableModel#getColumnWidth(int)
         */
        public int getColumnWidth(int col) {
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ovpnals.table.TableItemModel#getId()
         */
        public String getId() {
            return "attributeDefinitions";
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ovpnals.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ovpnals.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "name";
                default:
                    return "label";
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ovpnals.table.TableModel#getColumnClass(int)
         */
        public Class<String> getColumnClass(int col) {
            return String.class;
        }

    }
}
