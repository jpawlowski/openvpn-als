
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
			
package com.ovpnals.replacementproxy.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.replacementproxy.Replacement;
import com.ovpnals.replacementproxy.ReplacementItem;
import com.ovpnals.replacementproxy.forms.ReplacementsForm;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.webforwards.WebForwardDatabase;
import com.ovpnals.webforwards.WebForwardDatabaseFactory;
import com.ovpnals.webforwards.WebForwardEventConstants;

public class ReplacementsDispatchAction extends AuthenticatedDispatchAction {

    static Log log = LogFactory.getLog(ReplacementsDispatchAction.class);
    static String ATTR_REPLACEMENT = "replacement";

    public ReplacementsDispatchAction() {
        super(PolicyConstants.REPLACEMENTS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        try {
        	CoreUtil.clearFlow(request);
            
            WebForwardDatabase udb = WebForwardDatabaseFactory.getInstance();
            List replacements = udb.getReplacements();
            List replacementItems = new ArrayList();
            int idx = 0;
            ReplacementItem item = null;
            String lastMimeType = null;
            String lastSite = null;
            for (Iterator i = replacements.iterator(); i.hasNext();) {
                Replacement r = (Replacement) i.next();
                if (lastSite == null || !lastSite.equals(r.getSitePattern())) {
                    lastSite = r.getSitePattern();
                } else {
                    if (lastMimeType == null || !lastMimeType.equals(r.getMimeType())) {
                        lastMimeType = r.getMimeType();
                    }
                }
                ReplacementItem n = new ReplacementItem(idx, r);
                n.setCanMoveUp(replacementItems.size() > 0);
                n.setCanMoveDown(true);
                item = n;
                replacementItems.add(item);
                idx++;
            }
            if (item != null) {
                item.setCanMoveDown(false);
            }
            ((ReplacementsForm) form).initialize(replacementItems);
        } catch (Exception ex) {
            log.error("Failed to setup form.", ex);
        }
        return mapping.findForward("success");
    }

    public ActionForward swap(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        int idx1 = Integer.parseInt(request.getParameter("idx1"));
        int idx2 = Integer.parseInt(request.getParameter("idx2"));
        ReplacementsForm rf = (ReplacementsForm) form;
        ReplacementItem item1 = (ReplacementItem) rf.getReplacementItems().get(idx1);
        ReplacementItem item2 = (ReplacementItem) rf.getReplacementItems().get(idx2);
        Replacement rep1 = item1.getReplacement();
        Replacement rep2 = item2.getReplacement();
        String mimeType1 = rep1.getMimeType();
        String sitePattern1 = rep1.getSitePattern();
        String matchPattern1 = rep1.getMatchPattern();
        String replacePattern1 = rep1.getReplacePattern();
        rep1.setMatchPattern(rep2.getMatchPattern());
        rep1.setMimeType(rep2.getMimeType());
        rep1.setSitePattern(rep2.getSitePattern());
        rep1.setReplacePattern(rep2.getReplacePattern());
        rep2.setMatchPattern(matchPattern1);
        rep2.setMimeType(mimeType1);
        rep2.setSitePattern(sitePattern1);
        rep2.setReplacePattern(replacePattern1);
        SessionInfo info = this.getSessionInfo(request);
        try {
            WebForwardDatabaseFactory.getInstance().updateReplacement(rep1);
            CoreServlet.getServlet().fireCoreEvent(
                new CoreEvent(this, WebForwardEventConstants.REPLACEMENT_PRECEDENCE_CHANGED, null, info, CoreEvent.STATE_SUCCESSFUL).addAttribute(
                                WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, String.valueOf(rep1.getReplaceType())));
            try {
                WebForwardDatabaseFactory.getInstance().updateReplacement(rep2);
                CoreServlet.getServlet().fireCoreEvent(
                    new CoreEvent(this, WebForwardEventConstants.REPLACEMENT_PRECEDENCE_CHANGED, null, info, CoreEvent.STATE_SUCCESSFUL)
                                    .addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, String.valueOf(rep2
                                                    .getReplaceType())));
                return mapping.findForward("success");
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                    new CoreEvent(this, WebForwardEventConstants.REPLACEMENT_PRECEDENCE_CHANGED, null, info, CoreEvent.STATE_UNSUCCESSFUL)
                                    .addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, String.valueOf(rep2
                                                    .getReplaceType())));
                throw e;
            }
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                new CoreEvent(this, WebForwardEventConstants.REPLACEMENT_PRECEDENCE_CHANGED, null, info, CoreEvent.STATE_UNSUCCESSFUL).addAttribute(
                                WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, String.valueOf(rep1.getReplaceType())));
            throw e;
        }
    }

    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        List sel = new ArrayList();
        ReplacementsForm rf = (ReplacementsForm) form;
        int selectedSequence = Integer.parseInt((String) request.getParameter("sequence"));
        for (Iterator i = rf.getReplacementItems().iterator(); i.hasNext();) {
            ReplacementItem item = (ReplacementItem) i.next();
            if (item.getReplacement().getSequence() == selectedSequence) {
                sel.add(item.getReplacement());
            }
        }
        if (sel.size() == 0) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("replacements.error.noReplacementsSelectedToDelete"));
            saveErrors(request, errs);
        } else {
            SessionInfo info = this.getSessionInfo(request);
            for (Iterator i = sel.iterator(); i.hasNext();) {
                Replacement r = (Replacement) i.next();
                try {
                    WebForwardDatabaseFactory.getInstance().deleteReplacement(r.getSequence());
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, WebForwardEventConstants.DELETE_REPLACEMENT, null, info, CoreEvent.STATE_SUCCESSFUL)
                                        .addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_SEQUENCE, String.valueOf(r
                                                        .getSequence())));
                    ActionMessages msgs = new ActionMessages();
                    msgs.add(Globals.ERROR_KEY, new ActionMessage("replacements.message.replacementsDeleted", String.valueOf(sel
                                    .size())));
                    saveMessages(request, msgs);
                } catch (Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, WebForwardEventConstants.DELETE_REPLACEMENT, null, info, CoreEvent.STATE_UNSUCCESSFUL)
                                        .addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_SEQUENCE, String.valueOf(r
                                                        .getSequence())));
                }
            }
        }
        //
        ((ReplacementsForm) form).setReferer(CoreUtil.getReferer(request));
        return mapping.findForward("display");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("create");
    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ReplacementsForm rf = (ReplacementsForm) form;
        int selectedSequence = Integer.parseInt((String) request.getParameter("sequence"));
        List sel = new ArrayList();
        for (Iterator i = rf.getReplacementItems().iterator(); i.hasNext();) {
            ReplacementItem item = (ReplacementItem) i.next();
            if (item.getReplacement().getSequence() == selectedSequence) {
                sel.add(item.getReplacement());
            }
        }
        if (sel.size() != 1) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("replacements.error.invalidSelectionToEdit"));
            saveErrors(request, errs);
        } else {
            Replacement replacement = (Replacement) sel.get(0);
            request.setAttribute(ATTR_REPLACEMENT, replacement);
            return mapping.findForward("edit");
        }
        return mapping.findForward("success");
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

}