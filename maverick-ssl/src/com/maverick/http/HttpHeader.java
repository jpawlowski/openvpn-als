
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Encapsulates a list of <i>HTTP header</i>. Each request or response may consist
 * of multiple headers. Each named header may contain 1 or more values.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public abstract class HttpHeader {

    protected final static String WHITE_SPACE = " \t\r"; //$NON-NLS-1$
    
    private Hashtable fields;
    private Vector fieldNames;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpHeader.class);

    // #endif

    protected HttpHeader() {
        fields = new Hashtable();
        fieldNames = new Vector();
    }

	/**
	 * Add a header field. <b>If headers with the same name already exist
	 * they will not be replaced</b>. 
	 * 
	 * @param headerName header name
	 * @param value value
	 */
	public void addHeaderField(String headerName, String value) {
    	if(value == null) {
    		throw new IllegalArgumentException("Null value");
    	}

        if (!fields.containsKey(headerName.toLowerCase())) {
            fields.put(headerName.toLowerCase(), new Field(headerName, new Vector()));
            fieldNames.addElement(headerName);
        }

        Vector v = ((Field) fields.get(headerName.toLowerCase())).headerValues;
        v.addElement(value);		
	}

    /**
     * Get an enumeration of all unique header names. Each header may have
     * multiple values.
     * 
     * @return header field names
     */
    public Enumeration getHeaderFieldNames() {
    	return fieldNames.elements();
    }

    /**
     * Each named header may have multiple values. This method allows you
     * to retrieve a specific index in the list of values for the required
     * header. If not such header exists <code>null</code> will be returned.
     * 
     * @param headerName header name
     * @param num index of header value
     * @return header value
     */
    public String getHeaderField(String headerName, int num) {
        Field f = ((Field) fields.get(headerName.toLowerCase()));
        if (f == null)
            return null;
        else {
            if (f.headerValues.size() > num)
                return (String) (f.headerValues.elementAt(num));
            else
                return null;
        }
    }

    /**
     * Get the first header for a given name.
     * 
     * @param headerName String
     */
    public String getHeaderField(String headerName) {
        return getHeaderField(headerName, 0);
    }

    /**
     * Set the value of a header field. <b>This will replace any current
     * value for this header, regardless of how many times it occurs</b>.
     * 
     * @param headerName header name
     * @param value header value
     */
    public void setHeaderField(String headerName, String value) {
    	if(value == null) {
    		throw new IllegalArgumentException("Null value");
    	}
    	Vector v = new Vector();
        fields.put(headerName.toLowerCase(), new Field(headerName, v));
        if(!fieldNames.contains(headerName)) {
        	fieldNames.addElement(headerName);
        }
        v.addElement(value);
    }

    /**
     * Remove all values for the named header field.
     * 
     * @param header header name
     */
    public void removeFields(String header) {
        if (fields.containsKey(header.toLowerCase())) {
            Field f = (Field)fields.remove(header.toLowerCase());
            fieldNames.removeElement(f.n);
        }
    }
    
    /**
     * Remove <b>ALL</b> header fields
     */
    public void clearHeaderFields() {
    	fieldNames.removeAllElements();
    	fields.clear();
    }

    /**
     * Get the number of values the provider header name has.
     * 
     * @param headerName header name
     * @return number of values
     */
    public int getHeaderFieldCount(String headerName) {
        Field f = (Field) fields.get(headerName.toLowerCase());
        return f.headerValues == null ? 0 : f.headerValues.size();
    }

    /**
     * Get an array of all the header values. <code>null</code> will be
     * returned if no such header exists.
     * 
     * @param headerName
     * @return header fields
     */
    public String[] getHeaderFields(String headerName) {
        Field f = (Field) fields.get(headerName.toLowerCase());
        if (f != null) {
            String[] values = new String[f.headerValues.size()];
            f.headerValues.copyInto(values);
            return values;
        } else {
            return null;
        }
    }

    /**
     * Generate the HTTP protocol text that represents the list of headers
     * this object contains.  
     * 
     * @param startline start line (i.e. HTTP protocol header)
     * @return http 
     */
    public String generateOutput(String startline) {
        String str = startline + "\r\n"; //$NON-NLS-1$
        Enumeration it = getHeaderFieldNames();

        while (it.hasMoreElements()) {
            String fieldName = (String) it.nextElement();
            int count = getHeaderFieldCount(fieldName);
            for (int i = 0; i < count; i++) {
                str += (fieldName + ": " + getHeaderField(fieldName, i) + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        }

        str += "\r\n"; //$NON-NLS-1$
        return str;
    }

    class Field {
        Vector headerValues;
        String n;

        Field(String n, Vector v) {
            this.n = n;
            this.headerValues = v;
        }
    }
}
