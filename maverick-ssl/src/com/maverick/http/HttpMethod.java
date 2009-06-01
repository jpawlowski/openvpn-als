
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
			
package com.maverick.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public abstract class HttpMethod {

    private String name;
    private String uri;    
    private Hashtable parameters = new Hashtable();
    private Vector parameterNames = new Vector(); 

    public HttpMethod(String name, String uri) {
        this.uri = uri;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public String getVersion() {
        return "1.1"; //$NON-NLS-1$
    }

    public HttpResponse execute(HttpRequest request, HttpConnection con) throws IOException {

        // Add default headers
        request.setHeaderField("Host", con.getHostHeaderValue()); //$NON-NLS-1$
        request.setHeaderField("User-Agent", HttpClient.USER_AGENT); //$NON-NLS-1$
        request.performRequest(this, con);
        return new HttpResponse(con);
    }

	/**
	 * Add a parameter. <b>If parameters with the same name already exist
	 * they will not be replaced</b>. 
	 * 
	 * @param name parameter name
	 * @param value parameter value
	 */
	public void addParameter(String name, String value) {
    	if(value == null) {
    		throw new IllegalArgumentException("Null value");
    	}

        if (!parameters.containsKey(name)) {
        	parameters.put(name, new Vector());
        	parameterNames.addElement(name);
        }

        Vector v = (Vector)parameters.get(name);
        v.addElement(value);		
	}

    /**
     * Get an enumeration of all unique parameter names. Each parameter may have
     * multiple values.
     * 
     * @return parameter names
     */
    public Enumeration getParameterNames() {
    	return parameterNames.elements();
    }

    /**
     * Each parameter may have multiple values. This method allows you
     * to retrieve a specific index in the list of values for the required
     * parameter. If not such parameter exists <code>null</code> will be returned.
     * 
     * @param name parameter name
     * @param num index of parameter value
     * @return parameter value
     */
    public String getParameter(String headerName, int num) {
        Vector f = (Vector) parameters.get(headerName);
        if (f == null)
            return null;
        else {
            if (f.size() > num)
                return (String)f.elementAt(num);
            else
                return null;
        }
    }

    /**
     * Get the first parameter for a given name.
     * 
     * @param name parameter name
     */
    public String getParameter(String name) {
        return getParameter(name, 0);
    }

    /**
     * Set the value of a parameter. <b>This will replace any current
     * value for this parameter, regardless of how many times it occurs</b>.
     * 
     * @param name parameter name
     * @param value header value
     */
    public void setParameter(String name, String value) {
    	if(value == null) {
    		throw new IllegalArgumentException("Null value");
    	}
    	Vector v = new Vector();
        parameters.put(name, v);
        if(!parameterNames.contains(name)) {
        	parameterNames.addElement(name);
        }
        v.addElement(value);
    }

    /**
     * Remove all values for the named parameter.
     * 
     * @param name parameter name
     */
    public void removeParameter(String name) {
        if (parameters.containsKey(name)) {
            parameterNames.removeElement(name);
        }
    }
    
    /**
     * Remove <b>ALL</b> parameters
     */
    public void clearParameters() {
    	parameters.clear();
    	parameterNames.removeAllElements();
    }

    /**
     * Get the number of values the provided parameter name has.
     * 
     * @param name parameter name
     * @return number of values
     */
    public int getHeaderFieldCount(String name) {
        Vector f = (Vector) parameters.get(name);
        return f == null ? 0 : f.size();
    }

    /**
     * Get an array of all the parameter values. <code>null</code> will be
     * returned if no such parameter exists.
     * 
     * @param name parameter name
     * @return values
     */
    public String[] getParameterValues(String name) {
        Vector f = (Vector) parameters.get(name);
        if (f != null) {
            String[] values = new String[f.size()];
            f.copyInto(values);
            return values;
        } else {
            return null;
        }
    }

    /**
     * Get a vector of all the parameter values. <code>null</code> will be
     * returned if no such parameter exists.
     * 
     * @param name parameter name
     * @return values
     */
    public Vector getParameterValueList(String name) {
        return (Vector) parameters.get(name);
    }

}
