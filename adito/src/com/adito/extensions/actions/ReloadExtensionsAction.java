
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
			
package com.adito.extensions.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.BundleActionMessage;
import com.adito.core.CoreUtil;
import com.adito.core.GlobalWarning;
import com.adito.core.GlobalWarningManager;
import com.adito.core.LicenseAgreement;
import com.adito.core.GlobalWarning.DismissType;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.types.PluginType;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.SessionInfo;
import com.adito.setup.LicenseAgreementCallback;

/**
 * Action to reload the extension store.
 */
public class ReloadExtensionsAction extends AuthenticatedAction {

	final static Log log = LogFactory.getLog(ReloadExtensionsAction.class);

	/**
	 * Constructor.
	 * 
	 */
	public ReloadExtensionsAction() {
		super(PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward onExecute(ActionMapping mapping, ActionForm form, final HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		try {
			List errors = new ArrayList();

			// Get a list of the currently loaded plugin definitions. If this
			// changes display a restart warning
			Map currentlyLoadedPlugins = getLoadedPlugins();
			Map currentlyLoadedExtensions = getLoadedExtensions();

			// If there are any pending removals or installations, we cannot
			// reload
			boolean pending = false;
			for (Iterator i = currentlyLoadedExtensions.entrySet().iterator(); !pending && i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				final ExtensionBundle bundle = (ExtensionBundle) ent.getValue();
				pending = bundle.getType() == ExtensionBundle.TYPE_PENDING_INSTALLATION || bundle.getType() == ExtensionBundle.TYPE_PENDING_REMOVAL
					|| bundle.getType() == ExtensionBundle.TYPE_PENDING_UPDATE
					|| bundle.getType() == ExtensionBundle.TYPE_PENDING_STATE_CHANGE;
			}
			if (pending) {
				throw new Exception("There are pending installations / removals / updates. You must restart the server.");
			}

			// Reload

			if (request.getParameter("id") == null) {
				ExtensionStore.getInstance().reload();
			} else {
				String application = request.getParameter("id");
				ExtensionStore.getInstance().reload(application);
			}

			/*
			 * Build up an error message from any exceptions that may have
			 * occured during reloading
			 */
			if (errors != null && errors.size() > 0) {
				StringBuffer buf = new StringBuffer();
				for (ExtensionBundle bundle : ExtensionStore.getInstance().getExtensionBundles()) {
					if (bundle.getError() != null) {
						if (buf.length() > 0) {
							buf.append(". ");
						}
						buf.append(bundle.getError().getMessage());
					}
				}
				throw new Exception(buf.toString());
			}

			// Look for new plugins
			Map newLoadedPlugins = getLoadedPlugins();

			// Look for new plugins
			boolean newPluginsFound = false;
			for (Iterator i = newLoadedPlugins.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				if (!currentlyLoadedPlugins.containsKey(ent.getKey())) {
					ExtensionDescriptor des = (ExtensionDescriptor) newLoadedPlugins.get(ent.getKey());
					des.getApplicationBundle().setType(ExtensionBundle.TYPE_PENDING_INSTALLATION);
					newPluginsFound = true;
				}
			}
			if (newPluginsFound) {
				GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
								"extensionStore.message.pluginInstalledRestartRequired"), DismissType.DISMISS_FOR_USER));
			}

			// Look for new extensions
			Map newLoadedExtensions = getLoadedExtensions();

			// Look for extension updates
			File updatedExtensionsDir = ExtensionStore.getInstance().getUpdatedExtensionsDirectory();
			boolean updatesFound = false;
			for (Iterator i = newLoadedExtensions.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				if (new File(updatedExtensionsDir, (String) ent.getKey()).exists()) {
					final ExtensionBundle bundle = (ExtensionBundle) ent.getValue();
					for (Iterator j = bundle.iterator(); j.hasNext();) {
						ExtensionDescriptor des = (ExtensionDescriptor) j.next();
						des.getApplicationBundle().setType(ExtensionBundle.TYPE_PENDING_UPDATE);
					}
					updatesFound = true;
				}
			}
			if (updatesFound) {
				GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
								"extensionStore.message.extensionUpdatedRestartRequired"), DismissType.DISMISS_FOR_USER));
			}

			// Check for new extensions
			for (Iterator i = newLoadedExtensions.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				if (!currentlyLoadedExtensions.containsKey(ent.getKey())) {
					final ExtensionBundle bundle = (ExtensionBundle) ent.getValue();

					// If installing, there may be a license agreement to handle
					File licenseFile = bundle.getLicenseFile();
					if (licenseFile != null && licenseFile.exists()) {
						final boolean fNewPluginsFound = newPluginsFound;
						CoreUtil.requestLicenseAgreement(request.getSession(), new LicenseAgreement(bundle.getName(),
										licenseFile,
										new LicenseAgreementCallback() {
											public void licenseAccepted(HttpServletRequest request) {
												// Dont care
											}

											public void licenseRejected(HttpServletRequest request) {
												try {
													ExtensionStore.getInstance().removeExtensionBundle(bundle);
												} catch (Exception e) {
												}
												if (fNewPluginsFound) {
													GlobalWarningManager.getInstance().removeGlobalWarning(request.getSession(),
														"extensionStore.message.pluginInstalledRestartRequired");
													GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS,
														new BundleActionMessage("extensions",
																		"extensionStore.message.pluginLicenseRejectedRestartRequired"), DismissType.DISMISS_FOR_USER));
												}
											}

										},
										new ActionForward("/showExtensionStore.do", true)));
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to reload extension store.", e);
			ActionMessages errs = new ActionMessages();
			errs.add(Globals.ERROR_KEY, new ActionMessage("extensionStore.error.reloadFailed", e.getMessage()));
			saveErrors(request, errs);
		}

		return mapping.findForward("success");
	}

	private Map<String,ExtensionDescriptor> getLoadedPlugins() {
		Map<String,ExtensionDescriptor> map = new HashMap<String,ExtensionDescriptor>();
		for (Iterator i = getLoadedExtensions().entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			ExtensionBundle b = (ExtensionBundle) entry.getValue();
			for (Iterator j = b.iterator(); j.hasNext();) {
				ExtensionDescriptor des = (ExtensionDescriptor) j.next();
				if (des.getExtensionType() instanceof PluginType) {
					map.put(des.getId(), des);
				}
			}
		}
		return map;
	}

	private Map<String,ExtensionBundle> getLoadedExtensions() {
		Map<String,ExtensionBundle> map = new HashMap<String,ExtensionBundle>();
		for (Iterator i = ExtensionStore.getInstance().getExtensionBundles().iterator(); i.hasNext();) {
			ExtensionBundle bundle = (ExtensionBundle) i.next();
			map.put(bundle.getId(), bundle);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.core.actions.AuthenticatedAction#getNavigationContext(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
	}
}