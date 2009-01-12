
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
			
package com.adito.applications.wizards.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.applications.ApplicationsPlugin;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.adito.extensions.store.ExtensionStore;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * Extension of a {@link com.adito.wizard.forms.DefaultWizardForm} that
 * allows an administrator to select the application to create the shortcut for.
 */
public class ApplicationShortcutWizardApplicationForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(ApplicationShortcutWizardApplicationForm.class);
    
    /**
     * Sequence attribute storing the ID of the selected application
     */
    public final static String ATTR_SELECTED_APPLICATION = "selectedApplication";

    // Privater instance variables

    private List availableApplications;
    private String selectedApplication;

    /**
     * Constructor
     */
    public ApplicationShortcutWizardApplicationForm() {
        super(true, false, "/WEB-INF/jsp/content/applications/applicationShortcutWizard/application.jspf", null, true,
            false, "applicationShortcutApplication", ApplicationsPlugin.MESSAGE_RESOURCES_KEY, "applicationShortcutWizard.applicationShortcutApplication", 1);
    }

    /**
     * Get Id of the selected application extension
     * 
     * @return selected application extension
     */
    public String getSelectedApplication() {
        return selectedApplication;
    }

    /**
     * Set Id of the selected application extension
     * 
     * @param selectedApplication selected application
     */
    public void setSelectedApplication(String selectedApplication) {
        this.selectedApplication = selectedApplication;
    }

    /**
     * Get a list of {@link ExtensionDescriptor} objects for
     * all available application extensions
     * 
     * @return list of available application extensions
     */
    public List getAvailableApplications() {
        return availableApplications;
    }

    /**
     * Get a list of {@link ExtensionDescriptor} objects for
     * all available application extensions
     * 
     * @param availableApplications list of available application extensions
     */
    public void setParameterItems(List availableApplications) {
        this.availableApplications = availableApplications;
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence, javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence wizardSequence, HttpServletRequest request) throws Exception {
        super.init(wizardSequence, request);
        availableApplications = new ArrayList();
        selectedApplication = (String)wizardSequence.getAttribute(ATTR_SELECTED_APPLICATION, null);
        for (Iterator i = ExtensionStore.getInstance().getAllAvailableExtensionBundles().iterator(); i.hasNext();) {
            ExtensionBundle b = (ExtensionBundle)i.next();
            if(b.getStatus() == ExtensionBundleStatus.ACTIVATED) {
	            for(Iterator j = b.iterator(); j.hasNext(); ) {
	                ExtensionDescriptor d = (ExtensionDescriptor)j.next();
	                
	                // Only show extensions if the type is not hidden and the extension itself is not hidden
	                if(!d.isHidden() && !d.getExtensionType().isHidden()) {
	                    if (this.getFocussedField() == null){
	                        this.setFocussedField("check" + d.getId());
	                    }
	                    availableApplications.add(d);
	                }
	            }
            }
        }
        this.setNextAvailable(true);
        if(selectedApplication == null && availableApplications.size() > 0) {
            selectedApplication = ((ExtensionDescriptor)availableApplications.get(0)).getId();
        } else if (availableApplications.isEmpty()) {
                this.setNextAvailable(false);
        }
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        String oldVal = (String)sequence.putAttribute(ATTR_SELECTED_APPLICATION, selectedApplication);
        if(!selectedApplication.equals(oldVal)) {
            sequence.removeAttribute(ApplicationShortcutWizardAdditionalDetailsForm.ATTR_PARAMETERS);
        }
    }
}
