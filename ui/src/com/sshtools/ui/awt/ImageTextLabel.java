package com.sshtools.ui.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.image.FilteredImageSource;

/**
 * A label that can display both graphics and text, somewhat similar to swings
 * JLabel.
 * 
 * @author $Author: james $
 */
public class ImageTextLabel extends Canvas {

    /**
     * No border. This will not take up any space.
     */
    public static final int NONE = 0;

    /**
     * Lowered bevel border (takes up 2 pixels)
     */
    public static final int LOWERED_BEVEL = 1;

    /**
     * Raised bevel border (takes up 2 pixels)
     */
    public static final int RAISED_BEVEL = 2;

    /**
     * Empty space (takes up 2 pixels)
     */
    public static final int EMPTY = 3;

    /**
     * Raised bevel border (takes up 2 pixels)
     */
    public static final int RAISED_ROUNDED = 4;

    /**
     * Flat (takes up 1 pixel)
     */
    public static final int FLAT = 6;

    /**
     * Left alignment
     */
    public static final int LEFT_ALIGNMENT = 0;

    /**
     * Center alignment
     */
    public static final int CENTER_ALIGNMENT = 1;

    /**
     * Center alignment
     */
    public static final int RIGHT_ALIGNMENT = 2;

    //	Private statics
    private static final Insets DEFAULT_MARGIN = new Insets(0, 0, 0, 0);
    private static final int DEFAULT_TEXT_IMAGE_GAP = 3;

    //	Private instance variables
    private Image image;
    private Image grayImage;
    private Image buffer;
    private String text;
    private Insets margin;
    private int borderType = NONE;
    private Color borderShadowColor = SystemColor.controlShadow;
    private Color borderDarkShadowColor = SystemColor.controlDkShadow;
    private int width;
    private int height;
    private Container parentContainer;
    private FontMetrics metrics;
    private int textImageGap;
    private boolean textVisible;
    private boolean wasEnabled;
    private int horizontalAlignment;

    public ImageTextLabel() {
        this(null, null);
    }

    public ImageTextLabel(Image image, String text) {
        setImage(image);
        this.text = text;
        textVisible = true;
        textImageGap = DEFAULT_TEXT_IMAGE_GAP;
        margin = DEFAULT_MARGIN;
    }

    public void addNotify() {
        super.addNotify();
        metrics = getFontMetrics(getFont());
    }

    /**
     * Set whether or not the text is visible
     * 
     * @param textVisible
     *            text visible
     */
    public void setTextVisible(boolean textVisible) {
        this.textVisible = textVisible;
        buffer = null;
        repaint();
    }

    /**
     * Get whether or not the text is visible
     * 
     * @return text visible
     */
    public boolean isTextVisible() {
        return textVisible;
    }

    public void setMargin(Insets margin) {
        this.margin = margin;
        doLayout();
        repaint();
    }

    public Insets getMargin() {
        return margin;
    }

    public void paint(Graphics g1) {
        Dimension d = getSize();
        boolean enabled = isEnabled();
        if (buffer == null || d.width != buffer.getWidth(this) || d.height != buffer.getHeight(this) || wasEnabled != enabled) {
            if (buffer != null) {
                buffer.getGraphics().dispose();
            }
            try {
            	buffer = createImage(d.width, d.height);
            }
            catch(Throwable t) {
            	// Its possible the graphics cannot be created yet, just ignore and hope that they can be on the next repaint
            	return;
            }
        }
        wasEnabled = enabled;
        Graphics g = buffer == null ? g1 : buffer.getGraphics();
        renderComponent(g, d, enabled);
        if (buffer != null) {
            g1.drawImage(buffer, 0, 0, this);
        }
    }

