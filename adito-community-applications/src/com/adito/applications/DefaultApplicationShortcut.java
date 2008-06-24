
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
			
package com.adito.applications;

import java.util.Calendar;
import java.util.Map;

import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.store.ExtensionStore;
import com.adito.policyframework.AbstractResource;

/**
 * Default implementation of an {@link com.adito.applications.ApplicationShortcut}
 */
public class DefaultApplicationShortcut extends AbstractResource implements ApplicationShortcut {
    private static final long serialVersionUID = 6486467846413137897L;
    private final Map<String, String> parameters;
    private final String application;
    private boolean autoStart;

    /**
     * Constructor
     * 
     * @param resourceId resource ID
     * @param resourceName name
     * @param resourceDescription description
     * @param dateCreated date created
     * @param dateAmended date ameneded
     * @param application application extension ID
     * @param parameters shortcut parameters
     */
    public DefaultApplicationShortcut(int realmID, int resourceId, String resourceName, String resourceDescription, Calendar dateCreated,
                    Calendar dateAmended, String application, Map<String,String> parameters, boolean autoStart) {
        super(realmID, ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE, resourceId, resourceName, resourceDescription, dateCreated,
                        dateAmended);
        
        ExtensionDescriptor des = ExtensionStore.getInstance().getExtensionDescriptor(application);
        if(des != null) {
            setLaunchRequirement(des.getExtensionType().getLaunchRequirement());
        }
        this.application = application;
        this.parameters = parameters;
        this.autoStart = autoStart;
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.ApplicationShortcut#getParameters()
     */
    public Map<String,String> getParameters() {
        return parameters;
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.ApplicationShortcut#getApplication()
     */
    public String getApplication() {
        return application;
    }


    public boolean paramsRequirePassword() {
        for (String value : parameters.values()) {
            if (value.contains("${session:password}")) {
                return true;
            }
        }
        return false;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[application='").append(getApplication());
        builder.append("', parameters='").append(getParameters());
        builder.append("', autoStart='").append(isAutoStart()).append("']");
        return builder.toString();
    }
}