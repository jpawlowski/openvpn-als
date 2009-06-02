
				/*
 *  OpenVPN-ALS
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
			
package net.openvpn.als.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.boot.Util;

/**
 * Singleton responsible for managing registered <i>Language Packs</i> and
 * translation categories.
 * <p>
 * Internally, two lists of categories are maintained, one from resources called
 * <i>ApplicationResources.properties</i> automatically detected via the class
 * path and two from those registered via
 * {@link #registerCategory(LanguageCategory)}.
 * 
 * @see LanguagePackDefinition
 * @see LanguageCategory
 */
public class LanguagePackManager {

    final static Log log = LogFactory.getLog(LanguagePackManager.class);

    // Private instance variables
    private Map<String, LanguagePackDefinition> languagePackDefinitions;
    private List<LanguageCategory> detectedCategories;
    private Hashtable<String, LanguageCategory> detectedHaCategories;
    private List<LanguageCategory> categories;
    private Hashtable<String, LanguageCategory> haCategories;
    private URL[] classpath;

    // Private statics
    private static LanguagePackManager instance;

    /**
     * Although this call is used as a singleton, it has this package 
     * protected constructor for junit tests.
     */
    public LanguagePackManager() {
        super();
        languagePackDefinitions = new HashMap<String, LanguagePackDefinition>();
        categories = new ArrayList<LanguageCategory>();
        haCategories = new Hashtable<String, LanguageCategory>();
    }

    /**
     * Register a new category. This will always be available regardless of
     * whether or not it is found on the classpath. This can be used by plugins
     * to add categories for resource files that do not use the OpenVPN-ALS
     * wide <i>ApplicationResources.properties</i> resource name (e.g.
     * resources for audit reports).
     * 
     * @param category category
     */
    public void registerCategory(LanguageCategory category) {
        categories.add(category);
        haCategories.put(category.getId(), category);
    }

    /**
     * Add a new language pack definition
     * 
     * @param def language pack definition
     */
    public void addLanguagePackDefinition(LanguagePackDefinition def) {
        log.info("Adding new language pack '" + def.getName() + "'");
        if (def.getExtensionDescriptor() != null) {
            log.info("Pack requires host version " + def.getExtensionDescriptor().getApplicationBundle().getRequiredHostVersion());
        }
        languagePackDefinitions.put(def.getName(), def);
    }

    /**
     * Remove a language pack definition
     * 
     * @param def language pack definition
     */
    public void removeLanguagePack(LanguagePackDefinition def) {
        log.info("Removing language pack '" + def.getName() + "'");
        languagePackDefinitions.remove(def.getName());
    }

    /**
     * Get an instance of the language manager.
     * 
     * @return language manager
     */
    public static LanguagePackManager getInstance() {
        if (instance == null) {
            instance = new LanguagePackManager();
        }
        return instance;
    }

    /**
     * Get an iteration of all available {@link LanguagePackDefinition} objects
     * registered.
     * 
     * @return registered language pack definitions
     */
    public Iterator packDefinitions() {
        return languagePackDefinitions.values().iterator();
    }

    /**
     * Get an iteration of all available {@link Language} objects
     * If systemPropertiesFiler is true
     * the system check if some language are wanted to be hiddend. 
     * 
     * @param systemPropertiesFiler
     * @return languagePackDefinitions
     */
    public Iterator languages(boolean systemPropertiesFiler) {
        List<String> hiddenLanguagesList = new ArrayList<String>();
        if (systemPropertiesFiler) {
            String hiddenLanguages = SystemProperties.get("openvpnals.hiddenLanguages", "");
            StringTokenizer t = new StringTokenizer(hiddenLanguages, ",");
            while (t.hasMoreTokens()) {
                hiddenLanguagesList.add(t.nextToken());
            }
        }
        List<Language> l = new ArrayList<Language>();
        for (Iterator i = languagePackDefinitions.values().iterator(); i.hasNext();) {
            LanguagePackDefinition def = (LanguagePackDefinition) i.next();
            for (Iterator j = def.languages(); j.hasNext();) {
                Language lang = (Language) j.next();
                if (!l.contains(lang)) {
                    if (!systemPropertiesFiler
                        || !hiddenLanguagesList.contains(def.getExtensionDescriptor()!= null ? def.getName() : lang.getCode())) {
                        l.add(lang);
                    }
                }
            }
        }
        Collections.sort(l);
        return l.iterator();
    }

    /**
     * Get an iteration of all available {@link Language} objects
     * 
     * @return languagePackDefinitions
     */
    public Iterator languages() {
        return languages(false);
    }
    
    /**
     * Get all internationalisation categories. This includes all categories
     * registered using {@link #registerCategory(LanguageCategory)} and all
     * those found by looking for ApplicationResources.properties files in the
     * classpath.
     * <p>
     * This method returns a list of objects of type {@link LanguageCategory}.
     * <p>
     * The first call to this method may take some time as all JARS and class
     * directories will be scanned.
     * 
     * @return language category object
     * @throws IOException
     */
    public List<LanguageCategory> getCategories() throws IOException {
        checkCategories();
        List<LanguageCategory> l = new ArrayList<LanguageCategory>();
        l.addAll(detectedCategories);
        l.addAll(categories);
        return l;
    }

