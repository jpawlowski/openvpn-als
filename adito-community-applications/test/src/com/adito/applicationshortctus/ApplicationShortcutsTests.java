
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
			
package com.adito.applicationshortctus;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.applications.ApplicationShortcut;
import com.adito.applications.ApplicationShortcutDatabase;
import com.adito.applications.ApplicationShortcutDatabaseFactory;
import com.adito.applications.ApplicationsPlugin;
import com.adito.applications.DefaultApplicationShortcut;
import com.adito.policyframework.ResourceType;
import com.adito.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

public class ApplicationShortcutsTests extends AbstractTestPolicyEnabledResource<ApplicationShortcut> {

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("adito-agent,adito-community-applications,adito-community-tunnels");
    }

    @Override
    public ApplicationShortcut getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultApplicationShortcut(getDefaultRealm().getRealmID(), -1, "", "", calendar, calendar, "", Collections.<String,String>emptyMap(), false);
    }

    @Override
    public ApplicationShortcut getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultApplicationShortcut(getDefaultRealm().getRealmID(), -1, "RDP Name", "This is a RDP Description", calendar, calendar, "RDP", Collections.<String,String>emptyMap(), false);
    }

    @Override
    public ApplicationShortcut getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultApplicationShortcut(getDefaultRealm().getRealmID(), -1, null, null, calendar, calendar, null, Collections.<String,String>emptyMap(), false);
    }

    @Override
    public ApplicationShortcut createResource(ApplicationShortcut resource) throws Exception {
        return getApplicationShortcutService().getShortcut(
            getApplicationShortcutService().createApplicationShortcut(resource.getApplication(), resource.getResourceName(),
                resource.getResourceDescription(), resource.getParameters(), resource.isAutoStart(), resource.getRealmID()));
    }

    @Override
    public ApplicationShortcut updateResource(ApplicationShortcut resource) throws Exception {
        getApplicationShortcutService().updateApplicationShortcut(resource.getResourceId(), resource.getResourceName(),
            resource.getResourceDescription(), resource.getParameters(), resource.isAutoStart());
        return getApplicationShortcutService().getShortcut(resource.getResourceId());
    }

    @Override
    public List<ApplicationShortcut> getAllResources() throws Exception {
        return getApplicationShortcutService().getShortcuts();
    }

    @Override
    public ResourceType getResourceType() throws Exception {
        return ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE;
    }

    @Override
    public ApplicationShortcut deleteResource(ApplicationShortcut resource) throws Exception {
        ApplicationShortcut shortcut = ApplicationShortcutDatabaseFactory.getInstance().getShortcut(resource.getResourceId());
        getApplicationShortcutService().deleteShortcut(shortcut.getResourceId());
        return shortcut;
    }

    @Override
    public ApplicationShortcut getResource(ApplicationShortcut resource) throws Exception {
        return getApplicationShortcutService().getShortcut(resource.getResourceId());
    }

    protected static ApplicationShortcutDatabase getApplicationShortcutService() throws Exception {
        return ApplicationShortcutDatabaseFactory.getInstance();
    }

    @Override
    public void createNullResource() throws Exception {
    }

    @Test 
    public void mustHaveTests() {
    }
}