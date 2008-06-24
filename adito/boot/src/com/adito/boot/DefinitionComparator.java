
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
			
package com.adito.boot;

import java.util.Comparator;


/**
 * {@link java.util.Comparator} used for sorting
 * {@link com.adito.boot.PropertyDefinition} objects based on the category
 * and sort order.
 * 
 */
public class DefinitionComparator implements Comparator {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1) {
        PropertyDefinition def0 = (PropertyDefinition) arg0;
        PropertyDefinition def1 = (PropertyDefinition) arg1;
        int i = new Integer(def0.getCategory()).compareTo(new Integer(def1.getCategory()));
        if (i == 0) {
            i = new Integer(def0.getSortOrder()).compareTo(new Integer(def1.getSortOrder()));
            if (i == 0) {
                return def0.getName().compareTo(def1.getName());
            } else {
                return i;
            }
        } else {
            return i;
        }
    }

}