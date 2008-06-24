
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

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.DefinitionsFactoryConfig;
import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.TilesPlugin;
import org.apache.struts.tiles.TilesUtil;

public class CoreTilesPlugin extends TilesPlugin {

    DefinitionsFactoryConfig factoryConfig;

    public void reloadFactory(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
    	if (log.isInfoEnabled())
    		log.info("Reloading tiles definition factory.");
        try {
            definitionFactory = TilesUtil.createDefinitionsFactory(servlet.getServletContext(), factoryConfig);

        } catch (DefinitionsFactoryException ex) {
            log.error("Can't create Tiles definition factory for module '" + moduleConfig.getPrefix() + "'.");

            throw new ServletException(ex);
        }
    }

    protected DefinitionsFactoryConfig readFactoryConfig(ActionServlet servlet, ModuleConfig config) throws ServletException {
        factoryConfig = super.readFactoryConfig(servlet, config);
        return factoryConfig;
    }

}
