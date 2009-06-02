package net.openvpn.als.properties.attributes;

import net.openvpn.als.boot.PropertyClass;
import net.openvpn.als.properties.impl.userattributes.UserAttributes;

/**
 * Specialisation of a <i>Property Class</i> for <i>Attributes</i>.
 * <p>
 * Attributes differ from properties in that an administrator may actually
 * create new <i>Attribute Definitions</i> and the define and use them in
 * various OpenVPNALS features.
 * <p>
 * For example, {@link UserAttributes} may be defined by administrator. An
 * individual users and or administrator may then set the values for the each of
 * these attributes. The attribute name may then be used as a <i>Replacement
 * Variable</i> in various places, such as in the URI of a <i>Network Place</i>
 * or the value of an <i>Application Shortcut</i> parameter.
 * <p>
 * See {@link AttributeDefinition} for more details.
 * 
 * @author brett
 * @see AttributeDefinition
 * 
 */
public interface AttributesPropertyClass extends PropertyClass {
	/**
	 * Get the default bundle name for the property class. This bundle is used
	 * to get the localised name of the attributes class.
	 * 
	 * @return message resources key
	 */
	public String getMessageResourcesKey();

	/**
	 * Get whether the class of attribute supports the user visiblity flag.
	 * 
	 * @return supports user visibility
	 */
	public boolean isSupportsVisibility();

	/**
	 * Create a new instance of an attribute definition appropriate for this
	 * attribute class. This is used by the wizard.
	 * 
	 * @param type
	 *            type
	 * @param name
	 *            name
	 * @param typeMeta
	 *            type meta
	 * @param category
	 *            category ID or <code>-1</code> to use the categoryLabel
	 * @param categoryLabel
	 *            category label or <code>null</code>
	 * @param defaultValue
	 *            default value
	 * @param visibility
	 *            visibility. See class description
	 * @param sortOrder
	 *            sort order
	 * @param messageResourcesKey
	 * @param hidden
	 *            hidden
	 * @param label
	 *            label
	 * @param description
	 *            description
	 * @param system
	 *            system
	 * @param replaceable
	 * @param validationString
	 *            validation string
	 * @return attribute definition
	 */
	public AttributeDefinition createAttributeDefinition(int type, String name, String typeMeta, int category, String categoryLabel, String defaultValue,
            int visibility, int sortOrder, String messageResourcesKey, boolean hidden, String label, String description, boolean system, boolean replaceable, String validationString);
}
