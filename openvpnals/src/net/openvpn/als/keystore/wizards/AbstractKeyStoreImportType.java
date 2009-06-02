
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
			
package net.openvpn.als.keystore.wizards;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * Plugins may register implementations of this class with the
 * {@link net.openvpn.als.boot.KeyStoreType} to add new types to the key store
 * import wizard.
 * <p>
 * A number of message resources must be provided. For example, for a type that 
 * has a name <i>caRoot</i> must provide :-
 * <ul>
 * <li>keyStoreImportType.caRoot.title</li>,
 * <li>keyStoreImportType.caRoot.description</li>
 * <li>keyStoreImportType.caRoot.summaryTitle</li>,
 * <li>keyStoreImportType.caRoot.installed</li>
 * <li>keyStoreImportType.caRoot.installFailed</li>
 * </ul>
 * 
 * @see net.openvpn.als.keystore.wizards.KeyStoreImportTypeManager
 */
public abstract class AbstractKeyStoreImportType implements Comparable {

    // Private instance variables
    private String name;
    private String bundle;
    private boolean requiresPassphrase;
    private boolean requiresAlias;
    private int weight;
    private boolean restartRequired = true;

    /**
     * Constructor.
     * 
     * @param name name
     * @param bundle bunlde that contains resources
     * @param requiresPassphrase requires a passphrase
     * @param requiresAlias requires an alias
     * @param weight weight
     */
    public AbstractKeyStoreImportType(String name, String bundle, boolean requiresPassphrase, boolean requiresAlias, int weight) {
        this.name = name;
        this.bundle = bundle;
        this.requiresPassphrase = requiresPassphrase;
        this.requiresAlias = requiresAlias;
        this.weight = weight;
    }
    
    /**
     * Install the certificiate
     * 
     * @param file uploaded ifle
     * @param alias alias or <code>null</code> if not required
     * @param passphrase  passphrase  or <code>null</code> if not required
     * @param seq wizard sequence
     * @param sessionInfo TODO
     * @throws Exception on any error 
     */
    public abstract void doInstall(File file, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) throws Exception;

    
    /**
     * Validate the input. Default implementation does nothing.
     * 
     * @param errs errors ojbect to add to
     * @param alias alias
     * @param passphrase passphrase
     * @param seq wizard sequence 
     * @param sessionInfo sessionInfo
     */
    public void validate(ActionMessages errs, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) {
    	if(requiresAlias && (alias==null || alias.trim().equals(""))) {
    		errs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("keyStoreImportType.alias.required"));
    	}
    }
    
    /**
     * Invoked when the type is first selected.
     * 
     * @param request request 
     */
    public void init(HttpServletRequest request) {
    }

    /**
     * Get the bundle id that contains the resources for this import type
     * 
     * @return bundle that contains resources
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Set the bundle id that contains the resources for this import type
     * 
     * @param bundle bundle that contains resources
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    /**
     * Get the internal name for this type. This will be determine the resources
     * to use in the wizard.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the internal name for this type. This will be determine the resources
     * to use in the wizard.
     * 
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get if this import type requires an alias to be specified by the user
     * 
     * @return requires alias
     */
    public boolean isRequiresAlias() {
        return requiresAlias;
    }

    /**
     * Set if this import type requires an alias to be specified by the user
     * 
     * @param requiresAlias requires alias
     */
    public void setRequiresAlias(boolean requiresAlias) {
        this.requiresAlias = requiresAlias;
    }

    /**
     * Get if this import type requires a passphrase to be specified by the user
     * 
     * @return requires passphrase
     */
    public boolean isRequiresPassphrase() {
        return requiresPassphrase;
    }

    /**
     * Set if this import type requires a passphrase to be specified by the user
     * 
     * @param requiresPassphrase requires passphrase
     */
    public void setRequiresPassphrase(boolean requiresPassphrase) {
        this.requiresPassphrase = requiresPassphrase;
    }

    /**
     * Get the weight used to determine this import types order in the list
     * in the wizard. The lower the number the higher in the list
     * 
     * @return weight
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * Set the weight used to determine this import types order in the list
     * in the wizard. The lower the number the higher in the list
     * 
     * @param weight weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Compare this type with another based on the weight. If the weight
     * is equal then compare by nam,e
     * 
     * @param arg0
     * @return comparison
     */
    public int compareTo(Object arg0) {
        int i = new Integer(getWeight()).compareTo(new Integer(((AbstractKeyStoreImportType)arg0).getWeight()));
        return i == 0 ? getName().compareTo(((AbstractKeyStoreImportType)arg0).getName()) : i;
    }
    
    /**
     * Get if the import of this type requires the server 
     * is restarted.
     * 
     * @return restart required
     */
    public boolean isRestartRequired() {
        return restartRequired;
    }
    
    /**
     * Set if the import of this type requires the server 
     * is restarted.
     * 
     * @param restartRequired restart required
     */
    public void setRestartRequired(boolean restartRequired) {
        this.restartRequired = restartRequired;
    }

}
