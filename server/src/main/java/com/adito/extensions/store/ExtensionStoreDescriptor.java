
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
			
package com.adito.extensions.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.adito.boot.Util;
import com.adito.boot.VersionInfo;
import com.adito.extensions.ExtensionBundle;
import com.adito.util.Utils;

/**
 *
 */
public class ExtensionStoreDescriptor {
    private static final Log log = LogFactory.getLog(ExtensionStoreDescriptor.class);
    private URL descriptor;
    private String store;
    private Map<String, ExtensionBundle> applicationBundles;
    private List<ExtensionBundle> applicationBundlesList;
    private Document document;

    /**
     * @param descriptor
     * @throws IOException
     * @throws JDOMException
     */
    public ExtensionStoreDescriptor(URL descriptor) throws IOException, JDOMException {
        this.descriptor = descriptor;
        load();
    }

    /**
     * @return descriptor location
     */
    public URL getDescriptorLocation() {
        return descriptor;
    }

    /**
     * @return extension bundles
     */
    public List<ExtensionBundle> getExtensionBundles() {
        return applicationBundlesList;
    }

    /**
     * @return store
     */
    public String getStore() {
        return store;
    }
    
    /**
     * @return descriptor
     * @throws IOException
     */
    public Document getDescriptor() throws IOException {
        return (Document) document.clone();
    }

    /**
     * @param id
     * @return ExtensionBundle
     */
    public ExtensionBundle getApplicationBundle(String id) {
        return (ExtensionBundle) applicationBundles.get(id);
    }

    /**
     * @throws IOException
     * @throws JDOMException
     */
    @SuppressWarnings("unchecked")
    public void load() throws IOException, JDOMException {
        if (log.isInfoEnabled())
            log.info("Loading application descriptor " + descriptor.toExternalForm());
        loadDocument();
        
        applicationBundles = new HashMap<String, ExtensionBundle>();
        applicationBundlesList = new ArrayList<ExtensionBundle>();
        
        for (Iterator itr = document.getRootElement().getChildren().iterator(); itr.hasNext();) {
            Element element = (Element) itr.next();
            if (element.getName().equalsIgnoreCase("install") || element.getName().equalsIgnoreCase("configure")) {

                ExtensionBundle extensionBundle = buildExtensionBundle(element);
                if(applicationBundles.containsKey(extensionBundle.getId())) {
                    throw new JDOMException("Duplicate application bundle id.");
                }
                
                // If supported operating system
                if(extensionBundle.getPlatform()!=null && !extensionBundle.getPlatform().equals("")) {
                	StringTokenizer tokens = new StringTokenizer(extensionBundle.getPlatform(), ",");
	                while(tokens.hasMoreTokens()) {
	                	String platform = tokens.nextToken();
		                if(Utils.isSupportedPlatform(platform) && Utils.isSupportedArch(extensionBundle.getArch())) {
			                applicationBundles.put(extensionBundle.getId(), extensionBundle);
			                applicationBundlesList.add(extensionBundle);
			                break;
		                }
	                }
                } else {
	                applicationBundles.put(extensionBundle.getId(), extensionBundle);
	                applicationBundlesList.add(extensionBundle);
                }
                // End if
            } else {
                throw new JDOMException("Unknown element '" + element.getName() + "'.");
            }
        }

        Collections.sort(applicationBundlesList);
    }
    
    private void loadDocument() throws IOException, JDOMException {
        
        URLConnection conx = descriptor.openConnection();
        conx.setConnectTimeout(ExtensionStore.CONNECT_TIMEOUT);
        conx.setReadTimeout(ExtensionStore.READ_TIMEOUT);
        
        InputStream in = null;
        try {

            in = conx.getInputStream();
            
            SAXBuilder sax = new SAXBuilder();
            document = sax.build(in);

            if (!document.getRootElement().getName().equalsIgnoreCase("applications")) {
                throw new JDOMException("Application root element must be <applications>");
            }

            store = document.getRootElement().getAttribute("store").getValue();
            if (store == null) {
                throw new JDOMException("<applications> element requires attribute 'store'");
            }
        }
        finally {
            Util.closeStream(in);
        }
    }
    
    private static ExtensionBundle buildExtensionBundle(Element element) throws IOException {
        String id = element.getAttributeValue("id");
        if (id == null || id.equals("")) {
            throw new IOException("<" + element.getName() + "> requires an 'id' attribute.");
        }
        String name = element.getAttributeValue("name");
        if (id == null || id.equals("")) {
            throw new IOException("<" + element.getName() + "> requires a 'name' attribute.");
        }

        String instructionsURL = element.getAttributeValue("instructionsURL");
        if (element.getName().equalsIgnoreCase("configure") && (instructionsURL == null || instructionsURL.equals(""))) {
            throw new IOException("The instructionsURL is mandatory to applications of type <configure>.");
        }
        
        String version = element.getAttributeValue("version");
        if (version == null || version.equals("")) {
            throw new IOException("<" + element.getName() + "> requires a 'version' attribute.");
        }
        
        String requiredHostVersionText = element.getAttributeValue("requiredHostVersion");
        VersionInfo.Version requiredHostVersion = null;
        if (requiredHostVersionText != null && !requiredHostVersionText.equals("")) {
            requiredHostVersion = new VersionInfo.Version(requiredHostVersionText);
        }
        
        String license = element.getAttributeValue("license");
        String productURL = element.getAttributeValue("productURL");
        String description = element.getText();
        String dependencies = element.getAttributeValue("depends");
        Collection<String> dependencyNames = Util.isNullOrTrimmedBlank(dependencies) ? null : Arrays.asList(dependencies.split(","));
        String category = element.getAttributeValue("category");
        boolean mandatoryUpdate = Boolean.valueOf(element.getAttributeValue("mandatoryUpdate"));
        Attribute orderAttr = element.getAttribute("order");
        
        String changes = "";
        
        if(element.getChild("changes")!=null) {
        	changes = element.getChildText("changes");
        }
        
        String platform = element.getAttributeValue("platform","");
        String arch = element.getAttributeValue("arch", "");

        if(orderAttr == null) {
            log.warn("In extension store descriptor for " + id + ", <" + element.getName() + "> requires an 'order' attribute. Assuming '99999'");
        }  
        int type = element.getName().equalsIgnoreCase("install") ? ExtensionBundle.TYPE_INSTALLABLE : ExtensionBundle.TYPE_CONFIGUREABLE;
        try {
            return new ExtensionBundle(new VersionInfo.Version(version), type, id, name, description, license, productURL, instructionsURL, requiredHostVersion, dependencyNames, category, mandatoryUpdate, orderAttr == null ? 99999 : orderAttr.getIntValue(), changes, platform, arch);
        }
        catch(DataConversionException dce) {
            throw new IOException("Invalid order attribute. " + dce.getMessage());
        }
    }
}