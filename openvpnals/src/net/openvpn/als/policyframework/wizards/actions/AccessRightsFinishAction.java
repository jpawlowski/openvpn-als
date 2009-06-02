
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
			
package net.openvpn.als.policyframework.wizards.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreEventConstants;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.policyframework.AccessRight;
import net.openvpn.als.policyframework.AccessRights;
import net.openvpn.als.policyframework.DefaultAccessRights;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.forms.AbstractWizardPolicySelectionForm;
import net.openvpn.als.policyframework.wizards.forms.AccessRightsDetailsForm;
import net.openvpn.als.policyframework.wizards.forms.AccessRightsPermissionsForm;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.actions.AbstractWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardFinishForm;

/**
 * The <i>AccessRightsFinishAction> {@link net.openvpn.als.wizard.actions.AbstractWizardAction} implementation
 * is responsible finishing the creation of a Access right.
 */
public class AccessRightsFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(AccessRightsFinishAction.class);

    /**
     * 
     */
    public AccessRightsFinishAction() {
        super();
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence seq = getWizardSequence(request);
        SessionInfo info = this.getSessionInfo(request);
        String name = (String) seq.getAttribute(AccessRightsDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(AccessRightsDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        PropertyList permissionList = (PropertyList) seq.getAttribute(AccessRightsPermissionsForm.ATTR_SELECTED_ACCESS_RIGHTS, null);
        String permissionClass = (String) seq.getAttribute(AccessRightsDetailsForm.ATTR_RESOURCE_PERMISSION_CLASS, null);
        AccessRights accessRights = null;
        Calendar now = Calendar.getInstance();
        try {
            DefaultAccessRights defaultAccessRights = new DefaultAccessRights(info.getUser().getRealm().getRealmID(), 0, name, description, new ArrayList<AccessRight>(), permissionClass, now, now);
            defaultAccessRights.setAllAccessRights(info.getHttpSession(), permissionList);
            accessRights = PolicyDatabaseFactory.getInstance().createAccessRights(defaultAccessRights);          
            
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "accessRightsWizard.resourceFinish.status.resourceCreated"));
            
            CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.CREATE_ACCESS_RIGHT, accessRights, info, CoreEvent.STATE_SUCCESSFUL)
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, accessRights.getResourceName())
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_DESCRIPTION, accessRights.getResourceDescription())
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_ACCESS_RIGHT, getAccessRightType(request.getSession(), permissionClass));
           List permissionsList = accessRights.getAccessRights();
            if (permissionsList != null) {
                int j =0;
                for (Iterator i = permissionsList.iterator(); i.hasNext();) {
                    j++;
                    AccessRight permission = (AccessRight)i.next();

                    MessageResources mrPermission = CoreUtil.getMessageResources(request.getSession(), permission.getPermission().getBundle());
                    String permissionName = mrPermission.getMessage("permission."+permission.getPermission().getId()+".title").trim();

                    MessageResources mrResourceType = CoreUtil.getMessageResources(request.getSession(), permission.getResourceType().getBundle());
                    String resourceTypeName = mrResourceType.getMessage("resourceType."+permission.getResourceType().getResourceTypeId()+".title").trim();
                    coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_PERMISSION + Integer.toString(j), permissionName + " " + resourceTypeName);
                }
            }
            CoreServlet.getServlet().fireCoreEvent(coreEvent);
            
        } catch (Exception e) {
            log.error("Failed to create delegation resource.", e);
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.CREATE_ACCESS_RIGHT, accessRights, info, CoreEvent.STATE_UNSUCCESSFUL)
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, accessRights.getResourceName())
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_DESCRIPTION, accessRights.getResourceDescription())
                                    .addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_ACCESS_RIGHT, getAccessRightType(request.getSession(), permissionClass)));
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "accessRightsWizard.resourceFinish.status.failedToCreateResource", e.getMessage()));
        }
        if (accessRights != null) {
            actionStatus.add(attachToPolicies(seq, info, accessRights));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        // we now need to rebuild any menus, as more or less could be visible.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);
        return super.unspecified(mapping, form, request, response);
    }

    private static String getAccessRightType(HttpSession session, String permissionClass) {
        MessageResources messageResources = CoreUtil.getMessageResources(session, "policyframework");
        String accessRightType = messageResources.getMessage("permission.type." + permissionClass);
        return accessRightType;
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

    WizardActionStatus attachToPolicies(AbstractWizardSequence seq, SessionInfo info, Resource resource) {
        PropertyList selectedPolicies = (PropertyList) seq.getAttribute(AbstractWizardPolicySelectionForm.ATTR_SELECTED_POLICIES,
                        null);
        try {
            PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, info);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "accessRightsWizard.resourceFinish.status.attachedToPolicies");
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "accessRightsWizard.resourceFinish.status.failedToAttachToPolicies", e.getMessage());
        }
    }

}
