
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
			
package com.ovpnals.core.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.navigation.FavoriteResourceType;
import com.ovpnals.wizard.AbstractWizardSequence;

/**
 * Extension of {@link com.ovpnals.core.forms.AbstractResourceDetailsWizardForm}
 * that should be used for resources that support <i>Favorites</i>. 
 */
public abstract class AbstractFavoriteResourceDetailsWizardForm extends AbstractResourceDetailsWizardForm {

    /**
     * Wizard attribute for storing whether this resource should be
     * created as a <i>Favorite</i>.
     */
    public static final String ATTR_FAVORITE = "favorite";
    
    // Private instance variables
    
    private boolean favorite;
    
    /**
     * Constructor
     * 
     * @param nextAvailable next page available
     * @param previousAvailable previous page available
     * @param page page 
     * @param focussedField initial focussed field
     * @param autoComplete auto complete enabled
     * @param finishAvailable finish page available
     * @param pageName page name
     * @param resourceBundle message resources bundle bundle
     * @param resourcePrefix messages resources prefix
     * @param stepIndex step in wizard
     * @param resourceTypeForAccessRights resource type
     */
    public AbstractFavoriteResourceDetailsWizardForm(boolean nextAvailable, boolean previousAvailable, String page, String focussedField,
                                             boolean autoComplete, boolean finishAvailable, String pageName, String resourceBundle,
                                             String resourcePrefix, int stepIndex, FavoriteResourceType resourceTypeForAccessRights) {
        super(nextAvailable, previousAvailable, page, focussedField, autoComplete, finishAvailable, pageName, resourceBundle, resourcePrefix, stepIndex, resourceTypeForAccessRights);
    }

    /**
     * Set whether this resource should be configured as <i>Global Favorite</i>
     * or not.
     * 
     * @param favorite favorite
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    
    /**
     * Get whether this resource should be configured as <i>Global Favorite</i>
     * or not.
     * 
     * @return favorite
     */
    public boolean getFavorite() {
        return favorite;
    }


    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        AbstractWizardSequence sequence = this.getWizardSequence(); 
         if (sequence != null){
             if (this.getWizardSequence().getAttribute(ATTR_FAVORITE, "").toString().equals(""))
                 favorite = false;
         }
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_FAVORITE, Boolean.valueOf(favorite));
    }
    
}
