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
package com.adito.ldap;

import com.adito.core.UserDatabaseManager;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.DefaultPlugin;
import com.adito.extensions.types.PluginDefinition;
import com.adito.security.UserDatabaseDefinition;
import org.jdom.Element;

/**
 * Adds a Ldap <i>User Database</i> to Adito.
 */
public class LdapPlugin extends DefaultPlugin {


    /**
     * Constructor.
     */
    public LdapPlugin() {
        super(null, false);
    }

    public void startPlugin(PluginDefinition definition, ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        super.startPlugin(definition, descriptor, element);
        UserDatabaseDefinition databaseDefinition = new UserDatabaseDefinition(LdapUserDatabase.class, "ldap", "ldap", 90);
        UserDatabaseManager.getInstance().registerDatabase(databaseDefinition);
    }
}
