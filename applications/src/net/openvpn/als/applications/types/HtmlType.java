
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
			
package net.openvpn.als.applications.types;

import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.jdom.Element;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import net.openvpn.als.agent.AgentTunnel;
import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.applications.ApplicationLauncherType;
import net.openvpn.als.applications.ApplicationService;
import net.openvpn.als.applications.ApplicationShortcut;
import net.openvpn.als.applications.ApplicationsPlugin;
import net.openvpn.als.boot.ReplacementEngine;
import net.openvpn.als.boot.Replacer;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.BundleActionMessage;
import net.openvpn.als.core.RedirectWithMessages;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.Resource.LaunchRequirement;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;

/**
 * An implementation of an {@link net.openvpn.als.applications.ApplicationLauncherType}
 * that allows launching of applications that may be embedded in a browser
 * such as Java applets and ActiveX controls.
 * <p>
 * If the application extension that supports this launcher has been marked
 * as <i>OpenVPN-ALS Aware</i>, this type will process and display the 
 * provided template page in a new browser window. If the application is
 * not OpenVPN-ALS Aware then the browser will first contact the VPN client
 * to set up any required tunnels before redirect back to the server to
 * process and display the templates.   
 */
public class HtmlType implements ApplicationLauncherType {

    static Log log = LogFactory.getLog(HtmlType.class);
    
    final static String TYPE = "html";

    // Private instance variables
    private String template;
    private String window;
    private boolean openvpnalsAware;

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#start(net.openvpn.als.extensions.ExtensionDescriptor, org.jdom.Element)
     */
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        if (element.getName().equals(TYPE)) {
            openvpnalsAware = "true".equalsIgnoreCase(element.getAttributeValue("openvpnalsAware"));
            window = element.getAttributeValue("window");
            template = element.getAttributeValue("template");
            if (template == null) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "The <html> tag requires the template attribute.");
            }
        }

    }

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#verifyRequiredElements()
     */
    public void verifyRequiredElements() throws ExtensionException {

    }
    
    /**
     * Get the template name to use for HTML processing
     * 
     * @return template name
     */
    public String getTemplate() {
        return template;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return false;
    }    

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#getType()
     */
    public String getType() {
        return TYPE;
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.extensions.ExtensionType#stop()
	 */
	public void stop() throws ExtensionException {		
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.applications.ApplicationLauncherType#launch(java.util.Map, net.openvpn.als.extensions.ExtensionDescriptor, net.openvpn.als.applications.ApplicationShortcut, org.apache.struts.action.ActionMapping, net.openvpn.als.policyframework.LaunchSession, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public ActionForward launch(Map<String, String> parameters, ExtensionDescriptor descriptor, final ApplicationShortcut shortcut, ActionMapping mapping, LaunchSession launchSession, String returnTo, HttpServletRequest request) throws ExtensionException {
		
		// Create the javascript to execute to launch the HTML application window
		
		String windowParsed = null;
        if (window != null) {
            ReplacementEngine engine = new ReplacementEngine();
            engine.addPattern("\\$\\{shortcut:[^}]*\\}", new Replacer() {
                public String getReplacement(Pattern pattern, Matcher matcher, String sequence) {
                    String match = matcher.group();
                    try {
                        String param = match.substring(11, match.length() - 1);
                        String val = shortcut.getParameters().get(param);
                        val = val == null ? "" : val;
                        return val;
                    } catch (Throwable t) {
                    }
                    return "";
                }
            }, null);
            windowParsed = engine.replace(window);
        }

        String script = ""; 
        if (openvpnalsAware) {
            script = "myRef = window.open('" + "/getHTMLApplication.do?"  
            				+ LaunchSession.LAUNCH_ID + "=" +  launchSession.getId() 
                            + "&openvpnals=' + escape(window.location.protocol + '//' + "
                            + "window.location.host + ':' + window.location.port)," + "'_blank'," +
                            "'" + (windowParsed == null ? "" : windowParsed) + "');myRef.focus();";
        } else {
        	
        	SessionInfo session = launchSession.getSession();
        	StringBuffer tunnels = new StringBuffer();
        	
        	if (DefaultAgentManager.getInstance().hasActiveAgent(session)) {
    			try {
    				Request agentRequest = ((ApplicationService) DefaultAgentManager.getInstance().getService(ApplicationService.class)).launchApplication(launchSession);
    				AgentTunnel agent = DefaultAgentManager.getInstance().getAgentBySession(launchSession.getSession());
    				if (!agent.sendRequest(agentRequest, true, 60000)) {
    					throw new ExtensionException(ExtensionException.AGENT_REFUSED_LAUNCH);
    				}
    				ByteArrayReader baw = new ByteArrayReader(agentRequest.getRequestData());
    				try {
	    				while(true) {
	    					String name = baw.readString();
	    					String hostname = baw.readString();
	    					long port = baw.readInt();
	    					if(tunnels.length() > 0) {
	    						tunnels.append(",");
	    					}
	    					tunnels.append(name);
	    					tunnels.append(":");
	    					tunnels.append(hostname);
	    					tunnels.append(":");
	    					tunnels.append(port);
	    					log.info("Got temporary tunnel '" + name + "' = " + port + " (" + hostname + ")");
	    				}
    				}
    				catch(EOFException eofe) {    					
    				}
    			} catch (ExtensionException ee) {
    				throw ee;
    			} catch (Exception e) {
    				throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e);
    			}
    			
    		} 
    		else {
    			throw new ExtensionException(ExtensionException.NO_AGENT);
    		}

            script = "myRef = window.open('" + "/getHTMLApplication.do?" 
							+ LaunchSession.LAUNCH_ID + "=" +  launchSession.getId()
							+ "&tunnels=" + Util.urlEncode(tunnels.toString())
                            + "&openvpnals=' + escape(window.location.protocol + '//' + "
                            + "window.location.host + ':' + window.location.port)," + "'_blank'," +
                            "'" + (windowParsed == null ? "" : windowParsed) + "');myRef.focus();";
        }

    	ActionMessages msgs = new ActionMessages();
    	msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage(ApplicationsPlugin.MESSAGE_RESOURCES_KEY, "launchApplication.launched", shortcut.getResourceName()));
    	
    	if(request != null) {
    		request.setAttribute(Constants.REQ_ATTR_FORWARD_TO, 
    				RedirectWithMessages.addMessages(request, Globals.MESSAGE_KEY, msgs, returnTo) );
    		request.setAttribute(Constants.REQ_ATTR_FOLDER, "");
    		request.setAttribute(Constants.REQ_ATTR_TARGET, "");
    		request.setAttribute(Constants.REQ_ATTR_EXEC_ON_LOAD, script);
    	}
    	
    	return mapping.findForward("redirect");
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.applications.ApplicationLauncherType#isAgentRequired(net.openvpn.als.applications.ApplicationShortcut, net.openvpn.als.extensions.ExtensionDescriptor)
	 */
	public boolean isAgentRequired(ApplicationShortcut shortcut, ExtensionDescriptor descriptor) {
		return !openvpnalsAware;
	}

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#activate()
     */
    public void activate() throws ExtensionException {        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#canStop()
     */
    public boolean canStop() throws ExtensionException {
        return true;
    }    

	/* (non-Javadoc)
	 * @see net.openvpn.als.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {		
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "applications";
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.applications.ApplicationLauncherType#isServiceSide()
	 */
	public boolean isServerSide() {
		return false;
	}

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.REQUIRES_WEB_SESSION;
    }
}