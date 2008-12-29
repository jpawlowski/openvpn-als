/*
 */
package com.adito.extensions.types;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.adito.agent.AgentExtensionDefinition;
import com.adito.boot.Util;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.ExtensionType;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;

/**
 * Extension type that adds an <i>Agent Extension</i>.
 */
public class AgentType implements ExtensionType {

    final static Log log = LogFactory.getLog(AgentType.class);

    /**
     * Type name
     */
    public final static String TYPE = "agent";

    // Private instance variables

    private AgentExtensionDefinition def;

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#start(com.adito.extensions.ExtensionDescriptor,
     *      org.jdom.Element)
     */
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {

        if (element.getName().equals(TYPE)) {
            // Agent classname
            String className = element.getAttributeValue("class");
            String plugin = element.getAttributeValue("plugin");
            Element xml = (Element) element.clone();
            def = new AgentExtensionDefinition(descriptor, className, plugin, xml);
            for (Iterator i = xml.getChildren().iterator(); i.hasNext();) {
                processElement((Element) i.next(), descriptor);
            }

            /**
             * Notify the extension store of a new agent plugin
             */
            DefaultAgentType.addAgentExtension(def);
        }
    }
    
    private void processElement(Element xml, ExtensionDescriptor descriptor) throws ExtensionException {
    	
    	for (Iterator i = xml.getChildren().iterator(); i.hasNext();) {
    		Element el = (Element) i.next();
    		if (el.getName().equals("classpath")) {
    			processElement(el, descriptor);
    		} else if(el.getName().equals("if")) {
    			processElement(el, descriptor);
    		} else if(el.getName().equals("file")) {
    			descriptor.processFile(el);
    		} else if(el.getName().equals("jar")) {
    			descriptor.processFile(el);
    		}
    		
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#verifyRequiredElements()
     */
    public void verifyRequiredElements() throws ExtensionException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#getType()
     */
    public String getType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#stop()
     */
    public void stop() throws ExtensionException {
        if (def != null) {
            DefaultAgentType.removeAgentExtension(def);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#active()
     */
    public void activate() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#canStop()
     */
    public boolean canStop() throws ExtensionException {
        return false;
    }

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {		
	}

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "extensions";
	}

    /* (non-Javadoc)
     * @see com.adito.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.NOT_LAUNCHABLE;
    }
}