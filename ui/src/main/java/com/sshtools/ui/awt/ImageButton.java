package com.sshtools.ui.awt;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import com.sshtools.ui.awt.tooltips.ToolTipManager;

/**
 * An button implementation that can display an image.
 * 
 * @author $author$
 */
public class ImageButton extends ImageTextLabel implements MouseListener, ItemSelectable {

    private boolean mouseIsDown = false;
    private Vector listeners, itemListeners;
    private int darkness = -5263441;
    private Image grayImage = null;
    private String actionCommand;
    private Color borderColor;
    private boolean hoverButton;
    private boolean mouseInComponent;
    private int hoverBorder, depressedBorder, normalBorder;
    private String toolTipText;
    private Color hoverBackground = SystemColor.controlLtHighlight;
    private Color hoverForeground = SystemColor.controlText;
    private Color normalBackground = SystemColor.control;
    private Color normalForeground = SystemColor.controlText;
    private Color depressedBackground = SystemColor.controlShadow;
    private Color depressedForeground = SystemColor.controlHighlight;
    private boolean pressed;

    public ImageButton() {
        this(null, null, null);
    }

    public ImageButton(Image image, String text, String actionCommand) {
        this(image, text, actionCommand, FLAT, FLAT, NONE);
    }

    public ImageButton(Image image, String text, String actionCommand, int hoverBorder, int depressedBorder, int normalBorder) {
        super(image, text);
        this.actionCommand = actionCommand;
        this.hoverBorder = hoverBorder;
        this.depressedBorder = depressedBorder;
        this.normalBorder = normalBorder;
        setMargin(new Insets(4, 2, 4, 2));
        setBorderType(hoverBorder);
        addMouseListener(this);
        normalBackground = getBackground();
        normalForeground = getForeground();
    }
    
    public void setEnabled(boolean enabled) {
        if(!enabled) {
            setBorderType(normalBorder);
            super.setBackground(normalBackground);
            super.setForeground(normalForeground);
        }
        super.setEnabled(enabled);
        repaint();
    }
    
    public void setBaseBackground(Color base) {
        if(base == null) {
            setBackground(SystemColor.control);
            hoverBackground = SystemColor.controlHighlight;
            depressedBackground = SystemColor.controlShadow;
        }
        else {
            setBackground(base);
            hoverBackground = base.brighter();
            depressedBackground = base.darker();
        }
    }
    
    public void setBaseForeground(Color base) {
        if(base == null) {
            setForeground(SystemColor.controlText);
            hoverForeground = SystemColor.controlText;
            depressedForeground = SystemColor.controlHighlight;
        }
        else {
            hoverForeground = base.darker();
            depressedForeground = base.brighter();
            setForeground(base);
        }
    }
    
    public void setBackground(Color background) {
        this.normalBackground = background;
        super.setBackground(background);
    }
    
    public void setForeground(Color foreground) {
        this.normalForeground = foreground;
        super.setForeground(foreground);
    }

    /**
     * Set the type of border to use when this is a hover button
     * 
     * @param hoverBorder
     */
    public void setHoverBorder(int hoverBorder) {
        this.hoverBorder = hoverBorder;
    }

    /**
     * Set if this is a 'hover button'. If <code>true</code> a border will be
     * raised when the user moves the mouse pointer over this component.
     * 
     * @param hoverButton
     */
    public void setHoverButton(boolean hoverButton) {
        this.hoverButton = hoverButton;
        setBorderType(hoverButton ? (mouseInComponent ? hoverBorder : normalBorder) : hoverBorder);

    }
    
    /**
     * Set the background color to use when hovering over a button. <code>null</code>
     * means use default color.
     * 
     * @param hoverBackground background color
     */
    public void setHoverBackground(Color hoverBackground) {
        this.hoverBackground = hoverBackground;
    }
    
    /**
     * Set the foreground color to use when hovering over a button. <code>null</code>
     * means use default color.
     * 
     * @param hoverForeground foreground color
     */
    public void setHoverForeground(Color hoverForeground) {
        this.hoverForeground = hoverForeground;
    }

