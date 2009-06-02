/*
 */
package net.openvpn.als.extensions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.jdom.Element;
import org.jdom.JDOMException;


class ExtensionDescriptorMessageResourcesFactory extends MessageResourcesFactory {
  
  private Element element;
  final static Log log = LogFactory.getLog(ExtensionDescriptorMessageResourcesFactory.class);
  
  public ExtensionDescriptorMessageResourcesFactory(Element element) {
    this.element = element;
  }

  /* (non-Javadoc)
   * @see org.apache.struts.util.MessageResourcesFactory#createResources(java.lang.String)
   */
  public MessageResources createResources(String config) {
    try {
      return new ExtensionDescriptorMessageResources(element, this, config);
    }
    catch(JDOMException jde) {
      log.error("Failed to create resources. ", jde);
      return null;
    }
  }
  
}