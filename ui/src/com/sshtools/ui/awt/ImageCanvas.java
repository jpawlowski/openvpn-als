
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
			
package com.sshtools.ui.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

/**
 * Simple component to draw an image. The image can be either centered in the
 * container or
 * 
 * @author $author$
 */

public class ImageCanvas extends Canvas {

	/**
	 * Centered in container
	 */
	public final static int CENTERED = 0;

	/**
	 * Resize to take up all space allocated to component
	 */
	public final static int STRETCH = 1;

	// Private instance variables
	private MediaTracker tracker;
	private Image image;
	private int scale;
	private int border;
	private Color borderColor;
	private float valign = CENTER_ALIGNMENT;
	private boolean paintBackground;
	private float halign = CENTER_ALIGNMENT;
	private boolean doubleBuffered;
	private int bufferWidth;
	private int bufferHeight;
	private Image bufferImage;
	private Graphics bufferGraphics;

	/**
	 * <p>
	 * Construct a new image canvas
	 * </p>
	 * 
	 */
	public ImageCanvas() {
		super();
	}

	/**
	 * <p>
	 * Construct a new image canvas
	 * </p>
	 * 
	 * @param iamge image
	 * 
	 */
	public ImageCanvas(Image image) {
		super();
		setImage(image);
		repaint();
	}

	/**
	 * <p>
	 * Construct a new image canvas given a <code>Class</code> from which the
	 * <code>Classloader</code> can be determined, and the resource name. The
	 * image will be centered in the container.
	 * </p>
	 * 
	 * @param cls name of image
	 * @param scale scale
	 * 
	 */
	public ImageCanvas(Class cls, String image) {
		super();
		setImage(UIUtil.loadImage(cls, image));
	}

	/**
	 * Set the vertical alignment
	 * 
	 * @param f vertical alignment
	 */
	public void setValign(float valign) {
		this.valign = valign;
	}

	/**
	 * Set the horizontal alignment
	 * 
	 * @param f horizontal alignment
	 */
	public void setHalign(float halign) {
		this.halign = halign;
	}

	/**
	 * Set the border width around the image
	 * 
	 * @param border border
	 */
	public void setBorder(int border) {
		this.border = border;
	}

	/**
	 * Get the border width around the image
	 * 
	 * @return border
	 */
	public int getBorder() {
		return border;
	}

	/**
	 * Get the border color
	 * 
	 * @return border color
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Set the border color
	 * 
	 * @param borderColor border color
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * <p>
	 * Set the scale. Can be one of
	 * </p>
	 * 
	 * <ul>
	 * <li><code>ImageCanvas.STRETCH</code></li>
	 * <li><code>ImageCanvas.CENTERED</code></li>
	 * </ul>
	 * 
	 * @param scale scale
	 */
	public void setScale(int scale) {
		this.scale = scale;
		repaint();
	}

	/**
	 * <p>
	 * Get the scale. Can be one of
	 * </p>
	 * 
	 * <ul>
	 * <li><code>ImageCanvas.STRETCH</code></li>
	 * <li><code>ImageCanvas.CENTERED</code></li>
	 * </ul>
	 * 
	 * @return scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Set the image to display
	 * 
	 * @param image
	 */
	public void setImage(Image image) {
		// Prompt a layout if the image size changes
		if ((image == null && this.image != null) || (image != null && this.image == null)
			|| (image != null && this.image != null && (image.getWidth(this) != this.image.getWidth(this) || image.getHeight(this) != this.image.getHeight(this)))) {
			this.image = image;
			doLayout();
		} else {
			this.image = image;
		}
		paintBackground = true;
		repaint();
	}

	/**
	 * Set the image to display
	 * 
	 * @return image
	 */
	public Image getImage() {
		return image;
	}

	/*
	 * Prevent flicker
	 * 
	 * @see java.awt.Component#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		paint(g);
	}

	public boolean isDoubleBuffered() {
		return doubleBuffered;
	}

	public void setDoubleBuffered(boolean doubleBuffered) {
		this.doubleBuffered = doubleBuffered;
	}

	public void paint(Graphics g) {
		if (!doubleBuffered) {
			paintBuffer(g);
		} else {
			// checks the buffersize with the current panelsize
			// or initialises the image with the first paint
			if (bufferWidth != getSize().width || bufferHeight != getSize().height || bufferImage == null || bufferGraphics == null)
				resetBuffer();

			if (bufferGraphics != null) {
				// this clears the offscreen image, not the onscreen one
				bufferGraphics.clearRect(0, 0, bufferWidth, bufferHeight);

				// calls the paintbuffer method with
				// the offscreen graphics as a param
				paintBuffer(bufferGraphics);

				// we finaly paint the offscreen image onto the onscreen image
				g.drawImage(bufferImage, 0, 0, this);
			}
		}
	}

	private void paintBuffer(Graphics g) {

		Dimension d = getSize();
		if (paintBackground) {
			g.setColor(getBackground());
			g.fillRect(0, 0, d.width, d.height);
		}
		Dimension f = new Dimension(d.width - (border * 2), d.height - (border * 2));
		if (image != null) {
			if (scale == STRETCH) {
				g.drawImage(image, border, border, f.width, f.height, this);
			} else {
				int x = Math.max((f.width - image.getWidth(this)) / 2, 0);
				if (halign == Canvas.LEFT_ALIGNMENT) {
					x = 0;
				} else if (halign == Canvas.RIGHT_ALIGNMENT) {
					x = f.width - image.getWidth(this);
				}
				int y = Math.max((f.height - image.getHeight(this)) / 2, 0);
				if (valign == Canvas.TOP_ALIGNMENT) {
					y = 0;
				} else if (valign == Canvas.BOTTOM_ALIGNMENT) {
					y = f.height - image.getHeight(this);
				}
				g.drawImage(image, x + border, y + border, this);
			}
		}

	}

	private void resetBuffer() {
		// always keep track of the image size
		bufferWidth = getSize().width;
		bufferHeight = getSize().height;

		// clean up the previous image
		if (bufferGraphics != null) {
			bufferGraphics.dispose();
			bufferGraphics = null;
		}
		if (bufferImage != null) {
			bufferImage.flush();
			bufferImage = null;
		}
		System.gc();

		// create the new image with the size of the panel
		bufferImage = createImage(bufferWidth, bufferHeight);
		bufferGraphics = bufferImage.getGraphics();
	}

	public Dimension getPreferredSize() {
		if (image == null) {
			return new Dimension(border * 2, border * 2);
		} else {
			return new Dimension(image.getWidth(this) + (border * 2), image.getHeight(this) + (border * 2));
		}
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
