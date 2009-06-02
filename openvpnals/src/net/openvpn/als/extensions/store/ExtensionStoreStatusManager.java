
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
			
package net.openvpn.als.extensions.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.Util;
import net.openvpn.als.extensions.ExtensionBundle;

final class ExtensionStoreStatusManager {
    private static final String DISABLED_EXTENSIONS_FILE = "disabledExtensions.properties";

    private ExtensionStoreStatusManager() {
    }
    
    static ExtensionBundle.ExtensionBundleStatus getExtensionStatus(String bundleId) throws IOException {
        Properties properties = loadDisabledExtensionProperties();
        if(properties.containsKey(bundleId))
        {
            boolean isSystem = Boolean.valueOf((String)properties.get(bundleId));
            return isSystem ? ExtensionBundle.ExtensionBundleStatus.SYSTEM_DISABLED : ExtensionBundle.ExtensionBundleStatus.DISABLED;
        }
        return ExtensionBundle.ExtensionBundleStatus.ENABLED;
    }
    
    /**
     * Disables the extension, using this mode means it can't be re-enabled
     * @param bundleId
     * @throws IOException
     */
    static void systemDisableExtension(String bundleId) throws IOException {
        disableExtension(bundleId, true);
    }
    
    /**
     * Disables the extension
     * @param bundleId
     * @throws IOException
     */
    static void disableExtension(String bundleId) throws IOException {
        disableExtension(bundleId, false);
    }

    private static void disableExtension(String bundleId, boolean isSystem) throws IOException {
        Properties properties = loadDisabledExtensionProperties();
        properties.put(bundleId, String.valueOf(isSystem));
        storeProperties(properties);        
    }
    
    /**
     * Enables the extension
     * @param bundleId
     * @throws IOException
     */
    static void enableExtension(String bundleId) throws IOException {
        Properties disabledExtensions = loadDisabledExtensionProperties();
        if(!disabledExtensions.containsKey(bundleId))
            return;
        
        boolean isSystem = Boolean.valueOf((String)disabledExtensions.get(bundleId));
        if(isSystem)
            throw new IllegalArgumentException("System disabled extensions cannot be enabled");
        
        disabledExtensions.remove(bundleId);
        storeProperties(disabledExtensions);
    }
    
    /**
     * Remove the extension
     * @param bundleId
     * @throws IOException
     */
    static void removeExtension(String bundleId) throws IOException {
        Properties disabledExtensions = loadDisabledExtensionProperties();
        if(!disabledExtensions.containsKey(bundleId))
            return;        
        disabledExtensions.remove(bundleId);
        storeProperties(disabledExtensions);
    }
    
    /**
     * 
     * @param bundleId
     * @throws IOException
     */
    static void installExtension(String bundleId) throws IOException {
        Properties disabledExtensions = loadDisabledExtensionProperties();
        disabledExtensions.remove(bundleId);
        storeProperties(disabledExtensions);
    }

    private static Properties loadDisabledExtensionProperties() throws IOException {
        InputStream inputStream = new FileInputStream(getDisabledExtensionFile());
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    private static File getDisabledExtensionFile() throws IOException {
        File confDirectory = ContextHolder.getContext().getConfDirectory();
        File file = new File(confDirectory, DISABLED_EXTENSIONS_FILE);
        if(!file.exists() && !file.createNewFile())
            throw new IOException("Failed to create disabled extensions file");
        return file;
    }

    private static void storeProperties(Properties properties) throws IOException {
        OutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(getDisabledExtensionFile());
            properties.store(fileOutputStream, "");
            fileOutputStream.close();
        } finally {
            Util.closeStream(fileOutputStream);
        }
    }
}
