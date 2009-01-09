
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
			
package com.adito.networkplaces.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.NetworkPlaceDatabaseFactory;
import com.adito.networkplaces.NetworkPlaceItem;
import com.adito.networkplaces.NetworkPlaceUtil;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.util.TicketGenerator;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSProviderManager;
import com.adito.vfs.VFSStore;
import com.adito.vfs.utils.URI;
import com.adito.vfs.webdav.DAVProcessor;

public class ShowNetworkPlacesForm extends AbstractResourcesForm {

    List networkPlaceItems;

    static Log log = LogFactory.getLog(ShowNetworkPlacesForm.class);

    public ShowNetworkPlacesForm() {
        super("networkPlace");
    }

    /**
     * @param processor
     * @param session
     * @throws Exception
     */
    public void initialize(DAVProcessor processor, SessionInfo session) throws Exception {
        super.initialize(session.getHttpSession(), "name");
        if (session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
            List networkPlaceItems = NetworkPlaceUtil.refreshNetworkMounts(processor.getRepository(), session);
            for (Iterator i = networkPlaceItems.iterator(); i.hasNext();) {
                NetworkPlaceItem npi = (NetworkPlaceItem) i.next();
                npi.setFavoriteType(getFavoriteType(npi.getResource().getResourceId()));
                getModel().addItem(npi);
            }
        } else if (session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
            networkPlaceItems = new ArrayList();
            // Now create the items
            List resources = NetworkPlaceDatabaseFactory.getInstance().getNetworkPlaces(session.getUser().getRealm().getRealmID());
            for (Iterator i = resources.iterator(); i.hasNext();) {
                NetworkPlace np = (NetworkPlace) i.next();
                VFSProvider provider = VFSProviderManager.getInstance().getProvider(np.getScheme());
                if (provider == null) {
                	try {
	                	if(np.getScheme().equals("")) {
	            			URI uri = NetworkPlaceUtil.createURIForPath(np.getPath());
	            			provider = VFSProviderManager.getInstance().getProvider(uri.getScheme());
	                	}
	                	if(provider == null) {
	                		log.warn("Provider that handles '" + np.getScheme() + "' cannot be found.");
	                	}
                	} catch(Exception ex) {
                		log.error("Could not get provider for network place " + np.getPath(), ex);
                		continue;
                	}
                } 

                if(provider != null) {
	            	// Create a store so we can get the mount path
	            	VFSStore store = processor.getRepository().getStore(provider.getScheme());
	            	if(store == null) {
	            		log.warn("No store for " + provider.getScheme());
	            	}
	            	else {
		                NetworkPlaceItem npi = new NetworkPlaceItem(np, store.getMountPath(np.getResourceName()), PolicyDatabaseFactory.getInstance()
		                                .getPoliciesAttachedToResource(np, session.getUser().getRealm()), np
		                                .sessionPasswordRequired(session));
		                npi.setFavoriteType(getFavoriteType(np.getResourceId()));
		                getModel().addItem(npi);
	            	}
                }
            }
        }
        checkSort();
        getPager().rebuild(getFilterText());

        /*
         * Store a ticket for use with launching a web folder without
         * authenticating. This ticket will only be available until it is used
         */
        if (session.getHttpSession().getAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET) == null) {
            session.getHttpSession().setAttribute(Constants.WEB_FOLDER_LAUNCH_TICKET,
                TicketGenerator.getInstance().generateUniqueTicket("W", 6));
        }
    }
}