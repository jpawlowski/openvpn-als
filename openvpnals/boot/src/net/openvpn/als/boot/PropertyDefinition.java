
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


/**
 * Each property that may be stored store is keyed by its <i>id</i>. For
 * each unique <i>id</i> there must have been a single <i>Property Definition</i>
 * storing with the property database using 
 * {@link net.openvpn.als.properties.PropertyDatabase#storeAttributeValue(net.openvpn.als.properties.attributes.AbstractAttributeKey, String)}
 * <p>
 * Each definition contains information about the value that will be stored
 * against it. This includes the type of value (string, integer, boolean etc),
 * the bundle in which the human readable name and description is held,
 * its category ID and other information. 
 * <p>
 * Every property definition consists of the following attributes :-
 * 
 * <h3>Type</h3>
 * 
 * This determines the type of values that will be stored against this definition.
 * See {@link #getType()} for a list of the possible values. Properties
 * are be stored as strings. The type is then used by the user interface to 
 * present to appropriate component and convert to and from strings.
 * 
 * <h3>Name</h3>
 * 
 * This is the unique identifier of the property and must be unique. 
 * All properties are keyed by their name (or ID as it is sometimes known).
 * 
 * <h3>Default Value</h3>
 * 
 * Every property must have a sane default value that is used when no 
 * value has been set.
 * 
 * <h3>Type Meta</h3>
 * 
 * Meta-data about the property. The contents and format of this string
 * will vary depending on the type. For example, {@link #TYPE_TEXT_AREA}
 * type meta may contains a string in the format '[rows]X[cols]' that 
 * specifies the size of the text area the UI should display.
 * 
 * <h3>Category</h3>
 * 
 * The category ID this property exists under. See {@link net.openvpn.als.properties.PropertyDatabase}
 * for an explanation of categories.
 * 
 * <h3>Sort Order</h3>
 * 
 * A hint as to where the property should be placed in relation to others in
 * the same category and visibility. The lower the value, the higher up the
 * list. The two properties may exists with the same sort order but the 
 * behaviour is undefined.
 * 
 * <h3>Message Resources Key</h3>
 * 
 * The message resource bundle key as defined in the Struts configuration XML
 * files (struts-config.xml).
 * 
 * <h3>Restart Required</h3>
 * 
 * A few properties may require that a restart is performed before they
 * take effect. This flag provides a hint to this effect.
 * 
 * @see net.openvpn.als.properties.PropertyDatabase
 */
public interface PropertyDefinition extends Comparable<PropertyDefinition> {

    /**
     * Undefined
     */
    public static final String UNDEFINED_PARAMETER = "UNDEFINED";

    /**
     * Simple text field
     */
    public final static int TYPE_STRING = 0;
    
    /**
     * Integer only text field
     */
    public final static int TYPE_INTEGER = 1;
    
    /**
     * Checkbox
     */
    public final static int TYPE_BOOLEAN = 2;
    
    /**
     * Combobox
     */
    public final static int TYPE_LIST = 3;
    
    /**
     * Password text field
     */
    public final static int TYPE_PASSWORD = 4;
    
    /**
     * Multiple entry list.
     */
    public final static int TYPE_MULTI_ENTRY_LIST = 5;
    
    /**
     * Text area. Type metadata provides text area size
     */
    public final static int TYPE_TEXT_AREA = 6;
    
    /**
     * Time field. Meta data determines unit
     */
    public final static int TYPE_TIME_IN_MS = 7;
    
    /**
     * Colour
     */
    public final static int TYPE_COLOR = 8;
    
    /**
     * Multiple selection list. Type metadata provides a class name that 
     * implements
     */
    public final static int TYPE_MULTI_SELECT_LIST = 9;

    /**
     * Type value when undefined (e.g. during the creation of a user
     * defined attribute)
     */
    public final static int TYPE_UNDEFINED = -1;

    /**
     * The type of the constraint. Can be one of
     * <code>SystemPropertyDefinition.TYPE_STRING</code>,
     * <code>SystemPropertyDefinition.TYPE_INTEGER</code>,
     * <code>SystemPropertyDefinition.TYPE_BOOLEAN</code>,
     * <code>SystemPropertyDefinition.TYPE_LIST</code>,
     * <code>SystemPropertyDefinition.TYPE_PASSWORD</code>,
     * <code>SystemPropertyDefinition.TYPE_MULTI_ENTRY_LIST</code>,
     * <code>SystemPropertyDefinition.TYPE_TEXT_AREA</code>,
     * <code>SystemPropertyDefinition.TYPE_TIME_IN_MS</code>,
     * <code>SystemPropertyDefinition.TYPE_COLOR</code> or
     * <code>SystemPropertyDefinition.TYPE_MULTI_SELECT_LIST</code>.
     * 
     * @return type
     */
    public int getType();

    /**
     * Get name of the property this definition is to be applied to.
     * 
     * @return name
     */
    public String getName();

