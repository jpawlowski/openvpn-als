
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

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.adito.agent.client.util.BrowserLauncher;

public class SWTAboutDialog extends Dialog {

	private Shell shell;
	private Composite accessory;

	public SWTAboutDialog(Shell parent, String closeText, String title, final Image image, String message, String description,
							String copyright, final String link) {
		super(parent, SWT.TITLE | SWT.CLOSE | SWT.BORDER | SWT.RESIZE);


		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, true));
		
		// Common About Details

		Composite commonAboutDetails = new Composite(shell, 0);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 10;
		GridData data = new GridData(GridData.CENTER, GridData.CENTER, true, true);
//		data.widthHint = 120;
//		data.heightHint = 48;
		commonAboutDetails.setLayoutData(data);		
		commonAboutDetails.setLayout(gridLayout);
		

		// Close
		Button close = new Button(shell, SWT.PUSH);
		close.setText(closeText);
		close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		close.setLayoutData(data);
		
		// Common About Details Components

		Canvas canvas = new Canvas(commonAboutDetails, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});
		data = new GridData(GridData.CENTER, GridData.CENTER, true, true);
		data.widthHint = 48;
		data.heightHint = 48;
		canvas.setLayoutData(data);

		Label messageLabel = new Label(commonAboutDetails, SWT.CENTER);
		messageLabel.setFont(SWTUtil.newFont(parent.getDisplay(), messageLabel.getFont(), 18, SWT.BOLD));
		messageLabel.setText(message);

		Label descriptionLabel = new Label(commonAboutDetails, SWT.WRAP | SWT.BEGINNING);
		descriptionLabel.setText(description);
		data = new GridData();
		data.widthHint = 400;
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		descriptionLabel.setLayoutData(data);

		if (copyright != null) {
			Label copyrightLabel = new Label(commonAboutDetails, SWT.WRAP | SWT.CENTER);
			copyrightLabel.setText(copyright);
			copyrightLabel.setFont(SWTUtil.newFont(parent.getDisplay(), copyrightLabel.getFont(), 8, 0));
			data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			copyrightLabel.setLayoutData(data);
		}
		
		if(link != null) {
			Link linkButton = new Link(commonAboutDetails, SWT.CENTER);
			linkButton.setText("<a href=\"" + link + "\">" + link + "</a>");
			data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			linkButton.setLayoutData(data);
			linkButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {	
				}

				public void widgetSelected(SelectionEvent e) {	
					try {
						BrowserLauncher.openURL(link);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}				
			}); 
		}

		// Accessory

		accessory = new Composite(commonAboutDetails, 0);		
		accessory.setLayout(new GridLayout(1, false));
		data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		accessory.setLayoutData(data);
	}
	
	public void open() {
		shell.pack();
		SWTUtil.center(shell);
		shell.open();
	}

	public Composite getAccessory() {
		return accessory;
	}
}