    synchronized void checkCategories() throws IOException {
        boolean classpathChanged = false;
        if (classpath != null) {
            URL[] newClasspath = ContextHolder.getContext().getContextLoaderClassPath();
            if (classpathDiffer(classpath, newClasspath)) {
                classpath = newClasspath;
                classpathChanged = true;
            }
        } else {
            classpath = ContextHolder.getContext().getContextLoaderClassPath();
        }
        if (detectedCategories == null || classpathChanged) {
            log.info("Scanning classpath for default message resources");
            detectedCategories = new ArrayList<LanguageCategory>();
            detectedHaCategories = new Hashtable<String, LanguageCategory>();
            for (int i = 0; i < classpath.length; i++) {
                log.debug("Scanning " + classpath[i]);
                if (classpath[i].getProtocol().equals("file")) {
                    if (classpath[i].getPath().endsWith(".jar")) {
                        addFileJarCategory(classpath[i]);
                    } else {
                        addFileDirectoryCategory(classpath[i]);
                    }
                }
            }
        }
    }

    boolean classpathDiffer(URL[] classpath, URL[] newClasspath) {
        if (classpath.length != newClasspath.length) {
            return true;
        }
        for (int i = 0; i < classpath.length; i++) {
            if (!classpath[i].equals(newClasspath[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all internationalisation categories. This includes all categories
     * registered using {@link #registerCategory(LanguageCategory)} and all
     * those found by looking for ApplicationResources.properties files in the
     * classpath.
     * <p>
     * This method returns a Hashtable of objects of type
     * {@link LanguageCategory}.
     * <p>
     * The first call to this method may take some time as all JARS and class
     * directories will be scanned.
     * 
     * @return language category object
     * @throws IOException
     */
    public Hashtable getHaCategories() throws IOException {
        checkCategories();
        Hashtable<String, LanguageCategory> all = new Hashtable<String, LanguageCategory>();
        all.putAll(detectedHaCategories);
        all.putAll(haCategories);
        return all;
    }
    
    void addFileDirectoryCategory(URL url) throws IOException {
        File p = new File(Util.urlDecode(url.getPath()));
        List<File> files = new ArrayList<File>();
        findFile(p, "ApplicationResources.properties", files);
        for (Iterator it = files.iterator(); it.hasNext();) {
            File f = (File) it.next();
            String path = f.getAbsolutePath().substring(p.getAbsolutePath().length() + 1).replace("\\", "/");
            InputStream in = null;
            try {
                String name = path;
                String resourceBundleId = path;
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    resourceBundleId = name.substring(0, idx).replace('/', '.');
                    name = name.substring(idx + 1);
                }
                in = new FileInputStream(f);
                LanguageCategory category = new LanguageCategory(in, url, path, resourceBundleId);
                if (!detectedHaCategories.containsKey(category.getId())) {
                    detectedCategories.add(category);
                    detectedHaCategories.put(category.getId(), category);
                }
            }
            finally {
                Util.closeStream(in);
            }
        }
    	
    }
    
    void addFileJarCategory(URL url) throws IOException {

        InputStream in = null;
        try {
            File f = new File(Util.urlDecode(url.getPath()));
            if (!f.exists()) {
                return;
            }
            in = new FileInputStream(Util.urlDecode(url.getPath()));
            ZipInputStream zin = new ZipInputStream(in);
            while (true) {
                ZipEntry entry = zin.getNextEntry();
                if (entry == null) {
                    break;
                }
                String path = entry.getName();
                String name = path;
                String resourceBundleId = path;
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    resourceBundleId = name.substring(0, idx).replace('/', '.');
                    ;
                    name = name.substring(idx + 1);
                }
                if (name.equals("ApplicationResources.properties")) {
                    LanguageCategory category = new LanguageCategory(zin, url, path, resourceBundleId);
                    if (!detectedHaCategories.containsKey(category.getId())) {
                        detectedCategories.add(category);
                        detectedHaCategories.put(category.getId(), category);
                    }
                }
            }
        } finally {
            Util.closeStream(in);
        }
		
	}

	/**
     * Get a language pack given its name (e.g. language-fr).
     * <code>null</code> will be returned if no such language pack exists.
     * 
     * @param name name
     * @return language pack
     */
    public LanguagePackDefinition getLanguagePack(String name) {
    	return (LanguagePackDefinition)languagePackDefinitions.get(name);
    }

    void findFile(File dir, String name, List<File> files) {
        File[] l = dir.listFiles();
        for (int i = 0; l != null && i < l.length; i++) {
            if (l[i].isDirectory()) {
                findFile(l[i], name, files);
            } else if (l[i].isFile() && l[i].getName().equals(name)) {
                files.add(l[i]);
            }
        }
    }
}
