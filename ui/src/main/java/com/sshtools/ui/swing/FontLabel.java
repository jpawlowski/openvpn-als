
package com.sshtools.ui.swing;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class FontLabel
    extends JTextField {
    private int fixedPreviewSize;
    private Font chosenFont;
    private boolean showSize;

    /**
     * Creates a new FontLabel object.
     */
    public FontLabel() {
        this(null);
    }

    /**
     * Creates a new FontLabel object.
     *
     * @param chosenFont DOCUMENT ME!
     */
    public FontLabel(Font chosenFont) {
        this(chosenFont, UIManager.getFont("Label.font").getSize());
    }

    /**
     * Creates a new FontLabel object.
     *
     * @param chosenFont DOCUMENT ME!
     * @param fixedPreviewSize DOCUMENT ME!
     */
    public FontLabel(Font chosenFont, int fixedPreviewSize) {
        super();
        setFixedPreviewSize(fixedPreviewSize);
        setChosenFont(chosenFont);
        setEditable(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param fixedPreviewSize DOCUMENT ME!
     */
    public void setFixedPreviewSize(int fixedPreviewSize) {
        this.fixedPreviewSize = fixedPreviewSize;
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getFixedPreviewSize() {
        return fixedPreviewSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     */
    public void setChosenFont(Font f) {
        setFont(( f == null || f.getName().equals(FontChooser.AUTOMATIC) ? UIManager.getFont("Label.font") : f ).deriveFont((float)fixedPreviewSize));
        this.chosenFont = f;
        rebuildText();
    }
    
    void rebuildText() {
        if (chosenFont != null && !chosenFont.getName().equals(FontChooser.AUTOMATIC)) {
            setText(chosenFont.getName() + ( showSize ? ( "," + chosenFont.getSize() + "pt") : "" ) );
        }
        else {
            setText("Automatic");
        }
    }
    
    public void setShowSize(boolean showSize) {
        this.showSize = showSize;
        rebuildText();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Font getChosenFont() {
        return chosenFont == null || chosenFont.getName().equals(FontChooser.AUTOMATIC) ? null : chosenFont;
    }
}
