
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.attributes.forms.UserAttributesForm;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.properties.impl.userattributes.UserAttributes;
import com.adito.security.SessionInfo;
import com.adito.security.User;


/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedDispatchAction}
 * that displays and allows a user to change their attributes.
 *    
 * @see com.adito.properties.attributes.AttributeDefinition
 */
public class UserAttributesDispatchAction extends AuthenticatedDispatchAction {
    
    /**
     * Constructor.
     */
    public UserAttributesDispatchAction() {
        super(PolicyConstants.ATTRIBUTES_RESOURCE_TYPE, new Permission[]{PolicyConstants.PERM_MAINTAIN});
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        List<AttributeValueItem> a = new ArrayList<AttributeValueItem>();
        SessionInfo sessionInfo = getSessionInfo(request);
        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
        for(PropertyDefinition d : propertyClass.getDefinitions()) {
            AttributeDefinition def = (AttributeDefinition)d;
            if(def.isHidden())
            	continue;
            if(def.getVisibility() == AttributeDefinition.USER_OVERRIDABLE_ATTRIBUTE ||
                            def.getVisibility() == AttributeDefinition.USER_VIEWABLE_ATTRIBUTE
                            || def.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                a.add(new AttributeValueItem(def, request, Property.getProperty(new UserAttributeKey(sessionInfo.getUser(), def.getName()))));
            }
        }
        Collections.sort(a);
        ((UserAttributesForm)form).initialize(a);
        ((UserAttributesForm)form).setReferer(CoreUtil.getReferer(request));
        return mapping.findForward("display");
    }

    /**
     * Commit the details to the user database.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response 
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        List l = ((UserAttributesForm)form).getUserAttributeValueItems();
        SessionInfo sessionInfo = getSessionInfo(request);
        User u = sessionInfo.getUser();
        for(Iterator i = l.iterator(); i.hasNext(); ) {
            AttributeValueItem item = (AttributeValueItem)i.next();
            if(item.getDefinition().getVisibility() == AttributeDefinition.USER_OVERRIDABLE_ATTRIBUTE ||
                            item.getDefinition().getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                Property.setProperty(new UserAttributeKey(u.getPrincipalName(), item.getDefinition().getName(), u.getRealm()
                                .getResourceId()), item.getDefinition().formatAttributeValue(item.getValue()), sessionInfo);
            }
        }  
        sessionInfo.setUser(u);
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /**
     * Reset all user attributes.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward resetUserAttributes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        for(Iterator i = ((UserAttributesForm)form).getUserAttributeValueItems().iterator(); i.hasNext(); ) {
            AttributeValueItem v = (AttributeValueItem)i.next();
            v.setValue(v.getDefinition().parseValue(v.getDefinition().getDefaultValue()));
        }
        return mapping.findForward("display");
    }
}