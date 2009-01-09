
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
			
package com.adito.networkplaces.wizards.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.Util;
import com.adito.core.forms.AbstractResourceDetailsWizardForm;
import com.adito.input.validators.HostnameOrIPAddressWithReplacementsValidator;
import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.networkplaces.NetworkPlaceUtil;
import com.adito.networkplaces.NetworkPlaceVFSProvider;
import com.adito.policyframework.ResourceType;
import com.adito.vfs.DefaultVFSProvider;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSProviderManager;
import com.adito.wizard.AbstractWizardSequence;

public class NetworkPlaceDetailsForm extends AbstractResourceDetailsWizardForm {
	
	/**
	 * Default provider used for entering in URI format
	 */
	public final static VFSProvider DEFAULT_PROVIDER = new DefaultVFSProvider("automatic", null, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY);

	public final static String ATTR_PROVIDER = "provider";
	public final static String ATTR_HOST = "host";
	public final static String ATTR_PATH = "path";
	public final static String ATTR_PORT = "port";
	public final static String ATTR_USERNAME = "username";
	public final static String ATTR_PASSWORD = "password";
	public final static String ATTR_READ_ONLY = "readOnly";
	public final static String ATTR_SHOW_HIDDEN = "showHidden";
	public final static String ATTR_ALLOW_RECURSIVE = "allowRecursive";
	public final static String ATTR_NO_DELETE = "noDelete";
	public final static String ATTR_SCHEME = "scheme";

	private String host;
	private String path;
	private int port;
	private String username;
	private String password;
	private String scheme;
    private String queryString;
    private VFSProvider provider;
	private boolean readOnly;
	private boolean showHidden;
	private boolean allowRecursive;
	private boolean noDelete;
    private boolean autoDetected;

	final static Log log = LogFactory.getLog(NetworkPlaceDetailsForm.class);

	public NetworkPlaceDetailsForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField,
									boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle,
									String resourcePrefix, int stepIndex, ResourceType resourceTypeForAccessRights) {
		super(nextAvailable,
						previousAvailable,
						page,
						focussedField,
						autoComplete,
						finishAvailable,
						pageName,
						resourceBundle,
						resourcePrefix,
						stepIndex,
						resourceTypeForAccessRights);
	}

	public NetworkPlaceDetailsForm() {
    	/* Autocomplete must be false because this form contains passwords
    	 * and not all browsers seem to support disabling of autocomplete
    	 * for individual fields
    	 */
		super(true,
						true,
						"/WEB-INF/jsp/content/vfs/networkingWizard/networkPlaceDetails.jspf",
						"scheme",
						false,
						false,
						"networkPlaceDetails",
						NetworkPlacePlugin.MESSAGE_RESOURCES_KEY,
						"networkPlaceWizard.networkPlaceDetails",
						2,
						NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE);
	}

    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        super.init(sequence, request);

        if(provider == null) {
        	provider = DEFAULT_PROVIDER;
        }
        
        // Initialise
        path = (String) sequence.getAttribute(ATTR_PATH, "");
        host = (String) sequence.getAttribute(ATTR_HOST, "");
        username = (String) sequence.getAttribute(ATTR_USERNAME, "");
        password = (String) sequence.getAttribute(ATTR_PASSWORD, "");
        scheme = (String) sequence.getAttribute(ATTR_SCHEME, provider.getScheme());
        queryString = "";
        readOnly = ((Boolean) sequence.getAttribute(ATTR_READ_ONLY, Boolean.FALSE)).booleanValue();
        showHidden = ((Boolean) sequence.getAttribute(ATTR_SHOW_HIDDEN, Boolean.FALSE)).booleanValue();
        allowRecursive = ((Boolean) sequence.getAttribute(ATTR_ALLOW_RECURSIVE, Boolean.TRUE)).booleanValue();
        noDelete = ((Boolean) sequence.getAttribute(ATTR_NO_DELETE, Boolean.FALSE)).booleanValue();
    }

    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_PROVIDER, provider);
        sequence.putAttribute(ATTR_HOST, getHost());
        sequence.putAttribute(ATTR_PATH, getPath());
        sequence.putAttribute(ATTR_PORT, Integer.valueOf(getPort()));
        sequence.putAttribute(ATTR_USERNAME, getUsername());
        sequence.putAttribute(ATTR_PASSWORD, getPassword());
        sequence.putAttribute(ATTR_SCHEME, getScheme());
        sequence.putAttribute(ATTR_READ_ONLY, new Boolean(readOnly));
        sequence.putAttribute(ATTR_SHOW_HIDDEN, new Boolean(showHidden));
        sequence.putAttribute(ATTR_ALLOW_RECURSIVE, new Boolean(allowRecursive));
        sequence.putAttribute(ATTR_NO_DELETE, new Boolean(noDelete));
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (isCommiting()) {
            ActionErrors errs = super.validate(mapping, request);
            if (errs == null)
                errs = new ActionErrors();
            
            try {               
                
                /* If using the default URI provider, turn into a proper provider so validation can take place */
                if(provider == DEFAULT_PROVIDER) {
                	
                	// Using 'Automatic' means a path *must* be provided
                    
                    if(Util.isNullOrTrimmedBlank(path)) {
                        errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.noPath"));
                        return errs;
                    }
                	
                	try {
                		NetworkPlace np = NetworkPlaceUtil.createNetworkPlaceForPath(path);
	            		provider = VFSProviderManager.getInstance().getProvider(np.getScheme());
	            		if(provider == null) {
	            			throw new Exception("No provider " + np.getScheme());
	            		}
	            		scheme = np.getScheme();
	            		host = np.getHost();
	            		port = Math.max(np.getPort(), 0);
	            		path = np.getPath();
	            		username = np.getUsername();
	            		password = np.getPassword();
                        autoDetected = true;
                	}
            		catch(Exception e) {                    		
                        errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.invalidUri", path));
                        return errs;                			
            		}
                    changeProvider(provider);
                }
                
                if(provider == null) {
                	throw new Exception("No provider.");
                }
                
                if(!(provider instanceof NetworkPlaceVFSProvider)) {
                	throw new Exception("Must be a Network Place VFS provider.");
                }
                
                /* Validate all of the elements */
                
                if(provider.getHostRequirement() == VFSProvider.ELEMENT_REQUIRED) {
            		String name = Util.trimmedOrBlank(host);
                	if("".equals(name)) {                		
                		errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.noHost"));
                	}
                	else {
                		/* Officialy, the only supported characters in a hostname
                		 * are [a-z], [0-9], '.' and '-'. In practice though other
                		 * characters such as '_' are commonly used. Lets just catach
                		 * the obvious ones for now HTTP Proxy Host
                		 */
                        if(!HostnameOrIPAddressWithReplacementsValidator.isValidAsHostOrIp(name)) {                      
                    		errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.invalidHost"));                			
                		}
                	}
                }
                
                if(provider.getPortRequirement() == VFSProvider.ELEMENT_REQUIRED &&
                				port == 0) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.noPort"));
                }
                
                if(provider.getUserInfoRequirement() == VFSProvider.ELEMENT_REQUIRED &&
                				Util.isNullOrTrimmedBlank(username)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.noUserInfo"));
                }
                
                if(provider.getPathRequirement() == VFSProvider.ELEMENT_REQUIRED &&
                				Util.isNullOrTrimmedBlank(path)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("networkPlaceWizard.networkPlaceDetails.error.noPath"));
                }
            } catch (Exception ex) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("vfs.unexpected.error", ex.getMessage()));
            }
            return errs;
        }
        return null;
    }
    
    public VFSProvider getProvider() {
    	return provider;
    }

	public boolean isAllowRecursive() {
		return allowRecursive;
	}

	public void setAllowRecursive(boolean allowRecursive) {
		this.allowRecursive = allowRecursive;
	}

	public boolean isNoDelete() {
		return noDelete;
	}

	public void setNoDelete(boolean noDelete) {
		this.noDelete = noDelete;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isShowHidden() {
		return showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		this.showHidden = false;
		this.readOnly = false;
		this.allowRecursive = false;
		this.noDelete = false;
        this.autoDetected = false;
	}

	public void setProvider(VFSProvider provider) {
		this.provider = provider;		
	}

    public void changeProvider(VFSProvider provider) {
        setProvider(provider);
        setScheme(provider.getScheme());        
    }

    public boolean isAutomaticallyDetected() {
        return autoDetected;
    }
}
