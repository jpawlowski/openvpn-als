
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
			
package net.openvpn.als.agent.client.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import net.openvpn.als.agent.client.TaskProgress;

/**
 * SWT Progress Dialog
 */
public class SWTProgressDialog implements TaskProgress {

	// Private instace variables

	private Shell shell;
	private SWTSystemTrayGUI gui;
	private ProgressBar progressBar;
	private Label label;

	/**
	 * Constructor.
	 * 
	 * @param gui gui
	 * @param note 
	 * @param message 
	 */
	public SWTProgressDialog(SWTSystemTrayGUI gui, final int maxValue, final boolean allowCancel, final String message, final String note) {
		this.gui = gui;

		// Create the shell
		gui.getDisplay().asyncExec(new Runnable() {
			public void run() {
				doInit(maxValue, allowCancel, message, note);
			}			
		});
	}
	
	void doInit(int maxValue, boolean allowCancel, String message, String note) {
		shell = new Shell(gui.getDisplay(), SWT.TITLE | SWT.CLOSE | SWT.BORDER);
		GridLayout gridLayout = new GridLayout ();
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 10;
		shell.setLayout (gridLayout);
		shell.setText(note); //$NON-NLS-1$
		shell.setImage(gui.loadImage(SWTSystemTrayGUI.class, "/images/frame-agent.png")); //$NON-NLS-1$
		
		// Label
		label = new Label(shell, SWT.CENTER);
		label.setText(message);
		GridData data = new GridData ();
		data.horizontalAlignment = GridData.CENTER;
		label.setLayoutData (data);

		// Progress
		progressBar = new ProgressBar(shell, SWT.CENTER);
		progressBar.setMinimum(0);
		progressBar.setMaximum(maxValue);
		progressBar.setSelection(0);
		progressBar.setToolTipText(message);
		data = new GridData ();
		data.widthHint = 200;
		data.horizontalAlignment = GridData.CENTER;
		progressBar.setLayoutData (data);
		
		// Label

		// Clear Button
		if(allowCancel) {
			final Button cancel = new Button(shell, SWT.PUSH);
			cancel.setText(Messages.getString("TaskProgress.cancel")); //$NON-NLS-1$
			cancel.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			data = new GridData ();
			data.horizontalAlignment = GridData.CENTER;
			cancel.setLayoutData (data);
		}

		shell.pack();
		SWTUtil.center(shell);
		shell.open();
	}

	public void dispose() {
		gui.getDisplay().syncExec(new Runnable() {
			public void run() {
				shell.setVisible(false);
			}			
		});
	}

	public void setMessage(final String text) {
		gui.getDisplay().syncExec(new Runnable() {
			public void run() {
				label.setText(text);
			}			
		});
	}

	public void updateValue(final long value) {
		gui.getDisplay().syncExec(new Runnable() {
			public void run() {
				progressBar.setSelection((int)value);
			}			
		});
	}
}