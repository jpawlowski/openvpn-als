
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.core;

import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.xmlDefinition.I18nFactorySet;

/**
 * Extension of the Struts {@link org.apache.struts.tiles.xmlDefinition.I18nFactorySet}
 * that gets its list of tile configuration files from those that have been
 * registered with the core (i.e. plugins etc).
 */
public class CoreDefinitionsFactory extends I18nFactorySet {
    
    private static final long serialVersionUID = -4797604755334611221L;

    /* (non-Javadoc)
     * @see org.apache.struts.tiles.xmlDefinition.I18nFactorySet#initFactory(javax.servlet.ServletContext, java.lang.String)
     */
    protected void initFactory(
        ServletContext servletContext,
        String proposedFilename)
        throws DefinitionsFactoryException, FileNotFoundException {
    	if (log.isInfoEnabled())
    		log.info("Initialising tiles factory with path " + CoreServlet.getServlet().getTilesConfigurationFiles());
        super.initFactory(servletContext, CoreServlet.getServlet().getTilesConfigurationFiles());
    }
}
