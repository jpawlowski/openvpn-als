package com.adito.properties;


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
			

import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adito.boot.AbstractPropertyClass;
import com.adito.boot.AbstractPropertyKey;
import com.adito.boot.DefaultPropertyDefinition;
import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;


public class PropertyClassTest {
    
    final static String MEMORY = "Memory";

    @Before
    public void register() {        
        PropertyClassManager.getInstance().registerPropertyClass(new MemoryPropertyClassImpl());
    }
    
    @After
    public void deregister() {        
        PropertyClassManager.getInstance().deregisterPropertyClass(MEMORY);
    }
    
    @Test
    public void registerPropertyDefinitions() {       
        Assert.assertNotNull(PropertyClassManager.getInstance().getPropertyClass(MEMORY));
        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(MEMORY);
        Assert.assertTrue(!propertyClass.isDefinitionExists("con1"));
        propertyClass.registerPropertyDefinition(new DefaultPropertyDefinition(
            DefaultPropertyDefinition.TYPE_BOOLEAN, 
            "con1", "on,off", 10, "true",  50, true));
        Assert.assertTrue(propertyClass.isDefinitionExists("con1"));
    }
    
    @Test
    public void deregisterPropertyDefinitions() {       
        Assert.assertNotNull(PropertyClassManager.getInstance().getPropertyClass(MEMORY));
        PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(MEMORY);
        propertyClass.deregisterPropertyDefinition("con1");
        Assert.assertTrue(!propertyClass.isDefinitionExists("con1"));
    }
    
    class MemoryKey extends AbstractPropertyKey {
        public MemoryKey(String name) {
            super(name, MEMORY);
        }        
    }
    
    class MemoryPropertyClassImpl extends AbstractPropertyClass  {
        
        private Properties properties = new Properties();

        public MemoryPropertyClassImpl() {
            super(MEMORY, false);
        }

        public String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException {
            if(!properties.containsKey(key.getName())) {
                return getDefinition(key.getName()).getDefaultValue();
            }
            return properties.getProperty(key.getName());
        }

        public String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException {
            return properties.setProperty(key.getName(), value).toString();
        }
        
    }
}