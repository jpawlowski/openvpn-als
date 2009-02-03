
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
			
package com.adito.policyframework.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreUtil;
import com.adito.core.forms.CoreForm;
import com.adito.security.Constants;
import com.adito.wizard.AbstractWizardSequence;

public class ConfigurePoliciesDispatchAction extends PoliciesDispatchAction {

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(Constants.WIZARD_SEQUENCE,
                        request.getSession().getAttribute(Constants.SUSPENDED_WIZARD_SEQUENCE));
        request.getSession().removeAttribute(Constants.SUSPENDED_WIZARD_SEQUENCE);
        return new ActionForward(((CoreForm)form).getReferer(), true);
    }
    
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        // Store the current wizard flow so we can return to it when complete
        AbstractWizardSequence seq = (AbstractWizardSequence)request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
        if(seq != null) {
            // get referer from parameter
            ((CoreForm)form).setReferer(CoreUtil.getRequestReferer(request));
            request.getSession().removeAttribute(Constants.WIZARD_SEQUENCE);
            request.getSession().setAttribute(Constants.SUSPENDED_WIZARD_SEQUENCE, seq);
        }        
        return super.unspecified(mapping, form, request, response);
    }
}