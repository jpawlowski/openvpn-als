package com.adito.agent;


import com.adito.security.User;


/**
 * Not all <i>Agent Extensions</i> are appropriate for all platforms and environments. For
 * example, the <i>Enterprise Drives<i> extension only currently works on Windows and so
 * should not be allowed to downloaded and used on other platforms.
 * <p>
 * When the agent is launched, implementations of this verifier classes are invoked to 
 * check whether the connecting agent is <i>allowed</i> the extension. This is usually
 * done by checking the <i>clientProperties</i> attribute passed in to 
 * {@link #verifyAccess(String, User)}. The extension may then use stuff
 * like <i>os.name</i> to make the descision.
 * 
 * @see AgentExtensionDefinition
 */
public interface AgentExtensionVerifier {

    /**
     * Verify that the requested agent extension is allowed for the agent
     * requesting it.
     * 
     * @param agentExtension agent extension ID
     * @param user username
     * @param clientProperties selected system properties from the client
     * @return agent allowed to use extension
     */
    public boolean verifyAccess(String agentExtension, User user);
}
