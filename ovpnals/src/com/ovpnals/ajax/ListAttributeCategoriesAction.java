package com.ovpnals.ajax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.AttributesPropertyClass;
import com.ovpnals.properties.impl.resource.ResourceAttributes;
import com.ovpnals.security.SessionInfo;

/**
 * Implementation of {@link com.ovpnals.ajax.AbstractAjaxXMLAction} that
 * returns an XML document containing all currently configured <i>Attribute
 * Definition</i> categories.
 * <p>
 * A single request parameter is supported, <i>category</i> that will narrow
 * the results returned to those that begin with the supplied value.
 */
public class ListAttributeCategoriesAction extends AbstractAjaxXMLAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
                                 AjaxXmlBuilder builder) throws Exception {
        List<String> c = new ArrayList<String>();
        for (PropertyClass propertyClass : PropertyClassManager.getInstance().getPropertyClasses()) {
            if (propertyClass instanceof AttributesPropertyClass && !propertyClass.getName().equals(ResourceAttributes.NAME)) {
                Collection<PropertyDefinition> l = propertyClass.getDefinitions();
                String category = request.getParameter("category");
                for (PropertyDefinition d : l) {
                    AttributeDefinition def = (AttributeDefinition) d;
                    String categoryLabel = def.getCategoryLabel();
                    MessageResources mr = CoreUtil.getMessageResources(request.getSession(), def.getMessageResourcesKey());
                    String s = mr == null ? null : mr.getMessage("userAttributeCategory." + def.getCategory() + ".title");
                    if (s != null && !s.equals("")) {
                        categoryLabel = Util.urlDecode(s);
                    } else {
                        categoryLabel = categoryLabel == null || categoryLabel.equals("") ? "Attributes" : categoryLabel;
                    }
                    if ((category == null || category.equals("") || categoryLabel.toLowerCase().startsWith(category.toLowerCase()))
                                    && !c.contains(categoryLabel)) {
                        c.add(categoryLabel);
                    }
                }

            }
        }
        for (Iterator i = c.iterator(); i.hasNext();) {
            String n = Util.encodeHTML((String) i.next());
            builder.addItem(n, n);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}
