/* HEAD */
package net.openvpn.als.agent.client;

/**
 * <i>Agent Extensions</i> may register new actions to be added to the agents
 * GUI menu.
 * <p>
 * Implement this interface (({@link #getAction()})) to provide the text to
 * use for the action in the GUI (e.g. a menu item in the system tray) and to
 * provide the code to run on callback ({@link #actionPerformed()}).
 */
public interface AgentAction {

    /**
     * Return the text to use for the action in the GUI.
     * 
     * @return action text
     */
    public String getAction();

    /**
     * Perform the action. 
     */
    public void actionPerformed();
}