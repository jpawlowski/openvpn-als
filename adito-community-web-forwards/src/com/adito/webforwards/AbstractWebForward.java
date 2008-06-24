
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

import java.util.Calendar;

import com.adito.policyframework.AbstractResource;

/**
 * 
 */
public abstract class AbstractWebForward extends AbstractResource implements WebForward {

    private int type;
    private String destinationURL;
    private String category;
    private boolean autoStart;

    public AbstractWebForward(int realmID, int id, int type, String destinationURL, String shortName, String description, String category, boolean autoStart,
                              Calendar dateCreated, Calendar dateAmended) {
        super(realmID, WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE, id, shortName, description, dateCreated,
                        dateAmended);
        setLaunchRequirement(LaunchRequirement.REQUIRES_WEB_SESSION);
        this.type = type;
        this.destinationURL = destinationURL;
        this.category = category;
        this.autoStart = autoStart;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDestinationURL() {
   		return destinationURL;
    }

    public void setDestinationURL(String destinationURL) {
   		this.destinationURL = destinationURL;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean paramsRequirePassword() {
        return destinationURL.contains("${session:password}");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[type='").append(getType());
        builder.append("', destinationURL='").append(getDestinationURL());
        builder.append("', category='").append(getCategory());
        builder.append("', autoStart='").append(isAutoStart()).append("']");
        return builder.toString();
    }
}