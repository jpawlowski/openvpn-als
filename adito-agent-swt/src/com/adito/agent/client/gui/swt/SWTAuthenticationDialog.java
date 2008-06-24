
				/*
 *  Adito
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
			
package com.adito.agent.client.gui.swt;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.maverick.http.HttpAuthenticator;
import com.maverick.http.NTLMAuthentication;
import com.maverick.http.PasswordCredentials;

/**
 * Dialog that may be used to ask for username, password and optionally domain
 * (NTML) when authentication is required.
 */
public class SWTAuthenticationDialog {

	/**
	 * Prompt for credentials.
	 * 
	 * @param authenticator authenticator
	 * @return <code>falsE</code> if cancelled.
	 */
	public static boolean promptForCredentials(boolean proxy, Shell parent, HttpAuthenticator authenticator, final Image image) {
		SWTOptionDialog opt = new SWTOptionDialog(parent,
						SWT.TITLE | SWT.CLOSE | SWT.BORDER,
						Messages.getString("SWTAuthenticationDialog.ok"),
						Messages.getString("SWTAuthenticationDialog.cancel"),
						Messages.getString("SWTAuthenticationDialog.title"),
						null);

		Shell shell = opt.getShell();

		GridLayout gridLayout = new GridLayout(1, false);
		shell.setLayout(gridLayout);

		// Common About Details Components
		if (image != null) {
			Canvas canvas = new Canvas(shell, SWT.NONE);
			canvas.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(image, 0, 0);
				}
			});
			GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
			data.widthHint = image.getBounds().width;
			data.heightHint = image.getBounds().height;
			canvas.setLayoutData(data); 
		}

		// Text
		Label textLabel = new Label(shell, SWT.NONE | SWT.WRAP);
		String info = authenticator.getInformation();
		info = info == null ? "Authentication" : info;
		String host = authenticator.getPort() == 0 ? authenticator.getHost()
			: (authenticator.getHost() + ":" + authenticator.getPort());
		textLabel.setText(MessageFormat.format(Messages.getString("SWTAuthenticationDialog.text"), new Object[] { info, host, authenticator.getScheme() }));
		GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		textLabel.setLayoutData(data);

		// Domain
		Text domainText = null;
		if (authenticator instanceof NTLMAuthentication) {
			Label domainLabel = new Label(shell, SWT.NONE);
			domainLabel.setText(Messages.getString("SWTAuthenticationDialog.domain"));
			domainText = new Text(shell, SWT.BORDER);
			data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
			data.widthHint = 300;
			domainText.setLayoutData(data);
		}

		// Username
		Label usernameLabel = new Label(shell, SWT.NONE);
		usernameLabel.setText(Messages.getString("SWTAuthenticationDialog.username"));
		Text usernameText = new Text(shell, SWT.BORDER);
		usernameText.setText("");
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		data.widthHint = 300;
		usernameText.setLayoutData(data);

		// Password
		Label passwordLabel = new Label(shell, SWT.NONE);
		passwordLabel.setText(Messages.getString("SWTAuthenticationDialog.password"));

		Text passwordText = new Text(shell, SWT.BORDER);
		passwordText.setEchoChar('*');
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		data.widthHint = 300;
		passwordText.setLayoutData(data);

		// Show dialog

		try {
			if (opt.open()) {
				authenticator.setCredentials(new PasswordCredentials(usernameText.getText().trim(), passwordText.getText()));
				if (domainText != null && !domainText.getText().trim().equals("")) { //$NON-NLS-1$
					((NTLMAuthentication) authenticator).setDomain(domainText.getText().trim());
				}
				return true;
			}

			return false;
		} finally {
			opt.close();
		}
	}

}
