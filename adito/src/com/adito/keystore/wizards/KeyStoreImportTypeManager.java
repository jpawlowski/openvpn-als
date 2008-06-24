
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
			
package com.adito.keystore.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows <i>Key Store Import</i> types to be registered and integrated with
 * the import wizard.
 */
public class KeyStoreImportTypeManager {

    // Private instance variables
    private Map<String,AbstractKeyStoreImportType> types;

    // Private statics
    private static KeyStoreImportTypeManager instance;

    /*
     * Private constructor to prevent instantiation
     */
    private KeyStoreImportTypeManager() {
        super();
        types = new HashMap<String,AbstractKeyStoreImportType>();
    }

    /**
     * Register a new key store import type to be made available in the key
     * store import wizard
     * 
     * @param type key store import type
     */
    public void registerType(AbstractKeyStoreImportType type) {
        types.put(type.getName(), type);
    }

    /**
     * Deregister a key store import type from those available in the key store
     * import wizard
     * 
     * @param typeName key store import type name
     */
    public void deregisterType(String typeName) {
        types.remove(typeName);
    }

    /**
     * Get a sorted list of {@link AbstractKeyStoreImportType} implementations to
     * display in the wizard
     * 
     * @return list of types
     */
    public List getTypes() {
        List<AbstractKeyStoreImportType> listOfAbstractKeyStoreImportType = new ArrayList<AbstractKeyStoreImportType>(types.values());
        Collections.sort(listOfAbstractKeyStoreImportType);
        return listOfAbstractKeyStoreImportType;
    }

    /**
     * Get an import type given its name, or <code>null</code> if no such
     * type exists.
     * 
     * @param name name
     * @return import type
     */
    public AbstractKeyStoreImportType getType(String name) {
        return types.get(name);
    }

    /**
     * Get an instance of the key store import type manager.
     * 
     * @return key store import type manager
     */
    public static KeyStoreImportTypeManager getInstance() {
        if (instance == null) {
            instance = new KeyStoreImportTypeManager();
        }
        return instance;
    }

}
