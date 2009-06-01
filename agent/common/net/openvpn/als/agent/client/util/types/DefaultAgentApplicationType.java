
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
			
package net.openvpn.als.agent.client.util.types;

import java.io.IOException;
import java.util.Enumeration;

import net.openvpn.als.agent.client.util.AbstractApplicationLauncher;
import net.openvpn.als.agent.client.util.ApplicationLauncherEvents;
import net.openvpn.als.agent.client.util.Utils;
import net.openvpn.als.agent.client.util.XMLElement;

/**
 * Application type to use for the <i>OpenVPNALS Agent</i>.
 */
public class DefaultAgentApplicationType extends JavaApplicationType {
    /*
     * (non-Javadoc)
     * @see net.openvpn.als.vpn.util.types.JavaApplicationType#prepare(net.openvpn.als.vpn.util.ApplicationLauncher,
     *      net.openvpn.als.vpn.util.ApplicationLauncherEvents,
     *      net.openvpn.als.vpn.util.XMLElement)
     */
    public void prepare(AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, XMLElement element)
                    throws IOException {
        this.launcher = launcher;
        this.events = events;
        if (element.getName().equals("agents")) { //$NON-NLS-1$
            // Process agent extensions
            processAgents(launcher, events, element);
        } else {
            if (element.getName().equals(getTypeName())) { //$NON-NLS-1$
                String jre = (String) element.getAttribute("jre"); //$NON-NLS-1$
                if (isJreSupported(jre)) {
                    super.prepare(launcher, events, element);
                } else {
                    String mesage = Messages.getString("JavaApplicationType.applicationRequires", new Object[] { jre }); //$NON-NLS-1$
                    if (events != null) {
                        events.error(mesage);
                    }
                    throw new IOException(mesage);
                }
            }
        }
    }

    private void processAgents(AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, XMLElement element)
                    throws IOException {
        Enumeration e = element.enumerateChildren();
        String extensionClasses = (String) element.getAttribute("extensionClasses"); //$NON-NLS-1$
        if (extensionClasses != null) {
            addArgument("extensionClasses=" + extensionClasses); //$NON-NLS-1$
        }

        while (e.hasMoreElements()) {
            XMLElement el = (XMLElement) e.nextElement();
            if (el.getName().equalsIgnoreCase("agent")) { //$NON-NLS-1$
                String name = (String) el.getAttribute("name"); //$NON-NLS-1$
                String jre = (String) el.getAttribute("jre"); //$NON-NLS-1$
                if (isJreSupported(jre)) {
                    // Process classpath and/or file elements
                    Enumeration e2 = el.enumerateChildren();
                    while (e2.hasMoreElements()) {
                        XMLElement el2 = (XMLElement) e2.nextElement();
                        processAgentElements(el2, launcher, events, name);
                    }
                } else {
                    String message = Messages.getString("DefaultAgentApplicationType.applicationRequires", new Object[]{name, jre, System.getProperty("java.version")});
                    launcher.processErrorMessage(message); 
                }
            }
        }
    }

    private void processAgentElements(XMLElement el2, AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, String name) throws IOException {
        if (el2.getName().equalsIgnoreCase("if")) {
            if (AbstractApplicationLauncher.checkCondition(this, el2, launcher.getDescriptorParams())) {
                for (Enumeration e = el2.enumerateChildren(); e.hasMoreElements();) {
                    processAgentElements((XMLElement) e.nextElement(), launcher, events, name);
                }
            }
        } else if (el2.getName().equalsIgnoreCase("files")) { //$NON-NLS-1$
            if (AbstractApplicationLauncher.checkCondition(this, el2, launcher.getDescriptorParams())) {
                launcher.processFiles(el2, name);
            }
        } else if (el2.getName().equalsIgnoreCase("classpath")) { //$NON-NLS-1$
            buildClassPath(el2, name);
        } else if (el2.getName().equalsIgnoreCase("jvm")) { //$NON-NLS-1$
            if (AbstractApplicationLauncher.checkCondition(this, el2, launcher.getDescriptorParams())) {
                addJVMArgument(Utils.trimmedBothOrBlank(el2.getContent()));
            }
        }
    }
    
    /**
     * Must check for null, if no jre is specified then we assume all jres are supported
     * @param jre
     * @return true if the supplied JRE value is null or is a supported version
     */
    private static boolean isJreSupported(String jre) {
    	return jre == null || Utils.checkVersion(jre);
    }

    /*
     * (non-Javadoc)
     * @see net.openvpn.als.agent.client.util.ApplicationType#getTypeName()
     */
    public String getTypeName() {
        return "defaultAgent";
    }
}