
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.httpunit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public final class HttpTestEntryStep {
    static final String POST = "post";
    static final String GET = "get";
    
    private String name;
    private boolean isPost;
    private String url;
    private int expectedCode = 200;
    private String redirectUrl;
    private final Map<String, String> parameters;
    private final Set<String> errors;
    private final Set<String> messages;
    
    /**
     */
    public HttpTestEntryStep() {
        parameters = new HashMap<String, String>();
        messages = new HashSet<String>();
        errors = new HashSet<String>();
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
     * @return boolean
     */
    boolean isPost() {
        return isPost;
    }

    /**
     * @param method
     */
    public void setMethod(String method) {
        isPost = method == null || method.equals(GET) ? false : true;
    }

    /**
     * @return String
     */
    String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return int
     */
    int getExpectedCode() {
        return expectedCode;
    }

    /**
     * @param expectedCode
     */
    public void setExpectedCode(int expectedCode) {
        this.expectedCode = expectedCode;
    }

    /**
     * @return String
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * @param redirectUrl
     */
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * @return Map<String, String>
     */
    Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    /**
     * @param parameter
     */
    public void addParameter(Parameter parameter) {
        parameters.put(parameter.getKey(), parameter.getValue());
    }
    /**
     * @return Collection<String>
     */
    Collection<String> getErrors() {
        return Collections.unmodifiableSet(errors);
    }

    /**
     * @param message
     */
    public void addError(Value message) {
        String realMessage = message.getValue();
        if (realMessage != null)
            errors.add(realMessage);
    }

    /**
     * @return Collection<String>
     */
    Collection<String> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    /**
     * @param message
     */
    public void addMessage(Value message) {
        String realMessage = message.getValue();
        if (realMessage != null)
            messages.add(realMessage);
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Name='").append(name).append("' ");
        buffer.append("IsPort='").append(isPost).append("' ");
        buffer.append("Url='").append(url).append("' ");
        buffer.append("ExpectedCode='").append(expectedCode).append("' ");
        buffer.append("RedirectUrl='").append(redirectUrl).append("' ");
        buffer.append("Parameters='").append(parameters).append("' ");
        buffer.append("Errors='").append(errors).append("' ");
        buffer.append("Messages='").append(messages).append("'");
        return buffer.toString();
    }
    
    /**
     */
    public static final class Value {

        private String value;
        
        /**
         * @return String
         */
        private String getValue() {
            return value;
        }
        
        /**
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    /**
     */
    public static final class Parameter {
        private String key;
        private String value;
        
        private String getKey() {
            return key;
        }
        
        /**
         * @param key
         */
        public void setKey(String key) {
            this.key = key;
        }
        
        /**
         * @return String
         */
        private String getValue() {
            return value;
        }
        
        /**
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }       
    }
}