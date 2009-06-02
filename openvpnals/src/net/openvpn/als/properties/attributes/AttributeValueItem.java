
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.properties.attributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;

import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.TypeMetaListItem;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.properties.AbstractPropertyItem;
import net.openvpn.als.properties.Pair;
import net.openvpn.als.properties.PairListDataSource;

/**
 * Wrapper bean for displaying and editing a attribute and its value.
 */
public class AttributeValueItem extends AbstractPropertyItem {

    final static Log log = LogFactory.getLog(AttributeValueItem.class);

    static {

    }

    /**
     * Constructor.
     * 
     * @param definition definition
     * @param request request
     * @param value initial value
     */
    public AttributeValueItem(AttributeDefinition definition, HttpServletRequest request, String value) {
        this(definition, request, value, null);
    }
    
    
        /**
         * Constructor.
         * 
         * @param definition definition
         * @param request request
         * @param value initial value
         * @param subCategory 
         */
    public AttributeValueItem(AttributeDefinition definition, HttpServletRequest request, String value, String subCategory) {
        super(definition, request, subCategory);

        this.value = getDefinition().parseValue(value);

        // Process the type meta for any type specific parameters
        if (definition.getType() == PropertyDefinition.TYPE_TEXT_AREA) {
            this.value = value;
            StringTokenizer t = new StringTokenizer(definition.getTypeMeta().equals("") ? "25x5" : definition.getTypeMeta(), "x");
            try {
                columns = Integer.parseInt(t.nextToken());
                rows = Integer.parseInt(t.nextToken());
            } catch (Exception e) {
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_LIST) {
            List<Pair> listItemsList = new ArrayList<Pair>();
            if (!definition.getTypeMeta().startsWith("!")) {
                for (Iterator i = ((List) definition.getTypeMetaObject()).iterator(); i.hasNext();) {
                    TypeMetaListItem item = (TypeMetaListItem) i.next();
                    ServletContext context = CoreServlet.getServlet().getServletContext();
                    ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, context);
                    String mrKey = (item.getMessageResourcesKey() == null ? "properties" : item.getMessageResourcesKey())
                                    + moduleConfig.getPrefix();
                    MessageResources res = (MessageResources) context.getAttribute(mrKey);
                    String k = definition.getName() + ".value." + item.getValue();
                    String v = "";
                    if (res != null) {
                        v = res.getMessage((Locale) request.getSession().getAttribute(Globals.LOCALE_KEY), k);
                        if (v == null) {
                            v = item.getValue();
                        }
                    }
                    Pair pair = new Pair(item.getValue(), v);
                    if (item.getValue().equals(value)) {
                        this.value = pair.getValue();
                    }
                    listItemsList.add(pair);
                }
            } else {
                String className = definition.getTypeMeta().substring(1);
                try {
                    Class clazz = Class.forName(className);
                    Object obj = clazz.newInstance();
                    if (obj instanceof PairListDataSource)
                        listItemsList.addAll(((PairListDataSource) obj).getValues(request));
                    else
                        throw new Exception("Not a PairListDataSource.");
                } catch (Exception e) {
                    log.error("Failed to create list data source.", e);
                }
                this.value = value;
            }
            listItems = new Pair[listItemsList.size()];
            listItemsList.toArray(listItems);
        } else if (definition.getType() == PropertyDefinition.TYPE_STRING) {
            columns = 25;
            if (!definition.getTypeMeta().equals("")) {
                try {
                    columns = Integer.parseInt(definition.getTypeMeta());
                } catch (NumberFormatException nfe) {
                }
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_INTEGER) {
            columns = 8;
            if (!definition.getTypeMeta().equals("")) {
                try {
                    columns = Integer.parseInt(definition.getTypeMeta());
                } catch (NumberFormatException e) {
                }
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_PASSWORD) {
            columns = 25;
            if (!definition.getTypeMeta().equals("")) {
                try {
                    columns = Integer.parseInt(definition.getTypeMeta());
                } catch (NumberFormatException nfe) {
                }
            }
        } else if (definition.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
            try {
                int val = Integer.parseInt(value);
                if (definition.getTypeMeta().equalsIgnoreCase("s")) {
                    this.value = String.valueOf(val / 1000);
                } else if (definition.getTypeMeta().equalsIgnoreCase("m")) {
                    this.value = String.valueOf(val / 1000 / 60);
                } else if (definition.getTypeMeta().equalsIgnoreCase("h")) {
                    this.value = String.valueOf(val / 1000 / 60 / 60);
                } else if (definition.getTypeMeta().equalsIgnoreCase("d")) {
                    this.value = String.valueOf(val / 1000 / 60 / 60 / 24);
                } else {
                    this.value = String.valueOf(val);
                }
            } catch (Exception e) {
            }
        }
    }
}
