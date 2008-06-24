
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
			
package com.adito.boot;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public abstract class AbstractXMLDefinedPropertyClass extends AbstractPropertyClass {
    
    final static Log log = LogFactory.getLog(AbstractXMLDefinedPropertyClass.class);

    public AbstractXMLDefinedPropertyClass(String name, boolean supportsReplacementsVariablesInValues, ClassLoader classloader) throws IOException, JDOMException {
        super(name, supportsReplacementsVariablesInValues);
        loadPropertyCategoryDefinitionsFromResources(classloader);
        loadPropertyDefinitionsFromResources();
    }

    public AbstractXMLDefinedPropertyClass(String name, boolean supportsReplacementsVariablesInValues) throws IOException, JDOMException {
        super(name, supportsReplacementsVariablesInValues);
        loadPropertyCategoryDefinitionsFromResources(null);
        loadPropertyDefinitionsFromResources();
    }
    
    
    void loadPropertyCategoryDefinitionsFromResources(ClassLoader classloader) throws IOException, JDOMException {        
        SAXBuilder build = new SAXBuilder();
        if (classloader == null){
            classloader = getClass().getClassLoader();
        }
        for(Enumeration<URL> e = classloader.getResources(
            "META-INF/" + getName() + "-categories.xml"); e.hasMoreElements(); ) {
            URL u = e.nextElement();
            log.info("Loading categories for class "  + getName() + " from " + u);
            Element root = build.build(u).getRootElement();
            if(!root.getName().equals("categories")) {
                throw new JDOMException("Root element in " + u + " should be <categories>");
            }
            for(Iterator i = root.getChildren().iterator(); i.hasNext(); ) {
                Element c = (Element)i.next();
                if(c.getName().equals("category")) {
                    addCategories(c, null);
                }
                else {
                    throw new JDOMException("Expect root element of <categories> with child elements of <category>. Got <" + c.getName() + ">.");
                }
            }
        }
    }

    void loadPropertyDefinitionsFromResources() throws IOException, JDOMException {
        SAXBuilder build = new SAXBuilder();
        for(Enumeration<URL> e = getClass().getClassLoader().getResources(
            "META-INF/" + getName() + "-definitions.xml"); e.hasMoreElements(); ) {
            URL u = e.nextElement();
            log.info("Loading property definitions for class "  + getName() + " from " + u);
            Element root = build.build(u).getRootElement();
            if(!root.getName().equals("definitions")) {
                throw new JDOMException("Root element in " + u + " should be <definitions>");
            }
            for(Iterator i = root.getChildren().iterator(); i.hasNext(); ) {
                Element c = (Element)i.next();
                if(c.getName().equals("definition")) {
                    registerPropertyDefinition(createDefinition(c));
                }
                else {
                    throw new JDOMException("Expect root element of <definitions> with child elements of <definition>. Got <" + c.getName() + ">.");
                }
            }
        }
    }

    void addCategories(Element el, PropertyDefinitionCategory parent) throws JDOMException {
        PropertyDefinitionCategory cat = new DefaultPropertyDefinitionCategory(el.getAttribute("id").getIntValue(), el
                        .getAttributeValue("bundle"), el.getAttributeValue("image"));
        addPropertyDefinitionCategory(parent == null ? -1 : parent.getId(), cat);
        for (Iterator i = el.getChildren().iterator(); i.hasNext();) {
            addCategories((Element) i.next(), cat);
        }
    }
    
    protected PropertyDefinition createDefinition(Element element) throws JDOMException {
    	return new XMLPropertyDefinition(element);
    }

}
