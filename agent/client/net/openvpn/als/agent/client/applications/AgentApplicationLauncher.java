
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.agent.client.applications;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.maverick.multiplex.ChannelOpenException;
import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.WinRegistry;
import net.openvpn.als.agent.client.util.AbstractApplicationLauncher;
import net.openvpn.als.agent.client.util.ApplicationLauncherEvents;
import net.openvpn.als.agent.client.util.Messages;
import net.openvpn.als.agent.client.util.TunnelConfiguration;
import net.openvpn.als.agent.client.util.XMLElement;

/**
 * Extension of {@link AbstractApplicationLauncher} for use with the OpenVPNALS Agent.
 */
public class AgentApplicationLauncher extends AbstractApplicationLauncher {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AgentApplicationLauncher.class);
    // #endif

    private Agent agent;
    private String name;
    private String descriptor;
    private Vector tunnels = new Vector();
    
    /**
     * Constructor.
     * 
     * @param vpn agent
     * @param parameters parameters
     * @param events events callback
     */
    public AgentApplicationLauncher(Agent vpn, String name, Hashtable parameters, String descriptor, ApplicationLauncherEvents events) {
        super(vpn.getConfiguration().getCacheDir(), "https", vpn.getUsername(), vpn.getOpenVPNALSHost(), vpn.getOpenVPNALSPort(), parameters, events);
        this.agent = vpn;
        this.name = name;
        this.descriptor = descriptor;
    }
    
    public Agent getAgent() {
    	return agent;
    }

    protected void createTunnel(XMLElement e) throws IOException {

        String hostname = (String) e.getAttribute("hostname"); //$NON-NLS-1$
        String name = (String) e.getAttribute("name"); //$NON-NLS-1$
        int port = Integer.parseInt((String) e.getAttribute("port")); //$NON-NLS-1$
        boolean usePreferredPort = ("true".equals(e.getAttribute("usePreferredPort"))); //$NON-NLS-1$ //$NON-NLS-2$
        boolean singleConnection = !("false".equals(e.getAttribute("singleConnection"))); //$NON-NLS-1$ //$NON-NLS-2$
        boolean localhostWorkaround = "true".equals(e.getAttribute("localhostWorkaround")); //$NON-NLS-1$ //$NON-NLS-2$
        if (events != null) {
            TunnelConfiguration listeningSocketConfiguration = events.createTunnel(name,
                hostname,
                port,
                usePreferredPort,
                singleConnection, 
                localhostWorkaround ? "127.0.0.2" : agent.getConfiguration().getLocalhostAddress());
            tunnels.addElement(listeningSocketConfiguration);
        } else
            throw new IOException(Messages.getString("ApplicationLauncher.tunnelRequiredButNoEventHandler")); //$NON-NLS-1$

    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.agent.client.util.AbstractApplicationLauncher#replaceTokens(java.lang.String)
     */
    public String replaceTokens(String str) {
    	str = super.replaceTokens(str);
        for (Enumeration e = tunnels.elements(); e.hasMoreElements();) {
            TunnelConfiguration listeningSocketConfiguration = (TunnelConfiguration) e.nextElement();
            String paramHost = "${tunnel:" + listeningSocketConfiguration.getName() + ".hostname}"; //$NON-NLS-1$ //$NON-NLS-2$
            String paramPort = "${tunnel:" + listeningSocketConfiguration.getName() + ".port}"; //$NON-NLS-1$ //$NON-NLS-2$

            str = replaceAllTokens(str, paramHost, listeningSocketConfiguration.getSourceInterface()); //$NON-NLS-1$
            str = replaceAllTokens(str, paramPort, String.valueOf(listeningSocketConfiguration.getSourcePort()));
        }
        return str;
    }

    /**
     * Get a list of {@link TunnelConfiguration} objects that this
     * application launcher requires.
     * 
     * @return tunnels
     */
    public Vector getTunnels() {
        return tunnels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncher#processLauncherElement(net.openvpn.als.vpn.util.XMLElement)
     */
    protected boolean processLauncherElement(XMLElement element) {

        if (element.getName().equalsIgnoreCase("registry")) {
            Enumeration e = element.enumerateChildren();

            while (e.hasMoreElements()) {
                XMLElement el = (XMLElement) e.nextElement();

                processRegistryElements(el);
            }
            return true;
        } else
            return false;
    }

    private void processRegistryElements(XMLElement el) {
        if (el.getName().equalsIgnoreCase("get")) {
            String scope = el.getStringAttribute("scope");
            String key = replaceTokens(el.getStringAttribute("key"));
            String value = replaceTokens(el.getStringAttribute("value"));
            String param = replaceTokens(el.getStringAttribute("parameter"));
            String defaultValue = replaceTokens(el.getStringAttribute("default"));

            addParameter(param, WinRegistry.getRegistryValue(scope, key, value, defaultValue == null ? "" : defaultValue));
        } else if (el.getName().equalsIgnoreCase("set")) {
            String scope = el.getStringAttribute("scope");
            String key = replaceTokens(el.getStringAttribute("key"));
            String value = replaceTokens(el.getStringAttribute("value"));
            String arg = replaceTokens(el.getStringAttribute("arg"));

            WinRegistry.setRegistryValue(scope, key, value, arg);

        } else if (el.getName().equalsIgnoreCase("if")) {

            String scope = el.getStringAttribute("scope");
            String key = replaceTokens(el.getStringAttribute("key"));
            String value = replaceTokens(el.getStringAttribute("value"));
            String notAttr = el.getStringAttribute("not");
            String existsAttr = el.getStringAttribute("exists");
            String equalsAttr = el.getStringAttribute("equals");

            if (existsAttr != null) {
                boolean exists = Boolean.getBoolean(existsAttr);
                String v = WinRegistry.getRegistryValue(scope, key, value, "DEFAULT_VALUE");
                if (v.equals("DEFAULT_VALUE") && !exists) {
                    processRegistryElements(el);
                } else if (!v.equals("DEFAULT_VALUE") && exists) {
                    processRegistryElements(el);
                }
            } else if (notAttr != null) {
                boolean not = Boolean.getBoolean(notAttr == null ? "false" : notAttr);
                String v = WinRegistry.getRegistryValue(scope, key, value, "");

                if (equalsAttr.equals(v) && !not) {
                    processRegistryElements(el);
                } else if (!equalsAttr.equals(v) && not) {
                    processRegistryElements(el);
                }
            }

        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vpn.util.ApplicationLauncher#getApplicationDescriptor()
     */
    public InputStream getApplicationDescriptor() throws IOException {

        if (events != null)
            events.debug("Getting application descriptor using Maverick HTTP client");

		return new ByteArrayInputStream(descriptor.getBytes());
    }

    /**
     * Get download file.
     * 
     * @param name name
     * @param ticket ticket
     * @param filename filename
     * @return stream
     * @throws IOException on any error
     */
    public InputStream getDownloadFile(String name, String ticket, String filename) throws IOException {

        if (events != null)
            events.debug("Downloading application file " + filename);

        try {
			ApplicationFileChannel channel = new ApplicationFileChannel(name, ticket, filename);
			agent.getConnection().openChannel(channel);
			return channel.getInputStream();
		} catch (ChannelOpenException e) {
			throw new IOException(e.getMessage());
		}
        
    }
}
