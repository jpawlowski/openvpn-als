
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
			
package net.openvpn.als.agent.client.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import net.openvpn.als.agent.client.util.AbstractApplicationLauncher;
import net.openvpn.als.agent.client.util.ApplicationLauncherEvents;

public class AgentLauncherApplicationLauncher extends AbstractApplicationLauncher {


    /**
     * Constructor.
     *
     * @param cacheDir cache directory
     * @param applicationStoreProtocol
     * @param applicationStoreUser
     * @param applicationStoreHost
     * @param applicationStorePort
     * @param parameters
     * @param events
     */
    public AgentLauncherApplicationLauncher(File cacheDir, String applicationStoreProtocol, String applicationStoreUser, String applicationStoreHost, int applicationStorePort, Hashtable parameters, ApplicationLauncherEvents events) {
		super(cacheDir, applicationStoreProtocol, applicationStoreUser, applicationStoreHost, applicationStorePort, parameters, events);
	}

	/**
     * Download the application descriptor. This should not be called directory,
     * it gets called during {@link #prepare()}.
     * 
     * @return application descriptor stream
     * @throws IOException
     */
    protected InputStream getApplicationDescriptor() throws IOException {
    	StringBuffer parms = new StringBuffer();
    	Enumeration en = parameters.keys();
    	while(en.hasMoreElements()) {
    		if(parms.length() == 0) {
    			parms.append("?");  //$NON-NLS-1$
    		}
    		else {
    			parms.append("&");  //$NON-NLS-1$
    		}
            String key = (String) en.nextElement();
            parms.append(key);
            parms.append("="); //$NON-NLS-1$
            parms.append(URLEncoder.encode((String) parameters.get(key)));    		
    	}
    	
    	 URL file = new URL(applicationStoreProtocol, applicationStoreHost, applicationStorePort, "/getExtensionDescriptor.do" //$NON-NLS-1$
             + parms);
         if (events != null)
             events.debug(MessageFormat.format(Messages.getString("VPNLauncher.requestApplicationUsing"), new Object[] { file.toExternalForm() })); //$NON-NLS-1$

         URLConnection con = (URLConnection)file.openConnection();
         con.setUseCaches(false);

         try {
             Method m = con.getClass().getMethod("setConnectTimeout", new Class[] { int.class }); //$NON-NLS-1$
             if (events != null) {
                 events.debug(Messages.getString("VPNLauncher.runtime5")); //$NON-NLS-1$
             }
             m.invoke(con, new Object[] { new Integer(20000) });
             m = con.getClass().getMethod("setReadTimeout", new Class[] { int.class }); //$NON-NLS-1$
             m.invoke(con, new Object[] { new Integer(20000) });
         } catch (Throwable t) {
         }
         return con.getInputStream();
    }
}
