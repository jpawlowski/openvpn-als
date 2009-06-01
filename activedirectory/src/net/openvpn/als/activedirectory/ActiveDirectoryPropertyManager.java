
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
			
package net.openvpn.als.activedirectory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.realms.RealmKey;
import net.openvpn.als.realms.Realm;

final class ActiveDirectoryPropertyManager {

    private static final Log log = LogFactory.getLog(ActiveDirectoryPropertyManager.class);
    private static final String TEMPLATE_FILE = "krb5.template";
    private static final String CONF_DIRECTORY = "activedirectory";
    private static final String CONF_FILE = "krb5.conf";
    
    private static final String PORT_SEPARATOR = ":";
    private static final String START_REPLACEMENT = "${";
    private static final String END_REPLACEMENT = "}";

    private static final String DOMAIN = "DOMAIN";
    private static final String KDC_TIMEOUT = "KDC_TIMEOUT";
    private static final String KDC_MAX_TRIES = "KDC_MAX_RETRIES";
    private static final String HOST_REALMS = "HOST_REALMS";
    private static final String HOST_NAME = "HOST_NAME";
    private static final String HOST_REALM_REPLACEMENT = "kdc = " + START_REPLACEMENT + HOST_NAME + END_REPLACEMENT;
    private static final String DOMAIN_REALMS = "DOMAIN_REALMS";
    private static final String DOMAIN_REALM_REPLACEMENT = "." + START_REPLACEMENT + HOST_NAME + END_REPLACEMENT + " = " + START_REPLACEMENT + DOMAIN + END_REPLACEMENT;

    private final Properties propertyNames;
    private final Realm realm;
    
    ActiveDirectoryPropertyManager(Realm realm, Properties propertyNames) {
        this.propertyNames = propertyNames;
        this.realm = realm;
        System.setProperty("java.security.krb5.conf", getConfFile());
    }

    private static String getConfFile() {
        File tempDirectory = ContextHolder.getContext().getTempDirectory();
        File configurationFile = new File(tempDirectory, CONF_FILE);
        return configurationFile.getAbsolutePath();
    }
    
    void refresh() {
        refresh(Collections.<String, String>emptyMap());
    }
    
    void refresh(Map<String, String> alternativeValues) {
        try {
            doFileReplacement(alternativeValues);
        } catch (IOException e) {
            log.error("Failed to update Active Directory configuration " + CONF_FILE, e);
        }
    }

    private void doFileReplacement(Map<String, String> alternativeValues) throws IOException {
        File confDirectory = ContextHolder.getContext().getConfDirectory();
        File templateFile = new File(confDirectory, CONF_DIRECTORY + getFileSeparator() + TEMPLATE_FILE);
        String readFile = readFile(templateFile);

        File tempDirectory = ContextHolder.getContext().getTempDirectory();
        File configurationFile = new File(tempDirectory, CONF_FILE);
        if (!configurationFile.exists() && !configurationFile.createNewFile()) {
            log.error("Failed to create file " + CONF_FILE + ".");
        } else {
            String replacement = getReplacement(readFile, alternativeValues);
            writeFile(configurationFile, replacement);
        }
    }
    
    private String getReplacement(String fileContents, Map<String, String> alternativeValues) {
        Map<String, String> replacements = buildReplacements(alternativeValues);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            fileContents = replaceValue(fileContents, entry.getKey(), entry.getValue());
        }
        return fileContents;
    }

    private Map<String, String> buildReplacements(Map<String, String> alternativeValues) {
        Map<String, String> replacements = new HashMap<String, String>();
        
        String dbDomain = Property.getProperty(getRealmKey("activeDirectory.domain")).toUpperCase().trim();
        String domain = getRealValue(alternativeValues, DOMAIN, dbDomain);
        replacements.put(DOMAIN, domain);
        
        String dbControllerHost = Property.getProperty(getRealmKey("activeDirectory.controllerHost"));
        String controllerHost = getRealValue(alternativeValues, "activeDirectory.controllerHost", dbControllerHost);
        
        String dbTimeout = String.valueOf(Property.getPropertyInt(getRealmKey("activeDirectory.kdcTimeout")) * 1000);
        String timeout = getRealValue(alternativeValues, "activeDirectory.kdcTimeout", dbTimeout);
        replacements.put(KDC_TIMEOUT, timeout);
        
        String dbMaxTries = String.valueOf(Property.getPropertyInt(getRealmKey("activeDirectory.kdcMaxTries")));
        String maxTries = getRealValue(alternativeValues, "activeDirectory.kdcMaxTries", dbMaxTries);
        replacements.put(KDC_MAX_TRIES, maxTries);
        
        PropertyList dbActiveDirectryUris = new PropertyList();
        dbActiveDirectryUris.add(controllerHost);
        dbActiveDirectryUris.addAll(Property.getPropertyList(getRealmKey("activeDirectory.backupControllerHosts")));
        PropertyList activeDirectryUris = getRealValue(alternativeValues,"activeDirectory.backupControllerHosts", dbActiveDirectryUris);
        replacements.put(HOST_REALMS, buildBackupHostRealms(activeDirectryUris));
        replacements.put(DOMAIN_REALMS, buildBackupDomainRealms(domain, activeDirectryUris));
        return replacements;
    }
    
    private static String getRealValue(Map<String, String> alternativeValues, String key, String value) {
        return alternativeValues.containsKey(key) ? alternativeValues.get(key): value;
    }

    private static PropertyList getRealValue(Map<String, String> alternativeValues, String key, PropertyList values) {
        return alternativeValues.containsKey(key) ? new PropertyList (alternativeValues.get(key) ): values;
    }

    private static String replaceValue(String contents, String key, String value) {
        key = START_REPLACEMENT + key + END_REPLACEMENT;
        return contents.replace(key, value);
    }

    private static String buildBackupHostRealms(PropertyList activeDirectryUris) {
        StringBuffer buffer = new StringBuffer();
        for (String uri : activeDirectryUris) {
            uri = uri.contains(PORT_SEPARATOR) ? uri.substring(0, uri.lastIndexOf(PORT_SEPARATOR)) : uri;
            String replace = replaceValue(HOST_REALM_REPLACEMENT, HOST_NAME, uri);
            buffer.append(replace).append(getLineSeparator());
        }
        return buffer.toString();
    }

    private static String buildBackupDomainRealms(String domain, PropertyList activeDirectryUris) {
        StringBuffer buffer = new StringBuffer();
        for (String uri : activeDirectryUris) {
            uri = uri.contains(PORT_SEPARATOR) ? uri.substring(0, uri.lastIndexOf(PORT_SEPARATOR)) : uri;
            String replace = replaceValue(DOMAIN_REALM_REPLACEMENT, HOST_NAME, uri);
            replace = replaceValue(replace, DOMAIN, domain);
            buffer.append(replace).append(getLineSeparator());
        }
        return buffer.toString();
    }

    private RealmKey getRealmKey(String key) {
        String propertyOrDefault = propertyNames.getProperty(key, key);
        return new RealmKey(propertyOrDefault, realm);
    }

    private static void writeFile(File file, String contents) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(contents);
        } finally {
            close(output);
        }
    }

    private static void close(BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private static String readFile(File file) throws IOException {
        StringBuffer contents = new StringBuffer();
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(getLineSeparator());
            }
        } finally {
            close(input);
        }
        return contents.toString();
    }

    private static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    private static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    private static void close(BufferedReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}