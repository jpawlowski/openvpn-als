/*
 */
package com.adito.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;

/**
 * Wraps an {@link ApplicationParameterDefinition} for use when editing an
 * application shortcut.
 */
public class ShortcutParameterItem implements Comparable<ShortcutParameterItem> {

	/**
	 * Dummy password used to avoid rendering real password in HTML form
	 */
	public final static String DUMMY_PASSWORD = "******";

	// Private instance variables
	private ApplicationParameterDefinition definition;
	private Object value;
	private Pair[] listItems;
	private int rows, columns;
	private ExtensionDescriptor app;
	private Locale locale;

	/**
	 * Constructor.
	 * 
	 * @param app extension descriptor
	 * @param definition definition
	 * @param value value
	 * @param locale locale to use for name, description etc
	 */
	public ShortcutParameterItem(ExtensionDescriptor app, ApplicationParameterDefinition definition, String value, Locale locale) {
		this.definition = definition;
		this.app = app;
		this.locale = locale;

		rows = 0;
		columns = 0;

		if (definition.getType() == PropertyDefinition.TYPE_LIST) {
			StringTokenizer t = new StringTokenizer(definition.getTypeMeta(), ",");
			List<Pair> listItemsList = new ArrayList<Pair>();
			while (t.hasMoreTokens()) {
				String n = t.nextToken();
				String k = "application." + app.getId() + "." + definition.getName() + ".value." + n;
				String v = app.getMessageResources().getMessage(k);
				Pair pair = new Pair(n, v);
				if (n.equals(value)) {
					this.value = pair.getValue();
				}
				listItemsList.add(pair);
			}
			listItems = new Pair[listItemsList.size()];
			listItemsList.toArray(listItems);
		} else if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST) {
			this.value = new PropertyList(value).getAsTextFieldText();
			StringTokenizer t = new StringTokenizer(definition.getTypeMeta(), "x");
			try {
				columns = Integer.parseInt(t.nextToken());
				rows = Integer.parseInt(t.nextToken());
			} catch (NumberFormatException nfe) {

			}
		} else if (definition.getType() == PropertyDefinition.TYPE_TEXT_AREA) {
			this.value = value;
			StringTokenizer t = new StringTokenizer(definition.getTypeMeta(), "x");
			try {
				columns = Integer.parseInt(t.nextToken());
				rows = Integer.parseInt(t.nextToken());
			} catch (NumberFormatException nfe) {

			}
		} else if (definition.getType() == PropertyDefinition.TYPE_BOOLEAN) {
			String trueVal = (String) (((List) definition.getTypeMetaObject()).get(0));
			this.value = value.equals(trueVal) ? Boolean.TRUE : Boolean.FALSE;
		} else if (definition.getType() == PropertyDefinition.TYPE_STRING) {
			try {
				columns = Integer.parseInt(definition.getTypeMeta());
			} catch (NumberFormatException nfe) {
			}
			this.value = value;
		} else if (definition.getType() == PropertyDefinition.TYPE_PASSWORD) {
			try {
				columns = Integer.parseInt(definition.getTypeMeta());
			} catch (NumberFormatException nfe) {
			}
			this.value = value;
		} else if (definition.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
			try {
				int val = Integer.parseInt(definition.getDefaultValue());
				if (definition.getTypeMeta().equalsIgnoreCase("s")) {
					this.value = String.valueOf(val / 1000);
				} else if (definition.getTypeMeta().equalsIgnoreCase("m")) {
					this.value = String.valueOf(val / 1000 / 60);
				} else if (definition.getTypeMeta().equalsIgnoreCase("h")) {
					this.value = String.valueOf(val / 1000 / 60 / 60);
				} else if (definition.getTypeMeta().equalsIgnoreCase("d")) {
					this.value = String.valueOf(val / 1000 / 60 / 60 / 24);
				}
			} catch (Exception e) {
			}
		} else {
			this.value = value;
		}

