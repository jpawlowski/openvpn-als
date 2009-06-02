
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
			
package net.openvpn.als.boot;

import java.io.Serializable;
import java.util.Properties;


/**
 * Holds a pair of string values appropriate for name / value pairs.
 * 
 * @author Unknown
 */
public class NameValuePair implements Serializable {
    
    // Private instance variables

    private String name = null;
    private String value = null;

    /**
     * Default constructor.
     *
     */
    public NameValuePair() {
        this (null, null);
    }

    /**
     * Constructor.
     * @param name The name.
     * @param value The value.
     */
    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Take a name / value pair string separating on an equals symbol (=)
     * and construct a new name value pair object. If no separator can be
     * found then the value will be an empty string
     *
     * @param nameValuePair name value pair string separated by =
     */
    public NameValuePair(String nameValuePair) {
        int idx = nameValuePair.indexOf('=');
        if(idx == -1) {
            name = nameValuePair;
            value = "";
        }
        else {
            name = nameValuePair.substring(0, idx);
            value = nameValuePair.substring(idx + 1);
        }
    }

    /**
     * Set the name.
     *
     * @param name The new name
     * @see #getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name.
     *
     * @return String name The name
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value.
     *
     * @param value The new value.
     */
    public void setValue(String value) {
        this.value = value;
    }


    /**
     * Return the current value.
     *
     * @return String value The current value.
     */
    public String getValue() {
        return value;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Get a String representation of this pair.
     * @return A string representation.
     */
    public String toString() {
        return ("name=" + name + ", " + "value=" + value);
    }

    /**
     * Test if the given <i>object</i> is equal to me. <tt>NameValuePair</tt>s
     * are equals if both their <tt>name</tt> and <tt>value</tt> fields are equal.
     * If <tt>object</tt> is <tt>null</tt> this method returns <tt>false</tt>.
     *
     * @param object the {@link Object} to compare to or <tt>null</tt>
     * @return true if the objects are equal.
     */
    public boolean equals(Object object) {
        if (object == null) return false;
        if (this == object) return true;
        if (!(object instanceof NameValuePair)) return false;

        NameValuePair pair = (NameValuePair) object;
        return ((null == name ? null == pair.name : name.equals(pair.name))
              && (null == value ? null == pair.value : value.equals(pair.value)));
    }

    /**
     * hashCode. Returns a hash code for this object such that if <tt>a.{@link
     * #equals equals}(b)</tt> then <tt>a.hashCode() == b.hashCode()</tt>.
     * @return The hash code.
     */
    public int hashCode() {
        return (this.getClass().hashCode()
            ^ (null == name ? 0 : name.hashCode())
            ^ (null == value ? 0 : value.hashCode()));
    }

    /**
     * Add the name value pair to a {@link Properties} object.
     * 
     * @param p properties
     */
    public void add(Properties p) {
        p.setProperty(name, value);
    }
}
