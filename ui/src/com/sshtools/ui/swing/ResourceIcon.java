
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
			
package com.sshtools.ui.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * Icon that can use a <code>Class</code> reference to determine a
 * <code>ClassLoader</code> to use to load an icon resource.
 * 
 * @author $Author: brett $
 */
public class ResourceIcon extends ImageIcon {
    // Private instance variables
    private Class cls;

    private static HashMap imageCache = new HashMap();

    public ResourceIcon(String image) {
        super();
        init(getClass(), image);
    }

    /**
     * Creates a new ResourceIcon given a class to derive the class loader from
     * and the image path.
     * 
     * @param cls class to derive class loader for image from
     * @param image image path
     */
    public ResourceIcon(Class cls, String image) {
        super();
        init(cls, image);
    }

    public ResourceIcon(URL iconResource) {
        super(iconResource);
    }

    public void init(Class cls, String image) {
        this.cls = cls;
        if (image.startsWith("/")) { //$NON-NLS-1$
            loadImage(image);
        } else {
            int idx = cls.getName().lastIndexOf('.');
            String packageName = idx == -1 ? null : cls.getName().substring(0, idx);
            String path = packageName == null ? "" : "/" + packageName; //$NON-NLS-1$ //$NON-NLS-2$
            path = path.replace('.', '/');
            path += ("/" + image); //$NON-NLS-1$
            loadImage(path);
        }
    }

    protected void loadImage(String imageName) {
        Image image = (Image) imageCache.get(imageName);
        if (image == null) {
            URL url = cls.getResource(imageName);
            if (url != null) {
                image = Toolkit.getDefaultToolkit().getImage(url);
            } else {
                url = cls.getClassLoader().getResource(imageName);
                if (url != null) {
                    image = Toolkit.getDefaultToolkit().getImage(url);
                } else {
                    image = Toolkit.getDefaultToolkit().getImage(imageName);
                }
            }
            imageCache.put(imageName, image);
        }
        if (image != null) {
            this.setImage(image);
        } else {
            System.err.println(Messages.getString("ResourceIcon.nullImage")); //$NON-NLS-1$
        }
    }
}
