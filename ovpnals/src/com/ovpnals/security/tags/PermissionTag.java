
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
			
package com.ovpnals.security.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.User;

public class PermissionTag extends TagSupport {
    
    final static Log log = LogFactory.getLog(PermissionTag.class);
    
    boolean required = true;
    int resourceTypeId = -1;
    String permissionList = "";
    boolean all = false;

    public PermissionTag() {
    }

    public int doStartTag() {

        User user = null;
        try {
            user = LogonControllerFactory.getInstance().getUser(pageContext.getSession(), null);
            if (user == null) {
                return required ? SKIP_BODY : EVAL_BODY_INCLUDE;
            } else { 
                
                ResourceType resourceType = null;
                if (resourceTypeId != -1) {
                    if (permissionList.equals("")) {
                        throw new Error("No permissionMask attribute supplied.");
                    }
                    resourceType = PolicyDatabaseFactory.getInstance().getResourceType(resourceTypeId);
                }                
                if (resourceType != null) {                        
                    StringTokenizer t = new StringTokenizer(permissionList, ",");
                    List allowed = new ArrayList();
                    List denied = new ArrayList();
                    while(t.hasMoreTokens()) {
                        String perm = t.nextToken();
                        if(perm.startsWith("!")) {
                            int id = Integer.parseInt(perm.substring(1));
                            Permission permInfo = resourceType.getPermission(id);
                            if(permInfo == null) {
                                throw new Error("No permission with ID of " + id + " in resource type " + resourceType.getResourceTypeId());
                            }
                            denied.add(permInfo);
                        }
                        else {
                            int id = Integer.parseInt(perm);
                            Permission permInfo = resourceType.getPermission(id);
                            if(permInfo == null) {
                                throw new Error("No permission with ID of " + id + " in resource type " + resourceType.getResourceTypeId());
                            }
                            allowed.add(permInfo);
                        }
                    }                    
                    Permission[] allowedPerms = (Permission[])
                        allowed.toArray(new Permission[allowed.size()]);
                    Permission[] deniedPerms = (Permission[])
                    denied.toArray(new Permission[denied.size()]);
                    boolean allowedOk = allowedPerms.length == 0 ? true : PolicyDatabaseFactory.getInstance().isPermitted(
                        resourceType, allowedPerms, user, all); 
                    boolean deniedOk = deniedPerms.length == 0 ? all : !PolicyDatabaseFactory.getInstance().isPermitted(
                        resourceType, deniedPerms, user, all);
                    if(all) {
                        if(allowedOk && deniedOk) {
                            return required ? EVAL_BODY_INCLUDE : SKIP_BODY;
                        }
                        else {
                            return required ? SKIP_BODY : EVAL_BODY_INCLUDE;
                        }                        
                    }
                    else {
                        if(allowedOk || deniedOk) {
                            return required ? EVAL_BODY_INCLUDE : SKIP_BODY;
                        }
                        else {
                            return required ? SKIP_BODY : EVAL_BODY_INCLUDE;
                        }
                    }
                } else {
                    if (!PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(user, true, true, false)) {
                        return SKIP_BODY;
                    } else {
                        return EVAL_BODY_INCLUDE;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to term permissions.", e);
        }
        return SKIP_BODY;
    }
    
    public void setAll(boolean all) {
        this.all = all;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setResourceType(int resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public void setPermissionList(String permissionList) {
        this.permissionList = permissionList;
    }
    
    public void setPermissionList(int permissionList) {
        this.permissionList = String.valueOf(permissionList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release() {
        required = true;
        permissionList = "";
        resourceTypeId = -1;
        all = false;
        super.release();
    }
}