    /**
     * Get the action command
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Set the action command
     * 
     * @param actionCommand
     *            action command
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }

    public String getToolTipText() {
        return toolTipText;
    }

//    public boolean mouseDown(Event event, int x, int y) {
//        if (isEnabled()) {
//            mouseIsDown = true;
//            setBorderType(depressedBorder);
//        }
//        return true;
//    }

//    public boolean mouseEnter(Event event, int x, int y) {
//        if (isEnabled()) {
//            if (hoverButton) {
//                setBorderType(hoverBorder);
//            }
//        }
//        if (toolTipText != null) {
//            ToolTipManager.getInstance().requestToolTip(this, toolTipText);
//        }
//        mouseInComponent = true;
//        return true;
//    }

//    public boolean mouseUp(Event event, int x, int y) {
//        mouseIsDown = false;
//        if (isEnabled()) {
//            if (this.inside(x, y)) {
//                setBorderType(normalBorder);
//                paint(this.getGraphics());
//                event.id = 1001;
//                event.arg = this.getImage();
//                return action(event, event.arg);
//            }
//        }
//        return false;
//    }

    public void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }
    
    public void addItemListener(ItemListener l) {
        if (itemListeners == null) {
            itemListeners = new Vector();
        }
        itemListeners.addElement(l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners != null) {
            listeners.removeElement(l);
        }
    }
    
    public void removeItemListener(ItemListener l) {
        if (itemListeners != null) {
            itemListeners.removeElement(l);
        }
    }
    
    boolean itemEvent(boolean selected) {
        ItemEvent evt = null;
        for (int i = itemListeners == null ? -1 : itemListeners.size() - 1; i >= 0; i--) {
            if (evt == null) {
                evt = new ItemEvent(this, selected ? ItemEvent.SELECTED : 
                    ItemEvent.DESELECTED, this, 0);
            }
            ((ItemListener) itemListeners.elementAt(i)).itemStateChanged(evt);
        }
        return false;
        
    }

    boolean actionEvent(int modifiers) {
        ActionEvent evt = null;
        for (int i = listeners == null ? -1 : listeners.size() - 1; i >= 0; i--) {
            if (evt == null) {
                evt = new ActionEvent(this, 1001, actionCommand, modifiers);
            }
            ((ActionListener) listeners.elementAt(i)).actionPerformed(evt);
        }
        return false;
    }

//    public boolean mouseExit(Event event, int x, int y) {
//        mouseInComponent = false;
//        if (hoverButton) {
//            setBorderType(normalBorder);
//        } else {
//            if (mouseIsDown) {
//                paint(this.getGraphics());
//            }
//        }
//        if (toolTipText != null) {
//            ToolTipManager.getInstance().requestToolTip(null, null);
//        }
//        return true;
//    }

    public int getDarkness() {
        return darkness;
    }

    public void setDarkness(int darkness) {
        this.darkness = darkness;
    }

    public Image getGrayImage() {
        return grayImage;
    }

    public void setGrayImage(Image grayImage) {
        this.grayImage = grayImage;
    }

    /*
     * private void createGrayImage(Graphics g) { java.awt.image.ImageFilter
     * filter = new GrayFilter(darkness); java.awt.image.ImageProducer producer =
     * new FilteredImageSource(this.getImage().getSource(), filter); grayImage =
     * this.createImage(producer); int border = this.getBorder(); if (getScale() ==
     * STRETCH) this.prepareImage(grayImage, getWidth() - 2 * border,
     * getHeight() - 2 * border, this); else this.prepareImage(grayImage, this);
     * super.paint(g); }
     */

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if (isEnabled()) {
            mouseIsDown = true;
            setBorderType(depressedBorder);
            super.setBackground(depressedBackground);
            super.setForeground(depressedForeground);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent event) {
        mouseIsDown = false;
        if (isEnabled()) {
            if (this.inside(event.getX(), event.getY())) {
                setBorderType(normalBorder);
                paint(this.getGraphics());
                actionEvent(event.getModifiers());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        if (isEnabled()) {
            if (hoverButton && !pressed) {
                setBorderType(hoverBorder);
                if(hoverForeground != null) {
                    super.setForeground(hoverForeground);
                }
                if(hoverBackground != null) {
                    super.setBackground(hoverBackground);
                }  
                itemEvent(true);
            }
        }
        if (toolTipText != null) {
            ToolTipManager.getInstance().requestToolTip(this, toolTipText);
        }
        mouseInComponent = true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        mouseInComponent = false;
        if (hoverButton) {
            if(!pressed) {
	            setBorderType(normalBorder);
	            if(hoverForeground != null) {
	                super.setForeground(normalForeground);
	            }
	            if(hoverBackground != null) {
	                super.setBackground(normalBackground);
	            } 
                itemEvent(true);               
            }
        } else {
            if (mouseIsDown) {
                paint(this.getGraphics());
            }
        }
        if (toolTipText != null) {
            ToolTipManager.getInstance().requestToolTip(null, null);
        }
    }

    /**
     * @param b
     */
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        if(pressed) {
            setBorderType(depressedBorder);
            super.setBackground(depressedBackground);
            super.setForeground(depressedForeground);
        }
        else {
            setBorderType(normalBorder);
            super.setBackground(normalBackground);
            super.setForeground(normalForeground);
        };
        repaint();
        
    }

    /* (non-Javadoc)
     * @see java.awt.ItemSelectable#getSelectedObjects()
     */
    public Object[] getSelectedObjects() {
        return new Object[] { this };
    }
}