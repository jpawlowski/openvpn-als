
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;

/**
 * Useful UI utilies for AWT.
 *
 * @author $Author: brett $
 */
public class UIUtil {
  
  private static URL codebase;
  private static Hashtable imageCache = new Hashtable();
  private static Hashtable stockIds = new Hashtable(); 
  private static Frame sharedFrame;

  /** 
   * The central position in an area. Used for
   * both compass-direction constants (NORTH, etc.)
   * and box-orientation constants (TOP, etc.).
   */
  public static final int CENTER  = 0;

  /** 
   * Compass-direction North (up).
   */
  public static final int NORTH      = 1;
  /** 
   * Compass-direction north-east (upper right).
   */
  public static final int NORTH_EAST = 2;
  /** 
   * Compass-direction east (right).
   */
  public static final int EAST       = 3;
  /** 
   * Compass-direction south-east (lower right).
   */
  public static final int SOUTH_EAST = 4;
  /** 
   * Compass-direction south (down).
   */
  public static final int SOUTH      = 5;
  /** 
   * Compass-direction south-west (lower left).
   */
  public static final int SOUTH_WEST = 6;
  /** 
   * Compass-direction west (left).
   */
  public static final int WEST       = 7;
  /** 
   * Compass-direction north west (upper left).
   */
  public static final int NORTH_WEST = 8;

  /**
   * Wait for an image to load.
   *
   * @param image image to wait for
   * @param component image producer component
   * @return image or <code>null</code> if the image did not load
   */
  public static Image waitFor(Image image, Component component) {
    if (image != null) {
      MediaTracker tracker = new MediaTracker(component);
      tracker.addImage(image, 0);
      try {
        tracker.waitForAll();
        return image;
      }
      catch (InterruptedException ie) {
        ie.printStackTrace();
      }
      if (tracker.isErrorAny()) {
        /* DEBUG */System.err.println(MessageFormat.format(Messages.getString("UIUtil.imageDidNotLoad"), new Object[] { image.toString() } ) ); //$NON-NLS-1$
      }
    }
    /* DEBUG */System.err.println(Messages.getString("UIUtil.noImage")); //$NON-NLS-1$
    return null;
  }

  /**
   * If images are to be loaded in an applet, the this method should be called
   * during the applets initialisation so they can be located.
   *
   * @param codebase applet codebase
   */
  public static void setCodeBase(URL codebase) {
    UIUtil.codebase = codebase;
  }

  /**
   * Load an image using the specified <code>Class</code>es <code>ClassLoader</code>
       * and the given resource name. <code>null</code> will be returned if the image
   * could not be loaded.
   *
   * @param clazz class to get class loader from
   * @param resource resource name
   */
  public static Image loadImage(Class clazz, String resource) {
    String path = resource;

    /*
    String path = null;
    if (resource.startsWith("/")) {
      path = resource;
    }
    else {
      int idx = clazz.getName().lastIndexOf('.');
      String packageName = idx == -1 ? null : clazz.getName().substring(0, idx);
      path = packageName == null ? "" : "/" + packageName;
      path = path.replace('.', '/');
      path += ("/" + resource);
    }
    */    
    URL url = clazz.getResource(path);
    Image img = null;
    if (url != null) {
      img = (Image)imageCache.get(url.toExternalForm());
      if(img == null) {
        img = Toolkit.getDefaultToolkit().getImage(url);
        if(img != null) {
          imageCache.put(url.toExternalForm(), img);
        }
      }
    }
    else {
      if(codebase != null) {
        URL loc;
        try {
          loc = new URL(codebase, resource);
          img = (Image)imageCache.get(loc.toExternalForm());
          if(img == null) {
            img = Toolkit.getDefaultToolkit().getImage(loc);
            if(img != null) { 
              imageCache.put(loc.toExternalForm(), img);
            }
          }
        } catch (MalformedURLException e) {
          e.printStackTrace();
          img = null;
        }
        
      }
    }
    if(img == null) {
      img = (Image)imageCache.get(path);
      if(img == null) {
        img = Toolkit.getDefaultToolkit().getImage(path);
        if(img != null) {
          imageCache.put(path, img);
        }
      } 
    }
    
    if(img == null) {
      System.err.println(MessageFormat.format(Messages.getString("UIUtil.couldNotLocateImage"), new Object[] { resource } ) ); //$NON-NLS-1$
    }
    return img; 
  }

