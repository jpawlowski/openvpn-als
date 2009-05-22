
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
			
package com.ovpnals.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import com.ovpnals.boot.Util;
import com.ovpnals.security.SessionInfo;

/**
 * Superclass of all events that may be fired during the life of an
 * OpenVPN-ALS.
 * <p>
 * All events have the following attributes in common :-
 * <ul>
 * <li>Event code. This is an <code>int</code> and must be unique across the
 * whole of OpenVPN-ALS and all of its plugins.</li>
 * <li>Source. An arbitrary object pointing to the source of the event. </li>
 * <li>Parameter. An arbitrary parameter object appropriate for the type of
 * event</li>
 * <li>Session. The session that caused the event, or <code>null</code> if
 * this is a system event.</li>
 * <li>State. An integer specifying whether the event is the result of a
 * successful operation or a failed one.</li>
 * </ul>
 * <p>
 * All events may also contain a map of arbitrary name / value pair attributes
 * to store any other information that might be of interest to listeners of the
 * type of event being fired. For example, event event with the code
 * {@link com.ovpnals.core.CoreEventConstants#USER_CREATED} would also add
 * an attribute with the key 
 * {@link com.ovpnals.core.CoreAttributeConstants#EVENT_ATTR_PRINCIPAL_ID}
 * and a value of the username being created.
 * <p>
 * All events also have a time attribute that is set when the object is 
 * instantiated.  
 * 
 * @see com.ovpnals.core.CoreListener
 * @see com.ovpnals.core.CoreServlet
 * @see com.ovpnals.core.CoreEventConstants
 * @see com.ovpnals.core.CoreAttributeConstants
 */
public class CoreEvent extends EventObject {

    /**
     * Successful event
     */
    public static final int STATE_SUCCESSFUL = 0;
    
    /**
     * Failed event
     */
    public static final int STATE_UNSUCCESSFUL = 1;

    // Private instance variables
    
    private int id;
    private Object parameter;
    private SessionInfo session;
    private long time;
    private int state;
    private TreeMap eventAttributes;

    /**
     * Constructor.
     *
     * @param source source of event
     * @param id event code
     * @param parameter arbitrary parameter
     * @param session session that caused event or <code>null</code> for system event 
     * @param state state. May be one of {@link #STATE_SUCCESSFUL} or {@link #STATE_UNSUCCESSFUL}.
     */
    public CoreEvent(Object source, int id, Object parameter, SessionInfo session, int state) {
        super(source);
        this.id = id;
        this.parameter = parameter;
        this.session = session;
        this.state = state;
        time = System.currentTimeMillis();
        eventAttributes = new TreeMap();
        if(session!=null)
        	addAttribute(CoreAttributeConstants.EVENT_ATTR_SESSION_ID, session.getUniqueSessionId());
    }

    /**
     * Constructor for {@link #STATE_UNSUCCESSFUL}.
     *
     * @param source source of event
     * @param id event code
     * @param parameter arbitrary parameter
     * @param session session that caused event or <code>null</code> for system event 
     * @param exception exception
     * 
     */
    public CoreEvent(Object source, int id, Object parameter, SessionInfo session, Throwable exception) {
        this(source, id, parameter, session, STATE_UNSUCCESSFUL);
        addAttribute(CoreAttributeConstants.EVENT_ATTR_EXCEPTION_MESSAGE, 
            Util.getExceptionMessageChain(exception));
    }

    /**
     * Constructor for a successful event.
     *
     * @param source source of event
     * @param id event code
     * @param parameter arbitrary parameter
     * @param session session that caused event or <code>null</code> for system event
     */
    public CoreEvent(Object source, int id, Object parameter, SessionInfo session) {
        this(source, id, parameter, session, STATE_SUCCESSFUL);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        /*
         * Set up variables required for the use of SimpleDateFormat.
         */
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());

        /*
         * Set up a StringBuffer to hold the event message.
         */
        StringBuffer buff = new StringBuffer();

        /*
         * Build the event message.
         */
        buff.append("Date : ");
        buff.append(sdf.format(cal.getTime()));
        buff.append(" Time : ");
        buff.append(getTime());
        buff.append(" User : ");
        /*
         * SessionInfo can sometimes be null. If this is the case a default
         * value of "System" is always used.
         */
        if (getSessionInfo() != null) {
            buff.append(session.getUser().getPrincipalName());
        } else {
            buff.append("System");
        }
        buff.append(" Event ID: ");
        buff.append(getId());
        buff.append(" State : ");
        buff.append(getState());
        buff.append(" Source : ");
        if (getSource() != null) {
            buff.append(getSource());
        } else {
            buff.append("No Source Details");
        }

        for (Iterator it = eventAttributes.keySet().iterator(); it.hasNext();) {
            String parameter = (String) it.next();
            String value = (String) eventAttributes.get(parameter);
            buff.append(" Key : ");
            buff.append(parameter);
            buff.append(" Value : ");
            buff.append(value);
        }

        return buff.toString();
    }

    /**
     * Add an attribute to the event
     * 
     * @param key key of attribute
     * @param value value of attribute
     * @return this object to allow event attribute chains
     */
    public CoreEvent addAttribute(String key, String value) {
        eventAttributes.put(key, value);
        return this;
    }

    /**
     * Remove an attribute given its key
     * 
     * @param key key of attribute
     */
    public void removeAttribute(String key) {
        eventAttributes.remove(key);
    }

    /**
     * Get a {@link Set} of all the event attribute keys
     * 
     * @return set of attribute keys
     */
    public Set keySet() {
        return eventAttributes.keySet();
    }

    /**
     * Get the value of an event attribute or return the supplied if no
     * such attribute exists. 
     *  
     * @param key key of event
     * @param defaultValue default value
     * @return value
     */
    public String getAttribute(String key, String defaultValue) {
        String val = (String)eventAttributes.get(key); 
        return val == null ? defaultValue : val;
    }

    /**
     * Get the session that caused this event or <code>null</code> if 
     * this is a system event.
     * 
     * @return session that cause event or <code>null</code> if system event
     */
    public SessionInfo getSessionInfo() {
        return session;
    }

    /**
     * Get the arbitrary parameter object
     * 
     * @return arbitrary parameter object
     */
    public Object getParameter() {
        return parameter;
    }

    /**
     * Get the unique event id
     * 
     * @return unique event id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the time this event object was created
     * 
     * @return time this event object was created
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the event state. May be one of {@link #STATE_SUCCESSFUL}
     * or {@link #STATE_UNSUCCESSFUL}.
     * 
     * @return event state
     */
    public int getState() {
        return state;
    }

    /**
     * Get the number of attributes this event has
     * 
     * @return attribute count
     */
    public int getAttributeCount() {
        return eventAttributes.size();
    }

}