		// 
	}

	/**
	 * If this is to be used as a password field, get the initial value. This
	 * will be a dummy password if a password has been set. If the dummy
	 * password is then received upon form submission, the password is
	 * considered unchanged.
	 * 
	 * @return initial password field value
	 */
	public String getPasswordValue() {
		return getValue() == null || getValue().equals("") ? "" : DUMMY_PASSWORD;
	}

	/**
	 * Validate the value of this item against the rules in the property
	 * definition.
	 * 
	 * @return error message if invalid or <code>null</code> if ok
	 * @throws Exception 
	 */
	public ActionMessage validateItem() throws Exception {
		if (getType() == PropertyDefinition.TYPE_INTEGER) {
			if (getValue().toString().trim().length() == 0) {
				if (!definition.isOptional()) {
					return new BundleActionMessage("properties", "error.integerRequired", app.getMessageResources()
									.getMessage("application." + app.getId() + "." + getName() + ".name"));
				}
				return null;
			} 
		} else if (getType() == PropertyDefinition.TYPE_STRING || getType() == PropertyDefinition.TYPE_TEXT_AREA
			|| getType() == PropertyDefinition.TYPE_PASSWORD) {
			if (getValue().toString().trim().length() == 0) {
				if (!definition.isOptional()) {
					return new BundleActionMessage("properties", "error.requiredParameterEmpty", app.getMessageResources()
									.getMessage("application." + app.getId() + "." + getName() + ".name"));

				}
				return null;
			}
		}

		PropertyDefinition def = getDefinition(); 
		try {
			def.validate(String.valueOf(getPropertyValue()), getClass().getClassLoader());
		} catch (CoreException ce) {
			ce.getBundleActionMessage().setArg3(def.getName());
			return ce.getBundleActionMessage();
		} 
		return null;
	}

	/**
	 * Get if this field is optional.
	 * 
	 * @return optional
	 */
	public boolean getOptional() {
		return definition.isOptional();
	}

	/**
	 * Get the localised name of this field.
	 * 
	 * @return localised name
	 */
	public String getLocalisedName() {
		String key = "application." + app.getId() + "." + getName() + ".name";
		return app.getMessageResources().getMessage(key);
	}

	/**
	 * Get the localised description of this field.
	 * 
	 * @return localised description
	 */
	public String getLocalisedDescription() {
		String key = "application." + app.getId() + "." + getName() + ".description";
		return app.getMessageResources().isPresent(key) ? app.getMessageResources().getMessage(locale, key) : getLocalisedName();
	}

	/**
	 * Get the localised category
	 * 
	 * @return localised category
	 */
	public String getLocalisedCategory() {
		String key = "application." + app.getId() + ".category." + getCategory() + ".name";
		return app.getMessageResources().isPresent(key) ? app.getMessageResources().getMessage(locale, key) : "";
	}

	/**
	 * If the field supports it, get the number of columns to use for the
	 * rendered input component.
	 * 
	 * @return columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * If the field supports it, get the number of rows to use for the rendered
	 * input component.
	 * 
	 * @return rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Get the application parameter definition that defines this field.
	 * 
	 * @return application parameter definition
	 */
	public PropertyDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the application parameter definition that defines this field.
	 * 
	 * @param definition application parameter definition
	 */
	public void setDefinition(ApplicationParameterDefinition definition) {
		this.definition = definition;
	}

	/**
	 * Convience method to get the name of the definition.
	 * 
	 * @return definition name
	 */
	public String getName() {
		return definition.getName();
	}

	/**
	 * Convience method to get the category of the definition.
	 * 
	 * @return category
	 */
	public int getCategory() {
		return definition.getCategory();
	}

	/**
	 * Convience method to get the default value of the definition.
	 * 
	 * @return default value
	 */
	public String getDefaultValue() {
		return definition.getDefaultValue();
	}

	/**
	 * Get the default value to use for the rendered input component.
	 * 
	 * @return default type
	 */
	public String getDefaultText() {
		String val = getDefaultValue();
		if (definition.getType() == PropertyDefinition.TYPE_PASSWORD) {
			val = "";
		} else if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST) {
			PropertyList list = new PropertyList(definition.getDefaultValue());
			val = list.size() > 0 ? list.getPropertyItem(0) : "";
		} else if (definition.getType() == PropertyDefinition.TYPE_LIST) {
			try {
				int defaultItem = Integer.parseInt(definition.getDefaultValue());
				String k = "application." + app.getId() + "." + definition.getName() + ".value." + defaultItem;
				val = app.getMessageResources().getMessage(k);
			} catch (Exception e) {
			}
		} else if (definition.getType() == PropertyDefinition.TYPE_TIME_IN_MS) {
			try {
				int defaultItem = Integer.parseInt(getDefaultValue());
				if (definition.getTypeMeta().equalsIgnoreCase("s")) {
					val = String.valueOf(defaultItem / 1000);
				} else if (definition.getTypeMeta().equalsIgnoreCase("m")) {
					val = String.valueOf(defaultItem / 1000 / 60);
				} else if (definition.getTypeMeta().equalsIgnoreCase("h")) {
					val = String.valueOf(defaultItem / 1000 / 60 / 60);
				} else if (definition.getTypeMeta().equalsIgnoreCase("d")) {
					val = String.valueOf(defaultItem / 1000 / 60 / 60 / 24);
				} else {
					val = String.valueOf(val);
				}
			} catch (Exception e) {
				val = String.valueOf(getDefaultValue());
			}
		}
		if (val.length() > 15) {
			val = val.substring(0, 15);
		}
		return val;
	}

	/**
	 * Convenience method to get the type meta information from the definition
	 * 
	 * @return type meta
	 */
	public String getTypeMeta() {
		return definition.getTypeMeta();
	}

	/**
	 * Get an array of all name value pairs if the component supports it (i.e.
	 * multi-entry).
	 * 
	 * @return name value pairs
	 */
	public Pair[] getListItems() {
		return listItems;
	}

	/**
	 * Get the value as an object.
	 * 
	 * @return value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * For use by checkbox components, get if the value is <code>true</code>.
	 * 
	 * @return checkbox selected
	 */
	public boolean getSelected() {
		return value.equals(Boolean.TRUE);
	}

	/**
	 * For use by checkbox components, set if the value is <code>true</code>.
	 * 
	 * @param selected checkbox selected
	 */
	public void setSelected(boolean selected) {
		this.value = Boolean.valueOf(selected);
	}

	/**
	 * Set the value as an object.
	 * 
	 * @param value value
	 */
	public void setValue(Object value) {
		if (getType() != PropertyDefinition.TYPE_PASSWORD || !value.equals(DUMMY_PASSWORD)) {
			this.value = value;
		}
	}

	/**
	 * Convenience method to get the field type from the definition.
	 * 
	 * @return field type
	 */
	public int getType() {
		return definition.getType();
	}

	/**
	 * Get the value to actually store. The format of this will differ depending
	 * on the type.
	 * 
	 * @return property value
	 */
	public Object getPropertyValue() {
		if (definition.getType() == PropertyDefinition.TYPE_MULTI_ENTRY_LIST) {
			PropertyList l = new PropertyList();
			l.setAsTextFieldText(getValue().toString());
			return l.getAsPropertyText();
		} else if (definition.getType() == PropertyDefinition.TYPE_BOOLEAN) {
			String trueVal = (String) (((List) definition.getTypeMetaObject()).get(0));
			String falseVal = (String) (((List) definition.getTypeMetaObject()).get(1));
			return Boolean.TRUE.equals(getValue()) ? trueVal : falseVal;
		}
		return getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ShortcutParameterItem obj) {
		return definition.compareTo(obj.getDefinition());
	}

	/**
	 * Encapsulate a value / label pair
	 */
	public class Pair {
		Object value;
		String label;

		/**
		 * Constructor.
		 * 
		 * @param value
		 * @param label
		 */
		public Pair(Object value, String label) {
			this.value = value;
			this.label = label;
		}

		/**
		 * Get the value
		 * 
		 * @return value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Get the label
		 * 
		 * @return label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Set the value
		 * 
		 * @param value value
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "pair[label=" + label + ",value=" + value + "]";
		}
	}

}