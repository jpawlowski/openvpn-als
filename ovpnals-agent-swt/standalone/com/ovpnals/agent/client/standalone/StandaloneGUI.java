
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
			
package com.ovpnals.agent.client.standalone;

import java.io.IOException;

import org.eclipse.swt.graphics.Image;

import com.maverick.http.AuthenticationCancelledException;
import com.maverick.http.HttpAuthenticator;
import com.maverick.http.HttpException;
import com.maverick.http.UnsupportedAuthenticationException;
import com.ovpnals.agent.client.AgentAction;
import com.ovpnals.agent.client.gui.swt.SWTAuthenticationDialog;
import com.ovpnals.agent.client.gui.swt.SWTRunner;
import com.ovpnals.agent.client.gui.swt.SWTSystemTrayGUI;

public class StandaloneGUI extends SWTSystemTrayGUI {

	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(StandaloneGUI.class);
	// #endif

	private Image authImage;
	
	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.gui.swt.SWTSystemTrayGUI#doInit()
	 */
	protected void doInit() {
		super.doInit();
		authImage = loadImage(SWTSystemTrayGUI.class, "/images/authentication-dialog.png"); //$NON-NLS-1$
		
		addMenu("Connection");
		addMenuItem("Connection", new AgentAction() {

			public void actionPerformed() {
				try {
					getAgent().connect("localhost", 443, true, null, null, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedAuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AuthenticationCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public String getAction() {
				return "Connect";
			}
			
		});
		addMenuItem("Connection", new AgentAction() {

			public void actionPerformed() {
				getAgent().disconnect();
			}

			public String getAction() {
				return "Disconnect";
			}			
		});
		addMenuSeperator(null);
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.gui.swt.SWTSystemTrayGUI#exit()
	 */
	protected void exit() {
		super.exit();
		getAgent().startShutdownProcedure();
		// #ifdef DEBUG
		log.info("Exiting JVM."); //$NON-NLS-1$
		// #endif
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.gui.swt.SWTSystemTrayGUI#promptForCredentials(boolean, com.maverick.http.HttpAuthenticator)
	 */
	public boolean promptForCredentials(final boolean proxy, final HttpAuthenticator authenticator) {
		return ((Boolean)SWTRunner.syncExec(getDisplay(), new SWTRunner() {
			public Object doRun() {
				return new Boolean(SWTAuthenticationDialog.promptForCredentials(proxy, getShell(), authenticator, proxy ? null : authImage));
			}
			
		})).booleanValue();
	}

}
