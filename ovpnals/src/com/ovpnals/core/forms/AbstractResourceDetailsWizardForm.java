
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
			
package com.ovpnals.core.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

/**
 * Abstract implementation of a {@link DefaultWizardForm}. Provides setters and getters
 * for attributes common to all resource types in the wizards.
 */
public abstract class AbstractResourceDetailsWizardForm extends DefaultWizardForm {
    private String resourceName;
    private String resourceDescription;
    private ResourceType resourceTypeForAccessRights;

    final static Log log = LogFactory.getLog(AbstractResourceDetailsWizardForm.class);

    /**
     * Resource name constant
     */
    public final static String ATTR_RESOURCE_NAME = "resourceName";
    
    /**
     * Resource description constant 
     */
    public final static String ATTR_RESOURCE_DESCRIPTION = "resourceDescription";
    
    /**
     * Resource permission class constant. 
     */
    public static final String ATTR_RESOURCE_PERMISSION_CLASS = "class";

    /**
     * @param nextAvailable
     * @param previousAvailable
     * @param page
     * @param focussedField
     * @param autoComplete
     * @param finishAvailable
     * @param pageName must be name of action
     * @param resourceBundle
     * @param resourcePrefix
     * @param stepIndex
     * @param resourceTypeForAccessRights
     */
    public AbstractResourceDetailsWizardForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField,
                    boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle, String resourcePrefix,
                    int stepIndex, ResourceType resourceTypeForAccessRights) {
        super(nextAvailable, previousAvailable, page, focussedField, autoComplete, finishAvailable, pageName, resourceBundle,
            resourcePrefix, stepIndex);
        this.resourceTypeForAccessRights = resourceTypeForAccessRights;
    }

    /**
     * @return Returns the resourceDescription.
     */
    public String getResourceDescription() {
        return resourceDescription;
    }

    /**
     * @param resourceDescription The resourceDescription to set.
     */
    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription.trim();
    }

    /**
     * @return Returns the resourceName.
     */
    public String getResourceName() {
        return resourceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.DefaultWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_RESOURCE_NAME, resourceName);
        sequence.putAttribute(ATTR_RESOURCE_DESCRIPTION, resourceDescription);
    }

    /**
     * @param resourceName The resourceName to set.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName.trim();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.DefaultWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);
        resourceName = (String) sequence.getAttribute(ATTR_RESOURCE_NAME, "");
        resourceDescription = (String) sequence.getAttribute(ATTR_RESOURCE_DESCRIPTION, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.DefaultWizardForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (resourceName != null && isCommiting()) {
            ActionErrors errs = new ActionErrors();
            AbstractWizardSequence seq = getWizardSequence(request);
            if (resourceName.equals("")) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                    .getCurrentPageForm().getResourcePrefix()
                                + ".error.noResourceName"));
            }
            if (resourceName.length() > Resource.MAX_RESOURCE_NAME_LENGTH) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                    .getCurrentPageForm().getResourcePrefix()
                    + ".error.resourceNameTooLong", String.valueOf(Resource.MAX_RESOURCE_NAME_LENGTH)));
            }
            if (resourceDescription.equals("")) {
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                    .getCurrentPageForm().getResourcePrefix()
                                + ".error.noResourceDescription"));
            }
            try {
                if (this.getResourceTypeForAccessRights().getResourceByName(getResourceName(), seq.getSession()) != null) {
                    errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                        .getCurrentPageForm().getResourcePrefix()
                                    + ".error.duplicateName", getResourceName()));
                } 
            } catch (Exception e) {
                log.error("Failed to check if named resource already exists.", e);
                errs.add(Globals.ERROR_KEY, new BundleActionMessage(seq.getCurrentPageForm().getResourceBundle(), seq
                    .getCurrentPageForm().getResourcePrefix()
                                + ".error.failedToCheckForName", e.getMessage()));
            }
            return errs;
        }
        return null;
    }
    
    /**
     * @return Resource type
     */
    public ResourceType getResourceTypeForAccessRights() {
        return resourceTypeForAccessRights;
    }
}
