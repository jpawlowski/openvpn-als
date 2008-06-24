
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
			
package com.sshtools.ui.awt;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;

/**
 * Component that can display some text and an image
 *
 * @author $Author: james $
 */
public class ImageLabel
    extends BevelPanel {

  private ImageCanvas imageCanvas;
  private Label textLabel;

  /**
   * Construct a image label with no text or image
   */
  public ImageLabel() {
    this(null, null);
  }

  /**
   * Construct a new image label with an image
   *
   * @param image image
   */
  public ImageLabel(Image image) {
    this(null, image);
  }

  /**
   * Construct a new image label with some text
   *
   * @param text text
   */
  public ImageLabel(String text) {
    this(text, null);
  }

  /**
   * Construct a new image label with an image and some text
   *
   * @param text text
   * @param image image
   */
  public ImageLabel(String text, Image image) {
    super(NONE, new BorderLayout(2, 0));
    imageCanvas = new ImageCanvas();
    textLabel = new Label() {
      public Dimension getMinimumSize() {
        return getPreferredSize();
      }

      public void processEvent(AWTEvent evt) {
        ImageLabel.this.dispatchEvent(evt);
      }
    };
    add(imageCanvas, BorderLayout.WEST);
    add(textLabel, BorderLayout.CENTER);
    setText(text);
    setImage(image);
  }

  /**
   * Set the image
   *
   * @param image image
   */
  public void setImage(Image image) {
    imageCanvas.setImage(image);
    imageCanvas.setVisible(image != null);
  }

  /**
   * Get the image
   *
   * @retirm image
   */
  public Image getImage() {
    return imageCanvas.getImage();
  }

  /**
   * Set the text
   *
   * @param text text
   */
  public void setText(String text) {
    textLabel.setText(text);
    textLabel.setVisible(text != null);
  }

  /**
   * Set the font
   *
   * @param font font
   */
  public void setFont(Font font) {
    super.setFont(font);
    textLabel.setFont(font);
  }

  /**
   * Set foreground color of text
   *
   * @param color foreground color
   */
  public void setForeground(Color foreground) {
    super.setForeground(foreground);
    textLabel.setForeground(foreground);
  }
}
