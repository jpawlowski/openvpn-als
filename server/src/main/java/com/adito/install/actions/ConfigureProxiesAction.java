
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
			
package com.adito.install.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.wizard.forms.AbstractWizardForm;

/**
 * Implementation of a
 * {@link AbstractInstallWizardAction} that allows the
 * proxies servers to be used for outgoing connections to be configured.
 */
public class ConfigureProxiesAction extends AbstractInstallWizardAction {

    /**
     * Toggles the use of proxy servers. Simply applies and redisplays.
     *  
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response 
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward toggle(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        applyToSequence(mapping, (AbstractWizardForm) form, request, response);
        return unspecified(mapping, form, request, response);
    }
}
