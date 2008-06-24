
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
			
package com.adito.properties.attributes.wizards.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyClassManager;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributesPropertyClass;
import com.adito.security.SessionInfo;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.WizardActionStatus;
import com.adito.wizard.actions.AbstractWizardAction;
import com.adito.wizard.forms.AbstractWizardFinishForm;

/**
 * <p>
 * The final action in which the resource is created.
 * 
 */
public class AttributeDefinitionFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(AttributeDefinitionFinishAction.class);

    /**
     * Constructor.
     */
    public AttributeDefinitionFinishAction() {
        super();
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
    
    public static AttributeDefinition createDefinition(AbstractWizardSequence seq) {

        String name = (String) seq.getAttribute(AttributeDefinitionDetailsAction.ATTR_NAME, null);
        String description = (String) seq.getAttribute(AttributeDefinitionDetailsAction.ATTR_DESCRIPTION, null);
        String attributeClassName = (String) seq.getAttribute(AttributeDefinitionDetailsAction.ATTR_CLASS, null);
        String typeMeta = (String) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE_META, null);
        String label = (String) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_LABEL, null);
        String category = (String) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_CATEGORY, null);
        String defaultValue = (String) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_DEFAULT_VALUE, null);
        String validationString = (String) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_VALIDATION_STRING, null);
        int visibility = ((Integer) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_VISIBILITY, null)).intValue();
        int sortOrder = ((Integer) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_SORT_ORDER, null)).intValue();
        int type = ((Integer) seq.getAttribute(AttributeDefinitionOptionsAction.ATTR_TYPE, null)).intValue();
        AttributesPropertyClass attributeClass = (AttributesPropertyClass) PropertyClassManager.getInstance()
                        .getPropertyClass(attributeClassName);
        AttributeDefinition def = attributeClass.createAttributeDefinition(type,
                            name,
                            typeMeta,
                            -1,
                            category,
                            defaultValue,
                            visibility,
                            sortOrder,
                            null,
                            false,
                            label,
                            description,
                            false,
                            true,
                            validationString);
        return def;
    	
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        List actionStatus = new ArrayList();
        AbstractWizardSequence seq = getWizardSequence(request);
        try {
            try {
            	AttributeDefinition def = createDefinition(seq);                
                ProfilesFactory.getInstance().createAttributeDefinition(def);
                def.getPropertyClass().registerPropertyDefinition(def);
                CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
                                CoreEventConstants.ATTRIBUTE_DEFINITION_CREATED,
                                def,
                                getSessionInfo(request),
                                CoreEvent.STATE_SUCCESSFUL));
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
                                CoreEventConstants.ATTRIBUTE_DEFINITION_CREATED,
                                null,
                                getSessionInfo(request),
                                e));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "attributeDefinitionWizard.attributeDefinitionFinish.status.attributeCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "attributeDefinitionWizard.attributeDefinitionFinish.status.failedToCreateAttribute",
                            e.getMessage()));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

}
