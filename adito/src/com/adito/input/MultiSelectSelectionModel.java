
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts.util.LabelValueBean;

import com.adito.boot.PropertyList;
import com.adito.security.SessionInfo;


/**
 * Model that maintains the selection state for a 'Multi Select' component
 * given a 'value' containing the section items and the list of possible values 
 * from the datasource.
 */
public class MultiSelectSelectionModel {
    protected List<LabelValueBean> availableValues;
    protected List<LabelValueBean> selectedValues;
    protected Map<String, LabelValueBean> availableMap;
    protected MultiSelectDataSource dataSource;
    protected PropertyList propertyList;

    /**
     * Constructor
     * 
     * @param session session
     * @param dataSource data source for available values
     * @param propertyList selected values
     */
    public MultiSelectSelectionModel(SessionInfo session, MultiSelectDataSource dataSource, PropertyList propertyList) {
        super();
        this.dataSource = dataSource;
        this.propertyList = propertyList;
        rebuild(session);
    }
    
    /**
     * Constructor
     * 
     * @param session session
     * @param dataSource data source for available values
     * @param propertyList selected values
     */
    protected MultiSelectSelectionModel(MultiSelectDataSource dataSource, PropertyList propertyList) {
        super();
        this.dataSource = dataSource;
        this.propertyList = propertyList;
    }
    
    /**
     * Select all values
     * 
     * @param session values
     */
    public void selectAll(SessionInfo session) {
        for(Iterator i = availableValues.iterator(); i.hasNext(); ) {
            propertyList.add(((LabelValueBean)i.next()).getValue());
        }
        rebuild(session);
    }
    
    /**
     * Get if the model contains the value. This is true if the 
     * value is either selected or deselected.
     * 
     * @param value
     * @return available
     */
    public boolean contains(String value) {
        return available(value) || selected(value);
    }
    
    /**
     * Get if the value provided is available. I.e. in the list
     * provided by the data source.
     * 
     * @param value value
     * @return available
     */
    public boolean available(String value) {
        for(Iterator i = availableValues.iterator(); i.hasNext(); ) {
            if(((LabelValueBean)i.next()).getValue().equals(value)) {
                return true;
            }
        }
        return false;        
    }
    
    /**
     * Get if the value is selected.
     * 
     * @param value value
     * @return selected
     */
    public boolean selected(String value) {
        for(Iterator i = selectedValues.iterator(); i.hasNext(); ) {
            if(((LabelValueBean)i.next()).getValue().equals(value)) {
                return true;
            }
        }
        return false;        
    }
    
    /**
     * Rebuild the selected values.
     * 
     * @param session session
     */
    public void rebuild(SessionInfo session) {
        availableValues = new ArrayList<LabelValueBean>(dataSource.getValues(session));
        selectedValues = new ArrayList<LabelValueBean>();
        availableMap = new HashMap<String, LabelValueBean>();
        for(Iterator i = availableValues.iterator(); i.hasNext(); ) {
            LabelValueBean lvb = (LabelValueBean)i.next();
            availableMap.put(lvb.getValue(), lvb);
        }
        for(Iterator i = propertyList.iterator(); i.hasNext(); ) {
            String v = (String)i.next();
            LabelValueBean lvb = (LabelValueBean)availableMap.get(v);
            if(lvb != null) {
                selectedValues.add(lvb);
                availableValues.remove(lvb);
                availableMap.remove(lvb.getValue());
            }
        }
    }

    /**
     * Get the selected values in <i>Property Text</i> format
     * suitable for persisting.
     * 
     * @return selected values as property text
     */
    public String getAsPropertyText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = selectedValues.iterator(); i.hasNext();) {
            LabelValueBean lvb = (LabelValueBean)i.next();
            if (buf.length() > 0) {
                buf.append("!");
            }
            buf.append(lvb.getValue().replaceAll("\\!", "!!"));
        }
        return buf.toString();
    }
    
    /**
     * Get the list of available values.
     * 
     * @return available values
     */
    public List<LabelValueBean> getAvailableValues() {
        return availableValues;
    }

    
    /**
     * Get the list of selected values.
     * 
     * @return selected values
     */
    public List<LabelValueBean> getSelectedValues() {
        return selectedValues;
    }

	/**
	 * Sort the selected values
	 * 
	 * @param comparator
	 */
	public void sortSelection(Comparator comparator) {
		Collections.sort(selectedValues, comparator);		
	}
}
