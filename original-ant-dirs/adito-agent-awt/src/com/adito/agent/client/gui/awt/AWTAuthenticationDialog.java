
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
			
package com.adito.agent.client.gui.awt;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.maverick.http.BasicAuthentication;
import com.maverick.http.HttpAuthenticator;
import com.maverick.http.NTLMAuthentication;
import com.maverick.http.PasswordCredentials;
import com.sshtools.ui.awt.UIUtil;
import com.sshtools.ui.awt.options.OptionDialog;

/**
 * Dialog that may be used to ask for username, password and optionally domain
 * (NTML) when proxy authentication is required.
 */
public class AWTAuthenticationDialog {

    /**
     * Prompt for credentials.
     * 
     * @param authenticator authenticator
     * @return <code>falsE</code> if cancelled.
     */
    public static boolean promptForCredentials(boolean proxy, HttpAuthenticator authenticator) {
        Panel p = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Realm
        if (authenticator instanceof BasicAuthentication) {
            gbc.weightx = 0.0;
            UIUtil.gridBagAdd(p, new Label(Messages.getString("AuthenticationDialog.realm")), gbc, GridBagConstraints.RELATIVE); //$NON-NLS-1$
            gbc.weightx = 1.0;
            UIUtil.gridBagAdd(p, new Label(((BasicAuthentication) authenticator).getRealm()), gbc, GridBagConstraints.REMAINDER);
        }

        // Realm
        TextField domain = null;
        if (authenticator instanceof NTLMAuthentication) {
            gbc.weightx = 0.0;
            UIUtil.gridBagAdd(p, new Label(Messages.getString("AuthenticationDialog.domain")), gbc, GridBagConstraints.RELATIVE); //$NON-NLS-1$
            domain = new TextField("", 15); //$NON-NLS-1$
            gbc.weightx = 1.0;
            UIUtil.gridBagAdd(p, domain, gbc, GridBagConstraints.REMAINDER);
        }

        // Username
        gbc.weightx = 0.0;
        UIUtil.gridBagAdd(p, new Label(Messages.getString("AuthenticationDialog.username")), gbc, GridBagConstraints.RELATIVE); //$NON-NLS-1$
        final TextField username = new TextField("", 15); //$NON-NLS-1$
        gbc.weightx = 1.0;
        UIUtil.gridBagAdd(p, username, gbc, GridBagConstraints.REMAINDER);
        username.requestFocus();

        // Password
        gbc.weightx = 0.0;
        UIUtil.gridBagAdd(p, new Label(Messages.getString("AuthenticationDialog.password")), gbc, GridBagConstraints.RELATIVE); //$NON-NLS-1$
        final TextField password = new TextField("", 15); //$NON-NLS-1$
        gbc.weightx = 1.0;
        UIUtil.gridBagAdd(p, password, gbc, GridBagConstraints.REMAINDER);
        password.requestFocus();
        password.setEchoChar('*');

        final OptionDialog dialog = new OptionDialog(OptionDialog.QUESTION, p, OptionDialog.CHOICES_OK_CANCEL, null);
        username.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dialog.choice(OptionDialog.CHOICE_OK);
            }
        });
        password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dialog.choice(OptionDialog.CHOICE_OK);
            }
        });
        if (domain != null) {
            domain.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    dialog.choice(OptionDialog.CHOICE_OK);
                }
            });
        }

        if (dialog.dialogPrompt(null, Messages.getString("AuthenticationDialog.title")) == OptionDialog.CHOICE_OK) { //$NON-NLS-1$
            authenticator.setCredentials(new PasswordCredentials(username.getText().trim(), password.getText()));
            if (authenticator instanceof NTLMAuthentication && !domain.getText().trim().equals("")) { //$NON-NLS-1$
                ((NTLMAuthentication) authenticator).setDomain(domain.getText().trim());
            }
            return true;
        }

        return false;
    }

}
