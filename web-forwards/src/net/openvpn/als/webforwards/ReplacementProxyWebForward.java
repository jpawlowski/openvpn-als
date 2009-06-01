
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
			
package net.openvpn.als.webforwards;

import java.util.Calendar;
import java.util.Iterator;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.policyframework.LaunchSession;

/**
 * 
 */
public class ReplacementProxyWebForward extends AbstractAuthenticatingWebForward {

    private PropertyList restrictToHosts;
    private String encoding;

    /**
     * @param id
     * @param destinationURL
     * @param shortName
     * @param description
     * @param category
     * @param authenticationUsername
     * @param authenticationPassword
     * @param preferredAuthenticationMethod
     * @param encoding
     * @param restrictToHosts
     * @param formType
     * @param formParameters
     * @param dateCreated
     * @param dateAmended
     */
    public ReplacementProxyWebForward(int realmID, int id, String destinationURL, String shortName, String description, String category,
                    String authenticationUsername, String authenticationPassword, String preferredAuthenticationMethod,
                    String encoding, PropertyList restrictToHosts, String formType, String formParameters, boolean autoStart,
                    Calendar dateCreated, Calendar dateAmended) {
        super(realmID, id, WebForward.TYPE_REPLACEMENT_PROXY, destinationURL, shortName, description, category, authenticationUsername,
                        authenticationPassword, preferredAuthenticationMethod, formType, formParameters, autoStart,
                        dateCreated, dateAmended);
        this.restrictToHosts = restrictToHosts;
        this.encoding = encoding;
    }

    public PropertyList getRestrictToHosts() {
        return restrictToHosts;
    }

    public void setRestrictToHosts(PropertyList restrictToHosts) {
        this.restrictToHosts = restrictToHosts;
    }

    public String getEncoding() {
       return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public void addRestrictToHostsToEvent(CoreEvent evt, String key){
        int counter = 1;
        Iterator itr = this.restrictToHosts.iterator();
        while (itr.hasNext()) {
            String element = (String) itr.next();
            evt.addAttribute(key+" "+counter, element);
            counter++;
        }
    }

	public String getLaunchUri(LaunchSession launchSession) {
		return "launchReplacementProxy.do?" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId();
	}

}
