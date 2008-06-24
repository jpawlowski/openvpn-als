
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
			
package com.adito.agent.client.util;

import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.Test;

import com.adito.agent.client.BrowserProxySettings;
import com.adito.agent.client.ProxyInfo;

public class BrowserProxySettingsTest {

    @Test
    public void testFirstProxyIsActiveProfile() throws Exception {
        
        Vector proxies = new Vector();
        Vector bypassAddr = new Vector();

        ProxyInfo proxy1 = new ProxyInfo("ssl", "", "", "host1", 12, "sourceIdent1");
        ProxyInfo proxy2 = new ProxyInfo("ssl", "", "", "host2", 13, "sourceIdent2");
        proxy2.setActiveProfile(true);
        ProxyInfo proxy3 = new ProxyInfo("ssl", "", "", "host3", 14, "sourceIdent3");

        proxies.addElement(proxy1);
        proxies.addElement(proxy2);
        proxies.addElement(proxy3);
        
        BrowserProxySettings bps = new BrowserProxySettings();
        bps.setBrowser("Mozilla Firefox");
        bps.setProxiesActiveFirst(proxies);
        bps.setBypassAddr(new String[bypassAddr.size()]);
        bypassAddr.copyInto(bps.getBypassAddr());
        
        assertTrue("There should be three proxies.", bps.getProxies().length == 3);
        assertTrue("This first proxy should be the active profile.", bps.getProxies()[0].isActiveProfile());
    }

}
