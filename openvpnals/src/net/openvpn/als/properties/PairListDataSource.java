
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
			
package net.openvpn.als.properties;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.boot.PropertyDefinition;

/**
 * Interface to be implemented by classes that can provide a list of possible
 * values for {@link PropertyDefinition} objects that have a type of
 * {@link PropertyDefinition#TYPE_LIST}.
 */
public interface PairListDataSource {
    /**
     * Return a {@link List} of {@link Pair} objects to make
     * available as choices for a list property
     * 
     * @param request request
     * @return list of available value
     */
    public List<Pair> getValues(HttpServletRequest request);
}
