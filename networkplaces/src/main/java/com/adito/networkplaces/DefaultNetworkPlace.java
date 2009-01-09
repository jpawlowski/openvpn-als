
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
			
package com.adito.networkplaces;

import java.util.Calendar;

import com.adito.boot.Util;
import com.adito.policyframework.AbstractResource;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;
import com.adito.vfs.utils.URI.MalformedURIException;

public class DefaultNetworkPlace extends AbstractResource implements NetworkPlace {
    // Private instance variables

    private String host;
    private String uri;
    private int port;
    private String username;
    private String password;
    private int type;
    private boolean readOnly;
    private boolean showHidden;
    private boolean allowResursive;
    private boolean noDelete;
    private String scheme;
    private boolean autoStart;

    public DefaultNetworkPlace(int realmID, int uniqueId, String scheme, String shortName, String description, String uri,
                               int type, boolean readOnly, boolean allowResursive, boolean noDelete, boolean showHidden,
                               boolean autoStart, Calendar dateCreated, Calendar dateAmended) throws MalformedURIException {
        super(realmID, NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, uniqueId, shortName, description, dateCreated, dateAmended);
        setLaunchRequirement(LaunchRequirement.REQUIRES_WEB_SESSION);
        this.host = "";
        this.uri = uri;
        this.port = 0;
        this.username = "";
        this.password = "";
        this.scheme = scheme;
        this.type = type;
        this.readOnly = readOnly;
        this.showHidden = showHidden;
        this.allowResursive = allowResursive;
        this.noDelete = noDelete;
        this.autoStart = autoStart;
    }

    public DefaultNetworkPlace(int realmID, int uniqueId, String scheme, String shortName, String description, String host,
                               String uri, int port, String username, String password, int type, boolean readOnly,
                               boolean allowResursive, boolean noDelete, boolean showHidden, boolean autoStart,
                               Calendar dateCreated, Calendar dateAmended) throws MalformedURIException {
        super(realmID, NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, uniqueId, shortName, description, dateCreated, dateAmended);
        setLaunchRequirement(LaunchRequirement.REQUIRES_WEB_SESSION);
        this.host = host;
        this.uri = uri;
        this.port = port;
        this.username = username;
        this.password = password;
        this.scheme = scheme;
        this.type = type;
        this.readOnly = readOnly;
        this.showHidden = showHidden;
        this.allowResursive = allowResursive;
        this.noDelete = noDelete;
        this.autoStart = autoStart;
    }

    public void replaceParameters(SessionInfo session) {

    }

    public String getPath() {
        return uri;
    }

    public void setPath(String uri) {
        this.uri = uri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAllowRecursive() {
        return allowResursive;
    }

    public void setAllowResursive(boolean allowResursive) {
        this.allowResursive = allowResursive;
    }

    public boolean isNoDelete() {
        return noDelete;
    }

    public void setNoDelete(boolean noDelete) {
        this.noDelete = noDelete;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean paramsRequirePassword() {
        return uri.contains("${session:password}");
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public String getLaunchUri(LaunchSession launchSession) {
        return "fileSystem.do?actionTarget=launch&"
                        + LaunchSession.LAUNCH_ID
                        + "="
                        + launchSession.getId()
                        + "&path="
                        + Util.urlEncode(((NetworkPlace) launchSession.getResource()).getScheme() + "/"
                                        + launchSession.getResource().getResourceName());
    }
    
        @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[host='").append(getHost());
        builder.append("', uri='").append(getPath());
        builder.append("', port='").append(getPort());
        builder.append("', username='").append(getUsername());
        builder.append("', password='********");
        builder.append("', type='").append(getType());
        builder.append("', readOnly='").append(isReadOnly());
        builder.append("', showHidden='").append(isShowHidden());
        builder.append("', allowResursive='").append(isAllowRecursive());
        builder.append("', noDelete='").append(isNoDelete());
        builder.append("', scheme='").append(getScheme());
        builder.append("', autoStart='").append(isAutoStart()).append("']");
        return builder.toString();
    }
}