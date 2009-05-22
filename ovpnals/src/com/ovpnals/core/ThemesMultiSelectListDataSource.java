
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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.security.SessionInfo;

public class ThemesMultiSelectListDataSource implements MultiSelectDataSource {
    
    final static Log log = LogFactory.getLog(ThemesMultiSelectListDataSource.class);
    
    public ThemesMultiSelectListDataSource() {
    }

    public Collection<LabelValueBean> getValues(SessionInfo session) {
        ArrayList l = new ArrayList();
        l.add(new LabelValueBean("Default", "/theme/default"));
        return l;
    }
}
