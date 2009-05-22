
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.MessageResources;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.core.CoreUtil;

public class ApplicationLauncher {  
  final static Log log = LogFactory.getLog(ApplicationLauncher.class);

  HashSet files;
  HashMap parameters;
  ExtensionType launcherType;
  String typeName;
  MessageResources messageResources;
  Map tunnels;
  ExtensionDescriptor descriptor;
  String description;
  String id;
  String name;

  public static final String UNDEFINED_PARAMETER = "UNDEFINED";

  public ApplicationLauncher(ExtensionDescriptor descriptor) {
    this.descriptor = descriptor;
    this.files = new HashSet();
    this.parameters = new HashMap();
    this.tunnels = new HashMap();
  }

  public boolean isOptions() {
    return parameters.size() > 0;
  }

  public void load(Element element) throws JDOMException, IOException {
    parameters.clear();
    tunnels.clear();
    files.clear();
    

    if (element.getName().equalsIgnoreCase("launcher")) {
      throw new JDOMException("Launcher element must be <launcher>");
    }
    
    typeName = element.getAttribute("type").getValue();
    if (typeName == null) {
      throw new JDOMException("<launcher> element requires attribute 'type'");
    }
    // 
    try {
      String clazz = "com.ovpnals.applications.types." + (
          String.valueOf(typeName.charAt(0)).toUpperCase() + typeName.substring(1) ) + "ApplicationType";
      if (log.isDebugEnabled())
    	  log.debug("Loading type class " + clazz);
      launcherType = (ExtensionType)Class.forName(clazz).newInstance();
    }
    catch(Throwable t) {
      throw new JDOMException("Failed to load launcher extension for type " + typeName + ".");
    }

    id = (element.getAttribute("launcher").getValue());

    if (id == null) {
      throw new JDOMException("<launcher> element requires attribute 'launcher'");
    }

    name = element.getAttribute("name").getValue();
    if (log.isDebugEnabled())
    	log.debug("Launcher name is " + name);   

    if (name == null) {
      throw new JDOMException("<launcher> element requires the attribute 'name'");
    }

    for (Iterator it = element.getChildren().iterator();
         it.hasNext(); ) {
      Element e = (Element) it.next();
      if (e.getName().equalsIgnoreCase("description")) {
        description = e.getText();
      }
      else if (e.getName().equalsIgnoreCase("parameter")) {
        addParameter(e);
      }
      else if (e.getName().equalsIgnoreCase("messages")) {
        ExtensionDescriptorMessageResourcesFactory factory = new ExtensionDescriptorMessageResourcesFactory(e);
        messageResources = factory.createResources("dummy");
        if(messageResources == null) {
          throw new JDOMException("Failed to create message resources.");
        }
      }
      else if (e.getName().equalsIgnoreCase("tunnel")) {
        verifyTunnel(e);
      }
      else if (e.getName().equalsIgnoreCase("files")) {
        verifyFiles(e);
      }
      else {
//        launcherType.load(this, e);
      }
    }
    
    if(messageResources == null) {
      throw new JDOMException("No <messages> element supplied.");
    }
  }
  
  public TunnelDescriptor getTunnel(String name) {
    return (TunnelDescriptor)tunnels.get(name);
  }
  
  public ExtensionType getLauncherType() {
    return launcherType;
  }

  private void addParameter(Element e) throws JDOMException, IOException {
    ApplicationParameterDefinition definition = new ApplicationParameterDefinition(e);
    parameters.put(definition.getName(),
        			definition);
  }

  private void verifyTunnel(Element e) throws JDOMException, IOException {
    String name = e.getAttributeValue("name"); 
    if (name == null || name.equals("")) {
      throw new JDOMException("name attribute required for <tunnel> element");
    }
    String hostname = e.getAttributeValue("hostname"); 
    if (hostname == null || hostname.equals("")) {
      throw new JDOMException(
          "hostname attribute required for <tunnel> element");
    }
    String port = e.getAttributeValue("port");  
    if (port == null || port.equals("")) {
      throw new JDOMException(
          "port attribute required for <tunnel> element");
    }
    boolean usePreferredPort = !("false".equals(e.getAttributeValue("usePreferredPort")));
    tunnels.put(name, new TunnelDescriptor(name, hostname, port, usePreferredPort));

  }

  public ExtensionDescriptor getApplicationDescriptor() throws IOException {
    return descriptor;
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

  private void verifyFiles(Element element) throws JDOMException, IOException {

    for (Iterator it = element.getChildren().iterator();
         it.hasNext(); ) {
      Element e = (Element) it.next();
      if (e.getName().equalsIgnoreCase("if")) {
        verifyFiles(e);
      }
      else if (!e.getName().equalsIgnoreCase("file")) {
        throw new JDOMException("Unexpected element <" + e.getName() +
                                "> found in <files>");
      }
      else {
        processFile(e);
      }
    }

  }

  public void processFile(Element e) throws IOException {

    String filename = e.getText();
    
    File entry = new File(descriptor.getApplicationBundle().getBaseDir(), filename);

    if (!entry.exists()) {
      throw new IOException(
          "File specified in extension.xml does not exist! "
          + entry.getAbsolutePath());
    }

    e.setAttribute("checksum", String.valueOf(CoreUtil.generateChecksum(entry)));
    e.setAttribute("size", String.valueOf(entry.length()));

    files.add(e.getText());
  }

  public boolean containsFile(String filename) {
    return files.contains(filename);
  }

  public File getFile(String filename) throws IOException {
    if (!containsFile(filename)) {
      throw new IOException(filename + " is not a valid application file");
    }
    return new File(descriptor.getApplicationBundle().getBaseDir(), filename);
  }

  /**
   * @return
   */
  public MessageResources getMessageResources() {
    return messageResources;
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
