
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
			
package com.adito.properties.forms;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.PropertyProfile;
import com.adito.properties.impl.profile.ProfileProperties;
import com.adito.security.Constants;

/**
 * @author Brett Smith
 */
public class ProfilePropertiesForm extends AbstractPropertiesForm {

    static Log log = LogFactory.getLog(ProfilePropertiesForm.class);

    private List propertyProfiles;
    private int selectedPropertyProfile;
    private String profileScope;

    public ProfilePropertiesForm() {
        super(Arrays.asList(new PropertyClass[] { PropertyClassManager.getInstance().getPropertyClass(ProfileProperties.NAME) } ), true);
    }

    public boolean getEnabled() {
        try {
            PropertyProfile profile = ProfilesFactory.getInstance()
                            .getPropertyProfile(getSelectedPropertyProfile());
            boolean global = profile.getOwnerUsername() == null || profile.getOwnerUsername().equals("");
            return (Constants.SCOPE_PERSONAL.equals(getProfileScope()) && !global) || (Constants.SCOPE_GLOBAL.equals(getProfileScope()) && global);
        } catch (Exception e) {
            log.error("Failed to get property profile details. Disabling form.", e);
            return false;
        }

    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
//        selectedPropertyProfile = -1;
    }

    public void setPropertyProfiles(List propertyProfiles) {
        this.propertyProfiles = propertyProfiles;
    }

    public List getPropertyProfiles() {
        return propertyProfiles;
    }

    public void setSelectedPropertyProfile(int id) {
        selectedPropertyProfile = id;
    }

    public int getSelectedPropertyProfile() {
        return selectedPropertyProfile;
    }

    public boolean getHasProfiles() {
        return propertyProfiles != null && !Constants.SCOPE_SETUP.equals(profileScope);
    }
    public String getProfileScope() {
        return profileScope;
    }

    public void setProfileScope(String profileScope) {
        this.profileScope = profileScope;
    }
}