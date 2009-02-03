
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
			
package com.adito.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.adito.boot.BootProgressMonitor;


/**
 * Swing implementation of the boot progress monitor.
 */
public class SwingBootProgressMonitor implements BootProgressMonitor {
	
	private SplashWindow monitor;
	private JProgressBar progress;
	private String message;
	
	/**
	 * Constructor.
	 *
	 */
	public SwingBootProgressMonitor() {
		URL u = getClass().getResource("/images/enterprise.png");
		if(u == null) {
			u = getClass().getResource("/images/community.png");
		}
		if(u != null) {
				ImageIcon icon = new ImageIcon(u);
			
			// Create progress bar
			progress = new JProgressBar(0, 100);
			progress.setBackground(Color.white);
			progress.setForeground(Color.black);
			progress.setStringPainted(true);
			JPanel pp = new JPanel(new BorderLayout());
			pp.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
			pp.setOpaque(true);
			pp.setBackground(Color.white);
			pp.setForeground(Color.black);
			pp.add(progress, BorderLayout.CENTER);
			
			// Create the monitor / splash window
			monitor = new SplashWindow(null, icon == null ? null : icon.getImage(), 100000, pp);
			monitor.setBackground(Color.white);
			monitor.setForeground(Color.black);
			monitor.setVisible(true);
			monitor.setBorder(BorderFactory.createLineBorder(Color.black));
		}
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.BootProgressMonitor#updateMessage(java.lang.String)
	 */
	public void updateMessage(String message) {
		if(monitor != null) {
			this.message = message;
			progress.setString(message + " - " + progress.getValue() + "%");
		}
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.BootProgressMonitor#updateProgress(int)
	 */
	public void updateProgress(int percent) {
		if(monitor != null) {
			progress.setString(message == null ? percent + "%" : ( message + " - " + percent + "%") );
			progress.setValue(percent);
		}
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.BootProgressMonitor#dispose()
	 */
	public void dispose() {
		if(monitor != null) {
			monitor.hide();
		}
	}

}
