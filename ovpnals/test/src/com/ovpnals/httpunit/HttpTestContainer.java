
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
			
package com.ovpnals.httpunit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 */
public final class HttpTestContainer {
    private String rootUrl;
    private int port;
    private String defaultUsername;
    private String defaultPassword;
    private final Collection<HttpTestEntry> entries;

    /**
     */
    public HttpTestContainer() {
        entries = new ArrayList<HttpTestEntry>();
    }

    String getRootUrl() {
        return rootUrl;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.rootUrl = url;
    }
    
    /**
     * @return int
     */
    int getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return String
     */
    String getDefaultUsername() {
        return defaultUsername;
    }

    /**
     * @param defaultUsername
     */
    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    /**
     * @return String
     */
    String getDefaultPassword() {
        return defaultPassword;
    }

    /**
     * @param defaultPassword
     */
    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    Collection<HttpTestEntry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    /**
     * @param entry
     */
    public void addEntry(HttpTestEntry entry) {
        entries.add(entry);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("RootUrl='").append(rootUrl).append("' ");
        buffer.append("Port='").append(port).append("' ");
        buffer.append("DefaultUsername='").append(defaultUsername).append("' ");
        buffer.append("DefaultPassword='").append(defaultPassword).append("' ");
        buffer.append("TestCount='").append(entries.size()).append("'");
        return buffer.toString();
    }
}