
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
			
package com.ovpnals.security;

/**
 * Encapsulates a single <i>IP Restriction</i>. 
 * <p>
 * IP restrictions are used to test if the IP address of of any incoming
 * network request (i.e. load a page, connecting agent, connecting
 * WebDav client) is allowed access.
 * <p>
 * Each restrictions consists of an <i>Address Pattern</i> that may
 * either be :-
 * <ul>
 * <li>A fully qualified IPV4 address. E.g. 192.168.1.10 would only match when
 * this exact IP address attempts access.</li>
 * <li>A wildcard address. E.g. 192.168.1.* would match any host from the
 * 192.168.1 subnet.</li>
 * <ul>
 * <p>
 * Ip restrictions may either be <i>Allow</i> restrictions or <i>Deny</i>
 * restrictions. 
 * <p>
 * Ip restrictions have a <i>Priority</i> which determines the order in
 * which the are processed.
 */
public class IpRestriction implements Comparable<IpRestriction> {

    /**
     * The address pattern for this restriction <i>allows</i> any addresses
     * that <i>exactly matches</i>.
     */
    public final static int ALLOWED = 1;
    
    /**
     * The address pattern for this restriction <i>allows</i> any addresses
     * that match the <i>wildcard</i>
     */
    public final static int ALLOWED_WILDCARD = 2;
    
    /**
     * The address pattern for this restriction <i>denies</i> any addresses
     * that <i>exactly matches</i>.
     */
    public final static int DENIED = 3;
    
    /**
     * The address pattern for this restriction <i>denies</i> any addresses
     * that match the <i>wildcard</i>
     */
    public final static int DENIED_WILDCARD = 4;
    
    //  Private instance variables

    private final int id;
    private String addressPattern;
    private int type;
    private int priority;

    /**
     * Constructor.
     * 
     * @param isAllow address is an allow pattern
     */
    public IpRestriction(boolean isAllow) {
        this("", isAllow);
    }

    /**
     * Constructor.
     * 
     * @param addressPattern address pattern
     * @param isAllow address is an allow pattern
     */
    public IpRestriction(String addressPattern, boolean isAllow) {
        this(addressPattern, isAllow, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param addressPattern address pattern
     * @param isAllow address is an allow pattern
     * @param priority priority of restriction
     */
    public IpRestriction(String addressPattern, boolean isAllow, int priority) {
        this(-1, addressPattern, getType(addressPattern, isAllow), 0);
    }
    
    /**
     * Constructor.
     * 
     * @param id id
     * @param addressPattern address pattern
     * @param type type
     * @param priority priority of restriction
     */
    public IpRestriction(int id, String addressPattern, int type, int priority) {
        this.addressPattern = addressPattern;
        this.type = type;
        this.id = id;
        this.priority = priority;
    }

    /**
     * Get the Id of this restriction
     * 
     * @return restriction Id
     */
    public int getID() {
        return id;
    }

    /**
     * Get the address pattern of this restriction. See class description
     * for details of what is allowed as an address.
     * 
     * @return address
     */
    public String getAddress() {
        return addressPattern;
    }

    /**
     * Set the address pattern of this restriction. See class description
     * for details of what is allowed as an address.
     * 
     * @param addressPattern address pattern
     */
    public void setAddress(String addressPattern) {
        this.addressPattern = addressPattern;
    }
    
    /**
     * Get if this is an <i>allowed</i> address. This will be true
     * if the type is {@link #ALLOWED} or {@link #ALLOWED_WILDCARD}.
     * 
     * @return true if the IpRestriction is an allow
     */
    public boolean getAllowed() {
        return ALLOWED == type || ALLOWED_WILDCARD == type;
    }

    /**
     * Get if this is an <i>denied</i> address. This will be true
     * if the type is {@link #DENIED} or {@link #DENIED_WILDCARD}.
     * 
     * @return true if the IpRestriction is a deny
     */
    public boolean getDenied() {
        return DENIED == type || DENIED_WILDCARD == type;
    }
    
    /**
     * Get if this restriction contains a wildcard address pattern.
     * 
     * @return true if the IpRestriction supports wildcard matching
     */
    public boolean isWildcardMatch() {
        return ALLOWED_WILDCARD == getType() || DENIED_WILDCARD == getType();
    }
    
    /**
     * Get the type code for the given address pattern and allow / deny
     * type. 
     * 
     * @param address address pattern
     * @param isAllow <code>true</code> if this pattern is an allow type
     * @return int type representing the IpRestriction
     * @see #getType()
     */
    public static int getType(String address, boolean isAllow) {
        if (address.indexOf('*') > -1) {
            return isAllow ? ALLOWED_WILDCARD : DENIED_WILDCARD;
        } else {
            return isAllow ? ALLOWED : DENIED;
        }
    }

    /**
     * Get the priority of this restriction. The close to <code>zero</code>
     * the higher the priority of the restriction.
     * 
     * @return priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set the priority of this restriction. The close to <code>zero</code>
     * the higher the priority of the restriction.
     * 
     * @param priority priority
     */
    public void setPriority(int priority) {
        this.priority = priority;        
    }

    /**
     * Get the type of Ip restriction address pattern. Will be one of
     * {@link #ALLOWED}, {@link #ALLOWED_WILDCARD}, {@link #DENIED} or
     * {@link #DENIED_WILDCARD}.
     *  
     * @return ip restriction address pattern type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type of Ip restriction address pattern. Should be one of
     * {@link #ALLOWED}, {@link #ALLOWED_WILDCARD}, {@link #DENIED} or
     * {@link #DENIED_WILDCARD}.
     *  
     * @param type type
     * @see #getType(String, boolean)
     */
    public void setType(int type) {
        this.type = type;        
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return obj instanceof IpRestriction && ((IpRestriction)obj).getID() == getID();
    }

    /**
     * Compare this restriction against another using the address.
     * 
     * @param o other restriction to compare against
     * @return comparsion (see {@link Comparable#compareTo(Object)}).
     */
    public int compareTo(IpRestriction o) {
        return getAddress().compareTo(o.getAddress());
    }

    /**
     * Get if this restriction is the default restriction (i.e. has an 
     * address pattern of <strong>*.*.*.*</strong>)
     * 
     * @return default restriction
     */
    public boolean isDefault() {
        return getID() == 1;
    }
}