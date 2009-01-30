
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
			
package com.adito.webforwards;

import java.util.List;

import org.junit.BeforeClass;

import com.adito.policyframework.ResourceType;
import com.adito.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

public abstract class AbstractWebForwardTests extends AbstractTestPolicyEnabledResource<WebForward> {

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("adito-agent,adito-community-tunnels,adito-community-web-forwards");
    }

    @Override
    public ResourceType getResourceType() throws Exception {
      return WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE;
    }

    @Override
    public WebForward createResource(WebForward resource) throws Exception {
        return getWebForwardService().createWebForward(resource);
    }
    
    @Override
    public WebForward updateResource(WebForward resource) throws Exception {
        getWebForwardService().updateWebForward(resource);
        return getWebForwardService().getWebForward(resource.getResourceId()); 
    }
    
	@Override
	public WebForward deleteResource(WebForward resource) throws Exception {
		return getWebForwardService().deleteWebForward(resource.getResourceId());
	}

	@Override
	public WebForward getResource(WebForward resource) throws Exception {
		return getWebForwardService().getWebForward(resource.getResourceId());
	}
    
    @Override
    public List<WebForward> getAllResources() throws Exception {
        return getWebForwardService().getWebForwards();
    }

    protected static WebForwardDatabase getWebForwardService() throws Exception {
        return WebForwardDatabaseFactory.getInstance();
    }
}