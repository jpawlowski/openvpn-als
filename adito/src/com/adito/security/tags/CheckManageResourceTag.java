
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
			
package com.adito.security.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.TagUtils;

import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Permission;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceUtil;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

public class CheckManageResourceTag extends TagSupport {
    
    final static Log log = LogFactory.getLog(CheckManageResourceTag.class);
    
    boolean required = true;
    Resource resource;
    String resourceName;
    String resourceProperty;
    String resourceScope;
    Permission[] permissions;

    public CheckManageResourceTag() {
    }

    public int doStartTag() {

        SessionInfo session = null;
        try {
            session = LogonControllerFactory.getInstance().getSessionInfo(pageContext.getSession());
            if (session== null) {
                return required ? SKIP_BODY : EVAL_BODY_INCLUDE;
            } else {
                if(resource == null) {
                    if(resourceName == null) {
                        throw new Exception("Must supply either a resource object or a bean name / property that contains the resource.");
                    }
                    resource = (Resource)TagUtils.getInstance().lookup(pageContext, resourceName, resourceProperty, resourceScope);
                    if(resource == null) {
                        throw new Exception("No resource under bean name " + resourceName + "/" + resourceProperty + "/" + resourceScope);
                    }
                }
                boolean ok = false;
                try {
                    ResourceUtil.checkResourceManagementRights(resource, session, permissions);
                    ok = true;
                }
                catch(NoPermissionException npe) {
                }
                return required ? ( ok ? EVAL_BODY_INCLUDE : SKIP_BODY ) : ( ok ? SKIP_BODY : EVAL_BODY_INCLUDE );
            }
        } catch (Exception e) {
            log.error("Failed to test manageabilitiy of resource.", e);
        }
        return SKIP_BODY;
    }
    
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    
    public void setName(String name) {
        this.resourceName = name;
    }
    
    public void setScope(String scope) {
        this.resourceScope = scope;
    }
    
    public void setProperty(String property) {
        this.resourceProperty = property;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public void setPermission(Permission permission) {
        this.permissions = new Permission[] { permission };
    }
    
    public void setPermissions(Permission[] permissions) {
        this.permissions = permissions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release() {
        required = true;
        permissions = null;
        resourceName = null;
        resourceProperty = null;
        resource = null;
        super.release();
    }
}