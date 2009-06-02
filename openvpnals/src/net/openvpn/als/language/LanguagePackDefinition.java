
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
			
package net.openvpn.als.language;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.openvpn.als.extensions.ExtensionDescriptor;


/**
 * Encapsulates a language pack. Language packs may be added to OpenVPNALS via
 * the extensions mechanism (in which case {@link #getExtensionDescriptor()} will
 * return the extension that contains it or it will be loaded via the
 * core in which the {@link #getExtensionDescriptor()} will return <code>null</code>.
 * 
 * @see LanguagePackManager
 */
public class LanguagePackDefinition implements Comparable {
    
    // Private instance variables

    private List classPath;
    private String name;
    private String date;
    private List contains;
    private ExtensionDescriptor descriptor;

    /**
     * Constructor for new empty language pack.
     *
     * @param descriptor the extension descriptor that contains the language pack
     * @param name name of language pack
     */
    public LanguagePackDefinition(ExtensionDescriptor descriptor, String name) {
        this(descriptor, name, new ArrayList());
    }

    /**
     * Constructor. 
     * 
     * @param descriptor the extension descriptor that contains the language pack or <code>null</code> if the pack is part of the core
     * @param name name of language pack
     * @param contains {@link List} of {@link Language} objects supported by this pack
     */
    public LanguagePackDefinition(ExtensionDescriptor descriptor, String name, List contains) {
        classPath = new ArrayList();
        this.descriptor = descriptor;
        this.name = name;
        this.contains = contains;
    }
    
    /**
     * Get the extension descriptor that contains this
     * language pack or <code>null</code> if it is part of
     * the core.
     * 
     *  @return extension descriptor
     */
    public ExtensionDescriptor getExtensionDescriptor() {
    	return descriptor;
    }

    /**
     * Add a new URL to the classpath to search for the language resources.
     * 
     * @param url url to add to search for language resources
     */
    public void addClassPath(URL url) {
        classPath.add(url);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object o) {
        return name.compareTo(((LanguagePackDefinition)o).name);
    }

    /**
     * Get the name of this language pack.
     * 
     * @return name of language pack
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this language pack.
     * 
     * @param name name of language pack
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the creation date of this language pack.
     * 
     * @return date of language pack
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the creation date of this language pack.
     * 
     * @param date creation date of language pack
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get the classpath (a list of {@link String} ojects) that should be
     * added to the server classpath to support this language pack.
     * 
     * @return classpath
     */
    public List getClassPath() {
        return classPath;
    }

    /**
     * This list of language codes as {@link Language} objects
     * this language pack supports.
     * 
     * @return list of language codes contained in this pack
     */
    public List getContains() {
        return contains;
    }
    
    /**
     * Add a new language to the list supported by this language pack
     * 
     * @param language language to add
     */
    public void addLanguage(Language language) {
        contains.add(language);
    }

    /**
     * Get an iterator of all {@link Language}s supported by
     * this pack.
     * 
     * @return iterator of support languages
     */
    public Iterator languages() {        
        return contains.iterator();
    }
}
