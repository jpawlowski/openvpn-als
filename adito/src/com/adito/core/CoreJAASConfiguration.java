
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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Extension of the standard JASS {@link javax.security.auth.login.Configuration}
 * that allows configurations to be added programatically instead of through
 * the configuration files. 
 */
public class CoreJAASConfiguration extends Configuration {
    
    TreeMap entries;
    final static Log log = LogFactory.getLog(CoreJAASConfiguration.class);

    /**
     * Constructor
     */
    public CoreJAASConfiguration() {
        super();
        entries = new TreeMap();
        Configuration.setConfiguration(this);
    }
    
    /**
     * Add a new configuration entry.
     * 
     * @param name name
     * @param entry entry
     */
    public void addAppConfigurationEntry(String name, AppConfigurationEntry entry) {
    	if (log.isInfoEnabled())
    		log.info("Adding new entry for '" + name + "' [" + entry.getLoginModuleName() + "' to JAAS configuration ");
        List l = (List)entries.get(name);
        if(l == null) {
            l = new ArrayList();
            entries.put(name, l);
        }
        l.add(entry);
    }

    /* (non-Javadoc)
     * @see javax.security.auth.login.Configuration#getAppConfigurationEntry(java.lang.String)
     */
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        List en = (List)entries.get(name);
        if(en == null || en.size() == 0) {
            return null;
        }
        else {
            AppConfigurationEntry[] ena = new AppConfigurationEntry[en.size()];
            en.toArray(ena);
           	return ena;
        }
    }

    /* (non-Javadoc)
     * @see javax.security.auth.login.Configuration#refresh()
     */
    public void refresh() {
    	if (log.isInfoEnabled())
    		log.info("Refreshing JAAS configuration");
    }

}
