package com.ovpnals.properties;

import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.DefaultAttributeDefinition;
import com.ovpnals.properties.impl.userattributes.UserAttributeKey;
import com.ovpnals.properties.impl.userattributes.UserAttributes;
import com.ovpnals.security.SessionInfo;

public class PersistentSettings {

	public static int getIntValue(SessionInfo session, String key, int defaultValue) {
		
		UserAttributeKey attr = new UserAttributeKey(session.getUser(), key);
		
		checkDefinition(attr, PropertyDefinition.TYPE_INTEGER, String.valueOf(defaultValue));
		
		return Property.getPropertyInt(new UserAttributeKey(session.getUser(), key));
		
	}

	public static String getValue(SessionInfo session, String key, String defaultValue) {
		
		UserAttributeKey attr = new UserAttributeKey(session.getUser(), key);
		
		checkDefinition(attr, PropertyDefinition.TYPE_STRING, String.valueOf(defaultValue));
		
		return Property.getProperty(new UserAttributeKey(session.getUser(), key));
		
	}
	private static void checkDefinition(UserAttributeKey attr, int type, String defaultValue) {
		if(Property.getDefinition(attr)==null) {
			
			DefaultAttributeDefinition def = new DefaultAttributeDefinition(type,
					attr.getName(),
					"",
					90000,
					"",
					String.valueOf(defaultValue),
					AttributeDefinition.USER_OVERRIDABLE_ATTRIBUTE,
					10,
					"",
					true,
					"",
					"",
					true,
					false,
					null);

			PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME).registerPropertyDefinition(def);
		}
	}
	
	public static void setValue(SessionInfo session, String key, String actualValue, String defaultValue) {
		
		UserAttributeKey attr = new UserAttributeKey(session.getUser(), key);
		
		checkDefinition(attr, PropertyDefinition.TYPE_STRING, String.valueOf(defaultValue));
		
		Property.setProperty(new UserAttributeKey(session.getUser(), key), actualValue, session);
	}


	public static void setIntValue(SessionInfo session, String key, int actualValue, int defaultValue) {
		
		UserAttributeKey attr = new UserAttributeKey(session.getUser(), key);
		
		checkDefinition(attr, PropertyDefinition.TYPE_INTEGER, String.valueOf(defaultValue));
		
		Property.setProperty(new UserAttributeKey(session.getUser(), key), actualValue, session);
	}
}
