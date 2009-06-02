
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
			
package net.openvpn.als.wizard.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.wizard.AbstractWizardSequence;

public class DefaultWizardForm extends AbstractWizardForm {
    
    final static Log log = LogFactory.getLog(DefaultWizardForm.class);
    
    private boolean nextAvailable;
    private boolean previousAvailable;
    private String page;
    private String focussedField;
    private boolean autoComplete;
    private boolean finishAvailable;
    private String pageName;
    private String resourceBundle;
    private String resourcePrefix;
    private int stepIndex;
    private AbstractWizardSequence wizardSequence;

    /**
     * @param nextAvailable
     * @param previousAvailable
     * @param page
     * @param focussedField
     * @param autoComplete
     * @param finishAvailable
     * @param pageName
     * @param resourceBundle
     * @param resourcePrefix
     * @param stepIndex
     */
    public DefaultWizardForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField, boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle, String resourcePrefix, int stepIndex) {
        super();
        this.nextAvailable = nextAvailable;
        this.previousAvailable = previousAvailable;
        this.page = page;
        this.focussedField = focussedField;
        this.autoComplete = autoComplete;
        this.finishAvailable = finishAvailable;
        this.pageName = pageName;
        this.resourceBundle = resourceBundle;
        this.resourcePrefix = resourcePrefix;
        this.stepIndex = stepIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#init(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence wizardSequence, HttpServletRequest request) throws Exception {
        this.wizardSequence = wizardSequence;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#isNextAvailable()
     */
    public boolean getNextAvailable() {
        return nextAvailable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#isPreviousAvailable()
     */
    public boolean getPreviousAvailable() {
        return previousAvailable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getPage()
     */
    public String getPage() {
        return page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getResourcePrefix()
     */
    public String getResourcePrefix() {
        return resourcePrefix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getResourceBundle()
     */
    public String getResourceBundle() {
        return resourceBundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getPageName()
     */
    public String getPageName() {
        return pageName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getFinishAvailable()
     */
    public boolean getFinishAvailable() {
        return finishAvailable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getFocussedField()
     */
    public String getFocussedField() {
        return focussedField;
    }


    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getAutocomplete()
     */
    public boolean getAutocomplete() {
        return autoComplete;
    }

    /**
     * @param autoComplete The autoComplete to set.
     */
    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * @param finishAvailable The finishAvailable to set.
     */
    public void setFinishAvailable(boolean finishAvailable) {
        this.finishAvailable = finishAvailable;
    }

    /**
     * @param focussedField The focussedField to set.
     */
    public void setFocussedField(String focussedField) {
        this.focussedField = focussedField;
    }

    /**
     * @param nextAvailable The nextAvailable to set.
     */
    public void setNextAvailable(boolean nextAvailable) {
        this.nextAvailable = nextAvailable;
    }

    /**
     * @param page The page to set.
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * @param pageName The pageName to set.
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * @param previousAvailable The previousAvailable to set.
     */
    public void setPreviousAvailable(boolean previousAvailable) {
        this.previousAvailable = previousAvailable;
    }

    /**
     * @param resourceBundle The resourceBundle to set.
     */
    public void setResourceBundle(String resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * @param resourcePrefix The resourcePrefix to set.
     */
    public void setResourcePrefix(String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#getStepIndex()
     */
    public int getStepIndex() {
        return stepIndex;
    }
    
    public AbstractWizardSequence getWizardSequence() {
        return wizardSequence;
    }
}
