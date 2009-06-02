
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
			
package net.openvpn.als.extensions;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.boot.VersionInfo;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.extensions.ExtensionBundle.ExtensionBundleStatus;
import net.openvpn.als.table.TableItem;


public class ExtensionBundleItem implements TableItem {
    private ExtensionBundle bundle;
    private String selected;
    private String subFormName;
    
    public ExtensionBundleItem(ExtensionBundle bundle, boolean selected, String subFormName) {
        this.bundle = bundle;
        this.selected = String.valueOf(selected);
        this.subFormName = subFormName;
    }

    /**
     * @return Returns the selected.
     */
    public String getSelected() {
        return selected;
    }

    /**
     * @param selected The selected to set.
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }
    
    /**
     * Get the changes for this extension bundle. 
     * @return
     */
    public String getChanges() {
    	return bundle.getChanges();
    }
    
    public VersionInfo.Version getVersion() {
    	return bundle.isUpdateable() ? bundle.getUpdateVersion() : bundle.getVersion();
    }

    /**
     * @return Returns the bundle.
     */
    public ExtensionBundle getBundle() {
        return bundle;
    }
    
    public String getBundleStatusName() {
    	if(bundle.getError() != null || bundle.getStatus().getName().equals(ExtensionBundleStatus.ERROR.getName())) {
    		return bundle.getStatus().getName();
    	}
    	else {
    		return bundle.getType() != ExtensionBundle.TYPE_INSTALLABLE && bundle.getType() != ExtensionBundle.TYPE_CONFIGUREABLE ? bundle.getStatus().getName() : "notinstalled";
    	}
    }

    public Object getColumnValue(int col) {
        switch(col) {
            case 0:
                return getBundleStatusName();
            case 1:
                return bundle.getName();
            default:
                return "";
        }
    }

    public String getSubFormName() {
        return subFormName;
    }

    public void setSubFormName(String subFormName) {
        this.subFormName = subFormName;
    }
    
    public String getSmallIconPath(HttpServletRequest request) {
        if (bundle.getType() == ExtensionBundle.TYPE_PENDING_STATE_CHANGE){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/pending.gif";
        }
        else if (getBundleStatusName().equals("activated")){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/start.gif";
        }
        else if (getBundleStatusName().equals("enabled")){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/stop.gif";
        }
        else if ((getBundleStatusName().equals("disabled") || getBundleStatusName().equals("systemDisabled"))){
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/disabled.gif";
        }
        else if (getBundleStatusName().equals("error")){
            return CoreUtil.getThemePath(request.getSession()) + "/images/error.gif";
        }
        else{
            return CoreUtil.getThemePath(request.getSession()) + "/images/actions/extension.gif";
        }
    }

}
