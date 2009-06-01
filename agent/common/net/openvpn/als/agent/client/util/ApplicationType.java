
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
			
package net.openvpn.als.agent.client.util;

import java.io.IOException;

import net.openvpn.als.agent.client.util.types.JavaApplicationType;

/**
 * The application launching classes are capable of launching applicaitons of
 * different types.
 * <p>
 * Implementatiosn of this interface are responsible for accepting configuration
 * information from the launching classes and performing the actual launch.
 * <p>
 * For example, the {@link JavaApplicationType} must locate Java, create a
 * classpath for the application and launch it in a way appropriate for the
 * current platform.
 */
public interface ApplicationType {
    /**
     * Prepare the application for launch. 
     * 
     * @param launcher launcher
     * @param events events callback
     * @param element configuration element
     * @throws IOException on any error
     */
    public void prepare(AbstractApplicationLauncher launcher, ApplicationLauncherEvents events, XMLElement element) throws IOException;

    /**
     * Start the application
     */
    public void start();

    /**
     * Get the process monitor that is watch the applications output or <code>null</code>
     * if this type doesn't have one/
     * 
     * @return process monitor
     */
    public ProcessMonitor getProcessMonitor();

    /**
     * Get any parameters to add to the redirect when running from the OpenVPNALS
     * web interface. Parameters must be encoded name value pairs separated by '&'.
     *  
     * @return redirect parameters
     */
    public String getRedirectParameters();
    
    /**
     * Get the type name. This is used to determine the element name
     * to look for in the descriptor for the type specific options.
     * 
     * @return type name
     */
    public String getTypeName();

}
