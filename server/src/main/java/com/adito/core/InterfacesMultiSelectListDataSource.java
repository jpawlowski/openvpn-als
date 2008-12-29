
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
			
package com.adito.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import com.adito.input.MultiSelectDataSource;
import com.adito.security.SessionInfo;

/**
 * Implementation of a {@link com.adito.input.MultiSelectDataSource} that
 * retrieves its values from the available network interfaces on this machine.
 */

public class InterfacesMultiSelectListDataSource implements MultiSelectDataSource {
    
    final static Log log = LogFactory.getLog(InterfacesMultiSelectListDataSource.class);
    

    /* (non-Javadoc)
     * @see com.adito.input.MultiSelectDataSource#getValues(com.adito.security.SessionInfo)
     */
    public Collection<LabelValueBean> getValues(SessionInfo session) {
        ArrayList l = new ArrayList();
        try {
            // TODO make this localised
            l.add(new LabelValueBean("All Interfaces", 
                "0.0.0.0"));            
            for(Enumeration e = NetworkInterface.getNetworkInterfaces();
                e.hasMoreElements(); ) {
                NetworkInterface ni = (NetworkInterface)e.nextElement();
                for(Enumeration e2 = ni.getInetAddresses(); e2.hasMoreElements(); ) {
                    InetAddress addr = (InetAddress)e2.nextElement();
                    l.add(new LabelValueBean(addr.getHostAddress(), 
                        addr.getHostAddress()));
                }                
            }
        }
        catch(Throwable t) {
            log.error("Failed to list network interfaces.", t);
        }
        return l;
    }
}
