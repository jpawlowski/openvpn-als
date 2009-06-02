
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Encapsulates an I18N category. A single category maps to a single
 * resources file in a particular name space. For example, the resource
 * /net.openvpn.als/agent/ApplicationResources.properties maps to the category
 * with the ID of <i>net.openvpn.als.agent</i>.
 * <p>
 * Each category must provide have an ID as a dotted namespace,
 * the base URL the resource came from (i.e. the jar or directory
 * URL), a path for the resource inside the base resource, a name   
 * description and location.
 * <p>
 * The name and description elements will be in <b>English</b> as they
 * are loaded from the default English translation that is included with
 * the core. If you need localised category names you will have to 
 * retrieve then yourself from the appropriate message bundle.  
 * <p>
 * The location determines what component of OpenVPN-ALS the 
 * category is used in. It may currently be one of 
 * {@link #LOCATION_CORE}, {@link #LOCATION_AGENT} or 
 * {@link #LOCATION_LAUNCHER}.
 * <p>
 * The categories available are determined by scanning the
 * classpath for all default english ApplicationResources.properties 
 * resources. Use {@link LanguagePackManager#getCategories()} to retrieve
 * this list of all available categories. 
 */
public class LanguageCategory {
	
	/**
	 * The message key to use to retrieve the category name
	 */
	public final static String NAME_KEY = "openvpnals.resourceBundle.name";
	
	/**
	 * The message key to use to retrieve the category description
	 */
	public final static String DESCRIPTION_KEY = "openvpnals.resourceBundle.description";
	
	/**
	 * The message key to use to retrieve the category location
	 */
	public final static String LOCATION_KEY = "openvpnals.resourceBundle.location";
	
    /**
     * The message key to use to retrieve the extension id
     */
    public final static String EXTENSION_ID_KEY = "openvpnals.resourceBundle.extensionId";
    
    /**
     * Language category is in the core
     */
    public final static String EXTENSION_CORE = "core";
    
	/**
	 * Language category is for the core
	 */
	public final static int LOCATION_CORE = 0;
	
	/**
	 * Language category is for the agent
	 */
	public final static int LOCATION_AGENT = 1;
	
	/**
	 * Language category is for the launcher
	 */
	public final static int LOCATION_LAUNCHER = 2;
    
    // Private instance variables
    private String id;
    private String name;
    private String description;
    private String extensionId;
    private URL base;
    private String path;
    private int location;
    
    /**
     * Constructor for creating a category given a stream in property file format
     * (name/value pairs) that contains the name, description and location properties.
     * 
     * @param in input stream for property file
     * @param base URL of resource that contains category (i.e. .jar or directory file URL)
     * @param path path of category (path separated by / characters)
     * @param id id as a dotted namespace
     * @throws IOException if stream cannot be read
     */
    public LanguageCategory(InputStream in, URL base, String path, String id) throws IOException {
        Properties resources = new Properties();
        resources.load(in);
        name = resources.getProperty(LanguageCategory.NAME_KEY, id);
        description = resources.getProperty(LanguageCategory.DESCRIPTION_KEY, id);
        extensionId = resources.getProperty(LanguageCategory.EXTENSION_ID_KEY, EXTENSION_CORE);
        try {
            location = Integer.parseInt(resources.getProperty(LanguageCategory.LOCATION_KEY, String.valueOf(LanguageCategory.LOCATION_CORE)));
        }
        catch(NumberFormatException nfe) {
            location = LanguageCategory.LOCATION_CORE;
        }
        this.base = base;
        this.path = path;
        this.id = id;
    }
    
    /**
     * Constructor
     * 
     * @param base URL of resource that contains category (i.e. .jar or directory file URL)
     * @param path path of category (path separated by / characters)
     * @param id id as a dotted namespace
     * @param name name of category
     * @param description description of category
     * @param extensionId id of the category's extension
     * @param location location of category. May be one of {@link #LOCATION_CORE}, {@link #LOCATION_AGENT} or {@link #LOCATION_LAUNCHER}.
     */
    public LanguageCategory(URL base, String path, String id, String name, String description, String extensionId, int location) {
        super();
        this.base = base;
        this.path = path;
        this.id = id;
        this.name = name;
        this.description = description;
        this.extensionId = extensionId;
        this.location = location;
    }
    
    /**
     * Get the location this category is to be used. This may
     * be one of {@link #LOCATION_CORE}, {@link #LOCATION_AGENT} or {@link #LOCATION_LAUNCHER}.
     * 
     * @return location
     */
    public int getLocation() {
    	return location;
    }
    
    /**
     * Set the location this category is to be used. This may
     * be one of {@link #LOCATION_CORE}, {@link #LOCATION_AGENT} or {@link #LOCATION_LAUNCHER}.
     * 
     * @param location location
     * @throws IllegalArgumentException on invalid location
     */
    public void setLocation(int location) {
    	if(location == LOCATION_CORE || location == LOCATION_AGENT || location == LOCATION_LAUNCHER) {
        	this.location = location;
    	}
    	else {
    		throw new IllegalArgumentException("Illegal location: "+ location);
    	}
    }
    
    /**
     * Get the <b>English</b> description of this category.
     * 
     * @return english category description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the <b>English</b> description of this category.
     * 
     * @param description english category description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get the ID of this category. This will be in dotted
     * namespace format. For example, <i>net.openvpn.als.agent</i>.
     * 
     * @return category ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ID of this category. This will be in dotted
     * namespace format. For example, <i>net.openvpn.als.agent</i>.
     * 
     * @param id category ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Get the <b>English</b> name of this category.
     * 
     * @return english category name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the <b>English</b> name of this category.
     * 
     * @param name english category name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the URL of the resource this category was discovered
     * in. This will likely either be a file URL pointing to a 
     * JAR or a file URL pointing to a directory.
     * 
     * @return base URL of resource category discovered in
     */
    public URL getBase() {
        return base;
    }

    /**
     * Set the URL of the resource this category was discovered
     * in. This will likely either be a file URL pointing to a 
     * JAR or a file URL pointing to a directory.
     * 
     * @param base base URL of resource category discovered in
     */
    public void setBase(URL base) {
        this.base = base;
    }

    /**
     * Get the path of the resource inside the base resource this
     * category was discovered in. For example, a category with
     * an ID of net.openvpn.als.agent would have been found in
     * /net.openvpn.als/agent.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }


    /**
     * Set the path of the resource inside the base resource this
     * category was discovered in. For example, a category with
     * an ID of net.openvpn.als.agent would have been found in
     * /net.openvpn.als/agent.
     * 
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
    }

    
    /**
     * Get the id of the extension of this
     * category was discovered in. For example, a category with extensionId
     * openvpnals-enterprise-core would have been found in
     * the core extension.
     * 
     * @return extensionId
     */
    public String getExtensionId() {
        return extensionId;
    }

    
    /**
     * Set the id of the extension of this
     * category was discovered in. For example, a category with extensionId
     * openvpnals-enterprise-core would have been found in
     * the core extension.
     * 
     * @param extensionId extension id
     */
    public void setExtensionId(String extensionId) {
        this.extensionId = extensionId;
    }

    
}
