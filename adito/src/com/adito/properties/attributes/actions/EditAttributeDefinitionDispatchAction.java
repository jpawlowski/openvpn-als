
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
			
package com.adito.properties.attributes.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.forms.AttributeDefinitionForm;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;

/**
 * Extension of {@link com.adito.core.actions.AuthenticatedDispatchAction}
 * that allows an administrator to edit the details of an attribute
 * definition.
 */
public class EditAttributeDefinitionDispatchAction extends AuthenticatedDispatchAction {

    static Log log = LogFactory.getLog(EditAttributeDefinitionDispatchAction.class);

    /**
     * Constructor.
     */
    public EditAttributeDefinitionDispatchAction() {
        super(PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_MAINTAIN });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /**
     * Edit the user definition.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AttributeDefinitionForm df = (AttributeDefinitionForm) form;
        AttributeDefinition def = (AttributeDefinition) request.getSession().getAttribute(Constants.EDITING_ITEM);
        if (def.isSystem()) {
            throw new Exception("System attribute definitions may not be edited.");
        }
        df.initialise(def);
        df.setEditing();
        df.setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Edit the user definition.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AttributeDefinitionForm df = (AttributeDefinitionForm) form;
        AttributeDefinition def = (AttributeDefinition) request.getSession().getAttribute(Constants.EDITING_ITEM);
        if (def.isSystem()) {
            throw new Exception("System attribute definitions may not be edited.");
        }
        
        // Initialise the form
        df.initialise(def);
        df.setCreating();
        df.setReferer(CoreUtil.getReferer(request));
        
        // Display
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Commit the attribute definition.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AttributeDefinitionForm df = (AttributeDefinitionForm) form;
        df.applyToDefinition();
        if (df.getEditing()) {
            ProfilesFactory.getInstance().updateAttributeDefinition(df.getDefinition());
            df.getDefinition().getPropertyClass().registerPropertyDefinition(df.getDefinition());
        } else {
            ProfilesFactory.getInstance().createAttributeDefinition(df.getDefinition());
            df.getDefinition().getPropertyClass().registerPropertyDefinition(df.getDefinition());
        }
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /**
     * Cancel.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }
}