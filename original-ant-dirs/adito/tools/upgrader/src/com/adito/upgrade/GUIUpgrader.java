
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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GUIUpgrader extends JPanel implements DocumentListener, Upgrader {
    
    final static Log log = LogFactory.getLog(GUIUpgrader.class);

    private List upgrades;
    private JPanel mainPanel, upgradeSelectionPanel;
    private JTextField target, source;
    private JButton browseSource, browseTarget;
    private JTextPane console;

    public GUIUpgrader() {
        super(new BorderLayout());
        JPanel info = new JPanel(new BorderLayout(2, 2));
        
//        info.setBackground(Color.white);
//        info.setForeground(Color.black);
//        info.setOpaque(true);
        info.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JLabel l = new JLabel("<html><p>This utility upgrades configuration from "
                        + "one version 0.1.16 installation to another "
                        + "0.2.5+ installation. You may choose which resources you "
                        + "wish to be copied. If resources with the same name already " + "exist they will be left as is.");
        l.setIcon(new ImageIcon(GUIUpgrader.class.getResource("upgrader-48x48.png")));
        info.add(l, BorderLayout.CENTER);
        info.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
        mainPanel = new JPanel(new BorderLayout());
        add(info, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Installations panel
        JPanel installations = new JPanel(new GridBagLayout());
        installations.setBorder(BorderFactory.createTitledBorder("Installations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 2.0;
        UIUtil.jGridBagAdd(installations, new JLabel("Source"), gbc, GridBagConstraints.REMAINDER);
        gbc.weightx = 1.0;
        source = new JTextField();
        source.getDocument().addDocumentListener(this);
        UIUtil.jGridBagAdd(installations, source, gbc, GridBagConstraints.RELATIVE);
        browseSource = new JButton("Browse");
        browseSource.setMnemonic('b');
        browseSource.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(source.getText());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select source installation directory (0.16.1)");
                if (chooser.showOpenDialog(GUIUpgrader.this) == JFileChooser.APPROVE_OPTION) {
                    source.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        gbc.weightx = 0.0;
        UIUtil.jGridBagAdd(installations, browseSource, gbc, GridBagConstraints.REMAINDER);
        gbc.weightx = 2.0;
        UIUtil.jGridBagAdd(installations, new JLabel("Target"), gbc, GridBagConstraints.REMAINDER);
        gbc.weightx = 1.0;
        target = new JTextField(System.getProperty("user.dir"));
        target.getDocument().addDocumentListener(this);
        UIUtil.jGridBagAdd(installations, target, gbc, GridBagConstraints.RELATIVE);
        browseTarget = new JButton("Browse");
        browseTarget.setMnemonic('r');
        browseTarget.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(target.getText());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select target installation directory (0.2.5+)");
                if (chooser.showOpenDialog(GUIUpgrader.this) == JFileChooser.APPROVE_OPTION) {
                    target.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        gbc.weightx = 0.0;
        UIUtil.jGridBagAdd(installations, browseTarget, gbc, GridBagConstraints.REMAINDER);
        mainPanel.add(installations, BorderLayout.NORTH);

        // Upgrade selection
        upgradeSelectionPanel = new JPanel();
        upgradeSelectionPanel.setBorder(BorderFactory.createTitledBorder("Upgrades"));
        upgradeSelectionPanel.setLayout(new BoxLayout(upgradeSelectionPanel, BoxLayout.Y_AXIS));
        mainPanel.add(upgradeSelectionPanel, BorderLayout.CENTER);

    }

    void removeUpgradeSelectionComponent() {
        upgradeSelectionPanel.invalidate();
        upgradeSelectionPanel.removeAll();
        upgradeSelectionPanel.validate();
        upgradeSelectionPanel.repaint();

    }

    void addUpgradeSelectionComponent() {
        upgradeSelectionPanel.invalidate();
        upgradeSelectionPanel.removeAll();
        for (Iterator i = upgrades.iterator(); i.hasNext();) {
            AbstractDatabaseUpgrade upgrade = (AbstractDatabaseUpgrade) i.next();
            JCheckBox box = new JCheckBox(upgrade.getName());
            box.setSelected(upgrade.isSelectedByDefault());
            box.setToolTipText(upgrade.getDescription());
            upgradeSelectionPanel.add(box);
            box.putClientProperty("upgrade", upgrade);
        }
        upgradeSelectionPanel.validate();
    }

    public void changedUpdate(DocumentEvent e) {
        checkInstallations();
    }

    public void insertUpdate(DocumentEvent e) {
        checkInstallations();
    }

    public void removeUpdate(DocumentEvent e) {
        checkInstallations();
    }

    void checkInstallations() {
        File oldDir = new File(source.getText());
        try {
            if (!oldDir.exists() || !oldDir.isDirectory()) {
                throw new Exception(oldDir.getAbsolutePath() + " does not exists or is not a directory");
            }
            File newDir = new File(target.getText());
            if (!newDir.exists() || !newDir.isDirectory()) {
                throw new Exception(newDir.getAbsolutePath() + " does not exists or is not a directory");
            }
            if (oldDir.getCanonicalFile().equals(newDir.getCanonicalFile())) {
                throw new Exception("Source and target installation directories are identical");
            }
            File oldDbDir = new File(oldDir, "db");
            File newDbDir = new File(newDir, "db");
            if (!new File(newDir, "db").exists()) {
                throw new Exception("Target does not appear to be an installation.");
            }
            if (!new File(oldDir, "db").exists()) {
                throw new Exception("Source does not appear to be does not appear to be an installation.");
            }
            if (!new File(newDir, "install").exists()) {
                throw new Exception("Target installation does not appear to be 0.2.5+");
            }
            if (!new File(new File(oldDir, "db"),"explorer_accounts.data").exists()) {
                throw new Exception("Source installation does not appear to be 0.1.15+");
            }

            upgrades = new ArrayList();
            upgrades.add(new UserUpgrade(oldDbDir, newDbDir));
            upgrades.add(new AuthSchemeUpgrade(oldDbDir, newDbDir));
            upgrades.add(new TunnelsUpgrade(oldDbDir, newDbDir));
            upgrades.add(new NetworkPlacesUpgrade(oldDbDir, newDbDir));
            upgrades.add(new WebForwardsUpgrade(oldDbDir, newDbDir));
            upgrades.add(new IPRestrictionsUpgrade(oldDbDir, newDbDir));
            upgrades.add(new ApplicationShortcutsUpgrade(oldDbDir, newDbDir));
            upgrades.add(new ReplacementsUpgrade(oldDbDir, newDbDir));
            addUpgradeSelectionComponent();

        } catch (Exception e) {
            removeUpgradeSelectionComponent();
            upgrades = null;
            upgradeSelectionPanel.add(new JLabel("<html>" + e.getMessage() + "</html>"));
            upgradeSelectionPanel.revalidate();
        }
    }

    public void error(String message) {
        appendString(message, Color.red.darker());
        log.error(message);
    }

    public void error(String message, Throwable exception) {
        appendString(message, Color.red.darker());
        log.error(message, exception);
    }

    public void info(String message) {
        appendString(message, Color.blue.darker());
        log.info(message);
    }

    public void upgrade() throws Exception {
        if (JOptionPane
                        .showConfirmDialog(
                            this,
                            "All selected resources will be now upgrade from the source installation to the target. Are you sure you wish to continue?",
                            "Run Upgrade", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            //
            final List l = new ArrayList();
            for(int i = 0 ; i < upgradeSelectionPanel.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox)upgradeSelectionPanel.getComponent(i);
                if(b.isSelected()) {
                    l.add(b.getClientProperty("upgrade"));
                }
            }
            
            
            removeUpgradeSelectionComponent();
            invalidate();
            removeAll();

            // Progress panel
            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
            final JProgressBar b = new JProgressBar(0, l.size());
            b.setStringPainted(true);
            progressPanel.add(b, BorderLayout.CENTER);
            add(progressPanel, BorderLayout.NORTH);
            
            // Console panel
            JPanel consolePanel = new JPanel(new BorderLayout());
            consolePanel.setBorder(BorderFactory.createTitledBorder("Output"));
            console = new JTextPane();
            JScrollPane scrollPane = new JScrollPane(console);
            consolePanel.add(scrollPane, BorderLayout.CENTER);
            add(consolePanel, BorderLayout.CENTER);
            
            //
            
            validate();
            repaint();
            
            //
            
            Thread t = new Thread() {
                public void run() {
                    try {
                        for(Iterator i = l.iterator(); i.hasNext(); ) {
                            AbstractDatabaseUpgrade upgrade = (AbstractDatabaseUpgrade)i.next();
                            b.setValue(b.getValue() + 1);
                            upgrade.upgrade(GUIUpgrader.this);
                            try {
                                Thread.sleep(750);
                            }
                            catch(InterruptedException ie) {                                
                            }
                        }   
                        info("Complete");
                        Toolkit.getDefaultToolkit().beep();
                    }
                    catch(Exception e) {
                        error("Failed to upgrade.", e);
                    }
                }
            };
            t.start();
            

        }

    }

    public void warn(String message) {
        appendString(message, Color.orange.darker());
        log.warn(message);
    }

    public void warn(String message, Throwable exception) {
        appendString(message, Color.orange.darker());
        log.warn(message, exception);

    }
    
    void appendString(String message, Color c) {
        Document doc = console.getDocument();
        if(doc.getLength() != 0) {
            message = "\n" + message;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, c);
        try {
            doc.insertString(doc.getLength(), message, attr);
        }
        catch(Exception e) {            
        }
        console.setCaretPosition(doc.getLength());
        console.scrollRectToVisible(console.getVisibleRect());
    }
};
