
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
			
package com.adito.navigation.forms;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import com.adito.core.forms.CoreForm;

/**
 * For used by 'selectPropertyProfile' that allows the user to select his
 * current property profile. The user may also choose to make the profile they
 * select their default the next time they logon.
 */

public class ProfileSelectionForm extends CoreForm {

    static Log log = LogFactory.getLog(ProfileSelectionForm.class);

    // Private instance variables
    private List propertyProfiles;
    private int selectedPropertyProfile;
    private boolean makeDefault;

    /**
     * Get the list of {@link com.adito.properties.PropertyProfile} objects
     * that are available for selection. I.e. those that the current user
     * is allowed to use.
     * 
     * @return list of available property profiles
     */
    public List getPropertyProfiles() {
        return propertyProfiles;
    }

    /**
     * Set the list of {@link com.adito.properties.PropertyProfile} objects
     * that are available for selection. I.e. those that the current user
     * is allowed to use.
     * 
     * @param propertyProfiles list of property profiles
     */
    public void setPropertyProfiles(List propertyProfiles) {
        this.propertyProfiles = propertyProfiles;
    }

    /**
     * Set the ID of the selected profile
     * 
     * @param selectedPropertyProfile selected profile
     */
    public void setSelectedPropertyProfile(int selectedPropertyProfile) {
        this.selectedPropertyProfile = selectedPropertyProfile;
    }

    /**
     * Get the ID of the selected profile
     * 
     * @return ID of selected profile
     */
    public int getSelectedPropertyProfile() {
        return selectedPropertyProfile;
    }

    /**
     * Get whether this profile should be made the default for the user
     * 
     * @return <code>true</code> if the profile should be made the default
     */
    public boolean getMakeDefault() {
        return makeDefault;
    }

    /**
     * Set wthher this profile should be made the default for this user.
     * 
     * @param makeDefault <code>true</code> to make the profile default for the user
     */
    public void setMakeDefault(boolean makeDefault) {
        this.makeDefault = makeDefault;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        propertyProfiles = null;
        selectedPropertyProfile = 0;
    }
}