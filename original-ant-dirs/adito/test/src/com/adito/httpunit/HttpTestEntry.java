
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
			
package com.adito.httpunit;

import java.util.ArrayList;
import java.util.Collection;

/**
 */
public final class HttpTestEntry {
    private String name;
    private boolean isAuthenticated;
    private String username;
    private String password;
    private Collection<HttpTestEntryStep> steps;
    
    /**
     */
    public HttpTestEntry() {
        steps = new ArrayList<HttpTestEntryStep>();
    }

    /**
     * @return String
     */
    String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String
     */
    String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return String
     */
    String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return boolean
     */
    boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * @param isAuthenticated
     */
    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }
    
    /**
     * @return Collection<HttpTestEntryStep>
     */
    Collection<HttpTestEntryStep> getSteps() {
        return steps;
    }
    
    /**
     * @param step
     */
    public void addStep(HttpTestEntryStep step) {
        steps.add(step);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Name='").append(name).append("' ");
        buffer.append("Authenticated='").append(isAuthenticated).append("' ");
        buffer.append("Username='").append(username).append("' ");
        buffer.append("Password='").append(password).append("' ");
        buffer.append("Steps='").append(steps.size()).append("' ");
        return buffer.toString();
    }
}