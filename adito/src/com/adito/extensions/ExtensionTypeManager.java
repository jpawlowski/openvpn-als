package com.adito.extensions;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.extensions.types.AgentType;
import com.adito.extensions.types.DefaultAgentType;
import com.adito.extensions.types.LanguagePackType;
import com.adito.extensions.types.PluginType;


/**
 * Managers a registry of <i>Extension Types</i> name and the class
 * names that must be instantiated when these extension types are
 * encountered in an extension descriptor.
 */
public class ExtensionTypeManager {

	private static ExtensionTypeManager instance;
	private static Log log = LogFactory.getLog(ExtensionTypeManager.class);
	
	private HashMap<String, Class> extensionTypes = new HashMap<String, Class>();
	
	/**
	 * Constructor.
	 */
	public ExtensionTypeManager() {
		extensionTypes.put(PluginType.TYPE, PluginType.class);
		extensionTypes.put(AgentType.TYPE, AgentType.class);
		extensionTypes.put(DefaultAgentType.TYPE, DefaultAgentType.class);
		extensionTypes.put(LanguagePackType.TYPE, LanguagePackType.class);
	}
	
	/**
	 * Get an instance of the extension type manager, lazily creating it.
	 * 
	 * @return extension type manager
	 */
	public static ExtensionTypeManager getInstance() {
		return instance==null ? instance = new ExtensionTypeManager() : instance;
	}
	
	/**
	 * Register a new extension type.
	 * 
	 * @param type extension type name
	 * @param cls extension type class
	 */
	public void registerExtensionType(String type, Class cls) {
		extensionTypes.put(type, cls);
	}

	/**
	 * Unregister a registered extension type.
	 * 
	 * @param type extension type name
	 */
	public void unregisterExtensionType(String type) {
		extensionTypes.remove(type);
	}
	
	/**
	 * Create a new extension type instance given the type name. The
	 * bundle is required for the exception that may get thrown.

	 * @param type type name
	 * @param bundle bundle containing the type
	 * @return extension type instance
	 * @throws ExtensionException if the extension type cannot be located ({@link ExtensionException#UNKNOWN_EXTENSION_TYPE}.
	 */
	public ExtensionType getExtensionType(String type, ExtensionBundle bundle) throws ExtensionException {		
		try {
			if(extensionTypes.containsKey(type)) {
				return (ExtensionType) extensionTypes.get(type).newInstance();
			}
		} catch (Throwable t) {
			log.error("Failed to create extension type " + type, t);
		}		
		throw new ExtensionException(ExtensionException.UNKNOWN_EXTENSION_TYPE, type, bundle.getId());
		
	}
}
