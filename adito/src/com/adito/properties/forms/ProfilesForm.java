
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.adito.policyframework.ResourceItemModel;
import com.adito.policyframework.forms.AbstractResourcesForm;
import com.adito.properties.PropertyProfile;

public class ProfilesForm extends AbstractResourcesForm<PropertyProfileItem> {
    
    private String profileScope;
    private String selectedProfile;
    
    public ProfilesForm() {        
        super("profiles");
    }

    public void setProfileScope(String profileScope) {
        this.profileScope = profileScope;        
    }
    
    public String getProfileScope() {
        return profileScope;
    }
    
    public void initialize(List globalResources, List personalResources, HttpSession session, int selectedProfile) {
        super.initialize(globalResources, PropertyProfile.class, PropertyProfileItem.class, session, "name");
        if(personalResources != null) {
            for(Iterator i = personalResources.iterator(); i.hasNext(); ) {
                PropertyProfile p = (PropertyProfile)i.next();
                if(p.getOwnerUsername() != null){
                    getModel().addItem(new PropertyProfileItem(p, new ArrayList()));
                }
            }
        }
        this.selectedProfile = String.valueOf(selectedProfile);
        checkSort();
        getPager().rebuild(getFilterText());
    }

	public String getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(String selectedProfile) {
		this.selectedProfile = selectedProfile;
	}
	
	class PersonalProfilesModel extends ResourceItemModel {

		public PersonalProfilesModel() {
			super("personalProfiles");
		}
		
	}
}
