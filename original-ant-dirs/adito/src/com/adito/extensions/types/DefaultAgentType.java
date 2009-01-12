package com.adito.extensions.types;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

import com.adito.agent.AgentExtensionDefinition;
import com.adito.agent.AgentExtensionVerifier;
import com.adito.agent.AgentTunnel;
import com.adito.agent.DefaultAgentManager;
import com.adito.boot.ContextHolder;
import com.adito.core.CoreUtil;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;



/**
 * Special extension type for the <i>Agent</i>.
 */
public class DefaultAgentType extends AbstractJavaType {
	private static Map<String, AgentExtensionDefinition> agentExtensions = new HashMap<String, AgentExtensionDefinition>();
	
	
	/**
	 * Type name
	 */
    public final static String TYPE = "defaultAgent";

	/**
	 * Constructor.
	 */
	public DefaultAgentType() {
		super(TYPE, false);
	}

	/* (non-Javadoc)
	 * @see com.adito.extensions.types.AbstractJavaType#isHidden()
	 */
	public boolean isHidden() {
		return true;
	}

	/* (non-Javadoc)
     * @see com.adito.extensions.types.AbstractJavaType#start(com.adito.extensions.ExtensionDescriptor, org.jdom.Element)
     */
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        super.start(descriptor, element);
        try {
        	if(descriptor.containsFile("agent-en.jar")) {
        		ContextHolder.getContext().addContextLoaderURL(descriptor.getFile("agent-en.jar").toURL());
        	}
        	if(descriptor.containsFile("launcher-en.jar")) {
        		ContextHolder.getContext().addContextLoaderURL(descriptor.getFile("launcher-en.jar").toURL());
        	}
        }
        catch(IOException ioe) {
            throw new ExtensionException(ExtensionException.INTERNAL_ERROR, ioe, "Failed to configure classpath for agent.");
        }
    }

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {
		Element agents = new Element("agents");
		String extensionClasses = "";

		for (Iterator it = agentExtensions.values().iterator(); it.hasNext();) {
			AgentExtensionDefinition def = (AgentExtensionDefinition) it.next();

			if(PluginType.getPlugin(def.getPlugin()) instanceof AgentExtensionVerifier) {
				if(!((AgentExtensionVerifier)PluginType.getPlugin(def.getPlugin())).verifyAccess(def.getName(), 
						session.getUser()))
						continue;
			}
			Element a = (Element)def.getAgentXML().clone(); 

			if (def.getClassName() != null) {
				extensionClasses += (extensionClasses.equals("") ? def.getClassName() : "," + def.getClassName());
			}

			agents.addContent(a);
		}

		// Add the agents element to the root descriptor
		if (agents.getChildren().size() > 0) {
			agents.setAttribute("extensionClasses", extensionClasses);
			element.addContent(0, agents);
		}	
		
	}

	/**
	 * @param agent
	 */
	public static void addAgentExtension(AgentExtensionDefinition agent) {
		agentExtensions.put(agent.getName(), agent);
	}

	/**
	 * @param agent
	 */
	public static void removeAgentExtension(AgentExtensionDefinition agent) {
		agentExtensions.remove(agent.getName());
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
