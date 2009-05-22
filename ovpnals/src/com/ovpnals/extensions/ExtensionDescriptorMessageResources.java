/*
 */
package com.ovpnals.extensions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.MessageResources;
import org.jdom.Element;
import org.jdom.JDOMException;


class ExtensionDescriptorMessageResources extends MessageResources {
  
  final Log log = LogFactory.getLog(ExtensionDescriptorMessageResources.class);
  
  HashMap locales;

  ExtensionDescriptorMessageResources(Element element, ExtensionDescriptorMessageResourcesFactory factory, String config) throws JDOMException {
    super(factory, config, true);
    locales = new HashMap();
    for(Iterator i = element.getChildren().iterator(); i.hasNext(); ) {
      Element el = (Element)i.next();
      if(!el.getName().equals("message")) {
        throw new JDOMException("<messages> element may only contain <message> elements.");        
      }
      String key = el.getAttributeValue("key");
      if(key == null) {
        throw new JDOMException("<message> element must have a key attribute.");
      }
      String localeName = el.getAttributeValue("locale");
      if(localeName == null) {
        localeName = "";
      }
      Properties localeResources = (Properties)locales.get(localeName);
      if(localeResources == null) {
        localeResources = new Properties();
        locales.put(localeName, localeResources);
      }
      localeResources.put(key, el.getText());
    }
  }

  /* (non-Javadoc)
   * @see org.apache.struts.util.MessageResources#getMessage(java.util.Locale, java.lang.String)
   */
  public String getMessage(Locale locale, String key) {
    String localeKey =  locale.getCountry() + "_" + locale.getVariant();
    Properties resources = (Properties)locales.get(localeKey);
    if(resources == null) {      
      resources = (Properties)locales.get("");
      if(resources == null) {
        return null;
      }
      else {
        return resources.getProperty(key);        
      }
    }
    else {
      return resources.getProperty(key); 
    }
  }
}