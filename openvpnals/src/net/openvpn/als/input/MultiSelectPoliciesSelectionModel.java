
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
			
package net.openvpn.als.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.security.SessionInfo;


/**
 * Model that maintains the selection state for a 'Multi Select' component
 * given a 'value' containing the section items and the list of possible values 
 * from the datasource.
 */
public class MultiSelectPoliciesSelectionModel extends MultiSelectSelectionModel {
    private List<LabelValueBean> notHiddenAvailableValues;
    private MultiSelectDataSource notHiddenDataSource;

    /**
     * Constructor
     * 
     * @param session session
     * @param dataSource data source for available values
     * @param propertyList selected values
     */
    public MultiSelectPoliciesSelectionModel(SessionInfo session, MultiSelectDataSource dataSource, MultiSelectDataSource notHiddenDataSource, PropertyList propertyList) {
        super(dataSource, propertyList);
        this.notHiddenDataSource = notHiddenDataSource;
        rebuild(session);
        
    }
    
    /**
     * Rebuild the selected values.
     * 
     * @param session session
     */
    public void rebuild(SessionInfo session) {
        availableValues = new ArrayList<LabelValueBean>(dataSource.getValues(session));
        notHiddenAvailableValues = notHiddenDataSource == null ? Collections.<LabelValueBean>emptyList() : new ArrayList<LabelValueBean>(notHiddenDataSource.getValues(session));
        selectedValues = new ArrayList<LabelValueBean>();
        availableMap = new HashMap<String, LabelValueBean>();
        Iterator i = (notHiddenAvailableValues.equals(Collections.<LabelValueBean>emptyList())) ? availableValues.iterator() : notHiddenAvailableValues.iterator();
        while(i.hasNext()) {
            LabelValueBean lvb = (LabelValueBean)i.next();
            availableMap.put(lvb.getValue(), lvb);
        }
        for(Iterator iter = propertyList.iterator(); iter.hasNext(); ) {
            String v = (String)iter.next();
            LabelValueBean lvb = (LabelValueBean)availableMap.get(v);
            if(lvb != null) {
                selectedValues.add(lvb);
                availableValues.remove(lvb);
                availableMap.remove(lvb.getValue());
            }
        }
    }
    
    @Override
    public List<LabelValueBean> getAvailableValues() {
        return availableValues;
    }

    public List<LabelValueBean> getNotHiddenAvailableValues() {
        return notHiddenAvailableValues;
    }
}