    /**
     * The type may require additional information. For example, the
     * TYPE_LIST constraint requires a list of valid values, which in this case
     * would be provided as a comma separated string. A blank string indicates
     * no additional information, <code>null</code> is never returned
     * 
     * @return constraint type meta data
     */
    public String getTypeMeta();

    /**
     * The type may require additional information. For example, the
     * TYPE_LIST constraint requires a list of valid values, which in this case
     * would be provided as a comma separated string. If <code>null</code> is
     * supplied it will be converted to a blank string
     * 
     * @param typeMeta
     *            constraint type meta data
     */
    public void setTypeMeta(String typeMeta);

    /**
     * Get the default value for this property.
     * 
     * @return default value
     */
    public String getDefaultValue();

    /**
     * Get the category for this property.
     * 
     * @return category
     */
    public int getCategory();

    /**
     * Set the category for this property.
     * 
     * @param category
     */
    public void setCategory(int category);

    /**
     * Get the sort order within the category
     * 
     * @return sort order
     */
    public int getSortOrder();

    /**
     * Set the default value
     * 
     * @param name
     */
    public void setDefaultValue(String name);

    /**
     * Some types may provide their meta data through an object. This 
     * method should return that object
     * 
     * @return type meta object
     */
    public Object getTypeMetaObject();

    /**
     * Get the key of the message resources bundle that contains the names
     * and descriptions of this property
     * 
     * @return messages resources key
     */
    public String  getMessageResourcesKey();
    
    /**
     * Get if this property is hidden
     * 
     * @return hidden
     */
    public boolean isHidden();
    
    /**
     * Validate a value for this property. The implementation would usually have
     * a set of default {@link PropertyValidator} for the type and also allow 
     * customised validators. The OpenVPNALS core includes a default set of 
     * validators which may be useful.
     * 
     * @param value value to validate
     * @param classLoader class loader to get validators from
     * @throws CodedException on any validation error
     * @throws ClassNotFoundException if validator cannot be found
     */
    public void validate(String value, ClassLoader classLoader) throws CodedException, ClassNotFoundException;
    
    
    /**
     * This is a hack to get around the current validation framework.
     * @param validationString
     * @deprecated
     */
    public void setValidationString(String validationString);

	/**
	 * Invoked when the definition is registered with its type.
	 * 
	 * @param propertyClass property class
	 */
	public void init(PropertyClass propertyClass);

	/**
	 * Get the property class of this definition
	 * 
	 * @return property class
	 */
	public PropertyClass getPropertyClass();
    
    /**
     * Get if changing this property will require a restart
     * before the new value is used
     * 
     * @return restart required
     */
    public boolean isRestartRequired();

    /**
     * Set the visibility. This should only be called on initial creation.
     * 
     * @param visibility
     * @throws IllegalArgumentException if visibility has already been set
     */
    public void setVisibility(int visibility);

    /**
     * Get the visibility.
     *  
     * @return visibility
     */
    public int getVisibility();
    
    /**
     * Get the value of this item as it should be stored as an attribute.
     * 
     * @param value value to format
     * @return formatted attribute value
     */
    public String formatAttributeValue(Object value);

    /**
     * Get the appropriate object for this type.
     * 
     * @param value
     * @return typed value
     */
    public Object parseValue(String value);

    /**
     * Get the key of the message resources bundle that contains the names
     * and descriptions of this property
     * 
     * @return messages resource key
     */
    public String  getNameMessageResourceKey();

    /**
     * Get the key of the message resources bundle that contains the names
     * and descriptions of this property
     * 
     * @return messages resources key
     */
    public String  getDescriptionMessageResourceKey();
    
    /**
     * Get the label for this attribute. If an empty string is supplied the
     * name will be used as the label in the user interface. Note that the
     * actual label displayed to the use may be different if a message 
     * resource exists (see class documentation).
     * 
     * @return label
     */
    public String getLabel();

    /**
     * Set the label for this attribute. If an empty string is supplied the
     * name will be used as the label in the user interface. Note that the
     * actual label displayed to the use may be different if a message 
     * resource exists (see class documentation).
     * 
     * @param label label
     */
    public void setLabel(String label);

    /**
     * Get the category label. If this is not <code>null</code> then the
     * category ID will be used to derive the category label from message
     * resources.
     * 
     * @return category label
     */
    public String getCategoryLabel();

    /**
     * Set the category label. If this is not <code>null</code> then the
     * category ID will be used to derive the category label from message
     * resources.
     * 
     * @param categoryLabel category label
     */
    public void setCategoryLabel(String categoryLabel);

    /**
     * Set the label for this attribute. If an empty string is supplied the name
     * will be used as the label in the user interface. Note that the actual
     * description displayed to the use may be different if a message resource
     * exists (see class documentation).
     * 
     * @return description
     */
    public String getDescription();

    /**
     * Set the label for this attribute. If an empty string is supplied the name
     * will be used as the label in the user interface. Note that the actual
     * label displayed to the use may be different if there
     * 
     * @param description description
     */
    public void setDescription(String description);
}