
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
			
package com.adito.core;

import org.apache.struts.action.ActionMessage;

/**
 * An extension of the standard struts
 * {@link org.apache.struts.action.ActionMessage} that allows a resource bundle
 * ID to be supplied at the point of creating the message instead of inside the
 * JSP page.
 * <p>
 * In the JSP you <i>must</i> use the tag <b>adito:messages</b>, using
 * the standard struts tag will result in an attemp to get the resources from
 * the bundle specified in the tag (which may or may not work).
 * <p>
 * This is useful for generic message that may occur on any page at any time.
 * @see com.adito.core.tags.BundleMessagesTag
 */
public class BundleActionMessage extends ActionMessage {

    private static final long serialVersionUID = -3827880344627981596L;

    // Private instance variables

    private String bundle;

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     */
    public BundleActionMessage(String bundle, String key) {
        super(key);
        init(bundle);
    }

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     * @param value0 first argument value
     */
    public BundleActionMessage(String bundle, String key, Object value0) {
        super(key, value0);
        init(bundle);
    }

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     * @param value0 first argument value
     * @param value1 argument 0 value
     */
    public BundleActionMessage(String bundle, String key, Object value0, Object value1) {
        super(key, value0, value1);
        init(bundle);
    }

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     * @param value0 first argument value
     * @param value1 argument 0 value
     * @param value2 argument 0 value
     */
    public BundleActionMessage(String bundle, String key, Object value0, Object value1, Object value2) {
        super(key, value0, value1, value2);
        init(bundle);
    }

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     * @param value0 argument 0 value
     * @param value1 argument 0 value
     * @param value2 argument 0 value
     * @param value3 argument 0 value
     */
    public BundleActionMessage(String bundle, String key, Object value0, Object value1, Object value2, Object value3) {
        super(key, value0, value1, value2, value3);
        init(bundle);
    }

    /**
     * Constructor
     * 
     * @param bundle message bundle id
     * @param key message key
     * @param values all argument value
     */
    public BundleActionMessage(String bundle, String key, Object[] values) {
        super(key, values);
        init(bundle);
    }

    protected void init(String bundle) {
        this.bundle = bundle;
    }

    /**
     * Get the value of argument 0
     * 
     * @return value of argument 0
     */
    public String getArg0() {
        return values != null && values.length > 0 ? String.valueOf(values[0]) : null;
    }

    /**
     * Get the value of argument 1
     * 
     * @return value of argument 1
     */
    public String getArg1() {
        return values != null && values.length > 1 ? String.valueOf(values[1]) : null;
    }

    /**
     * Get the value of argument 2
     * 
     * @return value of argument 2
     */
    public String getArg2() {
        return values != null && values.length > 2 ? String.valueOf(values[2]) : null;
    }

    /**
     * Get the value of argument 3
     * 
     * @return value of argument 3
     */
    public String getArg3() {
        return values != null && values.length > 3 ? String.valueOf(values[3]) : null;
    }

    /**
     * Get the message bundle id where this message is displayed.
     * 
     * @return message bundle id
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Set the message bundle id where this message is displayed.
     * 
     * @param bundle message bundle id
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    /**
     * Set arguent 0.
     * 
     * @param arg0 argument 0
     */
    public void setArg0(Object arg0) {
        if (values == null || values.length == 0) {
            values = new Object[1];
        }
        values[0] = arg0;
    }

    /**
     * Set arguent 1.
     * 
     * @param arg1 argument 1
     */
    public void setArg1(Object arg1) {
        if (values == null || values.length < 2) {
            values = new Object[] { values == null ? null : values[0], arg1 };
        } else {
            values[1] = arg1;
        }
    }

    /**
     * Set arguent 2.
     * 
     * @param arg2 argument 2
     */
    public void setArg2(Object arg2) {
        if (values == null || values.length < 3) {
            values = new Object[] { values == null ? null : values[0], values == null ? null : values[1], arg2 };
        } else {
            values[2] = arg2;
        }
    }

    /**
     * Set arguent 3.
     * 
     * @param arg3 argument 3
     */
    public void setArg3(Object arg3) {
        if (values == null || values.length < 4) {
            values = new Object[] { values == null ? null : values[0],
                values == null ? null : values[1],
                values == null ? null : values[2],
                arg3 };
        } else {
            values[3] = arg3;
        }
    }
}
