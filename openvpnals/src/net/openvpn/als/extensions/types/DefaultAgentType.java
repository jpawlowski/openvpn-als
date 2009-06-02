package net.openvpn.als.extensions.types;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

import net.openvpn.als.agent.AgentExtensionDefinition;
import net.openvpn.als.agent.AgentExtensionVerifier;
import net.openvpn.als.agent.AgentTunnel;
import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.policyframework.Resource.LaunchRequirement;
import net.openvpn.als.security.SessionInfo;



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
	 * @see net.openvpn.als.extensions.types.AbstractJavaType#isHidden()
	 */
	public boolean isHidden() {
		return true;
	}

	/* (non-Javadoc)
     * @see net.openvpn.als.extensions.types.AbstractJavaType#start(net.openvpn.als.extensions.ExtensionDescriptor, org.jdom.Element)
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
	 * @see net.openvpn.als.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
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
	 * @see net.openvpn.als.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "extensions";
	}

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.NOT_LAUNCHABLE;
    }

}
