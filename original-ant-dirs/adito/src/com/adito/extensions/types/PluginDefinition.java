
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
			
package com.adito.extensions.types;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.adito.extensions.ExtensionDescriptor;

/**
 */
public class PluginDefinition implements Comparable {

    private List<URL> classPath, resourceBases;
    private String name, className;
    private int order;
    private PluginStatus status;
    private ExtensionDescriptor descriptor;
    private List nativeDirectories;

    /**
     * Constructor.
     *
     * @param descriptor
     */
    public PluginDefinition(ExtensionDescriptor descriptor) {
        classPath = new ArrayList<URL>();
        resourceBases = new ArrayList<URL>();
        status = new PluginStatus();
        nativeDirectories = new ArrayList();
        this.descriptor = descriptor;
    }
    
    public String getName() {
    	return name;
    }

    public void addResourceBase(URL url) {
        resourceBases.add(url);
    }

    public void removeResourceBase(URL url) {
        resourceBases.remove(url);
    }

    public void setDescriptor(ExtensionDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public static void main(String[] args) {
    }
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className The className to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return Returns the classPath.
     */
    public List<URL> getClassPath() {
        return classPath;
    }
    /**
     * @param classPath The classPath to set.
     */
    public void setClassPath(List classPath) {
        this.classPath = classPath;
    }

    /**
     * @return Returns the descriptor.
     */
    public ExtensionDescriptor getDescriptor() {
        return descriptor;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(PluginStatus status) {
        this.status = status;
    }
    /**
     * @return Returns the order.
     */
    public int getOrder() {
        return order;
    }
    /**
     * @param order The order to set.
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        return new Integer(getOrder()).compareTo(new Integer(((PluginDefinition) arg0).getOrder()));
    }

    public PluginStatus getStatus() {
        return status;
    }



    public class PluginStatus {
        Throwable exception;
        int status;

        public PluginStatus() {
        }

        public int getStatus() {
            return status;
        }

        public Throwable getException() {
            return exception;
        }
    }



    /**
     * @param url
     */
    public void addClassPath(URL url) {
        classPath.add(url);
    }

    /**
     * @return
     */
    public List<URL> getResourceBases() {
        return resourceBases;
    }

    public void addNativeDirectory(File directory) {
        nativeDirectories.add(directory);        
    }

    /**
     * @return
     */
    public Iterator getNativeDirectories() {
        return nativeDirectories.iterator();
    }
}
