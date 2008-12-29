
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
			
package com.adito.properties.forms;

import java.util.List;

import com.adito.boot.PropertyClass;
import com.adito.properties.PropertyItem;
import com.adito.properties.PropertyItemImpl;

/**
 * @author Brett Smith
 */
public interface PropertiesForm {

    public boolean getEnabled();

    public PropertyItem[] getPropertyItems();

    public List<PropertyClass> getPropertyClasses();

    public void setPropertyItems(PropertyItemImpl[] propertyItems);

    public void setPropertyItem(int idx, PropertyItemImpl item);

    public PropertyItem getPropertyItem(int idx);

    public int getParentCategory();

    public List getCategoryDefinitions();

    public void setCategoryDefinitions(List categoryDefinitions);
    
    public int getSelectedCategory();
}