    protected void renderComponent(Graphics g, Dimension d, boolean enabled) {
        g.setFont(getFont());
        if (metrics == null) {
            metrics = g.getFontMetrics(g.getFont());
        }
        g.setColor(getBackground());
        g.fillRect(0, 0, d.width, d.height);

        int imageX = -1;
        int textX = -1;
        int totalWidth = 0;

        // Get the relative positions for the image and the text
        if (image != null) {
            imageX = 0;
            int imgWidth = image.getWidth(this);
            totalWidth = imageX + imgWidth;
            if (text != null && textVisible) {
                textX = imageX + imgWidth + textImageGap;
                totalWidth += textImageGap + getFontMetrics(getFont()).stringWidth(text);
            }
        } else {
            if (text != null && textVisible) {
                textX = 0;
                totalWidth = getFontMetrics(getFont()).stringWidth(text);
            }
        }

        // Get the offset based on the alignment and the width
        Insets i = getInsets();
        int offx = i.left;
        int availableSpace = d.width - i.left - i.right;
        switch (horizontalAlignment) {
        case CENTER_ALIGNMENT:
            offx += (availableSpace - totalWidth) / 2;
            break;
        case RIGHT_ALIGNMENT:
            offx += availableSpace - totalWidth;
            break;
        }

        // Draw the image
        if (imageX != -1) {
            g.drawImage(enabled ? image : grayImage, offx + imageX, (d.height - image.getHeight(this)) / 2, this);
        }

        // Draw the text
        if (textX != -1) {
            Color c = getForeground();
            if (!enabled) {
                if (c == null) {
                    c = Color.black;
                }
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                if (hsb[2] > 0.5) {
                    hsb = new float[] { hsb[0], hsb[1], (hsb[2] - 0.4f) / 1.1f };
                } else {
                    hsb = new float[] { hsb[0], hsb[1], (hsb[2] + 0.4f) * 1.1f };
                }
                c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            }
            g.setColor(c);
            

            g.drawString(text, offx + textX, metrics.getHeight() - metrics.getDescent() + ((d.height - metrics.getHeight()) / 2));
            
            
//            g.drawString(text, offx + textX, ((d.height - i.bottom) / 2 + metrics.getAscent() / 2));
        }
        paintBorder(g);
    }
    
    public void setFont(Font font) {
        super.setFont(font);
        metrics = null;
    }

    public void doLayout() {
        buffer = null;
    }

    /**
     * Set the gap (in pixels) between the image and text. This will only be
     * taken into account if both the image and text are set.
     * 
     * @param textImageGap
     *            gap in pixels between text and image
     */
    public void setTextImageGap(int textImageGap) {
        this.textImageGap = textImageGap;
        buffer = null;
        repaint();
    }

    /**
     * Get the gap (in pixels) between the image and text. This will only be
     * taken into account if both the image and text are set.
     * 
     * @return image gap
     */
    public int getTextImageGap() {
        return textImageGap;
    }

    /**
     * Get the image
     * 
     * @return image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the image
     * 
     * @param image
     *            image
     */
    public void setImage(Image image) {
        this.image = image;
        if (image != null) {
            UIUtil.waitFor(image, this);
            grayImage = createImage(new FilteredImageSource(image.getSource(), new GrayFilter()));
            UIUtil.waitFor(grayImage, this);
        } else {
            grayImage = null;
        }
        buffer = null;
        if (getGraphics() != null) {
            repaint();
        }
    }

    public Color getBorderShadowColor() {
        return borderShadowColor;
    }

    public void setBorderShadowColor(Color borderShadowColor) {
        this.borderShadowColor = borderShadowColor;
    }

    public Color getBorderDarkShadowColor() {
        return borderDarkShadowColor;
    }

    public void setBorderDarkShadowColor(Color borderDarkShadowColor) {
        this.borderDarkShadowColor = borderDarkShadowColor;
    }

    /**
     * Set the text to display
     * 
     * @param text
     *            text
     */
    public void setText(String text) {
        this.text = text;
        buffer = null;
        repaint();
    }

