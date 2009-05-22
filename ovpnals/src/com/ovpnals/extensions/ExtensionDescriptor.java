
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
			
package com.ovpnals.extensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.ovpnals.boot.DefaultPropertyDefinitionCategory;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyDefinitionCategory;
import com.ovpnals.boot.SystemProperties;
import com.ovpnals.boot.Util;
import com.ovpnals.boot.XMLPropertyDefinition;
import com.ovpnals.core.CoreMessageResources;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.attributes.AttributesPropertyClass;
import com.ovpnals.properties.attributes.XMLAttributeDefinition;
import com.ovpnals.security.SessionInfo;

public class ExtensionDescriptor implements Comparable {

    final static Log log = LogFactory.getLog(ExtensionDescriptor.class);

    HashSet files;
    HashMap parameters;
    ExtensionType launcherType;
    String typeName;
    CoreMessageResources messageResources;
    Element element;
    Map tunnels;
    String description;
    String id;
    String smallIcon;
    String largeIcon;
    String name;
    ExtensionBundle bundle;
    Element typeElement;
    Element messagesElement;
    Element propertyDefinitionsElement;
    int status;
    boolean hidden = false;
    private List<String> messageKeys;
    private Map<String, PropertyDefinition> propertyDefinitions;
    private Map<Integer, PropertyDefinitionCategory> propertyDefinitionCategories;

    public ExtensionDescriptor() {
        this.files = new HashSet();
        this.parameters = new HashMap();
        this.tunnels = new HashMap();
    }

	public String getTypeName() {
		return typeName;
	}

    public boolean isOptions() {
        return parameters.size() > 0;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getDescription() {
        return description;
    }

    public void load(ExtensionBundle bundle, Element element) throws ExtensionException {

        propertyDefinitions = new HashMap<String, PropertyDefinition>();
        propertyDefinitionCategories = new HashMap<Integer, PropertyDefinitionCategory>();
        messageKeys = new ArrayList<String>();

        this.bundle = bundle;
        this.element = element;

        parameters.clear();
        tunnels.clear();
        files.clear();

        if (log.isInfoEnabled())
            log.info("Loading application descriptor");

        String rootName = element.getName();
        if (rootName.equals("application")) {
            log.warn("DEPRECATED. Extension descriptor in " + bundle.getFile().getPath()
                + " should now have <extension> as the root element not <application>");
        }

        typeName = element.getAttribute("type").getValue();
        if (typeName == null) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "<" + rootName
                + "> element requires attribute 'type'");
        }

        id = element.getAttributeValue("extension");

