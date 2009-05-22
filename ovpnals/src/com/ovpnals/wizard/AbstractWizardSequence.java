
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
			
package com.ovpnals.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForward;

import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.forms.AbstractWizardForm;

/**
 * Every wizard should provide a concreate implementation of this class. It 
 * is used to store state between wizard pages and should be created
 * when the wizard is started.
 * 
 * <p>For convenience, a simple object map is provided for storing values
 */
public abstract class AbstractWizardSequence {
    
    // Private instance variables
    
    private HashMap attributes;
    private ActionForward finishActionForward;
    private AbstractWizardForm currentPageForm;
    private String resourcePrefix;
    private String resourceBundle;
    private List steps;
    private String referer;
    private List forms;
    private String wizardName;
    private SessionInfo session;
    
    /**
     * Construct a new sequence.
     * 
     * @param finishActionForward forward to jump to for completion of the wizard
     * @param resourceBundle resource bundle to get resources for each steps menu item
     * @param resourcePrefix prefix for keys in the resource bundle. 
     * @param referer page to return to on completion or exit
     * @param wizardName the name of this wizard
     * @param session session 
     */
    public AbstractWizardSequence(ActionForward finishActionForward, String resourceBundle, String resourcePrefix, String referer, String wizardName, SessionInfo session) {
        super();
        this.finishActionForward = finishActionForward;
        this.resourceBundle = resourceBundle;
        this.resourcePrefix = resourcePrefix;
        this.referer = referer;
        this.wizardName = wizardName;
        this.session = session;
        steps = new ArrayList();
        attributes = new HashMap();
        forms = new ArrayList();
    }
    
    /**
     * Get all of the forms that have been used for this sequence.
     * 
     * @return forms
     */
    public List getForms() {
        return forms;
    }
    
    /**
     * Get all of the steps in this wizard.
     * 
     * @return steps
     */
    public List getSteps() {
        return steps;
    }
    
    /**
     * Add a new step to this sequence. This will be added as a menu item
     */
    public void addStep(WizardStep step) {
        steps.add(step);
    }
    
    /**
     * Get the current form for the active page
     * 
     * @return current page form
     */
    public AbstractWizardForm getCurrentPageForm() {
        return currentPageForm;
    }
    
    /**
     * Set the current form for the active page. This also stores the form
     * in a list so the instances can be cleared up when the wizard is
     * cancelled.
     * 
     * @param currentPageForm current page form 
     */
    public void setCurrentPageForm(AbstractWizardForm currentPageForm) {
        this.currentPageForm = currentPageForm;
        for(int i = 0; i < currentPageForm.getStepIndex(); i++) {
            ((WizardStep)steps.get(i)).setAvailable(true);
        }
        if(!forms.contains(currentPageForm)) {
            forms.add(currentPageForm);
        }
    }
    
    /**
     * Return all of the attributes stored in this sequence as a {@link java.util.Map}
     * 
     * @return attributes
     */
    public Map getAttributes() {
        return attributes;
    }
    
    /**
     * Store an attribute in the sequnce
     * 
     * @param key key
     * @param val values
     * @return old values
     */
    public Object putAttribute(Object key, Object val) {
        return attributes.put(key, val);
    }
    
    /**
     * Get the value of an attribute, or a default if it doesn't exist. 
 
     * @param key
     * @param defVal
     * @return
     */
    public Object getAttribute(Object key, Object defVal) {
        Object o = attributes.get(key);
        return o == null ? defVal : o;
    }

    /**
     * Get the action to jump to upon wizard completion
     * 
     * @return finish action forward
     */
    public ActionForward getFinishActionForward() {
        return finishActionForward;
    }

    /**
     * Remove an attribute given its key.
     * 
     * @param key key of attribute to remove
     */
    public void removeAttribute(String key) {
        attributes.remove(key);        
    }

    /**
     * @return Returns the resourceBundle.
     */
    public String getResourceBundle() {
        return resourceBundle;
    }

    /**
     * @return Returns the resourcePrefix.
     */
    public String getResourcePrefix() {
        return resourcePrefix;
    }

    /**
     * @return Returns the referer.
     */
    public String getReferer() {
        return referer;
    }

    /**
     * Get if the sequence has an attribute with the specified key
     * 
     * @parma key attribute key
     * @return has attribute
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * @return Returns the wizardName.
     */
    public String getWizardName() {
        return wizardName;
    }
    
    /**
     * Get the session that started this wizard
     * 
     * @return session
     */
    public SessionInfo getSession() {
        return session;
    }
}
