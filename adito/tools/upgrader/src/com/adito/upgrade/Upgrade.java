
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
			
package com.adito.upgrade;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Update from version 0.1.16 to 0.2.5+
 */
public class Upgrade {

    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
    }

    final static Log log = LogFactory.getLog(Upgrade.class);

    /**
     * @param args
     * @throws Exception on any error
     */
    public static void main(String[] args) throws Exception {

        boolean gui = System.getProperty("os.name").toLowerCase().startsWith("windows") || System.getenv("DISPLAY") != null;

        if (args.length == 2 || !gui) {
            Upgrader upgrader = new CommandLineUpgrader(args);
            upgrader.upgrade();
        } else {
            JFrame f = new JFrame("0.1.16 to 0.2.5+ Upgrader");
            final Upgrader upgrader = new GUIUpgrader();
            f.setIconImage(new ImageIcon(Upgrade.class.getResource("upgrader-32x32.png")).getImage());
            f.getContentPane().setLayout(new BorderLayout());
            f.getContentPane().add((JPanel)upgrader, BorderLayout.CENTER);
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            final JButton start = new JButton("Start");;
            final JButton close = new JButton("Close");
            start.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        start.setEnabled(false);
                        close.setEnabled(false);
                        upgrader.upgrade();
                    }
                    catch(Exception ex) {
                        upgrader.error("Failed to upgrade.", ex);
                    }
                    finally {
                        close.setEnabled(true);
                    }
                }                
            });
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(close.isEnabled())
                        System.exit(0);
                }                
            });
            bp.add(start);
            bp.add(close);
            f.getContentPane().add(bp, BorderLayout.SOUTH);
            f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    if(close.isEnabled())
                        System.exit(0);
                }
            });
            f.setSize(new Dimension(480, 460));
            UIUtil.positionComponent(SwingConstants.CENTER, f);
            f.setVisible(true);
        }
    }

}
