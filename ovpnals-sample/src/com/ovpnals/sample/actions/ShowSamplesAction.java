
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
			
package com.ovpnals.sample.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.actions.AbstractFavoriteResourcesDispatchAction;
import com.ovpnals.sample.Sample;
import com.ovpnals.sample.SamplePlugin;
import com.ovpnals.sample.forms.SamplesForm;
import com.ovpnals.security.SessionInfo;

/**
 * <p>
 * The actions performed on the list of operations.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class ShowSamplesAction extends AbstractFavoriteResourcesDispatchAction {

    final static Log log = LogFactory.getLog(ShowSamplesAction.class);

    /**
     * Constructor
     */
    public ShowSamplesAction() {
        super(Sample.SAMPLE_RESOURCE_TYPE, Sample.SAMPLE_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        SamplesForm samplesForm = (SamplesForm) form;

        // TODO get the samples and initialise the Form with the Items.

        List samples = getSessionInfo(request).getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? ResourceUtil
                        .filterManageableResources(SamplePlugin.getDatabase().getSamples(), getSessionInfo(request).getUser())
                        : ResourceUtil.getGrantedResource(getSessionInfo(request), getResourceType());
        samplesForm.initialise(samples, getSessionInfo(request), ".name");

        return fwd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}