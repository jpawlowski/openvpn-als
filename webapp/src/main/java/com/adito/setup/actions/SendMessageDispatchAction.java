
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
			
package com.adito.setup.actions;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyList;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.input.MultiSelectDataSource;
import com.adito.input.MultiSelectPoliciesSelectionModel;
import com.adito.input.MultiSelectSelectionModel;
import com.adito.notification.Message;
import com.adito.notification.Recipient;
import com.adito.policyframework.Permission;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDataSource;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyExcludePersonalDataSource;
import com.adito.policyframework.Resource;
import com.adito.realms.Realm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.setup.forms.MessageForm;

public class SendMessageDispatchAction extends AuthenticatedDispatchAction {
    public SendMessageDispatchAction() {
        super(PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, 
            new Permission[] { PolicyConstants.PERM_SEND });
    }

    final static Log log = LogFactory.getLog(SendMessageDispatchAction.class);

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        MessageForm mf = (MessageForm)form;
        mf.setReferer(CoreUtil.getReferer(request));
        PropertyList selectedPolicies = new PropertyList();
        MultiSelectDataSource policies = new PolicyDataSource();
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
      
        String users = request.getParameter("users"); 
        
        MultiSelectSelectionModel policyModel = initSelectModel(mf, selectedPolicies, mf.isShowPersonalPolicies(), users, session);
        
        mf.initialise(policyModel, selectedPolicies, getSessionInfo(request));

        return mapping.findForward("display");
    }

    protected MultiSelectPoliciesSelectionModel initSelectModel(MessageForm mf, PropertyList selectedPolicies, boolean isShowPersonalPolicies, String users, SessionInfo session) throws Exception {
        
        MultiSelectDataSource policies = new PolicyDataSource();
        MultiSelectDataSource personalPolicies = null;
        if (!isShowPersonalPolicies) {
            personalPolicies = policies;
            policies = new PolicyExcludePersonalDataSource();
        }
        MultiSelectPoliciesSelectionModel policyModel = new MultiSelectPoliciesSelectionModel(session, policies, personalPolicies, selectedPolicies);
        if(users != null) {
            if(users.equals("*")) {
                mf.setSelectedPolicies(String.valueOf(PolicyDatabaseFactory.getInstance().getEveryonePolicyIDForRealm(
                                session.getUser().getRealm())));
                policyModel.rebuild(session);
            }
            else {
                mf.setSelectedAccounts(users);
            }
        }
        return policyModel;
    }
    
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        MessageForm mf = (MessageForm)form;
        Realm realm = getSessionInfo(request).getUser().getRealm();
        Message msg = new Message(mf.getSubject(), mf.getContent(), mf.getUrgent());
        for(Iterator i = mf.getSelectedAccountsList().iterator(); i.hasNext(); ) {
            msg.getRecipients().add(new Recipient(Recipient.USER, (String)i.next(), realm.getResourceName()));
        }
        for(Iterator i = mf.getSelectedRolesList().iterator(); i.hasNext(); ) {
            msg.getRecipients().add(new Recipient(Recipient.ROLE, (String)i.next(), realm.getResourceName()));
        }
        for(Iterator i = mf.getSelectedPoliciesList().iterator(); i.hasNext(); ) {
            String policyName = PolicyDatabaseFactory.getInstance().getPolicy(Integer.parseInt((String)i.next())).getResourceName();
            msg.getRecipients().add(new Recipient(Recipient.POLICY, policyName, realm.getResourceName()));
        }
        if(msg.getRecipients().size() == 0) {
            throw new Exception("No recipients in any of the accounts, roles or policies selected.");
        }
        if(mf.getSelectedSink().equals("*")) {
            CoreServlet.getServlet().getNotifier().sendToAll(msg);
        }
        else if(mf.getSelectedSink().equals("^")) {
            CoreServlet.getServlet().getNotifier().sendToFirst(msg);
        }
        else  {
            CoreServlet.getServlet().getNotifier().sendToSink(mf.getSelectedSink(), msg);
        }
        return cancel(mapping, form, request, response);
    }

    /**
     * Toggle show personal policies.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward toogleShowPersonalPolicies(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                throws Exception {
        MessageForm mf = (MessageForm)form;
        PropertyList selectedPolicies = new PropertyList();
        
        SessionInfo session = this.getSessionInfo(request);
        
        String users = request.getParameter("users"); 

        MultiSelectSelectionModel policyModel = initSelectModel(mf, selectedPolicies, mf.isShowPersonalPolicies(), users, session);
        
        mf.setSelectedPolicySelection(policyModel);
        mf.setSelectedPolicies(selectedPolicies);
        
        return mapping.findForward("display");
    }
    
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}