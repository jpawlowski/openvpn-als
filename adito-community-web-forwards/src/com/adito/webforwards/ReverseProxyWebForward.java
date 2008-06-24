
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
			
package com.adito.webforwards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.adito.core.CoreEvent;
import com.adito.policyframework.LaunchSession;

/**
 * 
 */
public class ReverseProxyWebForward extends AbstractAuthenticatingWebForward {

	private String paths;

	private boolean activeDNS = false;

	private String hostHeader;

	private HashMap customHeaders = new HashMap();

	private boolean isURLRoot = false;

    private String charset = null;
    
	public ReverseProxyWebForward(int realmID, int id, int reverseProxyTypeId,
			String destinationURL, String shortName, String description,
			String category,
			String authenticationUsername,
			String authenticationPassword, String preferredAuthenticationMethod, String formType, String formParameters,
			String paths, String hostHeader,
			boolean activeDNS, boolean autoStart, Calendar dateCreated, Calendar dateAmended, String charset) {
		super(realmID, id, reverseProxyTypeId, destinationURL, shortName,
				description, category,
				authenticationUsername, authenticationPassword,
				preferredAuthenticationMethod, formType, formParameters, autoStart,
				dateCreated, dateAmended);
		this.paths = paths;
		this.activeDNS = activeDNS;
		this.charset = charset;
		try {
			URL u = new URL(destinationURL);
			isURLRoot = u.getPath().equals("") || u.getPath().equals("/");
		} catch (MalformedURLException ex) {
		}

		this.hostHeader = hostHeader;
	}

    public String getPaths() {
		return paths;
	}

	public boolean isURLRoot() {
		return isURLRoot;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}

	public boolean containsCustomHeader(String header) {
		return customHeaders.containsKey(header.toLowerCase());
	}

	public void setCustomHeader(String header, String value) {
		if (!customHeaders.containsKey(header.toLowerCase())) {
			customHeaders.put(header.toLowerCase(), new Vector());
		}

		Vector v = (Vector) customHeaders.get(header.toLowerCase());

		v.add(value);
	}

	public Map getCustomHeaders() {
		return customHeaders;
	}

	public void setActiveDNS(boolean activeDNS) {
		this.activeDNS = activeDNS;
	}

	public boolean getActiveDNS() {
		return activeDNS;
	}

	public boolean isValidPath(String pathInContext) {
		StringTokenizer t = new StringTokenizer(paths, "\n");
		while (t.hasMoreTokens()) {
			if (pathInContext.startsWith(t.nextToken())) {
				return true;
			}
		}
		return false;
	}

	public String getHostHeader() {
		return hostHeader;
	}

	public void setHostHeader(String hostHeader) {
		this.hostHeader = hostHeader;
	}
    
    public void addCustomHeadersToEvent(CoreEvent evt, String key){
        int counter = 1;
        Iterator itr = this.customHeaders.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry element = (Map.Entry) itr.next();
            if (!"".equals(element.getKey())){
                evt.addAttribute(key+" "+counter, element.toString());
                counter++;
            }
        }
    }

    public void addPathsToEvent(CoreEvent evt, String key){
        int counter = 1;
        StringTokenizer t = new StringTokenizer(this.paths, "\n");
        while (t.hasMoreElements()) {
            String element = (String) t.nextElement();
            evt.addAttribute(key+" "+counter, element);
            counter++;
        }
    }
    
    
    public String getCharset() {
        return charset;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }

	public String getLaunchUri(LaunchSession launchSession) {
		return "launchReverseProxy.do?" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId();
	}

}
