package com.ovpnals.agent;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.ovpnals.extensions.ExtensionDescriptor;

/**
 * Defines everything the server needs to know about an <i>Agent Extension</i>.
 * <p>
 * The <i>OpenVPN-ALS Agent may be extended using <i>Agent Extensions</i>. These
 * are simply additional classes with an optional <b>className</b> that points to 
 * a class that gets initialised when the extension is loaded.
 * <p>
 * Agent extensions that require 3rd party libraries may add additional elements
 * to the classpath using {@link #addClassPath(String)}. 
 * <p>
 * Agent extensions may add additional JVM arguments that will be used when the
 * agent is started using {@link #addJVMArgument(String)}.
 * <p>
 * Before an extension will work, the server calls the 
 * appropriate {@link AgentExtensionVerifier#verifyAccess(String, com.ovpnals.security.User)}.
 * 
 * @see AgentExtensionVerifier
 */
public class AgentExtensionDefinition {

    // Private instance variables
    
    private ExtensionDescriptor descriptor;
    private String className;
    private List classPath = new ArrayList();
    private List jvmArgs = new ArrayList();
    private String plugin;
    private Element agentXML;
    
	/**
     * Constructor.
     * 
	 * @param descriptor descriptor this extension is attached to
	 * @param className class to load or <code>null</code> if this extension just adds new resources to the classpath
	 * @param plugin the plugin the extension is attached to
	 */
	public AgentExtensionDefinition(ExtensionDescriptor descriptor, String className, String plugin, Element agentXML) {
		this.descriptor = descriptor;
		this.className = className;
        this.plugin = plugin;
        agentXML.detach();
        this.agentXML = agentXML;
	}
	
	public Element getAgentXML() {
		return agentXML;
	}
    
    /**
     * Get the ID of the plugin this extension is attached to.
     * 
     * @return plugin id
     */
    public String getPlugin() {
        return plugin;
    }
	
	/**
     * Get the Id of the descriptor this extension is attached to.
     * 
	 * @return plugin id
	 */
	public String getName() {
    	return descriptor.getId();
    }	
	
	/**
     * Get the extension descriptor this agent extension is attached to.
     * 
	 * @return extension descriptor.
	 */
	public ExtensionDescriptor getDescriptor() {
		return descriptor;
	}
	
    /**
     * Get the classname the agent should initialise when the extension is 
     * loaded. This may be <code>null</code> if the extension simply adds more
     * resources to the classpath (e.g. Language Packs).
     * 
     * @return classname of class to initialise on agent startup 
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Get a list of {@link String} objects detailing the classpath
     *  
     * @return classpath
     */
    public List getClassPath() {
    	return classPath;
    }
    
    /**
     * Add a new element to the classpath 
     * @param jarfile
     */
    public void addClassPath(String jarfile) {
    	classPath.add(jarfile);
    }
    
    /**
     * Add an additional JVM argument to be used when the agent is started.
     * 
     * @param arg JVM argument
     */
    public void addJVMArgument(String arg) {
    	jvmArgs.add(arg);
    }
    
    /**
     * Get a list of {@link String} objects details additional JVM arguments that
     * must be used when the agent is launched.
     *  
     * @return jvm arguments
     */
    public List getJVMArguments() {
    	return jvmArgs;
    }

}