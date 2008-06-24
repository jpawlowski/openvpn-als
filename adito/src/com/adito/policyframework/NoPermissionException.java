
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
			
package com.adito.policyframework;


/**
 */
public class NoPermissionException extends Exception {
    private final ResourceType resourceType;
    private final Principal principal;

    /**
     * @param message
     */
    public NoPermissionException(String message) {
        this(message, null, null, null);
    }
    
    /**
     * @param principal
     * @param resourceType
     */
    public NoPermissionException(Principal principal, ResourceType resourceType) {
        this("", null, principal, resourceType);
    }

    /**
     * @param message
     * @param principal
     * @param resourceType
     */
    public NoPermissionException(String message, Principal principal, ResourceType resourceType) {
        this(message, null, principal, resourceType);
    }

    /**
     * @param message
     * @param cause
     * @param principal
     * @param resourceType
     */
    public NoPermissionException(String message, Throwable cause, Principal principal, ResourceType resourceType) {
        super(message, cause);
        this.principal = principal;
        this.resourceType = resourceType;
    }

    /**
     * @return String
     */
    public String getPrincipalName() {
        return principal == null ? "" : principal.getPrincipalName();
    }

    /**
     * @return ResourceType
     */
    public ResourceType getResourceType() {
        return resourceType;
    }
}