
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
			
package net.openvpn.als.policyframework.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.input.MultiSelectSelectionModel;
import net.openvpn.als.navigation.FavoriteResourceType;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;

/**
 * Abstract extension of
 * {@link net.openvpn.als.policyframework.forms.AbstractResourceForm} that
 * should be used to edit resources that are capable of supporting favorites
 * (i.e. their {@link net.openvpn.als.policyframework.ResourceType} is an
 * instance of {@link net.openvpn.als.navigation.FavoriteResourceType}.
 */
public abstract class AbstractFavoriteResourceForm extends AbstractResourceForm {
    static Log log = LogFactory.getLog(AbstractFavoriteResourceForm.class);

    // Private instance variables

    private boolean favorite;

    /**
     * Get whether a policy favorite should be created for this resource
     * 
     * @return policy favorite
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Set wheth a policy favorite should be created for this resource
     * 
     * @param favorite policy favorite9+9+89+
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        this.favorite = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.forms.AbstractResourceForm#initialise(javax.servlet.http.HttpServletRequest,
     *      net.openvpn.als.policyframework.Resource, boolean,
     *      net.openvpn.als.input.MultiSelectSelectionModel,
     *      net.openvpn.als.boot.PropertyList, net.openvpn.als.security.User,
     *      boolean)
     */
    public void initialise(HttpServletRequest request, Resource resource, boolean editing, MultiSelectSelectionModel policyModel,
                           PropertyList selectedPolicies, User owner, boolean assignOnly) throws Exception {
        super.initialise(request, resource, editing, policyModel, selectedPolicies, owner, assignOnly);
        this.favorite = ResourceUtil.isResourceGlobalFavorite(resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.forms.AbstractResourceForm#getResourceByName(java.lang.String,
     *      net.openvpn.als.security.SessionInfo)
     */
    public Resource getResourceByName(String name, SessionInfo session) throws Exception {
        return ((FavoriteResourceType) getResource().getResourceType()).getResourceByName(name, session);
    }

}