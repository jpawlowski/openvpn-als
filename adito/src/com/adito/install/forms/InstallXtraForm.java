
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
			
package com.adito.install.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.store.ExtensionStoreDescriptor;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class InstallXtraForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(InstallXtraForm.class);

    // Private statics for sequence attributes
    public final static String ATTR_INSTALL_XTRA = "installXtra";
    
    // Private instance variables
    private boolean installXtra;
    private boolean xtraInstalled;
    private boolean xtraAvailable;
    private boolean updateRequired;
    private Exception connectionException;

    public static final String ENTERPRISE_CORE_BUNDLE_ID = "adito-enterprise-core";
    
    public InstallXtraForm() {
        super(true, true, "/WEB-INF/jsp/content/install/installXtra.jspf",
            "", true, false, "installXtra", "install", "installation.installXtra", 6);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        try {
            ExtensionStore.getInstance().getExtensionBundle(ENTERPRISE_CORE_BUNDLE_ID);
            xtraInstalled = true;
        }
        catch(Exception e) {
            xtraInstalled = false;
        }
        xtraAvailable = false;
        updateRequired = false;
        connectionException = (Exception)sequence.getAttribute(ConfigureProxiesForm.ATTR_EXTENSION_STORE_EXCEPTION, null);
        if(connectionException != null) {
            installXtra = false;
        }
        else {
            ExtensionStoreDescriptor installable = ExtensionStore.getInstance().getDownloadableExtensionStoreDescriptor(true);
            xtraAvailable = installable.getApplicationBundle(ENTERPRISE_CORE_BUNDLE_ID)!=null;
            
            boolean found = false;
            for (Iterator i = installable.getExtensionBundles().iterator(); i.hasNext();) {
                ExtensionBundle bundle = (ExtensionBundle) i.next();
                if (bundle.getId().equals(ENTERPRISE_CORE_BUNDLE_ID) && ExtensionStore.getInstance().isExtensionLoaded(bundle.getId())) {
                    ExtensionBundle installed = ExtensionStore.getInstance().getExtensionBundle(bundle.getId());
                    found = true;
                    xtraAvailable = true;
                    if (bundle.getVersion().compareTo(installed.getVersion()) > 0) {
                        updateRequired = true;
                    }
                }
            }

            installXtra = updateRequired || "true".equals(
                sequence.getAttribute(ATTR_INSTALL_XTRA, "true"));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence)  throws Exception {
        sequence.putAttribute(ATTR_INSTALL_XTRA, String.valueOf(installXtra));
    }

    /**
     * @return Returns the installXtra.
     */
    public boolean getInstallXtra() {
        return installXtra;
    }

    /**
     * @param installXtra The installXtra to set.
     */
    public void setInstallXtra(boolean installXtra) {
        this.installXtra = installXtra;
    }

    /**
     * @return Returns the connectionException.
     */
    public Exception getConnectionException() {
        return connectionException;
    }

    /**
     * @return Returns the updateRequired.
     */
    public boolean getUpdateRequired() {
        return updateRequired;
    }

    /**
     * @return Returns the xtraInstalled.
     */
    public boolean getXtraInstalled() {
        return xtraInstalled;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        installXtra = false;
    }

    public boolean getXtraAvailable() {
        return xtraAvailable;
    }
}
