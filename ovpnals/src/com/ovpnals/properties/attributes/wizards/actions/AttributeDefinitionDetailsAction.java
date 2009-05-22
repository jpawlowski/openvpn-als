
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
			
package com.ovpnals.properties.attributes.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.properties.attributes.AbstractAttributeKey;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.AttributesPropertyClass;
import com.ovpnals.properties.impl.userattributes.UserAttributes;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.DefaultWizardSequence;
import com.ovpnals.wizard.WizardStep;
import com.ovpnals.wizard.actions.AbstractWizardAction;

/**
 * Allows an administrator to create a new <i>Attribute Definition</i> of
 * some type. The name is also provided here. 
 * 
 * @author brett
 * @see AttributesPropertyClass
 * @see AbstractAttributeKey
 * @see AttributeDefinition
 */
public class AttributeDefinitionDetailsAction extends AbstractWizardAction {

    /**
     * Selected Property class (of type {@link AttributesPropertyClass}.
     */
    public final static String ATTR_CLASS = "class";

    /**
     * Name of attribute definition
     */
    public final static String ATTR_NAME = "name";

    /**
     * Description of attribute definition
     */
    public final static String ATTR_DESCRIPTION = "description";

    /**
     * Constructor
     */
    public AttributeDefinitionDetailsAction() {
        super(PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE, new Permission[] {PolicyConstants.PERM_MAINTAIN });
    }
    
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        throw new Exception("No previous steps.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ActionForward fwd = mapping.findForward("finish");
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        DefaultWizardSequence seq = new DefaultWizardSequence(fwd, "properties", "attributeDefinitionWizard", CoreUtil.getReferer(request),
                        "attributeDefinitionWizard", session);
        seq.putAttribute(ATTR_CLASS, UserAttributes.NAME);
        seq.addStep(new WizardStep("/attributeDefinitionDetails.do", true));
        seq.addStep(new WizardStep("/attributeDefinitionOptions.do"));
        seq.addStep(new WizardStep("/attributeDefinitionSummary.do"));
        return seq;
    }
}