        if (id == null) {
            id = element.getAttributeValue("application");
            if (id == null) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "<" + element.getName()
                    + "> element requires attribute 'application'");
            } else {
                log.warn("DEPRECATED. In " + bundle.getFile().getPath() + ", <" + rootName
                    + ">'s 'application' attribute should now be 'extension'");
            }
        }
        if (log.isInfoEnabled())
            log.info("Found extension descriptor " + getId());
        if (!id.matches("^[a-zA-Z0-9_-]*$")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "<" + element.getName() + "> attribute '"
                + id + "' may only contain word characters ([a-zA-Z_0-9]).");
        }

        name = element.getAttributeValue("name");
        if (log.isDebugEnabled())
            log.debug("Application name is " + name);
        if (name == null) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "<application> element requires the attribute 'name'");
        }
        smallIcon = element.getAttributeValue("smallIcon");
        largeIcon = element.getAttributeValue("largeIcon");

        if (element.getAttribute("hidden") != null) {
            try {
                hidden = element.getAttribute("hidden").getBooleanValue();
            } catch (DataConversionException e) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, e);
            }
        }

        messagesElement = null;
        typeElement = null;

        processElements(element);

        if(Util.isNullOrTrimmedBlank(description)) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PARSE_DESCRIPTOR,
            "&lt;extension&gt; element must contain a &lt;description&gt;.");    
        }
    }
    
    void processElements(Element element) throws ExtensionException {
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            
            if (e.getName().equalsIgnoreCase("if")) {
                processElements(e);
            } else if (e.getName().equalsIgnoreCase("description")) {
                description = e.getText();
            } else if (e.getName().equalsIgnoreCase("parameter")) {
                addParameter(e);
            } else if (e.getName().equalsIgnoreCase("messages")) {
            	messagesElement = e;
            } else if (e.getName().equalsIgnoreCase("tunnel")) {
                verifyTunnel(e);
            } else if (e.getName().equalsIgnoreCase("files")) {
                verifyFiles(e);
            } else if (e.getName().equals("propertyDefinitions")) {
                propertyDefinitionsElement = e;
            } else if(e.getName().equals(typeName)){
            	if(typeElement != null) {
                    throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                        "<" + e.getName() + "> must occur before type specific element.");            		
            	}
                typeElement = e;
            } else {
            	/* Ignore elements that may be for client side only */
            }
        }
    }

    public void start() throws ExtensionException {
    	/* Before starting this extension, we take a copy of 
    	 * all registered property definitions and categories
    	 * in each class. If the extension fails to start in
    	 * any way, the definitions will be rolled back  
    	 */ 
    	Map<PropertyClass,Collection<PropertyDefinition>> oldDefinitions = new HashMap<PropertyClass, Collection<PropertyDefinition>>();
    	Map<PropertyClass,Collection<PropertyDefinitionCategory>> oldCategories = new HashMap<PropertyClass, Collection<PropertyDefinitionCategory>>();
    	Collection<PropertyClass> oldPropertyClasses = PropertyClassManager.getInstance().getPropertyClasses();
    	try {
	    	for(PropertyClass propertyClass : oldPropertyClasses) {
	    		try {
					propertyClass.store();
				} catch (IOException e) {
					throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e, "Failed to store property class.");
				}
	    	}
    	}
    	catch(ExtensionException ee) {
	    	for(PropertyClass propertyClass : oldPropertyClasses) {
	    		propertyClass.reset();
	    	}
	    	throw ee;
    	}
    	
        try {

			// Load any message resources

			try {
				if (messagesElement != null) {
					messageResources = CoreServlet.getServlet()
							.getExtensionStoreResources();
					for (Iterator i = messagesElement.getChildren().iterator(); i
							.hasNext();) {
						Element el = (Element) i.next();
						if (!el.getName().equals("message")) {
							throw new ExtensionException(
									ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<messages> element may only contain <message> elements.");
						}
						String key = el.getAttributeValue("key");
						if (key == null) {
							throw new ExtensionException(
									ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<message> element must have a key attribute.");
						}
						key = "application." + id + "." + key;
						messageKeys.add(key);
						messageResources
								.setMessage(el.getAttributeValue("locale"),
										key, el.getText());
					}
					messageResources.setMessage("", "application." + getId()
							+ ".name", name);
				}

				// Load any definitions

				if (propertyDefinitionsElement != null) {
					for (Iterator ci = propertyDefinitionsElement.getChildren()
							.iterator(); ci.hasNext();) {
						Element c = (Element) ci.next();
						PropertyClassManager pcm = PropertyClassManager
								.getInstance();
						PropertyClass pc = pcm.getPropertyClass(c.getName());
						if (pc == null) {
							throw new ExtensionException(
									ExtensionException.INTERNAL_ERROR,
									"No property definition class named "
											+ c.getName());
						}
						for (Iterator ei = c.getChildren().iterator(); ei
								.hasNext();) {
							Element ec = (Element) ei.next();
							if (ec.getName().equals("definition")) {
								PropertyDefinition def;
								if (pc instanceof AttributesPropertyClass) {
									def = new XMLAttributeDefinition(ec);
								} else {
									def = new XMLPropertyDefinition(ec);
								}
								propertyDefinitions.put(def.getName(), def);
								pc.registerPropertyDefinition(def);
							} else if (ec.getName().equals("category")) {
								PropertyDefinitionCategory cat = new DefaultPropertyDefinitionCategory(
										ec.getAttribute("id").getIntValue(), ec
												.getAttributeValue("bundle"),
										ec.getAttributeValue("image"));
								cat.setPropertyClass(pc);
								if (ec.getAttributeValue("parent") == null) {
									pc.addPropertyDefinitionCategory(-1, cat);
								} else {
									PropertyDefinitionCategory parentCat = pc
											.getPropertyDefinitionCategory(ec
													.getAttribute("parent")
													.getIntValue());
									if (parentCat == null) {
										throw new ExtensionException(
												ExtensionException.INTERNAL_ERROR,
												"No parent category for "
														+ cat.getId()
														+ " of "
														+ ec
																.getAttributeValue("parent"));
									}
									pc.addPropertyDefinitionCategory(parentCat
											.getId(), cat);
								}
								propertyDefinitionCategories.put(cat.getId(),
										cat);
							} else {
								throw new ExtensionException(
										ExtensionException.INTERNAL_ERROR,
										"Expect child element of <definitions> with child elements of <definition>. Got <"
												+ ec.getName() + ">.");
							}
						}
					}
				}
			} catch (JDOMException e) {
				throw new ExtensionException(
						ExtensionException.FAILED_TO_PARSE_DESCRIPTOR, e,
						"Failed to parse descriptor.");
			}

			// Start the extension type

			launcherType = ExtensionTypeManager.getInstance().getExtensionType(
					typeName, bundle);
			launcherType.start(this, typeElement);
			
			// Reset stored property classes
	    	for(PropertyClass propertyClass : oldPropertyClasses) {
	    		propertyClass.reset();
	    	}
	    	
		} catch (Throwable ee) {
			propertyDefinitionCategories.clear();
			// Failed, so roll back definitions and categories
			for (PropertyClass propertyClass : new ArrayList<PropertyClass>(PropertyClassManager
					.getInstance().getPropertyClasses())) {
				
				// The plugin may have registed property classes so they need to be removed
				if(!oldPropertyClasses.contains(propertyClass)) {
					PropertyClassManager.getInstance().deregisterPropertyClass(propertyClass.getName());
				}
				else {
		    		try {
						propertyClass.restore();
					} catch (IOException e) {
                        log.error("Extension failed for some reason, but the property class cannot be restored. The original exception will follow.", ee);
						throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e, "Failed to restore property class, this is fatal and no further extensions will correctly load.");
					}
				}
			}
            if(ee instanceof ExtensionException) {
                throw (ExtensionException)ee;
            }
            else {
                throw new ExtensionException(ExtensionException.INTERNAL_ERROR, ee);
            }
		}
		launcherType.verifyRequiredElements();
    }

    private void addCat(PropertyDefinitionCategory cat, List<PropertyDefinitionCategory> l) {
    	l.add(cat);
    	for(PropertyDefinitionCategory c : cat.getCategories()) {
    		addCat(c, l);
    	}		
	}

	void addCategories(PropertyClass propertyClass, Element el, PropertyDefinitionCategory parent) throws JDOMException {
        PropertyDefinitionCategory cat = new DefaultPropertyDefinitionCategory(el.getAttribute("id").getIntValue(),
            el.getAttributeValue("bundle"),
            el.getAttributeValue("image"));
        propertyClass.addPropertyDefinitionCategory(parent == null ? -1 : parent.getId(), cat);
        for (Iterator i = el.getChildren().iterator(); i.hasNext();) {
            addCategories(propertyClass, (Element) i.next(), cat);
        }
    }

    public void activate() throws ExtensionException {
        log.info("Activating extension descriptor " + getId());
        if (null == launcherType) {
            launcherType = ExtensionTypeManager.getInstance().getExtensionType(
            typeName, bundle);
        }
        launcherType.activate();
    }

    public void stop() throws ExtensionException {
        log.info("Unloading extension descriptor " + getId());
        unloadDefinitionsCategoriesAndMessages();
        if (launcherType != null) {
            launcherType.stop();
        }
    }

    private void unloadDefinitionsCategoriesAndMessages() {
        for (PropertyDefinition def : propertyDefinitions.values()) {
            def.getPropertyClass().deregisterPropertyDefinition(def.getName());
        }
        for (PropertyDefinitionCategory cat : propertyDefinitionCategories.values()) {
        	if(cat==null || cat.getParent()==null)
        		continue;
            cat.getPropertyClass().removePropertyDefinitionCategory(cat.getParent().getId(), cat);
        }
        for(String messageKey : messageKeys) {
        	messageResources.removeKey(messageKey);
        }
	}

	public boolean canStop() {
        try {
            return launcherType != null ? launcherType.canStop() : true;
        } catch (ExtensionException e) {
            log.error("Failed to determine if extension may be stopped.", e);
            return false;
        }
    }

    public TunnelDescriptor getTunnel(String name) {
        return (TunnelDescriptor) tunnels.get(name);
    }

    public ExtensionType getExtensionType() {
        return launcherType;
    }

    private void addParameter(Element e) throws ExtensionException {
        try {
            ApplicationParameterDefinition definition = new ApplicationParameterDefinition(e);
            parameters.put(definition.getName(), definition);
        } catch (JDOMException jde) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PARSE_DESCRIPTOR, jde);
        }
    }

    private void verifyTunnel(Element e) throws ExtensionException {
        String name = e.getAttributeValue("name");
        if (name == null || name.equals("")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "name attribute required for <tunnel> element");
        }
        String hostname = e.getAttributeValue("hostname");
        if (hostname == null || hostname.equals("")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "hostname attribute required for <tunnel> element");
        }
        String port = e.getAttributeValue("port");
        if (port == null || port.equals("")) {
            throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                "port attribute required for <tunnel> element");
        }
        boolean usePreferredPort = !("false".equals(e.getAttributeValue("usePreferredPort")));
        tunnels.put(name, new TunnelDescriptor(name, hostname, port, usePreferredPort));

    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public Set getParameters() {
        return parameters.keySet();
    }

    public Map getParametersAndDefaults() {
        return parameters;
    }

    public ApplicationParameterDefinition getParameterDefinition(String parameter) {
        return (ApplicationParameterDefinition) parameters.get(parameter);
    }

    public static int[] getVersion(String version) {

        int idx = 0;
        int pos = 0;
        int[] result = new int[0];
        do {

            idx = version.indexOf('.', pos);
            int v;
            if (idx > -1) {
                v = Integer.parseInt(version.substring(pos, idx));
                pos = idx + 1;
            } else {
                int sub = version.indexOf('_', pos);
                if (sub > -1) {
                    v = Integer.parseInt(version.substring(pos, sub));
                } else {
                    v = Integer.parseInt(version.substring(pos));
                }
            }
            int[] tmp = new int[result.length + 1];
            System.arraycopy(result, 0, tmp, 0, result.length);
            tmp[tmp.length - 1] = v;
            result = tmp;

        } while (idx > -1);

        return result;
    }

    private void verifyFiles(Element element) throws ExtensionException {

        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if (e.getName().equalsIgnoreCase("if")) {
                verifyFiles(e);
            } else if (!e.getName().equalsIgnoreCase("file")) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "Unexpected element <" + e.getName()
                    + "> found in <files>");
            } else {
                processFile(e);
            }
        }

    }

    public Set getFiles() {
        return files;
    }

    public void processFile(Element e) throws ExtensionException {

        String filename = e.getAttributeValue("name");
        if(filename == null || filename.equals("")) {
        	 filename = e.getText();
        }

        File entry = getRealFile(filename);

        if (!entry.exists()) {
            if ("true".equals(SystemProperties.get("ovpnals.useDevConfig", "false"))) {
                log.warn("File '" + filename + "' specified in extension descriptor " + bundle.getFile().getAbsolutePath()
                    + " does not exist. As the server is running in Dev. mode, this will be ignored.");
            } else {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "File '" + filename
                    + "' specified in extension.xml does not exist! " + entry.getAbsolutePath());
            }
        } else {
            try {
                e.setAttribute("checksum", String.valueOf(CoreUtil.generateChecksum(entry)));
            } catch (IOException ioe) {
                throw new ExtensionException(ExtensionException.INTERNAL_ERROR, ioe, "Failed to generate checksum.");
            }
        }
        e.setAttribute("size", String.valueOf(entry.length()));

        files.add(filename);
    }

    public boolean containsFile(String filename) {
        return files.contains(filename);
    }

    public File getFile(String filename) throws IOException {

        if (!containsFile(filename)) {
            throw new IOException(filename + " is not a valid application file");
        }
        return getRealFile(filename);
    }
    
    File getRealFile(String filename) {
        if("true".equals(SystemProperties.get("ovpnals.useDevConfig"))) {
        	String basedir = ".." + File.separator + bundle.getId() + File.separator + "build" + File.separator + "extension";
        	if(filename == null) {
        		return new File(basedir);
        	}
            File realFile = new File(basedir, filename);
            if(realFile.exists()) {
            	return realFile;
            }
        }
        String basedir = bundle.getFile().getParent();
    	if(filename == null) {
    		return new File(basedir);
    	}
    	return new File(basedir, filename);
    }

    /**
     * @return
     */
    public CoreMessageResources getMessageResources() {
        return messageResources;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApplicationBundle(ExtensionBundle bundle) {
        this.bundle = bundle;
    }

    public ExtensionBundle getApplicationBundle() {
        return bundle;
    }

    public void removeDescriptor() {
        // TODO 'uninstaller'
    }

    /**
     * ARGGGGGGGGGGGGGGGGGGGGGGGGGGGHHHHHHHHHHHHHHHHHHHHHHHHH IF YOU
     * ARE GOING TO ADD JAVADOC THEN MAKE IT USEFUL. NOT JUST ENOUGH
     * TO GET RID OF THE COMPILER WARNINGS!!!!!!!!!!!!!!!!!!!!!
     * 
     * @return
     */
    public Element createProcessedDescriptorElement(SessionInfo session) {
        Element el = (Element) element.clone();
        try {
			launcherType.descriptorCreated(el, session);
		} catch (IOException e) {
			log.error("Failed to create processed descriptor element.", e);
		}
        return el;
    }

    public int compareTo(Object arg0) {
        int c = bundle == null ? 0 : (bundle.getType() - ((ExtensionDescriptor) arg0).getApplicationBundle().getType());
        return c != 0 ? c : name.compareTo(((ExtensionDescriptor) arg0).name);
    }

    public class TunnelDescriptor {
        private String name;
        private String hostname;
        private String port;
        private boolean usePreferredPort;

        public TunnelDescriptor(String name, String hostname, String port, boolean usePreferredPort) {
            this.name = name;
            this.hostname = hostname;
            this.port = port;
            this.usePreferredPort = usePreferredPort;
        }

        public String getName() {
            return name;
        }

        public String getHostname() {
            return hostname;
        }

        public String getPort() {
            return port;
        }

        public boolean isUsePreferredPort() {
            return usePreferredPort;
        }
    }

}