  /**
   * Add a component to a container that is using a <code>GridBagLayout</code>, together with its constraints and the <code>GridBagConstraints.gridwidth</code>
   * value.
   *
   * @param parent parent container
   * @param componentToAdd component to add
   * @param constraints contraints
   * @param pos grid width position
   *
   * @throws IllegalArgumentException
   */
  public static void gridBagAdd(Container parent, Component componentToAdd,
                                GridBagConstraints constraints, int pos) {
    if (! (parent.getLayout()instanceof GridBagLayout)) {
      throw new IllegalArgumentException(Messages.getString("UIUtil.parentMustHaveGridBagLayout")); //$NON-NLS-1$
    }

    //
    GridBagLayout layout = (GridBagLayout) parent.getLayout();

    //
    constraints.gridwidth = pos;
    layout.setConstraints(componentToAdd, constraints);
    parent.add(componentToAdd);
  }

  /**
   * Get the top level window that contains the given component.
   *
   * @param c component
   * @return window
   */
  public static Window getWindowAncestor(Component c) {
    for (Container p = c.getParent(); p != null; p = p.getParent()) {
      if (p instanceof Window) {
        return (Window) p;
      }
    }
    return null;
  }

  /**
   * Get the top level fra,e that contains the given component.
   *
   * @param c component
   * @return frame
   */
  public static Frame getFrameAncestor(Component c) {
    if(c == null) {
      return null;
    }
    for (Container p = c.getParent(); p != null; p = p.getParent()) {
      if (p instanceof Frame) {
        return (Frame) p;
      }
    }
    return null;
  }


  /**
       * Position a component on the screen (must be a <code>java.awt.Window</code> to
   * be useful)
   *
   * @param p postion from <code>SwingConstants</code>
   * @param c component
   */
  public static void positionComponent(int p, Component c) {

    /* TODO This is very lame doesnt require the component to position around, just assuming
     * its a window.
     */

    Rectangle d = null;
    try {
//#ifdef JAVA1
/*
throw new Exception();
*/
//#else
      d = c.getGraphicsConfiguration().getDevice().getDefaultConfiguration().
          getBounds();
//#endif JAVA1
    }
    catch (Throwable t) {
    }
    if(d == null) {
      Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
      d = new Rectangle(0, 0, s != null ? s.width : 800, s != null ? s.height : 600);
      // TODO Find a better way of taking care of centering on dual-head displays
      if( d.width > ( 2 * d.height ) ) {
        d.width = d.width / 2;
      }
    }

    switch (p) {
      case NORTH_WEST:
        c.setLocation(d.x, d.y);
        break;
      case NORTH:
        c.setLocation(d.x + (d.width - c.getSize().width) / 2, d.y);
        break;
      case NORTH_EAST:
        c.setLocation(d.x + (d.width - c.getSize().width), d.y);
        break;
      case WEST:
        c.setLocation(d.x, d.y + (d.height - c.getSize().height) / 2);
        break;
      case SOUTH_WEST:
        c.setLocation(d.x, d.y + (d.height - c.getSize().height));
        break;
      case EAST:
        c.setLocation(d.x + d.width - c.getSize().width,
                      d.y + (d.height - c.getSize().height) / 2);
        break;
      case SOUTH_EAST:
        c.setLocation(d.x + (d.width - c.getSize().width),
                      d.y + (d.height - c.getSize().height) - 30);
        break;
      case CENTER:
        c.setLocation(d.x + (d.width - c.getSize().width) / 2,
                      d.y + (d.height - c.getSize().height) / 2);
        break;
    }
  }

  /**
   * @return
   */
  public static Frame getSharedFrame() {
    if(sharedFrame == null) {
      sharedFrame = new Frame();
    }
    return sharedFrame;
  }
  
  /**
   * Get a stock image
   * 
   * @param id stock image id. See {@link com.sshtools.ui.StockIcons} for constants.
   * @param clazz the {@link Class} to derive a {@link ClassLoader} from to load the image with.
   */
  public static Image getStockImage(String id, Class clazz) {
      String resource = (String)stockIds.get(id);
      return loadImage(clazz, resource == null ? id : resource);
  }
  
  /**
   * Set an alternative stock image resource. Setting a <code>null</code> 
   * resource will remove any custom resource and rever to the default. 
   * 
   * @param id stock image id. See {@link com.sshtools.ui.StockIcons} for constants.
   * @param resource the resource path to use to load the image.
   */
  public static void setStockImage(String id, String resource) {
      if(resource == null) {
          stockIds.remove(id);
      }
      else {
          stockIds.put(id, resource);
      }
  }
}
