
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Default implementation of a {@link net.openvpn.als.boot.PropertyDefinition}.
 * <p>
 * As well as providing default attributes, this class understands a
 * <i>Validation Pattern</i>. This is used to provide a
 * {@link PropertyValidator} implementation used when the
 * {@link #validate(String, ClassLoader)} method is invoked.
 * 
 * <p>
 * If a <code>null</code> pattern is supplied, a default validator based on
 * property type will be created or
 * <p>
 * If not <code>null</code> the pattern should be in the format
 * <code>[className]([property1=value1,..)</code>. The bracketed property
 * list is optional. For example, to use the <code>IntegerValidator</code> to
 * validate integer values between 10 and 20 you would use
 * <code>net.openvpn.als.input.validators.IntegerValidator(minValue=10,maxValue=20)</code>.
 * 
 */
public class DefaultPropertyDefinition implements PropertyDefinition, Comparable<PropertyDefinition>, Serializable {

    // Private instance variables

    protected int type = TYPE_STRING, category;
    protected String name, typeMeta;
    protected String defaultValue;
    protected int sortOrder;
    protected Object typeMetaObject;
    protected String messageResourcesKey;
    protected boolean hidden;
    protected String validationString;
    protected transient PropertyClass propertyClass;
    protected boolean restartRequired;
    private String label;
    private String description;
    private String categoryLabel;

    static HashMap<String, PropertyValidator> validators = new HashMap<String, PropertyValidator>();

    /**
     * Constructor.
     */
    public DefaultPropertyDefinition() {
        this(TYPE_UNDEFINED, null, null, 0, null, 0, false);
    }

    /**
     * Constructor.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta-data
     * @param category category ID
     * @param defaultValue default value
     * @param sortOrder sort order
     * @param hidden hidden
     */
    public DefaultPropertyDefinition(int type, String name, String typeMeta, int category, String defaultValue, int sortOrder,
                    boolean hidden) {
        this(type, name, typeMeta, category, defaultValue, sortOrder, hidden, null);
    }

    /**
     * Constructor.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta-data
     * @param category category ID
     * @param defaultValue default value
     * @param sortOrder sort order
     * @param hidden hidden
     * @param validationString validation pattern. See class documentation.
     */
    public DefaultPropertyDefinition(int type, String name, String typeMeta, int category, String defaultValue, int sortOrder,
                    boolean hidden, String validationString) {
        this(type, name, typeMeta, category, defaultValue, sortOrder, "properties", hidden, validationString, null, null);
    }

    /**
     * Constructor.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta-data
     * @param category category ID
     * @param defaultValue default value
     * @param sortOrder sort order
     * @param messageResourcesKey message resource bundle bundle
     * @param hidden hidden
     * @param label
     * @param description
     */
    public DefaultPropertyDefinition(int type, String name, String typeMeta, int category, String defaultValue, int sortOrder,
                    String messageResourcesKey, boolean hidden, String label, String description) {
        this(type, name, typeMeta, category, defaultValue, sortOrder, messageResourcesKey, hidden, null, label, description);
    }

    /**
     * Constructor.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta-data
     * @param category category ID
     * @param defaultValue default value
     * @param sortOrder sort order
     * @param messageResourcesKey message resource bundle bundle
     * @param hidden hidden
     * @param validationString validation string. See class documentation.
     * @param label
     * @param description
     */
    public DefaultPropertyDefinition(int type, String name, String typeMeta, int category, String defaultValue, int sortOrder,
                    String messageResourcesKey, boolean hidden, String validationString, String label, String description) {
        this.type = type;
        this.name = name;
        this.typeMeta = Util.trimmedOrBlank(typeMeta);
        this.category = category;
        this.defaultValue = defaultValue;
        this.sortOrder = sortOrder;
        this.hidden = hidden;
        this.messageResourcesKey = messageResourcesKey == null ? "properties" : messageResourcesKey;
        this.validationString = validationString;
        this.label = label;
        this.description = description;
    }

    /**
     * Check the property
     */
    protected void check() {

        switch (type) {
            case TYPE_TIME_IN_MS:
            case TYPE_INTEGER:
                if (this.validationString == null || this.validationString.equals("")) {
                    this.validationString = "net.openvpn.als.input.validators.IntegerValidator(replacementVariables="
                                    + getPropertyClass().isSupportsReplacementVariablesInValues() + ")";
                }
                break;
            case TYPE_BOOLEAN: {
                List<String> l = new ArrayList<String>();
                StringTokenizer t = new StringTokenizer(typeMeta == null || typeMeta.equals("") ? "true,false" : typeMeta, ",");
                while (t.hasMoreTokens()) {
                    l.add(t.nextToken());
                }
                typeMetaObject = l;
                break;
            }
            case TYPE_LIST: {
                List<TypeMetaListItem> l = new ArrayList<TypeMetaListItem>();
                StringTokenizer t = new StringTokenizer(typeMeta, ",");
                while (t.hasMoreTokens()) {
                    l.add(new TypeMetaListItem(t.nextToken(), this.messageResourcesKey));
                }
                typeMetaObject = l;
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getMessageResourcesKey()
     */
    public String getMessageResourcesKey() {
        return messageResourcesKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getCategory()
     */
    public int getCategory() {
        return category;
    }

    /**
     * @param category
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name. Cannot be set once define.
     * 
     * @param name name
     * @throws IllegalStateException if already set
     */
    public void setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("Already set.");
        }
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getType()
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type. Cannot be set once defined.
     * 
     * @param type type
     * @throws IllegalStateException if type already set
     */
    public void setType(int type) {
        if (this.type != TYPE_UNDEFINED) {
            throw new IllegalStateException("Type is already set.");
        }
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getTypeMeta()
     */
    public String getTypeMeta() {
        return typeMeta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setTypeMeta(java.lang.String)
     */
    public void setTypeMeta(String typeMeta) {
        this.typeMeta = Util.trimmedOrBlank(typeMeta);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getDefaultValue()
     */
    public String getDefaultValue() {
        return defaultValue == null ? "" : defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setDefaultValue(java.lang.String)
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue == null ? "" : defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getSortOrder()
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Set the sort order
     * 
     * @param sortOrder sort order
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getTypeMetaObject()
     */
    public Object getTypeMetaObject() {
        return typeMetaObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#isHidden()
     */
    public boolean isHidden() {
        return hidden;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#validate(java.lang.String,
     *      java.lang.ClassLoader)
     */
    public void validate(String value, ClassLoader classLoader) throws CodedException {
        if (validationString == null || validationString.equals("")) {
            return;
        }
        int idx = validationString.indexOf('(');
        try {
            PropertyValidator v = getValidator(classLoader, idx == -1 ? validationString : validationString.substring(0, idx));
            Properties p = null;
            if (idx != -1) {
                if (!validationString.endsWith(")")) {
                    throw new Exception("Validation string in incorrect format, missing ).");
                }
                PropertyList pl = new PropertyList(validationString.substring(idx + 1, validationString.length() - 1), ',');
                p = pl.getAsNameValuePairs();
            }
            v.validate(this, value, p);
        } catch (CodedException ce) {
            throw ce;
        } catch (Exception e) {
            throw new Error("Failed to create validator using '" + validationString + "'. ", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(PropertyDefinition obj) {
        int i = new Integer(getCategory()).compareTo(new Integer(obj.getCategory()));
        return i == 0 ? new Integer(getSortOrder()).compareTo(new Integer(obj.getSortOrder())) : i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#isRestartRequired()
     */
    public boolean isRestartRequired() {
        return restartRequired;
    }

    /**
     * Set whether restart is required when this property changes
     * 
     * @param restartRequired restart required
     */
    public void setRestartRequired(boolean restartRequired) {
        this.restartRequired = restartRequired;
    }

    /**
     * Get the validation string
     * 
     * @return validation string
     */
    public String getValidationString() {
        return validationString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setValidationString(java.lang.String)
     */
    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getPropertyClass()
     */
    public PropertyClass getPropertyClass() {
        return propertyClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#init(net.openvpn.als.boot.PropertyClass)
     */
    public void init(PropertyClass propertyClass) {
        this.propertyClass = propertyClass;
        check();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "[PropertyDefinition name='" + name + "' type=" + type + " category=" + category + " defaultValue='" + defaultValue
                        + "' typeMeta='" + typeMeta + "' sortOrder=" + sortOrder + " messageResourcesKey='" + messageResourcesKey
                        + "' validationString='" + validationString + "'";
    }

    /**
     * Returns the property validator
     * 
     * @param classLoader
     * @param className
     * @return PropertyValidator
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    static PropertyValidator getValidator(ClassLoader classLoader, String className) throws InstantiationException,
                    IllegalAccessException, ClassNotFoundException {
        synchronized (validators) {
            PropertyValidator pv = validators.get(className);
            if (pv == null) {
                pv = (PropertyValidator) Class.forName(className, true, classLoader).newInstance();
                validators.put(className, pv);
            }
            return pv;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#formatAttributeValue(java.lang.Object)
     */
    public String formatAttributeValue(Object value) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getVisibility()
     */
    public int getVisibility() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#parseValue(java.lang.String)
     */
    public Object parseValue(String value) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setVisibility(int)
     */
    public void setVisibility(int visibility) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getDescriptionMessageResourceKey()
     */
    public String getDescriptionMessageResourceKey() {
        return getName() + ".description";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getNameMessageResourceKey()
     */
    public String getNameMessageResourceKey() {
        return getName() + ".name";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getCategoryLabel()
     */
    public String getCategoryLabel() {
        return categoryLabel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setCategoryLabel(java.lang.String)
     */
    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setLabel(java.lang.String)
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.PropertyDefinition#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

}