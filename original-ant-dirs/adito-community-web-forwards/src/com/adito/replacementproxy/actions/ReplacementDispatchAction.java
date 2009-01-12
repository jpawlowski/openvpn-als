
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
			
package com.adito.replacementproxy.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.replacementproxy.DefaultReplacement;
import com.adito.replacementproxy.Replacement;
import com.adito.replacementproxy.forms.ReplacementForm;
import com.adito.security.SessionInfo;
import com.adito.webforwards.WebForwardEventConstants;
import com.adito.webforwards.WebForwardDatabaseFactory;

public class ReplacementDispatchAction
    extends AuthenticatedDispatchAction {
  public ReplacementDispatchAction() {
      super(PolicyConstants.REPLACEMENTS_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
  }

  public ActionForward create(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response) throws
      Exception {
    ActionMessages mesgs = new ActionMessages();    
    mesgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.message.requiredFieldIndicator"));
    saveMessages(request, mesgs);
    ( (ReplacementForm) form).initialize(new DefaultReplacement("", Replacement.REPLACEMENT_TYPE_RECEIVED_CONTENT, -1, "", "", ""), false);
    ( (ReplacementForm) form).setReferer(CoreUtil.getReferer(request));
    return mapping.findForward("display");
  }

  public ActionForward edit(ActionMapping mapping,
                            ActionForm form,
                            HttpServletRequest request,
                            HttpServletResponse response) throws
      Exception {    
    Replacement replacement = (Replacement)request.getAttribute(ReplacementsDispatchAction.ATTR_REPLACEMENT);    
    ( (ReplacementForm) form).initialize(replacement, true);
    ( (ReplacementForm) form).setReferer(CoreUtil.getReferer(request));
    CoreUtil.addRequiredFieldMessage(this, request);
    return mapping.findForward("display");
  }

  public ActionForward commit(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
    ReplacementForm rf = (ReplacementForm)form;
    SessionInfo info = this.getSessionInfo(request);
    
    if (null == rf.getReplacement().getMatchPattern() ||"".equals(rf.getReplacement().getMatchPattern())) {
        ActionMessages mesgs = new ActionMessages();    
        mesgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.error.noMatchPattern"));
        saveErrors(request, mesgs);
        return mapping.findForward("display");
    }
    if(rf.isEditing()) {
    	try {
            WebForwardDatabaseFactory.getInstance().updateReplacement(rf.getReplacement());
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, WebForwardEventConstants.UPDATE_REPLACEMENT, null, info, CoreEvent.STATE_SUCCESSFUL)
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, rf.getReplaceType())
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_PATTERN, rf.getReplacement().getReplacePattern()));
    	} catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, WebForwardEventConstants.UPDATE_REPLACEMENT, null, info, CoreEvent.STATE_UNSUCCESSFUL)
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, rf.getReplaceType())
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_PATTERN, rf.getReplacement().getReplacePattern()));
    		throw e;
    	}
    }
    else {
    	try {
            WebForwardDatabaseFactory.getInstance().createReplacement(rf.getReplacement());
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, WebForwardEventConstants.CREATE_REPLACEMENT, null, info, CoreEvent.STATE_SUCCESSFUL)
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, rf.getReplaceType())
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_PATTERN, rf.getReplacement().getReplacePattern()));
    	} catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, WebForwardEventConstants.CREATE_REPLACEMENT, null, info, CoreEvent.STATE_UNSUCCESSFUL)
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_TYPE, rf.getReplaceType())
            		.addAttribute(WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_PATTERN, rf.getReplacement().getReplacePattern()));
    		throw e;
    	}
    }
    ActionMessages msgs = new ActionMessages();
    msgs.add(Globals.MESSAGE_KEY, new ActionMessage("createReplacement.message.saved"));
    saveMessages(request, msgs);
    return mapping.findForward("success");
  }

  public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
      return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
  }

}