
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
			
package net.openvpn.als.properties.attributes.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.boot.PropertyClassManager;
import net.openvpn.als.boot.Util;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.attributes.AttributeDefinition;
import net.openvpn.als.properties.attributes.AttributesPropertyClass;
import net.openvpn.als.properties.attributes.DefaultAttributeDefinition;
import net.openvpn.als.properties.attributes.forms.AttributeDefinitionsForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.actions.AbstractPagerAction;

/**
 * Implementation of a {@link net.openvpn.als.table.actions.AbstractPagerAction}
 * that allows an administrator to create, edit and delete <i>Attribute
 * Definitions</i>.
 * @see net.openvpn.als.properties.attributes.AttributeDefinition
 */
public class ShowAttributeDefinitionsDispatchAction extends AbstractPagerAction {
    /**
     * Constructor
     */
    public ShowAttributeDefinitionsDispatchAction() {
        super(PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_MAINTAIN });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return list(mapping, form, request, response);
    }

    /**
     * Confirm removal of the selected attribute definition.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_MAINTAIN, request);
        AttributeDefinitionsForm schemesForm = (AttributeDefinitionsForm) form;
        String name = schemesForm.getSelectedItem();
        int idx = name.indexOf('/');
        String propertyClass = name.substring(0, idx);
        name = name.substring(idx + 1);
        AttributeDefinition def = (AttributeDefinition) PropertyClassManager.getInstance()
                        .getPropertyClass(propertyClass)
                        .getDefinition(name);
        if (def == null) {
            throw new Exception("No attribute definition with name of " + name + ".");
        }
        return mapping.findForward("confirmRemove");
    }

    /**
     * Delete the selected attribute definition.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_MAINTAIN, request);
        AttributeDefinitionsForm f = (AttributeDefinitionsForm) form;
        String name = f.getSelectedItem();
        int idx = name.indexOf('/');
        String propertyClass = name.substring(0, idx);
        name = name.substring(idx + 1);
        AttributeDefinition def = (AttributeDefinition) PropertyClassManager.getInstance()
                        .getPropertyClass(propertyClass)
                        .getDefinition(name);
        if (def == null) {
            throw new Exception("No attribute definition with name of " + name + ".");
        }
        ProfilesFactory.getInstance().deleteAttributeDefinition(def.getPropertyClass().getName(), name);
        def.getPropertyClass().deregisterPropertyDefinition(def.getName());
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("attributeDefinitions.message.definitionDeleted", name));
        saveMessages(request, msgs);
        return mapping.findForward("refresh");
    }

    /**
     * Edit the selected attribute definition.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_MAINTAIN, request);
        AttributeDefinitionsForm f = (AttributeDefinitionsForm) form;
        String selected = f.getSelectedItem();
        int idx = selected.indexOf('/');
        String attributeClass = selected.substring(0, idx);
        String name = selected.substring(idx + 1);
        AttributeDefinition def = (AttributeDefinition) PropertyClassManager.getInstance()
                        .getPropertyClass(attributeClass)
                        .getDefinition(name);
        if (def == null) {
            throw new Exception("No attribute definition with name of " + name + ".");
        }
        request.getSession().setAttribute(Constants.EDITING_ITEM, def);
        return mapping.findForward("edit");
    }

    /**
     * Create a new attribute definition.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_MAINTAIN, request);
        AttributeDefinitionsForm f = (AttributeDefinitionsForm) form;

        // Get the property class
        AttributesPropertyClass attributesPropertyClass = (AttributesPropertyClass) PropertyClassManager.getInstance()
                        .getPropertyClass(f.getPropertyClassName());
        if (attributesPropertyClass == null) {
            throw new Exception("Invalid property class.");
        }

        // Create the definition to edit
        DefaultAttributeDefinition def = new DefaultAttributeDefinition(AttributeDefinition.TYPE_UNDEFINED,
                        null,
                        "",
                        0,
                        "",
                        "",
                        AttributeDefinition.USER_OVERRIDABLE_ATTRIBUTE,
                        0,
                        null,
                        false,
                        "",
                        "",
                        false,
                        true,
                        null);
        def.init(attributesPropertyClass);
        request.getSession().setAttribute(Constants.EDITING_ITEM, def);

        // Create
        return mapping.findForward("create");
    }

    /**
     * List the authentication schemes configured.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_MAINTAIN, request);
        AttributeDefinitionsForm f = (AttributeDefinitionsForm) form;
        getResources(request);
        Collection<AttributeDefinition> defs = (Collection) PropertyClassManager.getInstance()
                        .getDefinitions(AttributesPropertyClass.class);
        f.initialize(request.getSession(), defs);
        Util.noCache(response);
        return mapping.findForward("display");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}