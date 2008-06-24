
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
			
package com.adito.testcontainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

/**
 */
public final class StrutsExecutionStep {
    private final String requestPath;
    private final Map<String, String> requestParameters;
    private final String tileName;
    private final String forwardPath;
    private final boolean expectSuccess;
    private final Collection<String> messages;
    private final Collection<String> errors;

    /**
     * @param requestPath
     * @param forwardPath
     */
    public StrutsExecutionStep(String requestPath, String forwardPath) {
        this(requestPath, "", forwardPath);
    }
    
    /**
     * @param requestPath
     * @param tileName
     * @param forwardPath
     */
    public StrutsExecutionStep(String requestPath, String tileName, String forwardPath) {
        this(requestPath, tileName, forwardPath, true);
    }
    
    /**
     * @param requestPath
     * @param tileName
     * @param forwardPath
     * @param expectSuccess 
     */
    public StrutsExecutionStep(String requestPath, String tileName, String forwardPath, boolean expectSuccess) {
        this.requestPath = requestPath;
        this.requestParameters = new Hashtable<String, String>();
        this.tileName = tileName;
        this.forwardPath = forwardPath;
        this.expectSuccess = expectSuccess;
        this.messages = new HashSet<String>();
        this.errors = new HashSet<String>();
    }

    /**
     * @return String
     */
    public String getRequestPath() {
        return requestPath;
    }

    /**
     * @param key
     * @param value
     */
    public void addRequestParameter(String key, String value) {
        requestParameters.put(key, value);
    }

    /**
     * @return requestParameters
     */
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    /**
     * @return tileName
     */
    public String getTileName() {
        return tileName;
    }

    /**
     * @return forwardPath
     */
    public String getForwardPath() {
        return forwardPath;
    }

    /**
     * @return expectSuccess
     */
    public boolean isExpectSuccess() {
        return expectSuccess;
    }

    /**
     * @param message
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    /**
     * @return messages
     */
    public String[] getMessages() {
        return messages.toArray(new String[messages.size()]);
    }

    /**
     * @param error
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * @return errors
     */
    public String[] getErrors() {
        return errors.toArray(new String[errors.size()]);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[requestPath='").append(requestPath);
        builder.append("', requestParameters='").append(requestParameters);
        builder.append("', tileName='").append(tileName);
        builder.append("', forwardPath='").append(forwardPath);
        builder.append("', expectSuccess='").append(expectSuccess);
        builder.append("', messages='").append(messages);
        builder.append("', errors='").append(errors).append("']");
        return builder.toString();
    }
}