    /**
     * Get the text to display
     * 
     * @param text
     *            text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the border type. Can be one of :-
     * </p>
     * 
     * <ul>
     * <li><code>ImageTextLabel.NONE</code></li>
     * <li><code>ImageTextLabel.LOWERED</code></li>
     * <li><code>ImageTextLabel.RAISED</code></li>
     * 
     * @param borderType
     *            border type
     */
    public void setBorderType(int borderType) {
        this.borderType = borderType;
        buffer = null;
        repaint();
    }

    /**
     * Return insets sufficient for bevel and label drawing space.
     */
    public Insets getInsets() {
        Insets i = borderType == NONE ? new Insets(0, 0, 0, 0) : new Insets(2, 2, 2, 2);
        if (margin != null) {
            i.top += margin.top;
            i.bottom += margin.bottom;
            i.left += margin.left;
            i.right += margin.right;
        }
        return i;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paintBorder(Graphics g) {
        Dimension d = getSize();
        Color si = getBorderDarkShadowColor();
        Color so = si.darker();
        Color hi = getBorderShadowColor();
        Color ho = hi.brighter();
        switch (borderType) {
        case LOWERED_BEVEL:
            g.setColor(si);
            g.drawLine(0, 0, 0, d.height - 1);
            g.drawLine(1, 0, d.width - 1, 0);
            g.setColor(so);
            g.drawLine(1, 1, 1, d.height - 2);
            g.drawLine(2, 1, d.width - 2, 1);
            g.setColor(ho);
            g.drawLine(1, d.height - 1, d.width - 1, d.height - 1);
            g.drawLine(d.width - 1, 1, d.width - 1, d.height - 2);
            g.setColor(hi);
            g.drawLine(2, d.height - 2, d.width - 2, d.height - 2);
            g.drawLine(d.width - 2, 2, d.width - 2, d.height - 3);
            break;
        case RAISED_BEVEL:
            g.setColor(ho);
            g.drawLine(0, 0, 0, d.height - 2);
            g.drawLine(1, 0, d.width - 2, 0);
            g.setColor(hi);
            g.drawLine(1, 1, 1, d.height - 3);
            g.drawLine(2, 1, d.width - 3, 1);
            g.setColor(so);
            g.drawLine(0, d.height - 1, d.width - 1, d.height - 1);
            g.drawLine(d.width - 1, 0, d.width - 1, d.height - 2);
            g.setColor(si);
            g.drawLine(1, d.height - 2, d.width - 2, d.height - 2);
            g.drawLine(d.width - 2, 1, d.width - 2, d.height - 3);
            break;
        case RAISED_ROUNDED:
            g.setColor(ho);
            g.drawLine(1, 0, d.width - 2, 0);
            g.drawLine(0, 1, 0, d.height - 2);
            g.setColor(so);
            g.drawLine(d.width - 1, 1, d.width - 1, d.height - 2);
            g.drawLine(1, d.height - 1, d.width - 2, d.height - 1);
            g.setColor(si);
            g.drawLine(d.width - 2, 2, d.width - 2, d.height - 2);
            g.drawLine(2, d.height - 2, d.width - 3, d.height - 2);
            break;
        case FLAT:
            g.setColor(ho);
            g.drawRect(0, 0, d.width - 1, d.height - 1);
            break;
        }
    }

    /*
     * Prevent flicker
     * 
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    public void update(Graphics g) {
        paint(g);
    }

    public Dimension getPreferredSize() {
        Insets i = getInsets();
        return new Dimension(i.left + i.right + 2 + (image != null && text != null && textVisible ? textImageGap : 0)
                        + (image != null ? image.getWidth(this) : 0)
                        + (metrics != null && textVisible && text != null ? metrics.stringWidth(text) : 0), i.top + i.bottom
                        + Math.max(image != null ? image.getHeight(this) : 0, (metrics != null ? metrics.getHeight() : 0)));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        repaint();
    }
}