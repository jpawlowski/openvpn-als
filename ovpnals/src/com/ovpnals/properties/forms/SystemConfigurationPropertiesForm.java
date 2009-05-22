
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
			
package com.ovpnals.properties.forms;

import java.util.Arrays;

import com.ovpnals.boot.ContextConfig;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.properties.impl.realms.RealmProperties;
import com.ovpnals.properties.impl.systemconfig.SystemConfiguration;

public class SystemConfigurationPropertiesForm extends AbstractPropertiesForm {
    public SystemConfigurationPropertiesForm() {
        super(Arrays.asList(new PropertyClass[] { PropertyClassManager.getInstance().getPropertyClass(SystemConfiguration.NAME), PropertyClassManager.getInstance().getPropertyClass(ContextConfig.NAME), PropertyClassManager.getInstance().getPropertyClass(RealmProperties.NAME) } ), false);
    }
}