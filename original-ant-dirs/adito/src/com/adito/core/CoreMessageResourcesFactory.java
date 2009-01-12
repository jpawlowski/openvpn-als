
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
			
package com.adito.core;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

/**
 * Extension of {@link org.apache.struts.util.MessageResourcesFactory} that
 * creates {@link com.adito.core.CoreMessageResources}.
 * <p>
 * This factory requires a reference to the Adito so that message
 * resources can be loaded from plugins.
 */
public class CoreMessageResourcesFactory extends MessageResourcesFactory {

    private ClassLoader classLoader = null;
    
    /**
     * Constructor
     * 
     * @param classLoader class loader
     */
    public CoreMessageResourcesFactory(ClassLoader classLoader) {
        super();
        this.classLoader = classLoader;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.util.MessageResourcesFactory#createResources(java.lang.String)
     */
    public MessageResources createResources(String config) {
        return new CoreMessageResources(this, config, classLoader);
    }

}
