
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
			
package com.ovpnals.extensions.actions;

import java.io.InputStream;
import java.net.URLConnection;

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

import com.ovpnals.boot.Util;
import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.core.CoreException;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.GlobalWarning;
import com.ovpnals.core.GlobalWarningManager;
import com.ovpnals.core.RedirectWithMessages;
import com.ovpnals.core.GlobalWarning.DismissType;
import com.ovpnals.extensions.ExtensionBundle;
import com.ovpnals.extensions.ExtensionUploadHandler;
import com.ovpnals.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.ovpnals.extensions.forms.DefaultExtensionsForm;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.extensions.store.ExtensionStoreDescriptor;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.table.actions.AbstractPagerAction;
import com.ovpnals.vfs.UploadDetails;

public class DefaultExtensionsAction extends AbstractPagerAction {
    
    final static Log log = LogFactory.getLog(DefaultExtensionsAction.class);

    String extensionCategory = null;
    
    public DefaultExtensionsAction(String extensionCategory) {
        super(PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
        this.extensionCategory = extensionCategory;
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return this.list(mapping, form, request, response);
    }
        
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        DefaultExtensionsForm defaultExtensionForm = (DefaultExtensionsForm) form;
        CoreUtil.clearFlow(request);

        if (!ExtensionStore.STORE_PREF.getBoolean("warnedAboutExtensionStoreConnect", true)) {
            if (request.getParameter("agree") != null) {
                ExtensionStore.STORE_PREF.putBoolean("warnedAboutExtensionStoreConnect", true);
                SessionInfo info = this.getSessionInfo(request);
                Property.setProperty(new SystemConfigKey("updates.automaticallyConnectToApplicationStore"),
                    request.getParameter("agree"),
                    info);
            } else {
                return mapping.findForward("agreement");
            }
        }
        try {
            ExtensionStoreDescriptor storeDescriptor = ExtensionStore.getInstance()
            .getDownloadableExtensionStoreDescriptor(request.getParameter("connect") != null || Property.getPropertyBoolean(new SystemConfigKey("updates.automaticallyConnectToApplicationStore")));
            Util.noCache(response);
        } catch (Exception e) {
        }
        defaultExtensionForm.initialise(request.getSession(), ExtensionStore.getInstance().getAllAvailableExtensionBundles(extensionCategory));
        return null; // Return null because we are in a subform
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        DefaultExtensionsForm defaultExtensionsForm = (DefaultExtensionsForm) form;
        ExtensionStoreDescriptor storeDescriptor = null;
        PolicyUtil.checkPermissions(PolicyConstants.EXTENSIONS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE },
            request);
        ExtensionStore extensionStore = ExtensionStore.getInstance();
        ActionMessages msgs = new ActionMessages();
        try {
            extensionStore.resetExtensionStoreUpdate();
            storeDescriptor = extensionStore.getDownloadableExtensionStoreDescriptor(true, ExtensionStore.getWorkingVersion());
            msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("extensions", "extensionStore.message.refreshed"));
            saveMessages(request, msgs);
        } catch (Exception e) {
            log.error("Failed to refresh extension store.", e);
            msgs.add(Globals.ERROR_KEY, new BundleActionMessage("extensions", "extensionStore.message.failedToRefresh", e
                            .getMessage()));
            saveErrors(request, msgs);
        }
        defaultExtensionsForm.initialise(request.getSession(), ExtensionStore.getInstance().getAllAvailableExtensionBundles(extensionCategory));
        return null; // Return null because we are in a subform
    }

    /**
     * Upload an extension
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermissions(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            new Permission[] { PolicyConstants.PERM_CHANGE },
            request);
        UploadDetails details = new UploadDetails("extensions",
                        ExtensionUploadHandler.TYPE_EXTENSION,
                        null,
                        mapping.findForward("list"),
                        null, // Keep this as NULL. We do not want to show Exit button here!
                        mapping.findForward("list"));
        int id = CoreUtil.addUpload(request.getSession(), details);
        request.setAttribute(Constants.REQ_ATTR_UPLOAD_DETAILS, new Integer(id));
        return mapping.findForward("upload");
    }

    /**
     * Remove selected extension
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        String id = request.getParameter("id");
        if (id == null || id.equals("")) {
            throw new Exception("No id parameter supplied.");
        }
        ExtensionBundle bundle = ExtensionStore.getInstance().getExtensionBundle(id);
        if (bundle == null) {
            throw new Exception("No application with an id of " + id);
        }
        ExtensionStore.getInstance().removeExtensionBundle(bundle);
        if (bundle.isContainsPlugin())
        	GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
                            "extensionStore.message.pluginRemovedRestartRequired"), DismissType.DISMISS_FOR_USER));
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationRemoved", bundle.getName()));
        saveMessages(request, msgs);
        return new RedirectWithMessages(mapping.findForward("refresh"), request);
    }

    /**
     * Install selected extension
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward install(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        String version = request.getParameter("version");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        ActionForward fwd = new RedirectWithMessages(mapping.findForward("refresh"), request); 
        URLConnection con = ExtensionStore.getInstance().downloadExtension(id, version);
        
        try {
            InputStream in = con.getInputStream();            
            ExtensionBundle bundle = ExtensionStore.getInstance().installExtensionFromStore(id, in, request, con.getContentLength());
            ExtensionStore.getInstance().licenseCheck(bundle, request, fwd);
            ExtensionStore.getInstance().postInstallExtension(bundle, request);
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationInstalled", bundle.getName()));
            saveMessages(request, msgs);
        } catch (CoreException ce) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
            saveErrors(request, errs);
        }
        return new RedirectWithMessages(mapping.findForward("refresh"), request);
    }

    /**
     * Update selected extension
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        String version = request.getParameter("version");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        URLConnection con = ExtensionStore.getInstance().downloadExtension(id, version);
        try {
            InputStream in = con.getInputStream();            
            ExtensionBundle bundle = ExtensionStore.getInstance().updateExtension(id, in, request, con.getContentLength());
            if (bundle.isContainsPlugin())
            	GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
                                "extensionStore.message.extensionUpdatedRestartRequired"), DismissType.DISMISS_FOR_USER));
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationUpdated", bundle.getName()));
            saveMessages(request, msgs);
        } catch (CoreException ce) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
            saveErrors(request, errs);
        }
        return new RedirectWithMessages(mapping.findForward("refresh"), request);
    }

    /**
     * Start an extension. The 'id' request parameter must be supplied providing
     * the extension bundle ID to stop.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return ActionForward forward
     * @throws Exception on any error
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        ExtensionBundle bundle = ExtensionStore.getInstance().getExtensionBundle(id);
        if (bundle.getStatus() == ExtensionBundleStatus.ENABLED) {
            bundle.start();
        }
        if (bundle.getStatus() == ExtensionBundleStatus.STARTED) {
            bundle.activate();
        }
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationDisable", bundle.getName()));
        saveMessages(request, messages);
        return mapping.findForward("list");
    }

    /**
     * Stop an extension. The 'id' request parameter must be supplied providing
     * the extension bundle ID to stop.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return ActionForward forward
     * @throws Exception on any error
     */
    public ActionForward stop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        ExtensionBundle bundle = ExtensionStore.getInstance().getExtensionBundle(id);
        bundle.stop();
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationDisable", bundle.getName()));
        saveMessages(request, messages);
        return mapping.findForward("list");
    }

    /**
     * Disable an extension. The 'id' request parameter must be supplied
     * providing the extension bundle ID to stop.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward disable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        ExtensionBundle bundle = ExtensionStore.getInstance().getExtensionBundle(id);
        if (!bundle.canDisable()) {
            throw new Exception("Bundle cannot be disabled.");
        }
        if (!bundle.isContainsPlugin()) {
            bundle.stop();
        }
        ExtensionStore.getInstance().disableExtension(id);
        if(bundle.isContainsPlugin()) {
        	GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
                            "extensionStore.message.stateChangeRestartRequired"), DismissType.DISMISS_FOR_USER));
        }
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationDisable", bundle.getName()));
        saveMessages(request, messages);
        return new RedirectWithMessages(mapping.findForward("list"), request);
    }

    /**
     * Enable an extension. The 'id' request parameter must be supplied
     * providing the extension bundle ID to stop.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward enable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("id");
        PolicyUtil.checkPermission(PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            PolicyConstants.PERM_CHANGE,
            getSessionInfo(request).getUser());
        ExtensionBundle bundle = ExtensionStore.getInstance().getExtensionBundle(id);
        if (!bundle.canEnable()) {
            throw new Exception("Bundle cannot be enabled.");
        }
        ExtensionStore.getInstance().enableExtension(id);
        if(bundle.isContainsPlugin()) {
        	GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
                            "extensionStore.message.stateChangeRestartRequired"), DismissType.DISMISS_FOR_USER));
        } else {
            bundle.activate();
        }
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.MESSAGE_KEY, new ActionMessage("extensionStore.message.applicationEnable", bundle.getName()));
        saveMessages(request, messages);
        return new RedirectWithMessages(mapping.findForward("list"), request);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}