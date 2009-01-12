
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
			
package com.adito.input;

import java.util.Collection;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.adito.security.SessionInfo;


/**
 * Interface to be implemented by classes that supply lists of 
 * {@link LabelValueBean} objects for use in the <i>Multi Select</i>
 * component.
 * 
 * @author brett
 *
 */
public interface MultiSelectDataSource {
    /**
     * Return a {@link List} of {@link org.apache.struts.util.LabelValueBean} 
     * objects to make available as possible selected for a multi select
     * component
     * 
     * @param sessionInfo session requesting the values
     * @return list of available value
     */
    public Collection<LabelValueBean> getValues(SessionInfo sessionInfo);
}
