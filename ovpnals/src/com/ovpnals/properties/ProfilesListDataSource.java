
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
			
package com.ovpnals.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.security.Constants;

/**
 * {@link PairListDataSource} that implementation that provides a list of all
 * profiles the user has access to.
 */
public class ProfilesListDataSource implements PairListDataSource {
	
	/**
	 * Select profile on login
	 */
	public final static String SELECT_ON_LOGIN = "selectOnLogin";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.properties.PairListDataSource#getValues(javax.servlet.http.HttpServletRequest)
	 */
	public List<Pair> getValues(HttpServletRequest request) {
		List<Pair> l = new ArrayList<Pair>();
		String selectOnLoginMessage = CoreUtil.getMessage(request, "properties", "userAttributes.startupProfile." + SELECT_ON_LOGIN);
		l.add(new Pair(SELECT_ON_LOGIN, selectOnLoginMessage));
		List propertyProfiles = (List) request.getSession().getAttribute(Constants.PROFILES);
		if (propertyProfiles != null) {
			for (Iterator i = propertyProfiles.iterator(); i.hasNext();) {
				PropertyProfile pf = (PropertyProfile) i.next();
				l.add(new Pair(new Integer(pf.getResourceId()), pf.getResourceName()));
			}
		}
		return l;
	}

}
