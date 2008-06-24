
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
			
package com.adito.properties.attributes;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;

/**
 * Extension of the default {@link com.adito.boot.PropertyDefinition}
 * implementation, instances of which may be created and maintained by the
 * administrator to provide details for <i>Attributes</i>.
 * <p>
 * An attribute definition may have one of
 * 4 visibilities. This may be one of {@link #USER_OVERRIDABLE_ATTRIBUTE},
 * {@link #USER_VIEWABLE_ATTRIBUTE}, {@link #USER_USEABLE_ATTRIBUTE} or
 * {@link #USER_CONFIDENTIAL_ATTRIBUTE}.
 * <p>
 * <i>User Confidential attributes</i> are encrypted using the users
 * private key. See {@link com.adito.security.PublicKeyStore}.
 * 
 * <p>
 * Attribute definitions may be registered without being stored to the
 * database by using
 * {@link PropertyClass#registerPropertyDefinition(PropertyDefinition)} via 
 * {@link PropertyClassManager#getPropertyClass(String)}.
 * <p>
 * This is useful for plugins who may wish to attribute definitions without the
 * user having to configure them. Such definitions cannot be edited or removed
 * and should be marked as <i>System</i>.
 * <p>
 * In addition to the capabilities of a property definition, attribute
 * definitions also require a label. Attributes registered as <i>System</i> may
 * provide message resources that will be used in preference to the label field
 * this class supports. This requires that the bundle ID has also been supplied.
 * Message resources for labels should be specified as 
 * <i>userAttribte.[name].title</i>.
 * <p>
 * In the same way as labels, descriptions may also be supplied. This requires 
 * that the bundle ID has also been supplied and a message resource specified as 
 * <i>userAttribte.[name].description</i> exists.
 * <p>
 * Definitions may be categorised in one of two ways. Either a category ID that
 * is a registered {@link com.adito.boot.PropertyDefinitionCategory}
 * may be supplied, or a simple text label. The former is most likely to be used
 * by attribute definitions registered by the core and plugins, the later for
 * user defined definitions.
 * <p>
 * Attributes also have a <i>replaceable</i> attribute that determines if
 * they may be used as replacement variables for attributes of the various
 * different resource types.   
 * 
 * @see com.adito.security.UserDatabase
 */
public interface AttributeDefinition extends PropertyDefinition {

    /*
     * Visibilities
     */

    /**
     * The property may be set by an administrator and viewed, used and
     * overridden by a user
     */
    public final static int USER_OVERRIDABLE_ATTRIBUTE = 4;

    /**
     * The property may be set by an administrator and viewed / used by a user
     */
    public final static int USER_VIEWABLE_ATTRIBUTE = 5;

    /**
     * The property may be set by an administrator and used by a user
     */
    public final static int USER_USEABLE_ATTRIBUTE = 6;

    /**
     * The property may be set and used only by a user. The value will
     * be encrypted with the users private key
     */
    public final static int USER_CONFIDENTIAL_ATTRIBUTE = 7;

    /**
     * Visiblity is unknown
     */
    public static final int UNKNOWN = -1;

    /**
     * Get if this is a <i>System</i> attribute definition. If it is then
     * the def. cannot be edited or removed
     * 
     * @return system definition
     */
    public boolean isSystem();

    /**
     * Set if this is a <i>System</i> attribute definition. If it is then
     * the def. cannot be edited or removed
     * 
     * @param system system definition
     */
    public void setSystem(boolean system);

    /**
     * Set whether this this attribute may be used for replacements.
     * 
     * @param replaceable replaceable
     */
    public void setReplaceable(boolean replaceable);
    
    /**
     * Get whether this this attribute may be used for replacements.
     * 
     * @return replaceable
     */
    public boolean isReplaceable();

    /**
     * Get the validation string.
     * 
     * @return validation string
     */
    public String getValidationString();

    /**
     * Set the sort order
     * 
     * @param sortOrder sort order
     * @see PropertyDefinition#getSortOrder()
     */
    public void setSortOrder(int sortOrder);

    /**
     * Set the name of the attribute. This should only be called on initial creation.
     * 
     * @param string
     * @throws IllegalStateException if name has already been set
     */
    public void setName(String string);

    /**
     * Set the type. This should only be called on initial creation.
     * 
     * @param type
     * @see PropertyDefinition#getType()
     * @throws IllegalArgumentException if type has already been set
     */
    public void setType(int type);

}
