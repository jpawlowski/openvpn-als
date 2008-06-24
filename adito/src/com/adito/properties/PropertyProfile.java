
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
			
package com.adito.properties;

import com.adito.policyframework.OwnedResource;

/**
 * 
 * All property values are held in a profile. Profiles provide a way of of the
 * user selecting a set of property values depending on the environment they are
 * in. For example, there may be a profile configured whose properties are
 * appropriate for using when a user is connecting via the Office Lan. The
 * administrator may also have configured a second profile who property values
 * are more appropriate when users are connecting from somewhere less secure,
 * such as an Internet Cafe.
 * <p>
 * There are two types of profile, <b>Global</b>, and <b>Personal</b>. Global
 * profiles are created by an administrator and then assigned to users via
 * policies. Many users may share the same profile. Personal profiles are
 * created by users themselves and are only usable by them.
 * <p>
 * Only property definitions that have a visibility of
 * {@link com.adito.boot.PropertyDefinition#PROFILE} may exist in global
 * or personal profiles.
 * <p>
 * There also exists a special <b>Default</b> global profile. This profile has
 * an id of 0 and is also used store to the default global profile <strong>and</strong>
 * system configuration <strong>and</strong> hidden properties.
 * <p>
 * A Property Profiles is consider a type of
 * {@link com.adito.policyframework.Resource} so is subject ot all
 * the rules imposed on by the <i>Policy Framework</i>. In fact it is a
 * specialised type of resource known as a
 * {@link com.adito.policyframework.OwnedResource}. *
 */
public interface PropertyProfile extends OwnedResource {

    /**
     * Get the label. This will be whats show to the user when selecting a
     * profile.
     * 
     * @return label
     */
    public String getLabel();
}