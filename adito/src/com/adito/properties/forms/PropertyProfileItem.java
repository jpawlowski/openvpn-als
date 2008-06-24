package com.adito.properties.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.adito.core.CoreUtil;
import com.adito.policyframework.OwnedResource;
import com.adito.policyframework.ResourceItem;
import com.adito.properties.PropertyProfile;

public class PropertyProfileItem extends ResourceItem<PropertyProfile> {

	public PropertyProfileItem(PropertyProfile resource, List policies) {
		super(resource, policies);
	}

    public String getSmallIconPath(HttpServletRequest request) {
        if(((OwnedResource)getResource()).getOwnerUsername() != null) {
            return CoreUtil.getThemePath(request.getSession()) + "/images/personal.gif";            
        }
        else {
            return CoreUtil.getThemePath(request.getSession()) + "/images/global.gif";          
        }
    }

    public String getLargeIconAdditionalIcon(HttpServletRequest request) {
		if(((OwnedResource)getResource()).getOwnerUsername() != null) {
	        return CoreUtil.getThemePath(request.getSession()) + "/images/personal.gif";			
		}
		else {
	        return CoreUtil.getThemePath(request.getSession()) + "/images/global.gif";			
		}
	}

	public String getLargeIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/profileLarge.gif";
	}

}
