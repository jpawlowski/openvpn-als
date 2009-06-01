/*
 *  Gruntspud
 *
 *  Copyright (C) 2002 Brett Smith.
 *
 *  Written by: Brett Smith <t_magicthize@users.sourceforge.net>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.sshtools.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Choose a font
 * 
 * @author Brett Smith
 */
public class FontChooser extends JPanel {
    public final static String AUTOMATIC = "__AUTOMATIC__";

    public static final Font AUTOMATIC_FONT = new Font(AUTOMATIC, Font.PLAIN, 10);

    private static FontNameListModel fontNameListModel;
    private JList fontNameList;
    private JList fontSizeList;
    private JTextField fontName;
    private Font chosenFont;
    private JCheckBox bold;
    private JCheckBox italic;
    private JFormattedTextField fontSize;
    private JLabel preview;
    private boolean adjusting;
    private boolean sizeIsAdjusting;

    /**
     * Construct a <code>FontChooser</code>
     * 
     * @param none
     */
    public FontChooser() {
        this(null, true, false, false, true);
    }

    /**
     * Construct a <code>FontChooser</code>
     * 
     * @param font initiali font
     * @param allowAutomatic
     */
    public FontChooser(Font font, boolean showSize, boolean monospacedOnly, boolean allowAutomatic, boolean showStyles) {
        super(new BorderLayout());

        fontNameList = new JList();
        fontNameList.setCellRenderer(new FontNameListCellRenderer());
        fontNameList.setVisibleRowCount(7);
        // fontNameList.setAutoscrolls(true);
        fontNameList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!fontNameList.isSelectionEmpty() && !e.getValueIsAdjusting()) {
                    try {
                        adjusting = true;
                        String fn = ((Font) fontNameList.getSelectedValue()).getName();
                        fontName.setText(fn.equals(AUTOMATIC) ? "Automatic" : fn);
                        fontName.requestFocus();
                    } catch (IllegalStateException iee) {
                    }
                    adjusting = false;
                    changeFontBasedOnState();
                }
            }
        });

        // Create the font style selection panel
        JPanel stylePanel = null;
        if (showStyles) {
            stylePanel = new JPanel(new GridBagLayout());
            stylePanel.setBorder(BorderFactory.createTitledBorder("Font style"));

            GridBagConstraints gBC = new GridBagConstraints();
            gBC.fill = GridBagConstraints.BOTH;
            gBC.anchor = GridBagConstraints.CENTER;
            gBC.weighty = 0.0;
            gBC.insets = new Insets(2, 2, 2, 2);

            ActionListener l = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    changeFontBasedOnState();
                }
            };

            UIUtil.jGridBagAdd(stylePanel, bold = new JCheckBox("Bold"), gBC, GridBagConstraints.REMAINDER);
            bold.addActionListener(l);
            bold.setMnemonic('b');
            UIUtil.jGridBagAdd(stylePanel, italic = new JCheckBox("Italic"), gBC, GridBagConstraints.REMAINDER);
            italic.setMnemonic('i');
            italic.addActionListener(l);
        }

        // Create the font size list
        // @todo make this more specific to the font. not sure how yet :-)
        JPanel sizePanel = null;
        if (showSize) {
            fontSizeList = new JList(new Integer[] { new Integer(8), new Integer(9), new Integer(10), new Integer(11),
                            new Integer(12), new Integer(14), new Integer(16), new Integer(18), new Integer(20), new Integer(22),
                            new Integer(24), new Integer(26), new Integer(28), new Integer(36), new Integer(48), new Integer(72) });
            fontSizeList.setVisibleRowCount(4);
            fontSizeList.setAutoscrolls(true);
            fontSizeList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!fontNameList.isSelectionEmpty() && !e.getValueIsAdjusting()) {
                        try {
                            fontSize.setValue(((Integer) fontSizeList.getSelectedValue()));
                        } catch (IllegalStateException iee) {
                        }

                        changeFontBasedOnState();
                    }
                }
            });

            // Create the font size selection panel
            sizePanel = new JPanel(new GridBagLayout());
            sizePanel.setBorder(BorderFactory.createTitledBorder("Font size"));

            GridBagConstraints gBC3 = new GridBagConstraints();
            gBC3.fill = GridBagConstraints.BOTH;
            gBC3.anchor = GridBagConstraints.WEST;
            gBC3.weightx = 1.0;
            gBC3.weighty = 0.0;
            gBC3.insets = new Insets(2, 2, 2, 2);
            UIUtil.jGridBagAdd(sizePanel, new JLabel("Size:"), gBC3, GridBagConstraints.REMAINDER);
            UIUtil.jGridBagAdd(sizePanel, fontSize = new JFormattedTextField(new Integer(4)), gBC3, GridBagConstraints.REMAINDER);
            fontSize.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    if (!sizeIsAdjusting)
                        changeFontBasedOnState();
                }

                public void removeUpdate(DocumentEvent e) {
                    if (!sizeIsAdjusting)
                        changeFontBasedOnState();
                }

                public void changedUpdate(DocumentEvent e) {
                    if (!sizeIsAdjusting)
                        changeFontBasedOnState();
                }
            });
            gBC3.weighty = 1.0;
            UIUtil.jGridBagAdd(sizePanel, new JScrollPane(fontSizeList), gBC3, GridBagConstraints.REMAINDER);
        }

        // Create the panel where selection of the font name takes place
        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setBorder(BorderFactory.createTitledBorder("Font name"));

        GridBagConstraints gBC2 = new GridBagConstraints();
        gBC2.fill = GridBagConstraints.BOTH;
        gBC2.anchor = GridBagConstraints.WEST;
        gBC2.weightx = 1.0;
        gBC2.weighty = 0.0;
        gBC2.insets = new Insets(2, 2, 2, 2);
        UIUtil.jGridBagAdd(namePanel, new JLabel("Name:"), gBC2, GridBagConstraints.REMAINDER);
        UIUtil.jGridBagAdd(namePanel, fontName = new JTextField(10), gBC2, GridBagConstraints.REMAINDER);
        fontName.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                fontName.selectAll();
            }

            public void focusLost(FocusEvent e) {
            }
        });
        gBC2.weighty = 1.0;
        UIUtil.jGridBagAdd(namePanel, new JScrollPane(fontNameList), gBC2, GridBagConstraints.REMAINDER);

        // Create the preview label
        preview = new JLabel("Some sample text") {
            public Dimension getMinimumSize() {
                return new Dimension(super.getMinimumSize().width, 64);
            }

            public Dimension getPreferredSize() {
                return new Dimension(320, 64);
            }
        };
        preview.setBackground(Color.white);
        preview.setForeground(Color.black);
        preview.setOpaque(true);
        preview.setHorizontalAlignment(SwingConstants.CENTER);
        preview.setBorder(BorderFactory.createLineBorder(Color.black));

        // Create the preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.add(preview, BorderLayout.CENTER);

        // Create the right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        if (stylePanel != null) {
            rightPanel.add(stylePanel, BorderLayout.NORTH);
        }
        if (sizePanel != null) {
            rightPanel.add(sizePanel, BorderLayout.CENTER);
        }

        // Listen for changes in the font name and select the closest font in
        // the list
        fontName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!adjusting) {
                    findClosestFont();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (!adjusting) {
                    findClosestFont();
                }
            }

            public void changedUpdate(DocumentEvent e) {
                if (!adjusting) {
                    findClosestFont();
                }
            }
        });

        //
        add(namePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(previewPanel, BorderLayout.SOUTH);

        // Lazily create the font list name model
        if (fontNameListModel == null) {
            fontNameList.setEnabled(false);
            if(fontSizeList != null) {
            	fontSizeList.setEnabled(false);
                fontSize.setEnabled(false);
            }
            if (bold != null) {
                bold.setEnabled(false);
                italic.setEnabled(false);
            }
            fontNameListModel = new FontNameListModel(monospacedOnly, allowAutomatic, font);
            fontNameList.setModel(fontNameListModel);
        } else {
            fontNameList.setModel(fontNameListModel);
            setChosenFont(font);
        }

    }

    private void findClosestFont() {
        for (int i = 0; i < fontNameList.getModel().getSize(); i++) {
            Font f = (Font) fontNameList.getModel().getElementAt(i);
            if ((f.getName().equals(AUTOMATIC) ? "Automatic" : f.getName()).toLowerCase().startsWith(
                fontName.getText().toLowerCase())) {
                if (fontNameList.getSelectedIndex() != i) {
                    fontNameList.setValueIsAdjusting(true);
                    fontNameList.setSelectedValue(fontNameList.getModel().getElementAt(i), true);
                    fontNameList.setValueIsAdjusting(false);
                    changeFontBasedOnState();
                }

                break;
            }
        }
    }

    private void changeFontBasedOnState() {
        Font f = ((Font) fontNameList.getSelectedValue());

        if (f != null) {
            int size = fontSize == null ? (chosenFont == null ? 12 : chosenFont.getSize()) : ((Integer) fontSize.getValue())
                            .intValue();
            int style = bold == null ? Font.PLAIN : (bold.isSelected() ? Font.BOLD : 0) | (italic.isSelected() ? Font.ITALIC : 0);
            chosenFont = f.getName().equals(AUTOMATIC) ? null : new Font(f.getName(), style, size);
            if (fontSize != null) {
                fontSize.setEnabled(chosenFont != null);
                fontSizeList.setEnabled(chosenFont != null);
            }
            if (bold != null) {
                bold.setEnabled(chosenFont != null);
                bold.setEnabled(chosenFont != null);
            }
            preview.setFont(chosenFont == null ? AUTOMATIC_FONT : chosenFont);
        }
    }

    /**
     * Set the currently chosen font
     * 
     * @param font font
     */
    public void setChosenFont(Font f) {
        // We cant have a null font, so default to the one for JLabel

        sizeIsAdjusting = true;
        adjusting = true;
        if (fontSizeList != null) {
            fontSizeList.setValueIsAdjusting(true);
        }
        try {

            Font pFont = f == null ? AUTOMATIC_FONT : f;
            fontName.setText(f == null ? "Automatic" : pFont.getName());
            findClosestFont();
            if (fontSize != null) {
                Integer size = new Integer(pFont.getSize());
                fontSize.setValue(size);
                fontSizeList.setSelectedValue(size, true);
            }
            if (bold != null) {
                bold.setSelected(pFont.isBold());
                italic.setSelected(pFont.isItalic());
            }
            chosenFont = f;
            preview.setFont(pFont);
            fontName.requestFocus();
        } finally {
            sizeIsAdjusting = false;
            adjusting = false;
            if (fontSizeList != null) {
                fontSizeList.setValueIsAdjusting(false);
            }
        }

    }

    /**
     * Get the currently chosen font
     * 
     * @return font font
     */
    public Font getChosenFont() {
        return chosenFont;
    }

    /**
     * Show a chooser dialog
     */
    public static Font showDialog(JComponent parent, Font initialFont) {
        return showDialog(parent, initialFont, true, false, false, true);
    }

    /**
     * Show a chooser dialog
     */
    public static Font showDialog(JComponent parent, Font initialFont, boolean showSize, boolean monospacedOnly) {
        return showDialog(parent, initialFont, showSize, monospacedOnly, false, true);
    }

    /**
     * Show a chooser dialog
     */
    public static Font showDialog(JComponent parent, Font initialFont, boolean showSize, boolean monospacedOnly,
                                  boolean allowAutomatic, boolean showStyles) {
        // Create the font chooser
        final FontChooser fc = new FontChooser(initialFont, showSize, monospacedOnly, allowAutomatic, showStyles);
        fc.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        if (JOptionPane.showConfirmDialog(parent, fc, "Choose Font", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            return fc.getChosenFont();

        }
        return null;
    }

    // font name list model
    class FontNameListModel extends AbstractListModel implements Runnable {
        private Vector fonts;
        private boolean monospacedOnly;
        private boolean allowAutomatic;
        private Font initialFont;

        FontNameListModel(boolean monospacedOnly, boolean allowAutomatic, Font initialFont) {
            fonts = new Vector();
            this.initialFont = initialFont;
            this.monospacedOnly = monospacedOnly;
            this.allowAutomatic = allowAutomatic;
            Thread t = new Thread(this);
            t.start();
        }

        public void run() {
            if (allowAutomatic) {
                fonts.addElement(AUTOMATIC_FONT);
            }
            Font[] f = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

            for (int i = 0; i < f.length; i++) {
                if (!monospacedOnly || isMonospaced(f[i])) {
                    fonts.addElement(f[i]);
                }

            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireContentsChanged(this, 0, getSize() - 1);
                    fontNameList.setEnabled(true);
                    fontSizeList.setEnabled(true);
                    if (bold != null) {
                        bold.setEnabled(true);
                        italic.setEnabled(true);
                    }
                    fontSize.setEnabled(true);
                    setChosenFont(initialFont);
                }
            });
        }

        boolean isMonospaced(Font f) {
            int width = -1;
            FontMetrics fm = getFontMetrics(f);
            for (char i = ' '; i <= 'z'; i++) {
                int cw = fm.charWidth(i);
                if (width == -1 || cw == width) {
                    width = cw;
                } else {
                    return false;
                }
            }
            return true;

        }

        public Object getElementAt(int i) {
            return fonts.elementAt(i);
        }

        public int getSize() {
            return fonts.size();
        }
    }

    // Render just the font name in the list
    class FontNameListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String fontName = ((Font) value).getName();
            setText(fontName.equals(AUTOMATIC) ? "Automatic" : fontName);
            return this;
        }
    }
}

