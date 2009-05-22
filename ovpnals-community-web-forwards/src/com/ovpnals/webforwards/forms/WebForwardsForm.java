
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
			
package com.ovpnals.webforwards.forms;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.HostService;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.forms.AbstractResourcesForm;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.webforwards.WebForward;
import com.ovpnals.webforwards.WebForwardItem;

/**
 * Form providing the list of web forwards to the jspf.
 */
public class WebForwardsForm extends AbstractResourcesForm {

    private static Log log = LogFactory.getLog(WebForwardsForm.class);
    private String type;

    /**
     * Construtor
     */
    public WebForwardsForm() {
        super("webForward");
    }

    /**
     * <p>
     * Initialise the pager and the items and the ability to sort.
     * 
     * @param webForwards List of Web Forwards to be added.
     * @param hostService host service
     * @param vpnSession vpn session
     * @param sessionInfo session
     */
    public void initialise(List webForwards, HostService hostService, SessionInfo sessionInfo) {
        super.initialize(sessionInfo.getHttpSession(), "name");
        try {
            for (Iterator i = webForwards.iterator(); i.hasNext();) {
                WebForward wf = (WebForward) i.next();
                List policies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(wf, sessionInfo.getUser().getRealm());
                WebForwardItem wfi = new WebForwardItem(wf, hostService, policies, wf.sessionPasswordRequired(sessionInfo));
                wfi.setFavoriteType(getFavoriteType(wf.getResourceId()));
                getModel().addItem(wfi);
            }
            checkSort();
            getPager().rebuild(getFilterText());
        } catch (Throwable t) {
            log.error("Failed to initialise resources form.", t);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}